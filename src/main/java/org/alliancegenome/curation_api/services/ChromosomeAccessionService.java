package org.alliancegenome.curation_api.services;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.dao.ChromosomeAccessionDAO;
import org.alliancegenome.curation_api.model.entities.ChromosomeAccession;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChromosomeAccessionService {

	@Inject ChromosomeAccessionDAO chromosomeAccessionDAO;

	private volatile List<ChromosomeAccession> cache;

	private List<ChromosomeAccession> getCache() {
		if (cache == null) {
			synchronized (this) {
				if (cache == null) {
					cache = chromosomeAccessionDAO.findAll().getResults().stream()
						.sorted(Comparator.comparing(ChromosomeAccession::getDisplayOrder))
						.toList();
				}
			}
		}
		return cache;
	}

	public String getChromosomeAccession(String chromosomeName, String assemblyIdentifier) {
		if (chromosomeName == null || assemblyIdentifier == null) {
			return null;
		}
		for (ChromosomeAccession chromosome : getCache()) {
			if (chromosomeName.equals(chromosome.getChromosomeName())
					&& assemblyIdentifier.equals(chromosome.getAssemblyIdentifier())) {
				return chromosome.getAccession();
			}
		}
		return null;
	}

	public ChromosomeAccession getChromosomeAccessionByAccession(String accession) {
		if (accession == null) {
			return null;
		}
		for (ChromosomeAccession chromosome : getCache()) {
			if (Objects.equals(accession, chromosome.getAccession())) {
				return chromosome;
			}
		}
		return null;
	}

}
