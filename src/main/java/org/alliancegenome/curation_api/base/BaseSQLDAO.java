package org.alliancegenome.curation_api.base;

import java.util.*;

import javax.inject.Singleton;
import javax.persistence.*;
import javax.persistence.criteria.*;

import org.alliancegenome.curation_api.model.dto.Pagination;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@Singleton
public class BaseSQLDAO<E extends BaseEntity> extends BaseDAO<E> {

	@PersistenceContext(name = "primary")
	private EntityManager entityManager;
	
	//private SearchSession searchSession = null;

	protected BaseSQLDAO(Class<E> myClass) {
		super(myClass);
		//searchSession = Search.session(entityManager);
		log.debug("Entitymanager: " + entityManager);
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
		try {
			log.debug("Starting Index for: " + myClass);
			org.hibernate.search.jpa.FullTextEntityManager ftem = org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);
			ftem.createIndexer(myClass).startAndWait();

			//MassIndexer indexer = searchSession.massIndexer(myClass).threadsToLoadObjects(10);
			//indexer.startAndWait();
			
			log.debug("Indexing finished: ");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
//	public SearchResult<E> searchByField(String field, String value) {
//		return searchSession.search(myClass).where(f -> f.match().field(field).matching(value)).fetch(1000);
//	}
//	
//	public SearchResult<E> searchByParams(Map<String, Object> params) {
//		return searchByParams(params, null);
//	}
//	
//	public SearchResult<E> searchByParams(Map<String, Object> params, String orderByField) {
//		
//		return null;
//	}
	
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
