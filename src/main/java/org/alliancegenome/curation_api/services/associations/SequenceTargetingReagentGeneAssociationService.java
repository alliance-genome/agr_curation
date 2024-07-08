package org.alliancegenome.curation_api.services.associations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.associations.SequenceTargetingReagentGeneAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleGeneAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.sequenceTargetingReagentAssociations.SequenceTargetingReagentGeneAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleGeneAssociationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentFmsDTO;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.associations.SequenceTargetingReagentGeneAssociationFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class SequenceTargetingReagentGeneAssociationService extends
		BaseAssociationDTOCrudService<SequenceTargetingReagentGeneAssociation, SequenceTargetingReagentFmsDTO, SequenceTargetingReagentGeneAssociationDAO> {
	@Inject
	SequenceTargetingReagentGeneAssociationDAO sequenceTargetingReagentGeneAssociationDAO;
	@Inject
	SequenceTargetingReagentGeneAssociationFmsDTOValidator sequenceTargetingReagentGeneAssociationFmsDTOValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(sequenceTargetingReagentGeneAssociationDAO);
	}

	@Transactional
	public SequenceTargetingReagentGeneAssociation upsert(SequenceTargetingReagentFmsDTO dto,
			BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		// TODO: fix this placeholder code
		SequenceTargetingReagentGeneAssociation sequenceTargetingReagentGeneAssociation = new SequenceTargetingReagentGeneAssociation();
		return sequenceTargetingReagentGeneAssociation;
	}

	// TODO: rename?
	@Transactional
	public List<Long> addGeneAssociations(SequenceTargetingReagentFmsDTO dto, BackendBulkDataProvider dataProvider)
			throws ObjectUpdateException {

		List<SequenceTargetingReagentGeneAssociation> associations = sequenceTargetingReagentGeneAssociationFmsDTOValidator
				.validateSQTRGeneAssociationFmsDTO(dto, dataProvider);

		for (SequenceTargetingReagentGeneAssociation association : associations) {
			if (association != null) {
				addAssociationToSQTR(association);
				addAssociationToGene(association);
			}
		}

		return associations.stream().map(SequenceTargetingReagentGeneAssociation::getId)
				.collect(Collectors.toList());
	}

	private void addAssociationToSQTR(SequenceTargetingReagentGeneAssociation association) {
		SequenceTargetingReagent sqtr = association.getSequenceTargetingReagentAssociationSubject();
		List<SequenceTargetingReagentGeneAssociation> currentAssociations = sqtr
				.getSequenceTargetingReagentGeneAssociations();
		if (currentAssociations == null) {
			currentAssociations = new ArrayList<>();
			sqtr.setSequenceTargetingReagentGeneAssociations(currentAssociations);
		}

		List<Long> currentAssociationIds = new ArrayList<>();
		for (SequenceTargetingReagentGeneAssociation sqtrga : currentAssociations) {
			currentAssociationIds.add(sqtrga.getId());
		}

		if (!currentAssociationIds.contains(association.getId())) {
			currentAssociations.add(association);
		}
	}

	private void addAssociationToGene(SequenceTargetingReagentGeneAssociation association) {
		Gene gene = association.getSequenceTargetingReagentGeneAssociationObject();
		List<SequenceTargetingReagentGeneAssociation> currentAssociations = gene
				.getSequenceTargetingReagentGeneAssociations();
		if (currentAssociations == null) {
			currentAssociations = new ArrayList<>();
			gene.setSequenceTargetingReagentGeneAssociations(currentAssociations);
		}

		List<Long> currentAssociationIds = new ArrayList<>();
		for (SequenceTargetingReagentGeneAssociation sqtrga : currentAssociations) {
			currentAssociationIds.add(sqtrga.getId());
		}

		if (!currentAssociationIds.contains(association.getId())) {
			currentAssociations.add(association);
		}

	}
}
