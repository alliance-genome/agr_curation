package org.alliancegenome.curation_api.services.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.model.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.apache.commons.collections4.map.HashedMap;

public abstract class BaseOntologyTermService<E extends OntologyTerm, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, BaseEntityDAO<E>> {

	@Inject
	CrossReferenceDAO crossReferenceDAO;
	@Inject
	SynonymDAO synonymDAO;
	@Inject
	CrossReferenceService crossReferenceService;
	
	@Inject
	@AuthenticatedUser
	LoggedInPerson authenticatedPerson;
	
	@Transactional
	public E processUpdate(E inTerm) {

		E term = dao.find(inTerm.getCurie());

		if(term == null) {
			term = dao.getNewInstance();
			term.setCurie(inTerm.getCurie());
		}
		
		term.setName(inTerm.getName());
		term.setType(inTerm.getType());
		term.setObsolete(inTerm.getObsolete());
		term.setNamespace(inTerm.getNamespace());
		term.setDefinition(inTerm.getDefinition());
		
		handleSubsets(term, inTerm);
		handleDefinitionUrls(term, inTerm);
		handleSecondaryIds(term, inTerm);
		handleSynonyms(term, inTerm);
		handleCrossReferences(term, inTerm);

		dao.persist(term);

		return term;
	}
	
	@Transactional
	public E processUpdateRelationships(E inTerm) {
		// TODO: 01 - figure out issues with ontologies
		E term = dao.find(inTerm.getCurie());

		term.setIsaParents(inTerm.getIsaParents());
		term.setIsaAncestors(inTerm.getIsaAncestors());
		
		return term;
	}

	private void handleDefinitionUrls(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		Set<String> currentDefinitionUrls;
		if(dbTerm.getDefinitionUrls() == null) {
			currentDefinitionUrls = new HashSet<>();
			dbTerm.setDefinitionUrls(new ArrayList<>());
		} else {
			currentDefinitionUrls = dbTerm.getDefinitionUrls().stream().collect(Collectors.toSet());
		}
		
		Set<String> newDefinitionUrls;
		if(incomingTerm.getDefinitionUrls() == null) {
			newDefinitionUrls = new HashSet<>();
		} else {
			newDefinitionUrls = incomingTerm.getDefinitionUrls().stream().collect(Collectors.toSet());
		}
		
		newDefinitionUrls.forEach(id -> {
			if(!currentDefinitionUrls.contains(id)) {
				dbTerm.getDefinitionUrls().add(id);
			}
		});
		
		currentDefinitionUrls.forEach(id -> {
			if(!newDefinitionUrls.contains(id)) {
				dbTerm.getDefinitionUrls().remove(id);
			}
		});

	}
	
	public ObjectListResponse<E> getRootNodes() {
		SearchResponse<E> t = dao.findByField("isaParents", null);
		return new ObjectListResponse<E>(t.getResults());
	}

	public ObjectListResponse<E> getChildren(String curie) {
		E term = dao.find(curie);
		if(term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getIsaChildren());
		} else {
			return new ObjectListResponse<E>();
		}
	}
	
	public ObjectListResponse<E> getDescendants(String curie) {
		E term = dao.find(curie);
		if(term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getIsaDescendants());
		} else {
			return new ObjectListResponse<E>();
		}
	}
	
	public ObjectListResponse<E> getParents(String curie) {
		E term = dao.find(curie);
		if(term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getIsaParents());
		} else {
			return new ObjectListResponse<E>();
		}
	}
	
	public ObjectListResponse<E> getAncestors(String curie) {
		E term = dao.find(curie);
		if(term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getIsaAncestors());
		} else {
			return new ObjectListResponse<E>();
		}
	}
	
	private void handleSubsets(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		Set<String> currentSubsets;
		if(dbTerm.getSubsets() == null) {
			currentSubsets = new HashSet<>();
			dbTerm.setSubsets(new ArrayList<>());
		} else {
			currentSubsets = dbTerm.getSubsets().stream().collect(Collectors.toSet());
		}
		
		Set<String> newSubsets;
		if(incomingTerm.getSubsets() == null) {
			newSubsets = new HashSet<>();
		} else {
			newSubsets = incomingTerm.getSubsets().stream().collect(Collectors.toSet());
		}
		
		newSubsets.forEach(id -> {
			if(!currentSubsets.contains(id)) {
				dbTerm.getSubsets().add(id);
			}
		});
		
		currentSubsets.forEach(id -> {
			if(!newSubsets.contains(id)) {
				dbTerm.getSubsets().remove(id);
			}
		});

	}
	
	private void handleCrossReferences(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		Map<String, CrossReference> currentIds;
		if(dbTerm.getCrossReferences() == null) {
			currentIds = new HashedMap<>();
			dbTerm.setCrossReferences(new ArrayList<>());
		} else {
			currentIds = dbTerm.getCrossReferences().stream().collect(Collectors.toMap(CrossReference::getCurie, Function.identity()));
		}
		Map<String, CrossReference> newIds;
		if(incomingTerm.getCrossReferences() == null) {
			newIds = new HashedMap<>();
		}
		else {
			newIds = incomingTerm.getCrossReferences().stream().collect(Collectors.toMap(CrossReference::getCurie, Function.identity()));
		}
		
		newIds.forEach((k, v) -> {
			if(!currentIds.containsKey(k)) {
				if(crossReferenceDAO.find(k) == null) {
					CrossReference cr = new CrossReference();
					cr.setCurie(v.getCurie());
					crossReferenceService.create(cr);
				}
				dbTerm.getCrossReferences().add(v);
			}
		});
		
		currentIds.forEach((k, v) -> {
			if(!newIds.containsKey(k)) {
				dbTerm.getCrossReferences().remove(v);
			}
		});
	}
	
	private void handleSynonyms(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		Set<Synonym> currentSynonyms;
		if(dbTerm.getSynonyms() == null) {
			currentSynonyms = new HashSet<>();
			dbTerm.setSynonyms(new ArrayList<>());
		} else {
			currentSynonyms = dbTerm.getSynonyms().stream().collect(Collectors.toSet());
		}
		List<String> currentSynonymNames = currentSynonyms.stream().map(Synonym::getName).collect(Collectors.toList());
		
		Set<Synonym> newSynonyms;
		if(incomingTerm.getSynonyms() == null) {
			newSynonyms = new HashSet<>();
		} else {
			newSynonyms = incomingTerm.getSynonyms().stream().collect(Collectors.toSet());
		}
		List<String> newSynonymNames = currentSynonyms.stream().map(Synonym::getName).collect(Collectors.toList());
		
		newSynonyms.forEach(syn -> {
			if(!currentSynonymNames.contains(syn.getName())) {
				SearchResponse<Synonym> response = synonymDAO.findByField("name", syn.getName());
				Synonym synonym;
				if (response == null) {
					synonym = synonymDAO.persist(syn);
				} else {
					synonym = response.getSingleResult();
				}
				dbTerm.getSynonyms().add(synonym);
			}
		});
		
		currentSynonyms.forEach(syn -> {
			if(!newSynonymNames.contains(syn.getName())) {
				dbTerm.getSynonyms().remove(syn);
			}
		});

	}
	
	private void handleSecondaryIds(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		Set<String> currentIds;
		if(dbTerm.getSecondaryIdentifiers() == null) {
			currentIds = new HashSet<>();
			dbTerm.setSecondaryIdentifiers(new ArrayList<>());
		} else {
			currentIds = dbTerm.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
		}
		
		Set<String> newIds;
		if(incomingTerm.getSecondaryIdentifiers() == null) {
			newIds = new HashSet<>();
		} else {
			newIds = incomingTerm.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
		}
		
		newIds.forEach(id -> {
			if(!currentIds.contains(id)) {
				dbTerm.getSecondaryIdentifiers().add(id);
			}
		});
		
		currentIds.forEach(id -> {
			if(!newIds.contains(id)) {
				dbTerm.getSecondaryIdentifiers().remove(id);
			}
		});

	}

}
