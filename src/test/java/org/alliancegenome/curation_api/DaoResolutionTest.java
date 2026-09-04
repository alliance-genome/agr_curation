package org.alliancegenome.curation_api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.GeneGeneticInteractionDAO;
import org.alliancegenome.curation_api.dao.GeneMolecularInteractionDAO;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetAnnotationDAO;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetSampleAnnotationDAO;
import org.alliancegenome.curation_api.dao.base.BaseCurieSQLDAO;
import org.junit.jupiter.api.Test;

/**
 * Review feedback on #2860: AlleleDAO still declared countMissingCuries and
 * findIdsMissingCuries with signatures identical to BaseCurieSQLDAO, so it overrode the shared
 * JPQL implementation and /system/mintallelecuries kept running hand-written native SQL while
 * every other class ran the generic path. Nothing failed, which is why it went unnoticed.
 *
 * This pins the resolution: every curie-carrying DAO must inherit all three methods from
 * BaseCurieSQLDAO rather than declaring its own.
 */
class DaoResolutionTest {

	private static final List<Class<? extends BaseCurieSQLDAO<?>>> DAOS =
		List.of(AlleleDAO.class, DiseaseAnnotationDAO.class, GeneDAO.class,
			// SCRUM-6463 — the four classes wired up last. Both interaction DAOs already carried
			// hand-written native SQL for other purposes, so they are the likeliest to reacquire a
			// local countMissingCuries and quietly stop using the shared path.
			GeneMolecularInteractionDAO.class, GeneGeneticInteractionDAO.class,
			HTPExpressionDatasetAnnotationDAO.class, HTPExpressionDatasetSampleAnnotationDAO.class);

	private static final List<String> SHARED_METHODS =
		List.of("countMissingCuries", "findIdsMissingCuries", "assignCuries");

	@Test
	void curieMintMethodsResolveToTheSharedBaseClass() throws Exception {
		for (Class<?> dao : DAOS) {
			for (String name : SHARED_METHODS) {
				long declared = java.util.Arrays.stream(dao.getDeclaredMethods())
					.filter(m -> m.getName().equals(name))
					.count();
				assertEquals(0, declared,
					dao.getSimpleName() + " declares its own " + name
						+ "(), which overrides BaseCurieSQLDAO and silently diverges from the shared path");
			}
		}
	}
}
