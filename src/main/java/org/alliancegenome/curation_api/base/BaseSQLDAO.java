package org.alliancegenome.curation_api.base;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;

import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.hibernate.search.engine.search.common.BooleanOperator;
import org.hibernate.search.engine.search.query.*;
import org.hibernate.search.engine.search.sort.dsl.CompositeSortComponentsStep;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class BaseSQLDAO<E extends BaseEntity> extends BaseDAO<E> {

    @Inject
    protected EntityManager entityManager;

    @Inject
    protected SearchSession searchSession;

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

    public E merge(E entity) {
        log.debug("SqlDAO: merge: " + entity);
        entityManager.merge(entity);
        return entity;
    }

    public E remove(String id) {
        log.debug("SqlDAO: remove: " + id);
        E entity = find(id);
        entityManager.remove(entity);
        return entity;
    }

    public E remove(Long id) {
        log.debug("SqlDAO: remove: " + id);
        E entity = find(id);
        entityManager.remove(entity);
        return entity;
    }

    public void reindex() {
        reindex(myClass, 4);
    }

    public void reindex(Class<E> objectClass, int threads) {
        log.debug("Starting Index for: " + objectClass);
        MassIndexer indexer = searchSession.massIndexer(objectClass).threadsToLoadObjects(threads);
        //indexer.dropAndCreateSchemaOnStart(true);
        indexer.transactionTimeout(900);
        indexer.start();
    }

    public SearchResponse<E> searchAll(Pagination pagination) {
        return searchByParams(pagination, null);
    }

    public SearchResponse<E> searchByField(Pagination pagination, String field, String value) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(field, value);
        return searchByParams(pagination, params);
    }


    public SearchResponse<E> searchByParams(Pagination pagination, Map<String, Object> params) {

        log.debug("Search: " + pagination + " Params: " + params);
        //SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //sourceBuilder.sort(new FieldSortBuilder("id").order(SortOrder.ASC)); 

        //Sort s = new 

        SearchQuery<E> query = searchSession.search(myClass)
                .where( p -> p.bool( b -> {

                    if(params.containsKey("searchFilters")) {
                        HashMap<String, HashMap<String, Object>> searchFilters = (HashMap<String, HashMap<String, Object>>)params.get("searchFilters");
                        for(String filterName: searchFilters.keySet()) {
                            for(String field: searchFilters.get(filterName).keySet()) {
                                b.must(
                                    p.simpleQueryString()
                                        .field(field)
                                        .matching((String)searchFilters.get(filterName).get(field))
                                        .defaultOperator(BooleanOperator.AND)
                                );
                            }
                        }
                    }
                }))
                .sort(f -> {
                    CompositeSortComponentsStep<?> com = f.composite();
                    if(params.containsKey("sortOrders")) {
                        ArrayList<HashMap<String, Object>> sortOrders = (ArrayList<HashMap<String, Object>>)params.get("sortOrders");
                        for(HashMap<String, Object> map: sortOrders) {
                            log.info("Map: " + map);
                            String key = (String)map.get("field");
                            log.info("Key: " + key);
                            int value = (int)map.get("order");
                            log.info("Value: " + value);
                            if(value == 1) {
                                com.add(f.field(key + "_keyword").asc());
                            }
                            if(value == -1) {
                                com.add(f.field(key + "_keyword").desc());
                            }
                        }
                    }
                    return com;
                })
                .toQuery();

        log.debug(query);
        SearchResult<E> result = query.fetch(pagination.getPage() * pagination.getLimit(), pagination.getLimit());

        SearchResponse<E> results = new SearchResponse<E>();
        results.setResults(result.hits());
        results.setTotalResults(result.total().hitCount());

        return results;

    }

    public SearchResponse<E> findByField(String field, String value) {
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
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
