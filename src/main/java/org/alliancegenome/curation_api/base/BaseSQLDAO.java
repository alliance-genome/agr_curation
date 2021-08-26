package org.alliancegenome.curation_api.base;

import java.util.*;

import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;

import org.alliancegenome.curation_api.model.input.Pagination;
import org.hibernate.search.engine.search.query.*;
import org.hibernate.search.engine.search.sort.dsl.CompositeSortComponentsStep;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class BaseSQLDAO<E extends BaseEntity> extends BaseDAO<E> {

    @Inject
    EntityManager entityManager;

    @Inject
    SearchSession searchSession;

    protected BaseSQLDAO(Class<E> myClass) {
        super(myClass);
        log.debug("EntityManager: " + entityManager);
        log.debug("SearchSession: " + searchSession);
    }

    public E persist(E entity) {
        log.debug("SqlDAO: persist: " + entity);
        entityManager.persist(entity);
        return entity;
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

    public SearchResults<E> findAll(Pagination pagination) {
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
        SearchResults<E> results = new SearchResults<E>();
        results.setResults(allQuery.getResultList());
        results.setTotalResults(totalResults);
        return results;
    }

    public E merge(E entity) {
        log.debug("SqliteDAO: merge: " + entity);
        entityManager.merge(entity);
        return entity;
    }

    public E remove(String id) {
        log.debug("SqliteDAO: remove: " + id);
        E entity = find(id);
        entityManager.remove(entity);
        return entity;
    }

    public void reindex() {
        log.debug("Starting Index for: " + myClass);
        MassIndexer indexer = searchSession.massIndexer(myClass).threadsToLoadObjects(4);
        //indexer.dropAndCreateSchemaOnStart(true);
        indexer.transactionTimeout(600);
        indexer.start();
    }

    public SearchResults<E> searchAll(Pagination pagination) {
        return searchByParams(pagination, null);
    }

    public SearchResults<E> searchByField(Pagination pagination, String field, String value) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(field, value);
        return searchByParams(pagination, params);
    }


    public SearchResults<E> searchByParams(Pagination pagination, Map<String, Object> params) {

        log.debug("Search: " + pagination + " Params: " + params);
        //SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //sourceBuilder.sort(new FieldSortBuilder("id").order(SortOrder.ASC)); 

        //Sort s = new 

        SearchQuery<E> query = searchSession.search(myClass)
                .where( p -> p.bool( b -> {
                    if(params.containsKey("searchFilters")) {
                        HashMap<String, Object> searchFilters = (HashMap<String, Object>)params.get("searchFilters");
                        for(String key: searchFilters.keySet()) {
                            b.filter(
                                    p.wildcard().field(key).matching("*" + (String)searchFilters.get(key) + "*")
                                    );
                        }
                    }
                }))
                .sort(f -> {
                    CompositeSortComponentsStep<?> com = f.composite();
                    if(params.containsKey("sortOrders")) {
                        HashMap<String, Object> sortOrders = (HashMap<String, Object>)params.get("sortOrders");
                        for(String key: sortOrders.keySet()) {
                            if((int)sortOrders.get(key) == 1) {
                                com.add(f.field(key).asc());
                            }
                            if((int)sortOrders.get(key) == -1) {
                                com.add(f.field(key).desc());
                            }
                        }
                    }
                    return com;
                })
                .toQuery();

        log.debug(query);
        SearchResult<E> result = query.fetch(pagination.getPage() * pagination.getLimit(), pagination.getLimit());
        

        SearchResults<E> results = new SearchResults<E>();
        results.setResults(result.hits());
        results.setTotalResults(result.total().hitCount());

        return results;

    }

    public List<E> findByField(String field, String value) {
        log.debug("SqlDAO: findByField: " + field + " " + value);
        HashMap<String, Object> params = new HashMap<>();
        params.put(field, value);
        List<E> list = findByParams(params);
        log.debug("Result List: " + list);
        if(list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    public List<E> findByParams(Map<String, Object> params) {
        return findByParams(params, null);
    }

    public List<E> findByParams(Map<String, Object> params, String orderByField) {
        if(orderByField != null) {
            log.debug("Search By Params: " + params + " Order by: " + orderByField);
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(myClass);
        Root<E> root = query.from(myClass);
        //System.out.println("Root: " + root);
        List<Predicate> restrictions = new ArrayList<Predicate>();
        //System.out.println(params);
        for(String key: params.keySet()) {
            Path<Object> column = null;
            //System.out.println("Key: " + key);
            if(key.contains(".")) {
                String[] objects = key.split("\\.");
                for(String s: objects) {
                    //System.out.println("Looking up: " + s);
                    if(column != null) {
                        column = column.get(s);
                    } else {
                        column = root.get(s);
                    }
                    //System.out.println(column.getAlias());
                }
            } else {
                column = root.get(key);
            }

            //System.out.println(column.getAlias());

            Object value = params.get(key);
            log.debug("Object Type: " + value.getClass());
            if (value instanceof Integer) {
                log.debug("Integer Type: " + value);
                Integer desiredValue = (Integer) value;
                restrictions.add(builder.equal(column, desiredValue));
            } else if(value instanceof Enum) {
                log.debug("Enum Type: " + value);
                restrictions.add(builder.equal(column, value));
            } else if(value instanceof Long) {
                log.debug("Long Type: " + value);
                Long desiredValue = (Long) value;
                restrictions.add(builder.equal(column, desiredValue));
            } else if(value instanceof Boolean) {
                log.debug("Boolean Type: " + value);
                Boolean desiredValue = (Boolean) value;
                restrictions.add(builder.equal(column, desiredValue));
            } else {
                log.debug("String Type: " + value);
                String desiredValue = (String) value;
                restrictions.add(builder.equal(column, desiredValue));
            }
        }
        if(orderByField != null) {
            query.orderBy(builder.asc(root.get(orderByField)));
        }

        query.where(builder.and(restrictions.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getResultList();
    }

}
