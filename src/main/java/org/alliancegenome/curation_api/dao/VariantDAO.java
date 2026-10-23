package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseCurieSQLDAO;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.output.VariantHgvs;
import org.alliancegenome.curation_api.response.ObjectListResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class VariantDAO extends BaseCurieSQLDAO<Variant> {

	protected VariantDAO() {
		super(Variant.class);
	}

	public List<String> getAllVariantPrimaryExternalIds() {
		String sql = """
			SELECT be.primaryexternalid
			FROM biologicalentity be, variant as v
			WHERE be.id = v.id and be.primaryexternalid is not NULL
		""";
		
		Query query = entityManager.createNativeQuery(sql);
		List<Object> objects = query.getResultList();
		List<String> list = new ArrayList<>();
		
		objects.forEach(object -> {
			list.add((String) object);
		});
		
		return list;
	}

	// SCRUM-6757: the allele - variant pairs of a data provider's variants, none of them obsolete
	private static final String DATA_PROVIDER_ALLELE_VARIANTS = """
			FROM allelevariantassociation ava
			JOIN biologicalentity a ON a.id = ava.alleleassociationsubject_id
			JOIN biologicalentity v ON v.id = ava.allelevariantassociationobject_id
			JOIN organization o ON o.id = v.dataprovider_id
			WHERE o.abbreviation = :dataProvider AND NOT ava.obsolete AND NOT a.obsolete AND NOT v.obsolete
		""";

	public long countVariantHgvs(String dataProvider) {
		Query query = entityManager.createNativeQuery("SELECT count(*) " + DATA_PROVIDER_ALLELE_VARIANTS);
		query.setParameter("dataProvider", dataProvider);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * A page of a data provider's allele - variant pairs with their HGVS: the genomic HGVS of the
	 * variant's curated location, and the distinct coding and protein HGVS of that location's predicted
	 * consequences. The page is cut first, so the HGVS is gathered for its rows only.
	 */
	public List<VariantHgvs> findVariantHgvs(String dataProvider, int page, int limit) {
		String sql = """
			WITH page AS (
				SELECT ava.id, a.primaryexternalid AS allele_id, v.id AS variant_id, v.curie AS variant_curie
			""" + DATA_PROVIDER_ALLELE_VARIANTS + """
				ORDER BY ava.id LIMIT :limit OFFSET :offset
			)
			SELECT page.allele_id, page.variant_curie, l.hgvs,
				(SELECT string_agg(DISTINCT p.hgvscodingnomenclature, '|' ORDER BY p.hgvscodingnomenclature)
					FROM predictedvariantconsequence p WHERE p.variantgenomiclocation_id = l.id AND NOT p.obsolete),
				(SELECT string_agg(DISTINCT p.hgvsproteinnomenclature, '|' ORDER BY p.hgvsproteinnomenclature)
					FROM predictedvariantconsequence p WHERE p.variantgenomiclocation_id = l.id AND NOT p.obsolete)
			FROM page
			LEFT JOIN curatedvariantgenomiclocation l ON l.variantassociationsubject_id = page.variant_id AND NOT l.obsolete
			ORDER BY page.id
		""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("dataProvider", dataProvider);
		query.setParameter("limit", limit);
		query.setParameter("offset", page * limit);

		List<Object[]> rows = query.getResultList();
		List<VariantHgvs> results = new ArrayList<>();
		for (Object[] row : rows) {
			results.add(new VariantHgvs((String) row[0], (String) row[1], (String) row[2], splitHgvs(row[3]), splitHgvs(row[4])));
		}
		return results;
	}

	// HGVS expressions never contain '|', so it separates them in the aggregate
	private static List<String> splitHgvs(Object aggregate) {
		if (aggregate == null) {
			return List.of();
		}
		return List.of(((String) aggregate).split("\\|"));
	}

	public ObjectListResponse<String> getAllVariantNames() {

		String sql = """
				SELECT DISTINCT hgvs from curatedvariantgenomiclocation
			""";
		Query query = entityManager.createNativeQuery(sql);
		List<Object> objects = query.getResultList();
		List<String> hgvsList = new ArrayList<>();
		objects.forEach(object -> {
			hgvsList.add((String) object);
		});
		ObjectListResponse<String> ret = new ObjectListResponse<>();
		ret.setEntities(hgvsList);
		return ret;
	}
}
