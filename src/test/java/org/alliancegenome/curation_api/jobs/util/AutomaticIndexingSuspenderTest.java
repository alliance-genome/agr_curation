package org.alliancegenome.curation_api.jobs.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.hibernate.search.mapper.orm.mapping.SearchMapping;
import org.hibernate.search.mapper.pojo.work.SearchIndexingPlanFilter;
import org.hibernate.search.mapper.pojo.work.SearchIndexingPlanFilterContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pins the counting behind the application wide indexing plan filter. Construct loads for
 * different providers run concurrently, so getting this wrong either turns indexing back on
 * under a load that is still writing, or leaves constructs unindexed for good.
 */
class AutomaticIndexingSuspenderTest {

	private AutomaticIndexingSuspender suspender;
	private Set<Class<?>> excluded;

	@BeforeEach
	void setUp() {
		excluded = new HashSet<>();
		SearchMapping searchMapping = mock(SearchMapping.class);
		doAnswer(invocation -> {
			SearchIndexingPlanFilter filter = invocation.getArgument(0);
			Set<Class<?>> applied = new HashSet<>();
			SearchIndexingPlanFilterContext ctx = mock(SearchIndexingPlanFilterContext.class);
			doAnswer(i -> {
				applied.add(i.getArgument(0));
				return ctx;
			}).when(ctx).exclude(any(Class.class));
			filter.apply(ctx);
			excluded = applied;
			return null;
		}).when(searchMapping).indexingPlanFilter(any());

		suspender = new AutomaticIndexingSuspender();
		suspender.searchMapping = searchMapping;
		suspender.threadsToLoadObjects = 12;
	}

	@Test
	void singleLoadSuspendsAndResumes() {
		suspender.suspend(Construct.class);
		assertEquals(Set.of(Construct.class), excluded);

		assertTrue(suspender.resume(Construct.class));
		assertTrue(excluded.isEmpty());
	}

	@Test
	void overlappingLoadsKeepIndexingOffUntilTheLastFinishes() {
		suspender.suspend(Construct.class);
		suspender.suspend(Construct.class);

		assertFalse(suspender.resume(Construct.class));
		assertEquals(Set.of(Construct.class), excluded);

		assertTrue(suspender.resume(Construct.class));
		assertTrue(excluded.isEmpty());
	}

	@Test
	void resumingOneTypeLeavesOthersSuspended() {
		suspender.suspend(Construct.class);
		suspender.suspend(Gene.class);

		assertTrue(suspender.resume(Construct.class));
		assertEquals(Set.of(Gene.class), excluded);
	}

	@Test
	void onlyTheLastOverlappingLoadReindexes() {
		BaseSQLDAO<?> dao = mock(BaseSQLDAO.class);
		when(dao.reindex(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt())).thenAnswer(i -> {
			// Automatic indexing must already be back on, or edits made during the reindex are lost
			assertTrue(excluded.isEmpty());
			return CompletableFuture.completedFuture(null);
		});

		suspender.suspend(Construct.class);
		suspender.suspend(Construct.class);

		suspender.resumeAndReindex(Construct.class, dao);
		verify(dao, never()).reindex(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt());

		suspender.resumeAndReindex(Construct.class, dao);
		verify(dao, times(1)).reindex(anyInt(), anyInt(), anyInt(), eq(12), anyInt(), anyInt());
	}
}
