package org.alliancegenome.curation_api.jobs.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.search.mapper.orm.mapping.SearchMapping;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6535: switches off automatic (per-commit) indexing of an entity type while a bulk load
 * writes it, so the load does not wait on OpenSearch once per record. The caller reindexes the
 * type once, when it has finished.
 *
 * The indexing plan filter is application wide and loads run concurrently, so suspensions are
 * counted: indexing of a type only comes back when the last load that suspended it resumes it.
 */
@ApplicationScoped
public class AutomaticIndexingSuspender {

	@Inject SearchMapping searchMapping;

	// The post-load reindex spends most of its time loading entities and their embedded collections
	// from the database, not writing to OpenSearch, so it scales with loading threads. At the
	// default of 4 the FB cassette reindex ran in bursts: ~4,000 documents in 5s, then 15-20s idle.
	@ConfigProperty(name = "reindex.postLoad.threadsToLoadObjects", defaultValue = "12")
	Integer threadsToLoadObjects;

	private final Map<Class<?>, Integer> suspensions = new HashMap<>();
	private final Map<Class<?>, Object> reindexLocks = new ConcurrentHashMap<>();

	public synchronized void suspend(Class<?> entityClass) {
		suspensions.merge(entityClass, 1, Integer::sum);
		Log.info("Automatic indexing suspended for " + entityClass.getSimpleName() + " (" + suspensions.get(entityClass) + " load(s))");
		applyFilter();
	}

	/**
	 * @return true when this was the last suspension of the type, i.e. automatic indexing is back
	 *         on and the caller is responsible for reindexing what was written in the meantime.
	 */
	public synchronized boolean resume(Class<?> entityClass) {
		Integer remaining = suspensions.merge(entityClass, -1, Integer::sum);
		if (remaining != null && remaining > 0) {
			Log.info("Automatic indexing still suspended for " + entityClass.getSimpleName() + " (" + remaining + " load(s))");
			return false;
		}
		suspensions.remove(entityClass);
		Log.info("Automatic indexing resumed for " + entityClass.getSimpleName());
		applyFilter();
		return true;
	}

	/**
	 * Resumes automatic indexing of the type and, if no other load still has it suspended, rebuilds
	 * its index from the database and waits for that to finish.
	 */
	public void resumeAndReindex(Class<?> entityClass, BaseSQLDAO<?> dao) {
		if (!resume(entityClass)) {
			Log.info("Another " + entityClass.getSimpleName() + " load is still running, it will reindex when it finishes");
			return;
		}
		// Serialised per type so a load finishing while an earlier reindex runs does not drop the index under it.
		// Not under this object's monitor, which would block every other load's suspend for the whole reindex.
		synchronized (reindexLocks.computeIfAbsent(entityClass, c -> new Object())) {
			Log.info("Reindexing " + entityClass.getSimpleName() + " with " + threadsToLoadObjects + " loading threads");
			dao.reindex(1000, 10000, 0, threadsToLoadObjects, 14400, 1).toCompletableFuture().join();
			Log.info("Reindexing " + entityClass.getSimpleName() + " finished");
		}
	}

	private void applyFilter() {
		Set<Class<?>> excluded = new HashSet<>(suspensions.keySet());
		searchMapping.indexingPlanFilter(ctx -> excluded.forEach(ctx::exclude));
	}
}
