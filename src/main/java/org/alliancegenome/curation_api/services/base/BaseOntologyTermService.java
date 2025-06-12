package org.alliancegenome.curation_api.services.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.dao.ontology.OntologyTermClosureDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

public abstract class BaseOntologyTermService<E extends OntologyTerm, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, BaseEntityDAO<E>> {

	@Inject
	CrossReferenceDAO crossReferenceDAO;
	@Inject
	SynonymDAO synonymDAO;
	@Inject
	OntologyTermClosureDAO ontologyTermClosureDAO;
	@Inject
	CrossReferenceService crossReferenceService;
	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;

	@Inject
	@AuthenticatedUser
	Person authenticatedPerson;

	public E findByCurieOrSecondaryId(String id) {
		return findByAlternativeFields(List.of("curie", "secondaryIdentifiers"), id);
	}

	@Transactional
	public E processUpdate(E inTerm) {

		E term = findByCurie(inTerm.getCurie());

		boolean newTerm = false;
		if (term == null) {
			term = dao.getNewInstance();
			term.setCurie(inTerm.getCurie());
			newTerm = true;
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

		if (newTerm) {
			return dao.persist(term);
		} else {
			return term;
		}
	}

	@Transactional
	public void processUpdateRelationships(Set<OntologyTermClosure> ancestors) {
		if (ancestors == null || ancestors.size() == 0) {
			return;
		}
		Set<OntologyTermClosure> newSet = new HashSet<>();
		OntologyTerm subjectTerm = findByCurie(ancestors.iterator().next().getClosureSubject().getCurie());
		if (subjectTerm != null) {
			for (OntologyTermClosure closure : ancestors) {
				closure.setClosureSubject(subjectTerm);
				OntologyTerm objectTerm = findByCurie(closure.getClosureObject().getCurie());
				if (objectTerm != null) {
					closure.setClosureObject(objectTerm);
					newSet.add(closure);
				}
			}

			Set<OntologyTermClosure> toAdd = new HashSet<>(newSet);
			toAdd.removeAll(subjectTerm.getAncestors());

			Set<OntologyTermClosure> toRemove = new HashSet<>(subjectTerm.getAncestors());
			toRemove.removeAll(newSet);

			subjectTerm.getAncestors().removeAll(toRemove);
			subjectTerm.getAncestors().addAll(toAdd);

			for (OntologyTermClosure closure : toAdd) {
				ontologyTermClosureDAO.persist(closure);
			}
			for (OntologyTermClosure closure : toRemove) {
				ontologyTermClosureDAO.remove(closure.getId());
			}
		}
	}

	@Transactional
	public E processCounts(E inTerm) {
		E term = findByCurie(inTerm.getCurie());
		int sum = 0;
		for (OntologyTermClosure closure : term.getDescendants()) {
			if (closure.getClosureTypes().equals(Set.of("is_a", "part_of"))) {
				sum++;
			}
		}
		term.setDescendantCount(sum);
		return term;
	}

	private void handleDefinitionUrls(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		Set<String> currentDefinitionUrls;
		if (dbTerm.getDefinitionUrls() == null) {
			currentDefinitionUrls = new HashSet<>();
			dbTerm.setDefinitionUrls(new ArrayList<>());
		} else {
			currentDefinitionUrls = dbTerm.getDefinitionUrls().stream().collect(Collectors.toSet());
		}

		Set<String> newDefinitionUrls;
		if (incomingTerm.getDefinitionUrls() == null) {
			newDefinitionUrls = new HashSet<>();
		} else {
			newDefinitionUrls = incomingTerm.getDefinitionUrls().stream().collect(Collectors.toSet());
		}

		newDefinitionUrls.forEach(id -> {
			if (!currentDefinitionUrls.contains(id)) {
				dbTerm.getDefinitionUrls().add(id);
			}
		});

		currentDefinitionUrls.forEach(id -> {
			if (!newDefinitionUrls.contains(id)) {
				dbTerm.getDefinitionUrls().remove(id);
			}
		});

	}

	public ObjectListResponse<E> getRootNodes() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("ancestors", null);
		SearchResponse<E> rootNodesRes = dao.findByParams(params);
		if (rootNodesRes != null) {
			return new ObjectListResponse<E>(rootNodesRes.getResults());
		} else {
			return new ObjectListResponse<E>();
		}
	}

	public ObjectListResponse<E> getChildren(String curie, Set<String> relationTypes) {
		E term = findByCurie(curie);
		if (relationTypes == null || relationTypes.size() == 0) {
			relationTypes = Set.of("is_a", "part_of");
		}
		if (term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getChildren(relationTypes));
		} else {
			return new ObjectListResponse<E>();
		}
	}

	public ObjectListResponse<E> getDescendants(String curie, Set<String> relationTypes) {
		E term = findByCurie(curie);
		if (relationTypes == null || relationTypes.size() == 0) {
			relationTypes = Set.of("is_a", "part_of");
		}
		if (term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getDescendants(relationTypes));
		} else {
			return new ObjectListResponse<E>();
		}
	}

	public ObjectListResponse<E> getParents(String curie, Set<String> relationTypes) {
		E term = findByCurie(curie);
		if (relationTypes == null || relationTypes.size() == 0) {
			relationTypes = Set.of("is_a", "part_of");
		}
		if (term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getParents(relationTypes));
		} else {
			return new ObjectListResponse<E>();
		}
	}

	public ObjectListResponse<E> getAncestors(String curie, Set<String> relationTypes) {
		E term = findByCurie(curie);
		if (relationTypes == null || relationTypes.size() == 0) {
			relationTypes = Set.of("is_a", "part_of");
		}
		if (term != null) {
			return (ObjectListResponse<E>) new ObjectListResponse<OntologyTerm>(term.getAncestors(relationTypes));
		} else {
			return new ObjectListResponse<E>();
		}
	}

	private void handleSubsets(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		Set<String> currentSubsets;
		if (dbTerm.getSubsets() == null) {
			currentSubsets = new HashSet<>();
			dbTerm.setSubsets(new ArrayList<>());
		} else {
			currentSubsets = dbTerm.getSubsets().stream().collect(Collectors.toSet());
		}

		Set<String> newSubsets;
		if (incomingTerm.getSubsets() == null) {
			newSubsets = new HashSet<>();
		} else {
			newSubsets = incomingTerm.getSubsets().stream().collect(Collectors.toSet());
		}

		newSubsets.forEach(id -> {
			if (!currentSubsets.contains(id)) {
				dbTerm.getSubsets().add(id);
			}
		});

		currentSubsets.forEach(id -> {
			if (!newSubsets.contains(id)) {
				dbTerm.getSubsets().remove(id);
			}
		});

	}

	private void handleSynonyms(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		Set<Synonym> currentSynonyms;
		if (dbTerm.getSynonyms() == null) {
			currentSynonyms = new HashSet<>();
			dbTerm.setSynonyms(new ArrayList<>());
		} else {
			currentSynonyms = new HashSet<>(dbTerm.getSynonyms());
		}
		List<String> currentSynonymNames = currentSynonyms.stream().map(Synonym::getName).toList();

		Set<Synonym> newSynonyms;
		if (incomingTerm.getSynonyms() == null) {
			newSynonyms = new HashSet<>();
		} else {
			newSynonyms = new HashSet<>(incomingTerm.getSynonyms());
		}
		List<String> newSynonymNames = newSynonyms.stream().map(Synonym::getName).toList();

		for (Synonym syn : newSynonyms) {
			if (!currentSynonymNames.contains(syn.getName())) {
				SearchResponse<Synonym> response = synonymDAO.findByField("name", syn.getName());
				Synonym synonym;
				if (response == null) {
					synonym = synonymDAO.persist(syn);
				} else {
					synonym = response.getSingleResult();
					updateSynonym(synonym, syn);
				}
				dbTerm.getSynonyms().add(synonym);
			} else {
				for (Synonym dbSynonym : dbTerm.getSynonyms()) {
					if (dbSynonym.getName().equals(syn.getName())) {
						updateSynonym(dbSynonym, syn);
						break;
					}
				}
			}
		}

		for (Synonym syn : currentSynonyms) {
			if (!newSynonymNames.contains(syn.getName())) {
				dbTerm.getSynonyms().remove(syn);
			}
		}
	}

	private void handleSecondaryIds(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		Set<String> currentIds;
		if (dbTerm.getSecondaryIdentifiers() == null) {
			currentIds = new HashSet<>();
			dbTerm.setSecondaryIdentifiers(new ArrayList<>());
		} else {
			currentIds = dbTerm.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
		}

		Set<String> newIds;
		if (incomingTerm.getSecondaryIdentifiers() == null) {
			newIds = new HashSet<>();
		} else {
			newIds = incomingTerm.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
		}

		newIds.forEach(id -> {
			if (!currentIds.contains(id)) {
				dbTerm.getSecondaryIdentifiers().add(id);
			}
		});

		currentIds.forEach(id -> {
			if (!newIds.contains(id)) {
				dbTerm.getSecondaryIdentifiers().remove(id);
			}
		});

	}

	private void handleCrossReferences(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
		List<Long> currentIds;
		if (dbTerm.getCrossReferences() == null) {
			currentIds = new ArrayList<>();
		} else {
			currentIds = dbTerm.getCrossReferences().stream().map(CrossReference::getId).collect(Collectors.toList());
		}

		List<Long> mergedIds;
		if (incomingTerm.getCrossReferences() == null) {
			mergedIds = new ArrayList<>();
			dbTerm.setCrossReferences(null);
		} else {
			List<CrossReference> mergedCrossReferences = crossReferenceService.getUpdatedXrefList(incomingTerm.getCrossReferences(), dbTerm.getCrossReferences());
			mergedIds = mergedCrossReferences.stream().map(CrossReference::getId).collect(Collectors.toList());
			for (CrossReference xref : mergedCrossReferences) {
				String prefix = xref.getReferencedCurie().substring(0, xref.getReferencedCurie().indexOf(":"));
				ResourceDescriptorPage page = resourceDescriptorPageService.getPageForResourceDescriptor(prefix, "ontology_provided_cross_reference");
				if (page == null) {
					// TODO: some how figure out how to make this less verbose by adding more
					// resource descriptors
					// Log.warn(dbTerm);
					// Log.warn("Unable to find ResourceDescriptorPage for (prefix, page): (" +
					// prefix + ", " + page + ")");
				}
				xref.setResourceDescriptorPage(page);
			}
			dbTerm.setCrossReferences(mergedCrossReferences);
		}

		for (Long currentId : currentIds) {
			if (!mergedIds.contains(currentId)) {
				crossReferenceDAO.remove(currentId);
			}
		}
	}

	public <T extends OntologyTerm> T findSubsetTerm(T childTerm, String subsetName) {
		Log.info(childTerm + " " + subsetName);
		if (childTerm.getSubsets().contains(subsetName)) {
			return childTerm;
		}
		Log.info("getAncestors: " + childTerm.getAncestors());
		for (OntologyTermClosure closure : childTerm.getAncestors()) {
			if (closure.getClosureSubject().getSubsets().contains(subsetName)) {
				return (T) closure.getClosureSubject();
			}
			if (closure.getClosureObject().getSubsets().contains(subsetName)) {
				return (T) closure.getClosureObject();
			}
		}
		return null;
	}

	private void updateSynonym(Synonym oldSyn, Synonym newSyn) {
		oldSyn.setName(newSyn.getName());
		oldSyn.setIsDisplaySynonym(newSyn.getIsDisplaySynonym());
		oldSyn.setHasBroadSynonym(newSyn.getHasBroadSynonym());
		oldSyn.setHasExactSynonym(newSyn.getHasExactSynonym());
		oldSyn.setHasNarrowSynonym(newSyn.getHasNarrowSynonym());
		oldSyn.setHasRelatedSynonym(newSyn.getHasRelatedSynonym());
	}

}
