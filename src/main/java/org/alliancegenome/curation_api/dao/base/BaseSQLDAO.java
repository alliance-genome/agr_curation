package org.alliancegenome.curation_api.dao.base;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.Metamodel;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.base.BaseEntity;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ProcessDisplayService;
import org.alliancegenome.curation_api.util.EsClientFactory;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.common.BooleanOperator;
import org.hibernate.search.engine.search.common.ValueConvert;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
import org.hibernate.search.engine.search.sort.dsl.CompositeSortComponentsStep;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.search.loading.dsl.SearchLoadingOptionsStep;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.massindexing.MassIndexingMonitor;
import org.reflections.Reflections;

import io.quarkus.logging.Log;

public class BaseSQLDAO<E extends BaseEntity> extends BaseEntityDAO<E> {

	@ConfigProperty(name = "quarkus.hibernate-search-orm.elasticsearch.hosts")
	String esHosts;
	
	@ConfigProperty(name = "quarkus.hibernate-search-orm.elasticsearch.protocol")
	String esProtocol;

	@Inject
	protected EntityManager entityManager;
	@Inject
	protected SearchSession searchSession;
	@Inject
	protected ProcessDisplayService processDisplayService;

	
	private int outerBoost = 0;
	private int innerBoost = 0;

	protected BaseSQLDAO(Class<E> myClass) {
		super(myClass);
		Log.debug("EntityManager: " + entityManager);
		Log.debug("SearchSession: " + searchSession);
	}

	@Transactional
	public E persist(E entity) {
		Log.debug("SqlDAO: persist: " + entity);
		entityManager.persist(entity);
		return entity;
	}

	@Transactional
	public List<E> persist(List<E> entities) {
		Log.debug("SqlDAO: persist: " + entities);
		entityManager.persist(entities);
		return entities;
	}

	public E find(String id) {
		Log.debug("SqlDAO: find: " + id + " " + myClass);
		if (id != null) {
			E entity = entityManager.find(myClass, id);
			Log.debug("Entity Found: " + entity);
			return entity;
		} else {
			Log.debug("Input Param is null: " + id);
			return null;
		}
	}

	public E find(Long id) {
		Log.debug("SqlDAO: find: " + id + " " + myClass);
		if (id != null) {
			E entity = entityManager.find(myClass, id);
			Log.debug("Entity Found: " + entity);
			return entity;
		} else {
			Log.debug("Input Param is null: " + id);
			return null;
		}
	}

	public SearchResponse<String> findAllIds() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> findQuery = cb.createQuery(myClass);
		Root<E> rootEntry = findQuery.from(myClass);
		
		CriteriaQuery<E> all = findQuery.select(rootEntry);

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		countQuery.select(cb.count(countQuery.from(myClass)));
		Long totalResults = entityManager.createQuery(countQuery).getSingleResult();

		TypedQuery<E> allQuery = entityManager.createQuery(all);
		SearchResponse<String> results = new SearchResponse<String>();

		List<String> primaryKeys = new ArrayList<>();

		for (E entity : allQuery.getResultList()) {
			String pkString;
			try{
				pkString = (String) entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
			} catch (ClassCastException e) {
				pkString = Long.toString((Long) entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity));
			}
			primaryKeys.add(pkString);
		}

		results.setResults(primaryKeys);
		results.setTotalResults(totalResults);
		return results;
	}

	public SearchResponse<E> findAll(Pagination pagination) {
		Log.debug("SqlDAO: findAll: " + myClass);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> findQuery = cb.createQuery(myClass);
		Root<E> rootEntry = findQuery.from(myClass);

		Metamodel metaModel = entityManager.getMetamodel();
		IdentifiableType<E> of = (IdentifiableType<E>) metaModel.managedType(myClass);
		
		CriteriaQuery<E> all = findQuery.select(rootEntry).orderBy(cb.asc(rootEntry.get(of.getId(of.getIdType().getJavaType()).getName())));

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		countQuery.select(cb.count(countQuery.from(myClass)));
		Long totalResults = entityManager.createQuery(countQuery).getSingleResult();

		TypedQuery<E> allQuery = entityManager.createQuery(all);
		if (pagination != null && pagination.getLimit() != null && pagination.getPage() != null) {
			int first = pagination.getPage() * pagination.getLimit();
			if (first < 0)
				first = 0;
			allQuery.setFirstResult(first);
			allQuery.setMaxResults(pagination.getLimit());
		}
		SearchResponse<E> results = new SearchResponse<E>();
		results.setResults(allQuery.getResultList());
		results.setTotalResults(totalResults);
		return results;
	}

	@Transactional
	public E merge(E entity) {
		Log.debug("SqlDAO: merge: " + entity);
		entityManager.merge(entity);
		return entity;
	}

	@Transactional
	public E remove(String id) {
		Log.debug("SqlDAO: remove: " + id);
		E entity = find(id);

		try {
			entityManager.remove(entity);
			entityManager.flush();
		} catch (PersistenceException e) {
			handlePersistenceException(entity, e);
		}
		return entity;
	}

	@Transactional
	public E remove(Long id) {
		Log.debug("SqlDAO: remove: " + id);
		E entity = find(id);

		try {
			entityManager.remove(entity);
			entityManager.flush();
		} catch (PersistenceException e) {
			handlePersistenceException(entity, e);
		}
		return entity;
	}

	private void handlePersistenceException(E entity, Exception e) {
		ObjectResponse<E> response = new ObjectResponse<E>(entity);
		Throwable rootCause = e.getCause();
		while (rootCause.getCause() != null)
			rootCause = rootCause.getCause();
		if (rootCause instanceof ConstraintViolationException) {
			ConstraintViolationException cve = (ConstraintViolationException) rootCause;
			response.setErrorMessage("Violates database constraint " + cve.getConstraintName());
		} else {
			response.setErrorMessage(rootCause.getMessage());
		}
		throw new ApiErrorException(response);
	}

	// DB Count
	public Long dbCount(Class<?> clazz) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = qb.createQuery(Long.class);
		cq.select(qb.count(cq.from(clazz)));
		return entityManager.createQuery(cq).getSingleResult();
	}

	// ES Count
	public Long esCount(Class<?> clazz) {
		return searchSession.search(clazz).where(f -> f.matchAll()).fetchTotalHitCount();
	}

	public void flush() {
		entityManager.flush();
	}

	public void commit() {
		entityManager.getTransaction().commit();
	}

	public void reindex() {
		reindex(myClass, 1000, 10000, 0, 4, 14400, 1);
	}

	public void reindex(Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout, Integer typesToIndexInParallel) {
		reindex(myClass, batchSizeToLoadObjects, idFetchSize, limitIndexedObjectsTo, threadsToLoadObjects, transactionTimeout, typesToIndexInParallel);
	}

	public void reindexEverything(Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout,
		Integer typesToIndexInParallel) {
		Reflections reflections = new Reflections("org.alliancegenome.curation_api");
		Set<Class<?>> annotatedClasses = reflections.get(TypesAnnotated.with(Indexed.class).asClass(reflections.getConfiguration().getClassLoaders()));

		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("Mass Index Everything");
		MassIndexer indexer = searchSession.massIndexer(annotatedClasses).batchSizeToLoadObjects(batchSizeToLoadObjects).idFetchSize(idFetchSize).dropAndCreateSchemaOnStart(true)
			.mergeSegmentsOnFinish(true).typesToIndexInParallel(typesToIndexInParallel).threadsToLoadObjects(threadsToLoadObjects).monitor(new MassIndexingMonitor() {
				public void documentsAdded(long increment) {
				}

				public void entitiesLoaded(long increment) {
				}

				public void addToTotalCount(long increment) {
				}

				public void documentsBuilt(long increment) {
					ph.progressProcess();
				}

				public void indexingCompleted() {
					ph.finishProcess();
					setRefreshInterval();
				}
			});

		indexer.transactionTimeout(transactionTimeout);
		if (limitIndexedObjectsTo > 0) {
			indexer.limitIndexedObjectsTo(limitIndexedObjectsTo);
		}
		try {
			Log.info("Waiting for Full Indexer to finish");
			indexer.startAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void reindex(Class<?> objectClass, Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout,
		Integer typesToIndexInParallel) {

		Log.debug("Starting Indexing for: " + objectClass);
		MassIndexer indexer = searchSession.massIndexer(objectClass).batchSizeToLoadObjects(batchSizeToLoadObjects).idFetchSize(idFetchSize).dropAndCreateSchemaOnStart(true)
			.mergeSegmentsOnFinish(true).typesToIndexInParallel(typesToIndexInParallel).threadsToLoadObjects(threadsToLoadObjects).monitor(new MassIndexingMonitor() {

				ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);

				public void documentsAdded(long increment) {
				}

				public void entitiesLoaded(long increment) {
				}

				@Override
				public void addToTotalCount(long increment) {
					ph.addDisplayHandler(processDisplayService);
					ph.startProcess("Mass Indexer for: " + objectClass.getSimpleName(), increment);
				}

				@Override
				public void documentsBuilt(long increment) {
					ph.progressProcess();
				}

				@Override
				public void indexingCompleted() {
					ph.finishProcess();
					setRefreshInterval();
				}

			});
		// indexer.dropAndCreateSchemaOnStart(true);
		indexer.transactionTimeout(transactionTimeout);
		if (limitIndexedObjectsTo > 0) {
			indexer.limitIndexedObjectsTo(limitIndexedObjectsTo);
		}
		indexer.start();
	}

	public void setRefreshInterval() {
		RestHighLevelClient client = EsClientFactory.createClient(esHosts, esProtocol);
		Log.info("Creating Settings Search Client: " + esProtocol + "://" + esHosts);

		Map<String, String> settings = new HashMap<>();
		settings.put("refresh_interval", "1s");
		Log.info("Setting Refresh Interval: " + settings);
		UpdateSettingsRequest request = new UpdateSettingsRequest();
		request.indices("_all");
		request.settings(settings);
		try {
			AcknowledgedResponse resp = client.indices().putSettings(request, RequestOptions.DEFAULT);
			Log.info("Settings Change Complete: " + resp.isAcknowledged());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SearchResponse<E> searchAll(Pagination pagination) {
		return searchByParams(pagination, null);
	}

	public SearchResponse<E> searchByField(Pagination pagination, String field, String value) {
		HashMap<String, Object> params = new HashMap<>();
		params.put(field, value);
		return searchByParams(pagination, params);
	}

	public SearchResponse<E> searchByParams(Pagination pagination, Map<String, Object> params) {
		Log.debug("Search: " + pagination + " Params: " + params);

		SearchQueryOptionsStep<?, E, SearchLoadingOptionsStep, ?, ?> step = searchSession.search(myClass).where(p -> {
			return p.bool(b -> {
				if (params.containsKey("searchFilters")) {
					HashMap<String, HashMap<String, HashMap<String, Object>>> searchFilters = (HashMap<String, HashMap<String, HashMap<String, Object>>>) params.get("searchFilters");
					outerBoost = searchFilters.keySet().size();
					for (String filterName : searchFilters.keySet()) {
						b.must(m -> {
							return m.bool(s -> {
								s.must(f -> f.bool(q -> {
									innerBoost = searchFilters.get(filterName).keySet().size();
									for (String field : searchFilters.get(filterName).keySet()) {
										if (field.equals("nonNullFields") || field.equals("nullFields"))
											continue;
										float boost = (outerBoost * 10000) + (innerBoost * 1000);

										String op = (String) searchFilters.get(filterName).get(field).get("tokenOperator");
										BooleanOperator booleanOperator = op == null ? BooleanOperator.AND : BooleanOperator.valueOf(op);

										Boolean useKeywordFields = (Boolean) searchFilters.get(filterName).get(field).get("useKeywordFields");

										String queryType = (String) searchFilters.get(filterName).get(field).get("queryType");
										if (queryType != null && queryType.equals("matchQuery")) {
											BooleanPredicateClausesStep<?> clause = p.bool();
											if (useKeywordFields != null && useKeywordFields) {
												clause.should(p.match().field(field + "_keyword").matching(searchFilters.get(filterName).get(field).get("queryString").toString()).boost(boost + 500));
											}
											clause.should(p.match().field(field).matching(searchFilters.get(filterName).get(field).get("queryString").toString()).boost(boost));
											q.should(clause);
										} else { // assume simple query
											BooleanPredicateClausesStep<?> clause = p.bool();
											if (useKeywordFields != null && useKeywordFields) {
												clause.should(p.simpleQueryString().fields(field + "_keyword").matching(searchFilters.get(filterName).get(field).get("queryString").toString())
													.defaultOperator(booleanOperator).boost(boost + 500));
											}
											clause.should(p.simpleQueryString().fields(field).matching(searchFilters.get(filterName).get(field).get("queryString").toString())
												.defaultOperator(booleanOperator).boost(boost));
											q.should(clause);
										}
										innerBoost--;
									}
								}));
								if (searchFilters.get(filterName).containsKey("nonNullFields")) {
									s.must(f -> f.bool(q -> {
										List<String> fields = (List<String>) searchFilters.get(filterName).get("nonNullFields");
										fields.forEach(field -> q.must(p.exists().field(field)));
									}));
								}
								if (searchFilters.get(filterName).containsKey("nullFields")) {
									s.must(f -> f.bool(q -> {
										List<String> fields = (List<String>) searchFilters.get(filterName).get("nullFields");
										fields.forEach(field -> q.mustNot(p.exists().field(field)));
									}));
								}
							});
						});
						outerBoost--;
					}
				}
				if (params.containsKey("nonNullFieldsTable")) {
					List<String> fields = (List<String>) params.get("nonNullFieldsTable");
					fields.forEach(field -> b.must(m -> m.bool(s -> s.should(p.exists().field(field)))));
				}
			});
		});

		if (params.containsKey("sortOrders")) {
			step = step.sort(f -> {
				CompositeSortComponentsStep<?> com = f.composite();
				ArrayList<HashMap<String, Object>> sortOrders = (ArrayList<HashMap<String, Object>>) params.get("sortOrders");
				if (sortOrders != null) {
					for (HashMap<String, Object> map : sortOrders) {
						String key = (String) map.get("field");
						int value = (int) map.get("order");
						if (value == 1) {
							com.add(f.field(key + "_keyword").asc());
						}
						if (value == -1) {
							com.add(f.field(key + "_keyword").desc());
						}
					}
				}
				return com;
			});
		}

		List<AggregationKey<Map<String, Long>>> aggKeys = new ArrayList<>();

		if (params.containsKey("aggregations")) {
			ArrayList<String> aggList = (ArrayList<String>) params.get("aggregations");
			for (String aggField : aggList) {
				AggregationKey<Map<String, Long>> aggKey = AggregationKey.of(aggField);
				aggKeys.add(aggKey);
				step = step.aggregation(aggKey, p -> p.terms().field(aggField + "_keyword", String.class, ValueConvert.NO).maxTermCount(30));
			}
		}

		SearchQuery<E> query = step.toQuery();

		SearchResult<E> result = query.fetch(pagination.getPage() * pagination.getLimit(), pagination.getLimit());
		SearchResponse<E> results = new SearchResponse<E>();

		if (params.containsKey("debug")) {
			results.setDebug((String) params.get("debug"));
			results.setEsQuery(query.queryString());
			Log.info(query);
		} else {
			Log.debug(query);
		}

		if (aggKeys.size() > 0) {
			Map<String, Map<String, Long>> aggregations = aggKeys.stream().collect(Collectors.toMap(AggregationKey::name, result::aggregation));
			results.setAggregations(aggregations);
		}

		results.setResults(result.hits());
		results.setTotalResults(result.total().hitCount());

		return results;

	}

	public SearchResponse<E> findByField(String field, Object value) {
		Log.debug("SqlDAO: findByField: " + field + " " + value);
		HashMap<String, Object> params = new HashMap<>();
		params.put(field, value);
		SearchResponse<E> results = findByParams(null, params);
		Log.debug("Result List: " + results);
		if (results.getResults().size() > 0) {
			return results;
		} else {
			return null;
		}
	}
	
	public SearchResponse<E> findByParams(Map<String, Object> params) {
		return findByParams(null, params, null);
	}

	public SearchResponse<E> findByParams(Pagination pagination, Map<String, Object> params) {
		return findByParams(pagination, params, null);
	}

	public SearchResponse<E> findByParams(Pagination pagination, Map<String, Object> params, String orderByField) {
		if (orderByField != null) {
			Log.debug("Search By Params: " + params + " Order by: " + orderByField + " for class: " + myClass);
		} else {
			Log.debug("Search By Params: " + params + " for class: " + myClass);
		}

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> query = builder.createQuery(myClass);
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<E> root = query.from(myClass);
		Root<E> countRoot = countQuery.from(myClass);
		
		// System.out.println("Root: " + root);
		List<Predicate> restrictions = new ArrayList<>();
		List<Predicate> countRestrictions = new ArrayList<>();

		for (String key : params.keySet()) {
			Path<Object> column = null;
			Path<Object> countColumn = null;
			Log.debug("Key: " + key);
			if (key.contains(".")) {
				String[] objects = key.split("\\.");
				for (String s : objects) {
					Log.debug("Looking up: " + s);
					if (column != null) {
						Log.debug("Looking up via column: " + s);

						Path<Object> pathColumn = column.get(s);
						if (pathColumn.getJavaType().equals(List.class)) {
							column = ((Join) column).join(s);
						} else {
							column = pathColumn;
						}
						Path<Object> pathCountColumn = countColumn.get(s);
						if (pathCountColumn.getJavaType().equals(List.class)) {
							countColumn = ((Join) countColumn).join(s);
						} else {
							countColumn = pathCountColumn;
						}
					} else {
						Log.debug("Looking up via root: " + s);
						column = root.get(s);
						if (column.getJavaType().equals(List.class)) {
							column = root.join(s);
						}
						countColumn = countRoot.get(s);
						if (countColumn.getJavaType().equals(List.class)) {
							countColumn = countRoot.join(s);
						}
					}

					Log.debug("Column Alias: " + column.getAlias() + " Column Java Type: " + column.getJavaType() + " Column Model: " + column.getModel() + " Column Type Alias: "
						+ column.type().getAlias() + " Column Parent Path Alias: " + column.getParentPath().getAlias());
					Log.debug("Count Column Alias: " + countColumn.getAlias() + " Count Column Java Type: " + countColumn.getJavaType() + " Count Column Model: " + countColumn.getModel()
						+ " Count Column Type Alias: " + countColumn.type().getAlias() + " Count Column Parent Path Alias: " + countColumn.getParentPath().getAlias());
				}
			} else {
				column = root.get(key);
				if (column.getJavaType().equals(List.class)) {
					column = root.join(key);
				}
				countColumn = countRoot.get(key);
				if (countColumn.getJavaType().equals(List.class)) {
					countColumn = countRoot.join(key);
				}
			}

			Log.debug("Column Alias: " + column.getAlias() + " Column Java Type: " + column.getJavaType() + " Column Model: " + column.getModel() + " Column Type Alias: " + column.type().getAlias()
				+ " Column Parent Path Alias: " + column.getParentPath().getAlias());
			Log.debug("Count Column Alias: " + countColumn.getAlias() + " Count Column Java Type: " + countColumn.getJavaType() + " Count Column Model: " + countColumn.getModel()
				+ " Count Column Type Alias: " + countColumn.type().getAlias() + " Count Column Parent Path Alias: " + countColumn.getParentPath().getAlias());

			Object value = params.get(key);
			if (value != null) {
				Log.debug("Object Type: " + value.getClass());
				if (value instanceof Integer) {
					Log.debug("Integer Type: " + value);
					Integer desiredValue = (Integer) value;
					restrictions.add(builder.equal(column, desiredValue));
					countRestrictions.add(builder.equal(countColumn, desiredValue));
				} else if (value instanceof Enum) {
					Log.debug("Enum Type: " + value);
					restrictions.add(builder.equal(column, value));
					countRestrictions.add(builder.equal(countColumn, value));
				} else if (value instanceof Long) {
					Log.debug("Long Type: " + value);
					Long desiredValue = (Long) value;
					restrictions.add(builder.equal(column, desiredValue));
					countRestrictions.add(builder.equal(countColumn, desiredValue));
				} else if (value instanceof Boolean) {
					Log.debug("Boolean Type: " + value);
					Boolean desiredValue = (Boolean) value;
					restrictions.add(builder.equal(column, desiredValue));
					countRestrictions.add(builder.equal(countColumn, desiredValue));
				} else {
					Log.debug("String Type: " + value);
					String desiredValue = (String) value;
					restrictions.add(builder.equal(column, desiredValue));
					countRestrictions.add(builder.equal(countColumn, desiredValue));
				}
			} else {
				restrictions.add(builder.isEmpty(root.get(key)));
				countRestrictions.add(builder.isEmpty(countRoot.get(key)));
			}
		}

		if (orderByField != null) {
			query.orderBy(builder.asc(root.get(orderByField)));
		} else {
			Metamodel metaModel = entityManager.getMetamodel();
			IdentifiableType<E> of = (IdentifiableType<E>) metaModel.managedType(myClass);
			query.orderBy(builder.asc(root.get(of.getId(of.getIdType().getJavaType()).getName())));
		}

		query.where(builder.and(restrictions.toArray(new Predicate[0])));

		countQuery.select(builder.count(countRoot));
		countQuery.where(builder.and(countRestrictions.toArray(new Predicate[0])));
		Long totalResults = entityManager.createQuery(countQuery).getSingleResult();

		TypedQuery<E> allQuery = entityManager.createQuery(query);
		if (pagination != null && pagination.getLimit() != null && pagination.getPage() != null) {
			int first = pagination.getPage() * pagination.getLimit();
			if (first < 0)
				first = 0;
			allQuery.setFirstResult(first);
			allQuery.setMaxResults(pagination.getLimit());
		}

		SearchResponse<E> results = new SearchResponse<E>();
		results.setResults(allQuery.getResultList());
		results.setTotalResults(totalResults);
		return results;

	}

	public E getNewInstance() {
		try {
			return myClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
