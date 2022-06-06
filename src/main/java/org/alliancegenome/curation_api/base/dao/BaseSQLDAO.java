package org.alliancegenome.curation_api.base.dao;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.entity.BaseEntity;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.common.*;
import org.hibernate.search.engine.search.query.*;
import org.hibernate.search.engine.search.query.dsl.*;
import org.hibernate.search.engine.search.sort.dsl.CompositeSortComponentsStep;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.search.loading.dsl.SearchLoadingOptionsStep;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.massindexing.MassIndexingMonitor;
import org.reflections.Reflections;

import io.micrometer.core.instrument.*;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class BaseSQLDAO<E extends BaseEntity> extends BaseEntityDAO<E> {

    @Inject protected EntityManager entityManager;
    @Inject protected SearchSession searchSession;
    @Inject protected MeterRegistry registry;

    protected BaseSQLDAO(Class<E> myClass) {
        super(myClass);
        log.debug("EntityManager: " + entityManager);
        log.debug("SearchSession: " + searchSession);
    }

    @Transactional
    public E persist(E entity) {
        log.debug("SqlDAO: persist: " + entity);
        entityManager.persist(entity);
        return entity;
    }

    @Transactional
    public List<E> persist(List<E> entities) {
        log.debug("SqlDAO: persist: " + entities);
        entityManager.persist(entities);
        return entities;
    }

    public E find(String id) {
        log.debug("SqlDAO: find: " + id + " " + myClass);
        if(id != null) {
            E entity = entityManager.find(myClass, id);
            log.debug("Entity Found: " + entity);
            return entity;
        } else {
            log.debug("Input Param is null: " + id);
            return null;
        }
    }

    public E find(Long id) {
        log.debug("SqlDAO: find: " + id + " " + myClass);
        if(id != null) {
            E entity = entityManager.find(myClass, id);
            log.debug("Entity Found: " + entity);
            return entity;
        } else {
            log.debug("Input Param is null: " + id);
            return null;
        }
    }

    public SearchResponse<E> findAll(Pagination pagination) {
        log.debug("SqlDAO: findAll: " + myClass);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> findQuery = cb.createQuery(myClass);
        Root<E> rootEntry = findQuery.from(myClass);
        CriteriaQuery<E> all = findQuery.select(rootEntry);

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(myClass)));
        Long totalResults = entityManager.createQuery(countQuery).getSingleResult();

        TypedQuery<E> allQuery = entityManager.createQuery(all);
        if(pagination != null && pagination.getLimit() != null && pagination.getPage() != null) {
            int first = pagination.getPage() * pagination.getLimit();
            if(first < 0) first = 0;
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
        log.debug("SqlDAO: merge: " + entity);
        entityManager.merge(entity);
        return entity;
    }

    @Transactional
    public E remove(String id) {
        log.debug("SqlDAO: remove: " + id);
        E entity = find(id);
        entityManager.remove(entity);
        return entity;
    }

    @Transactional
    public E remove(Long id) {
        log.debug("SqlDAO: remove: " + id);
        E entity = find(id);
        entityManager.remove(entity);
        return entity;
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
        return searchSession.search(clazz).where( f -> f.matchAll() ).fetchTotalHitCount();
    }

    public void flush() {
        entityManager.flush();
    }

    public void commit() {
        entityManager.getTransaction().commit();
    }

    public void reindex() {
        reindex(myClass, 4, 0, 1000);
    }

    public void reindex(int threads, int indexAmount, int batchSize) {
        reindex(myClass, threads, indexAmount, batchSize);
    }

    public void reindexEverything(int threads, int indexAmount, int batchSize) {
        Reflections reflections = new Reflections("org.alliancegenome.curation_api");
        Set<Class<?>> annotatedClasses = reflections.get(TypesAnnotated.with(Indexed.class).asClass(reflections.getConfiguration().getClassLoaders()));

        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("MassIndex:");

        MassIndexer indexer =
                searchSession
                .massIndexer(annotatedClasses)
                .batchSizeToLoadObjects(batchSize)
                .dropAndCreateSchemaOnStart(true)
                .mergeSegmentsOnFinish(true)
                .typesToIndexInParallel(threads)
                .threadsToLoadObjects(threads)
                .monitor(new MassIndexingMonitor() {

                    @Override
                    public void documentsAdded(long increment) {
                    }

                    @Override
                    public void documentsBuilt(long increment) {
                        ph.progressProcess();
                    }

                    @Override
                    public void entitiesLoaded(long increment) {
                    }

                    @Override
                    public void addToTotalCount(long increment) {
                    }

                    @Override
                    public void indexingCompleted() {
                        ph.finishProcess();
                    }

                });
        //indexer.dropAndCreateSchemaOnStart(true);
        indexer.transactionTimeout(900);
        if(indexAmount > 0){
            indexer.limitIndexedObjectsTo(indexAmount);
        }
        indexer.start();

    }

    public void reindex(Class<?> objectClass, int threads, int indexAmount, int batchSize) {
        log.debug("Starting Index for: " + objectClass);
        MassIndexer indexer =
                searchSession
                .massIndexer(objectClass)
                .batchSizeToLoadObjects(batchSize)
                .dropAndCreateSchemaOnStart(true)
                .mergeSegmentsOnFinish(true)
                .typesToIndexInParallel(threads)
                .threadsToLoadObjects(threads)
                .monitor(new MassIndexingMonitor() {

                    ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
                    Counter counter;

                    @Override
                    public void documentsAdded(long increment) {
                        //log.info("documentsAdded: " + increment);
                    }

                    @Override
                    public void documentsBuilt(long increment) {
                        ph.progressProcess();
                        counter.increment();
                    }

                    @Override
                    public void entitiesLoaded(long increment) {
                        //log.info("entitiesLoaded: " + increment);
                    }

                    @Override
                    public void addToTotalCount(long increment) {
                        ph.startProcess("Mass Indexer for: " + objectClass.getSimpleName(), increment);
                        counter = registry.counter("reindex." + objectClass.getSimpleName());
                        //registry.timer("").
                    }

                    @Override
                    public void indexingCompleted() {
                        ph.finishProcess();
                        registry.remove(counter);
                    }

                });
        //indexer.dropAndCreateSchemaOnStart(true);
        indexer.transactionTimeout(900);
        if(indexAmount > 0){
            indexer.limitIndexedObjectsTo(indexAmount);
        }
        indexer.start();
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
        log.debug("Search: " + pagination + " Params: " + params);

        SearchQueryOptionsStep<?, E, SearchLoadingOptionsStep, ?, ?> step =
                searchSession.search(myClass).where( p -> {
                    return p.bool( b -> {
                        if(params.containsKey("searchFilters") ) {
                            HashMap<String, HashMap<String, HashMap<String, Object>>> searchFilters = (HashMap<String, HashMap<String, HashMap<String, Object>>>)params.get("searchFilters");
                            for(String filterName: searchFilters.keySet()) {
                                b.must(m -> {
                                    return m.bool(s -> {
                                        int boost = 0;
                                        for(String field: searchFilters.get(filterName).keySet()) {
                                            float value = (float)(100/Math.pow(10, boost));
                                            String op = (String)searchFilters.get(filterName).get(field).get("tokenOperator");
                                            if(op== null) op = "AND";

                                            String queryField = field;

                                            Boolean useKeywordFields = (Boolean)searchFilters.get(filterName).get(field).get("useKeywordFields");
                                            if(useKeywordFields != null && useKeywordFields) {
                                                queryField = field + "_keyword";
                                            }

                                            s.should(
                                                    p.simpleQueryString()
                                                    .fields(queryField)
                                                    .matching(searchFilters.get(filterName).get(field).get("queryString").toString())
                                                    .defaultOperator(op != null ? BooleanOperator.valueOf(op) : BooleanOperator.AND)
                                                    .boost(value >=1 ? value : 1)
                                                    //p.match().field(field).matching(searchFilters.get(filterName).get(field).toString()).boost(boost*10)
                                                    );
                                            boost++;
                                        }
                                    });
                                });
                            }
                        }
                       if(params.containsKey("nonNullFields") ) {
                          List<String> fields = (List<String>)params.get("nonNullFields");
                          fields.forEach(field -> b.must(m -> m.bool(s -> s.should(p.exists().field(field)))));
                       }
                    });
                });

        if(params.containsKey("sortOrders")) {
            step = step.sort(f -> {
                CompositeSortComponentsStep<?> com = f.composite();
                ArrayList<HashMap<String, Object>> sortOrders = (ArrayList<HashMap<String, Object>>)params.get("sortOrders");
                if(sortOrders != null){
                    for(HashMap<String, Object> map: sortOrders) {
                        String key = (String)map.get("field");
                        int value = (int)map.get("order");
                        if(value == 1) {
                            com.add(f.field(key + "_keyword").asc());
                        }
                        if(value == -1) {
                            com.add(f.field(key + "_keyword").desc());
                        }
                    }
                }
                return com;
            });
        }

        List<AggregationKey<Map<String, Long>>> aggKeys = new ArrayList<>();

        if(params.containsKey("aggregations")) {
            ArrayList<String> aggList = (ArrayList<String>)params.get("aggregations");
            for(String aggField: aggList) {
                AggregationKey<Map<String, Long>> aggKey = AggregationKey.of(aggField);
                aggKeys.add(aggKey);
                step = step.aggregation(aggKey, p -> p.terms().field(aggField + "_keyword", String.class, ValueConvert.NO).maxTermCount(10));
            }
        }

        SearchQuery<E> query = step.toQuery();

        log.debug(query);
        SearchResult<E> result = query.fetch(pagination.getPage() * pagination.getLimit(), pagination.getLimit());

        SearchResponse<E> results = new SearchResponse<E>();

        if(aggKeys.size() > 0) {
            Map<String, Map<String, Long>> aggregations = aggKeys.stream().collect(Collectors.toMap(AggregationKey::name, result::aggregation));
            results.setAggregations(aggregations);
        }

        results.setResults(result.hits());
        results.setTotalResults(result.total().hitCount());

        return results;

    }

    public SearchResponse<E> findByField(String field, Object value) {
        log.debug("SqlDAO: findByField: " + field + " " + value);
        HashMap<String, Object> params = new HashMap<>();
        params.put(field, value);
        SearchResponse<E> results = findByParams(null, params);
        log.debug("Result List: " + results);
        if(results.getResults().size() > 0) {
            return results;
        } else {
            return null;
        }
    }

    public SearchResponse<E> findByParams(Pagination pagination, Map<String, Object> params) {
        return findByParams(pagination, params, null);
    }

    public SearchResponse<E> findByParams(Pagination pagination, Map<String, Object> params, String orderByField) {
        if(orderByField != null) {
            log.debug("Search By Params: " + params + " Order by: " + orderByField);
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(myClass);
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<E> root = query.from(myClass);
        Root<E> countRoot = countQuery.from(myClass);

        //System.out.println("Root: " + root);
        List<Predicate> restrictions = new ArrayList<>();
        List<Predicate> countRestrictions = new ArrayList<>();
        //System.out.println(params);
        for(String key: params.keySet()) {
            Path<Object> column = null;
            Path<Object> countColumn = null;
            //System.out.println("Key: " + key);
            if(key.contains(".")) {
                String[] objects = key.split("\\.");
                for(String s: objects) {
                    //System.out.println("Looking up: " + s);
                    if(column != null) {
                        column = column.get(s);
                        countColumn = countColumn.get(s);
                    } else {
                        column = root.get(s);
                        countColumn = countRoot.get(s);
                    }
                    //System.out.println(column.getAlias());
                }
            } else {
                column = root.get(key);
                countColumn = countRoot.get(key);
            }

            //System.out.println(column.getAlias());

            Object value = params.get(key);
            log.debug("Object Type: " + value.getClass());
            if (value instanceof Integer) {
                log.debug("Integer Type: " + value);
                Integer desiredValue = (Integer) value;
                restrictions.add(builder.equal(column, desiredValue));
                countRestrictions.add(builder.equal(countColumn, desiredValue));
            } else if(value instanceof Enum) {
                log.debug("Enum Type: " + value);
                restrictions.add(builder.equal(column, value));
                countRestrictions.add(builder.equal(countColumn, value));
            } else if(value instanceof Long) {
                log.debug("Long Type: " + value);
                Long desiredValue = (Long) value;
                restrictions.add(builder.equal(column, desiredValue));
                countRestrictions.add(builder.equal(countColumn, desiredValue));
            } else if(value instanceof Boolean) {
                log.debug("Boolean Type: " + value);
                Boolean desiredValue = (Boolean) value;
                restrictions.add(builder.equal(column, desiredValue));
                countRestrictions.add(builder.equal(countColumn, desiredValue));
            } else {
                log.debug("String Type: " + value);
                String desiredValue = (String) value;
                restrictions.add(builder.equal(column, desiredValue));
                countRestrictions.add(builder.equal(countColumn, desiredValue));
            }
        }

        if(orderByField != null) {
            query.orderBy(builder.asc(root.get(orderByField)));
        }

        query.where(builder.and(restrictions.toArray(new Predicate[0])));


        countQuery.select(builder.count(countRoot));
        countQuery.where(builder.and(countRestrictions.toArray(new Predicate[0])));
        Long totalResults = entityManager.createQuery(countQuery).getSingleResult();

        TypedQuery<E> allQuery = entityManager.createQuery(query);
        if(pagination != null && pagination.getLimit() != null && pagination.getPage() != null) {
            int first = pagination.getPage() * pagination.getLimit();
            if(first < 0) first = 0;
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
