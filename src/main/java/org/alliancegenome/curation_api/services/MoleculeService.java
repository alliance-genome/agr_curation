package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.MoleculeDAO;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.MoleculeFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.MoleculeValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class MoleculeService extends BaseEntityCrudService<Molecule, MoleculeDAO> {

	@Inject
	MoleculeDAO moleculeDAO;
	@Inject
	SynonymDAO synonymDAO;
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

		if (StringUtils.isBlank(molecule.getId())) {
			log.debug(molecule.getName() + " has no ID - skipping");
			throw new ObjectUpdateException(molecule, molecule.getId() + " has no ID - skipping");
		}

		if (molecule.getId().startsWith("CHEBI:")) {
			log.debug("Skipping processing of " + molecule.getId());
			throw new ObjectUpdateException(molecule, "Skipping processing of " + molecule.getId());
		}

		if (StringUtils.isBlank(molecule.getName())) {
			log.debug(molecule.getId() + " has no name - skipping");
			throw new ObjectUpdateException(molecule, molecule.getId() + " has no name - skipping");
		}

		if (molecule.getCrossReferences() != null) {
			for (CrossReferenceFmsDTO xrefDTO : molecule.getCrossReferences()) {
				if (StringUtils.isBlank(xrefDTO.getId())) {
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

			String inchi = null;
			if (StringUtils.isNotBlank(molecule.getInchi()))
				inchi = molecule.getInchi();
			m.setInchi(inchi);

			String inchikey = null;
			if (StringUtils.isNotBlank(molecule.getInchikey()))
				inchikey = molecule.getInchikey();
			m.setInchiKey(inchikey);

			String iupac = null;
			if (StringUtils.isNotBlank(molecule.getIupac()))
				iupac = molecule.getIupac();
			m.setIupac(iupac);

			String formula = null;
			if (StringUtils.isNotBlank(molecule.getFormula()))
				formula = molecule.getFormula();
			m.setFormula(formula);

			String smiles = null;
			if (StringUtils.isNotBlank(molecule.getSmiles()))
				smiles = molecule.getSmiles();
			m.setSmiles(smiles);

			if (CollectionUtils.isNotEmpty(molecule.getSynonyms())) {
				List<Synonym> synonyms = new ArrayList<Synonym>();
				for (String synonymName : molecule.getSynonyms()) {
					SearchResponse<Synonym> response = synonymDAO.findByField("name", synonymName);
					Synonym synonym;
					if (response == null || response.getSingleResult() == null) {
						synonym = new Synonym();
						synonym.setName(synonymName);
						synonym = synonymDAO.persist(synonym);
					} else {
						synonym = response.getSingleResult();
					}
					synonyms.add(synonym);
				}
				m.setSynonyms(synonyms);
			} else {
				m.setSynonyms(null);
			}

			m.setNamespace("molecule");

			moleculeDAO.persist(m);

			handleCrossReferences(molecule, m);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ObjectUpdateException(molecule, e.getMessage(), e.getStackTrace());
		}
	}

	private void handleCrossReferences(MoleculeFmsDTO moleculeFmsDTO, Molecule molecule) {
		Map<String, CrossReference> currentIds;
		if (molecule.getCrossReferences() == null) {
			currentIds = new HashedMap<>();
			molecule.setCrossReferences(new ArrayList<>());
		} else {
			currentIds = molecule.getCrossReferences().stream().collect(Collectors.toMap(CrossReference::getCurie, Function.identity()));
		}
		Map<String, CrossReferenceFmsDTO> newIds;
		if (moleculeFmsDTO.getCrossReferences() == null) {
			newIds = new HashedMap<>();
		} else {
			newIds = moleculeFmsDTO.getCrossReferences().stream().collect(Collectors.toMap(CrossReferenceFmsDTO::getId, Function.identity(), (cr1, cr2) -> {
				HashSet<String> pageAreas = new HashSet<>();
				if (cr1.getPages() != null)
					pageAreas.addAll(cr1.getPages());
				if (cr2.getPages() != null)
					pageAreas.addAll(cr2.getPages());
				CrossReferenceFmsDTO newCr = new CrossReferenceFmsDTO();
				newCr.setId(cr2.getId());
				newCr.setPages(new ArrayList<>(pageAreas));
				return newCr;
			}));
		}

		newIds.forEach((k, v) -> {
			if (!currentIds.containsKey(k)) {
				molecule.getCrossReferences().add(crossReferenceService.processUpdate(v));
			}
		});

		currentIds.forEach((k, v) -> {
			if (!newIds.containsKey(k)) {
				molecule.getCrossReferences().remove(v);
			}
		});

	}

}
