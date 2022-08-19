package org.alliancegenome.curation_api.services;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.MoleculeDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.dto.fms.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.helpers.validators.MoleculeValidator;
import org.apache.commons.collections4.map.HashedMap;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class MoleculeService extends BaseEntityCrudService<Molecule, MoleculeDAO> {

	@Inject
	MoleculeDAO moleculeDAO;
	@Inject
	MoleculeValidator moleculeValidator;
	@Inject
	CrossReferenceService crossReferenceService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(moleculeDAO);
	}
	
	@Transactional
	public Molecule getByIdOrCurie(String id) {
		Molecule molecule = moleculeDAO.getByIdOrCurie(id);
		if (molecule != null) {
			molecule.getSynonyms().size();
		}
		return molecule;
	}
	
	@Override
	@Transactional
	public ObjectResponse<Molecule> update(Molecule uiEntity) {
		Molecule dbEntity = moleculeValidator.validateMolecule(uiEntity);
		return new ObjectResponse<>(moleculeDAO.persist(dbEntity));
	}
	
	@Transactional
	public void processUpdate(MoleculeFmsDTO molecule) throws ObjectUpdateException {
		log.debug("processUpdate Molecule: ");
	
		if (molecule.getId() == null) {
			log.debug(molecule.getId() + " has no ID - skipping");
			throw new ObjectUpdateException(molecule, molecule.getId() + " has no ID - skipping");
		}
		
		if (molecule.getId().startsWith("CHEBI:")) {
			log.debug("Skipping processing of " + molecule.getId());
			throw new ObjectUpdateException(molecule, "Skipping processing of " + molecule.getId());
		}
		
		if (molecule.getName() == null || molecule.getName().length() == 0) {
			log.debug(molecule.getId() + " has no name - skipping");
			throw new ObjectUpdateException(molecule, molecule.getId() + " has no name - skipping");
		}
		
		if (molecule.getCrossReferences() != null) {
			for (CrossReferenceFmsDTO xrefDTO : molecule.getCrossReferences()) {
				if (xrefDTO.getId() == null) {
					log.debug("Missing xref ID for molecule " + molecule.getId() + " - skipping");
					throw new ObjectUpdateException(molecule, "Missing xref ID for molecule " + molecule.getId() + " - skipping");
				}
			}
		}
		
		try {
			Molecule m = moleculeDAO.find(molecule.getId());
			
			if (m == null) {
				m = new Molecule();
				m.setCurie(molecule.getId());
			}
			
			m.setName(molecule.getName());
			m.setInchi(molecule.getInchi());
			m.setInchiKey(molecule.getInchikey());
			m.setIupac(molecule.getIupac());
			m.setFormula(molecule.getFormula());
			m.setSmiles(molecule.getSmiles());
			m.setSynonyms(molecule.getSynonyms());
			m.setNamespace("molecule");
				
			moleculeDAO.persist(m); 
		
			handleCrossReferences(molecule, m);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ObjectUpdateException(molecule, e.getMessage());
		}

	}
	
	
	private void handleCrossReferences(MoleculeFmsDTO moleculeFmsDTO, Molecule molecule) {
		Map<String, CrossReference> currentIds;
		if(molecule.getCrossReferences() == null) {
			currentIds = new HashedMap<>();
			molecule.setCrossReferences(new ArrayList<>());
		} else {
			currentIds = molecule.getCrossReferences().stream().collect(Collectors.toMap(CrossReference::getCurie, Function.identity()));
		}
		Map<String, CrossReferenceFmsDTO> newIds;
		if(moleculeFmsDTO.getCrossReferences() == null) {
			newIds = new HashedMap<>();
		} else {
			newIds = moleculeFmsDTO.getCrossReferences().stream().collect(Collectors.toMap(CrossReferenceFmsDTO::getId, Function.identity(),
					(cr1, cr2) -> {
						HashSet<String> pageAreas = new HashSet<>();
						if(cr1.getPages() != null) pageAreas.addAll(cr1.getPages());
						if(cr2.getPages() != null) pageAreas.addAll(cr2.getPages());
						CrossReferenceFmsDTO newCr = new CrossReferenceFmsDTO();
						newCr.setId(cr2.getId());
						newCr.setPages(new ArrayList<>(pageAreas));
						return newCr;
					}
			));
		}
		
		newIds.forEach((k, v) -> {
			if(!currentIds.containsKey(k)) {
				molecule.getCrossReferences().add(crossReferenceService.processUpdate(v));
			}
		});
		
		currentIds.forEach((k, v) -> {
			if(!newIds.containsKey(k)) {
				molecule.getCrossReferences().remove(v);
			}
		});

	}

}
