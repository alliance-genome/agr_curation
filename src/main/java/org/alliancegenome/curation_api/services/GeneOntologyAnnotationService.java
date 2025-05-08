package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.GeneOntologyAnnotationDAO;
import org.alliancegenome.curation_api.dao.SpeciesDAO;
import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneOntologyAnnotation;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneOntologyAnnotationService extends BaseEntityCrudService<GeneOntologyAnnotation, GeneOntologyAnnotationDAO> {

	@Inject
	@AuthenticatedUser
	protected Person authenticatedPerson;
	@Inject
	GeneOntologyAnnotationDAO gafDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	SpeciesDAO speciesDAO;
	@Inject
	GoTermDAO goTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(gafDAO);
	}

	@Transactional
	public ObjectListResponse<GeneOntologyAnnotation> insert(String geneId, Set<String> entities) {
		
		ObjectListResponse<GeneOntologyAnnotation> ret = new ObjectListResponse<>(new HashSet<>());
		
		Map<String, Object> geneParams = new HashMap<String, Object>();
		geneParams.put("primaryExternalId", geneId);
		Gene gene = geneDAO.findByParams(geneParams).getSingleResult();
		
		if(gene == null) {
			for(String goTermId: entities) {
				ret.addErrorMessage(geneId + "_" + goTermId, "No Gene: " + geneId + " Found for: " + goTermId);
			}
			//System.out.println("Gene Not Found: " + geneId);
			return ret;
		}

		Set<GeneOntologyAnnotation> currentAnnotations;
		if (gene.getGeneOntologyAnnotations() == null) {
			currentAnnotations = new HashSet<>();
			gene.setGeneOntologyAnnotations(new ArrayList<>());
		} else {
			currentAnnotations = new HashSet<>(gene.getGeneOntologyAnnotations());
		}
		List<String> currentAnnotationGoTermCuries = currentAnnotations.stream().map(goa -> goa.getGoTerm().getCurie()).toList();
		
		//System.out.println("Current Curies: " + currentAnnotationGoTermCuries);
		
		//System.out.println("New Curies: " + entities);
		
		for(String goTermId: entities) {
			if(!currentAnnotationGoTermCuries.contains(goTermId)) {
				//System.out.println("Term not found adding: " + goTermId + " to " + geneId);
				Map<String, Object> goParams = new HashMap<String, Object>();
				goParams.put("curie", goTermId);
				GOTerm goTerm = goTermDAO.findByParams(goParams).getSingleResult();
				if(goTerm != null) {
					GeneOntologyAnnotation newAnnotation = new GeneOntologyAnnotation();
					newAnnotation.setSingleGene(gene);
					newAnnotation.setGoTerm(goTerm);
					gene.getGeneOntologyAnnotations().add(gafDAO.persist(newAnnotation));
					ret.getEntities().add(newAnnotation);
				} else {
					ret.addErrorMessage(goTermId, "Go Term not found: " + goTermId);
				}
			}
		}
		
		for(GeneOntologyAnnotation annotation: currentAnnotations) {
			if(!entities.contains(annotation.getGoTerm().getCurie())) {
				gene.getGeneOntologyAnnotations().remove(annotation);
			} else {
				ret.getEntities().add(annotation);
			}
		}
		
		return ret;
	}

	@Override
	@Transactional
	public GeneOntologyAnnotation deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean deprecate) {
		return gafDAO.remove(id);
	}
	
	public List<Long> getAllGafIdsPerProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put("singleGene.taxon.species.dataProvider.abbreviation", dataProvider);
		return gafDAO.findIdsByParams(params);
	}

}
