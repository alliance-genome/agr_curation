package org.alliancegenome.curation_api.controllers.document;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.interfaces.document.OntologyTermClosureDocumentInterface;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ontology.OntologyTermClosureService;

import jakarta.inject.Inject;

public class OntologyTermClosureDocumentController implements OntologyTermClosureDocumentInterface {

	@Inject
	OntologyTermClosureService ontologyTermClosureService;

	@Override
	public SearchResponse<Long> getAllIds(String ontologyTermType, String relationTypes) {
		Set<String> parsedRelationTypes = relationTypes == null || relationTypes.isBlank()
			? new HashSet<>()
			: Arrays.stream(relationTypes.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toCollection(HashSet::new));
		List<Long> ids = ontologyTermClosureService.getAllIds(ontologyTermType, parsedRelationTypes);
		SearchResponse<Long> response = new SearchResponse<>(ids);
		response.setTotalResults((long) ids.size());
		return response;
	}

	@Override
	public SearchResponse<OntologyTermClosure> findByIds(List<Long> ids) {
		List<OntologyTermClosure> entities = ontologyTermClosureService.findByIds(ids);
		SearchResponse<OntologyTermClosure> response = new SearchResponse<>(entities);
		response.setTotalResults((long) entities.size());
		return response;
	}
}
