package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.mati.entity.IdentifiersRange;
import org.alliancegenome.mati.interfaces.IdentifierResourceRESTInterface;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.arc.Arc;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import si.mazi.rescu.RestProxyFactory;

/**
 * Thin wrapper around agr_mati's {@link IdentifierResourceRESTInterface}. MaTI
 * returns only the first and last counter of the minted range; this service
 * expands the range into the full list of AGRKB curies the caller can assign.
 * <p>
 * Curie format follows agr_mati's IdentifierResource.formatCounter:
 * AGRKB:{subdomain_code}{counter as %012d}
 * e.g. AGRKB:100000000000001 for disease_annotation counter 1.
 */
@Log4j2
@ApplicationScoped
public class MatiService {
	public static final String SUBDOMAIN_DISEASE_ANNOTATION = "disease_annotation";

	@ConfigProperty(name = "mati.url")
	String matiURL;

	@Inject
	JsonWebToken jsonWebToken;

	@Inject
	AdminTokenService adminTokenService;

	private IdentifierResourceRESTInterface matiApi;

	/**
	 * Mints {@code n} consecutive curies in the given subdomain. Counts are
	 * advanced atomically by MaTI; once this method returns successfully, the
	 * curies are owned by this caller and the MaTI sequence will not hand them
	 * out again. Callers are responsible for persisting them durably; any
	 * curies returned but not persisted are lost.
	 *
	 * @param subdomain MaTI subdomain name (e.g. "disease_annotation")
	 * @param n         how many curies to mint; must be &gt; 0
	 * @return ordered list of {@code n} AGRKB curies
	 */

	@PostConstruct
	public void init() {
		matiApi = RestProxyFactory.createProxy(IdentifierResourceRESTInterface.class, matiURL + "/api");
	}

	/**
	 * Mints a single curie in the given subdomain. Convenience wrapper around
	 * {@link #mintCuries(String, int)} for callers that need exactly one.
	 *
	 * @param subdomain MaTI subdomain name (e.g. "disease_annotation")
	 * @return a single AGRKB curie
	 */
	public String mintCurie(String subdomain) {
		return mintCuries(subdomain, 1).getFirst();
	}

	public List<String> mintCuries(String subdomain, int n) {
		if (n <= 0) {
			return List.of();
		}

		// Forward the incoming curator's JWT as the Authorization header when one
		// is present; MaTI validates it against the same Cognito user pool. On
		// background/load paths there is no curator token, so fall back to the
		// admin (machine-to-machine) token. getRawToken()/getAdminAccessToken()
		// return the bare token, so prefix the standard "Bearer " scheme.
		String curatorRawToken = getCuratorRawToken();
		String authorization;
		if (curatorRawToken != null && !curatorRawToken.isBlank()) {
			authorization = "Bearer " + curatorRawToken;
		} else {
			authorization = "Bearer " + adminTokenService.getAdminAccessToken();
		}
		IdentifiersRange range = matiApi.increment(authorization, subdomain, n);

		long firstCtr = range.getFirst().getCounter();
		long lastCtr = range.getLast().getCounter();
		String code = range.getFirst().getSubdomain_code();

		long actual = lastCtr - firstCtr + 1;
		if (actual != n) {
			throw new IllegalStateException(
				"MaTI returned " + actual + " curies for subdomain=" + subdomain
					+ " but " + n + " were requested (first=" + firstCtr + ", last=" + lastCtr + ")");
		}

		// Log the range BEFORE returning so an operator can reconcile if the
		// caller crashes between this method returning and the curies being
		// persisted. The MaTI sequence has already advanced — these curies are
		// burned regardless of what happens next.
		log.info("Minted MaTI range subdomain={} code={} first={} last={} n={}",
			subdomain, code, firstCtr, lastCtr, n);

		List<String> curies = new ArrayList<>(n);
		for (long c = firstCtr; c <= lastCtr; c++) {
			curies.add(String.format("AGRKB:%s%012d", code, c));
		}
		return curies;
	}

	/**
	 * Returns the raw curator JWT if a request context is active and carries one,
	 * otherwise {@code null}. JsonWebToken is request-scoped, so touching it with
	 * no active request context (background/load threads) would throw; guard on
	 * the request context first so a missing token cleanly means "use admin token".
	 */
	private String getCuratorRawToken() {
		if (!Arc.container().requestContext().isActive()) {
			return null;
		}
		try {
			return jsonWebToken.getRawToken();
		} catch (Exception e) {
			return null;
		}
	}
}
