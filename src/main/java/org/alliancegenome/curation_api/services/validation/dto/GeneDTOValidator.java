package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.slotAnnotations.SlotAnnotationIdentityHelper;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class GeneDTOValidator extends BaseDTOValidator {

	@Inject
	GeneDAO geneDAO;
	@Inject
	GeneSymbolSlotAnnotationDAO geneSymbolDAO;
	@Inject
	GeneFullNameSlotAnnotationDAO geneFullNameDAO;
	@Inject
	GeneSystematicNameSlotAnnotationDAO geneSystematicNameDAO;
	@Inject
	GeneSynonymSlotAnnotationDAO geneSynonymDAO;
	@Inject
	GeneSecondaryIdSlotAnnotationDAO geneSecondaryIdDAO;
	@Inject
	GeneSymbolSlotAnnotationDTOValidator geneSymbolDtoValidator;
	@Inject
	GeneFullNameSlotAnnotationDTOValidator geneFullNameDtoValidator;
	@Inject
	GeneSystematicNameSlotAnnotationDTOValidator geneSystematicNameDtoValidator;
	@Inject
	GeneSynonymSlotAnnotationDTOValidator geneSynonymDtoValidator;
	@Inject
	GeneSecondaryIdSlotAnnotationDTOValidator geneSecondaryIdDtoValidator;
	@Inject
	SlotAnnotationIdentityHelper identityHelper;

	@Transactional
	public Gene validateGeneDTO(GeneDTO dto) throws ObjectValidationException {

		ObjectResponse<Gene> geneResponse = new ObjectResponse<Gene>();

		Gene gene = null;
		if (StringUtils.isBlank(dto.getCurie())) {
			geneResponse.addErrorMessage("curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			gene = geneDAO.find(dto.getCurie());
		}

		if (gene == null)
			gene = new Gene();

		gene.setCurie(dto.getCurie());

		ObjectResponse<Gene> geResponse = validateGenomicEntityDTO(gene, dto);
		geneResponse.addErrorMessages(geResponse.getErrorMessages());
		gene = geResponse.getEntity();

		GeneSymbolSlotAnnotation symbol = gene.getGeneSymbol();
		if (dto.getGeneSymbolDto() == null) {
			geneResponse.addErrorMessage("gene_symbol_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<GeneSymbolSlotAnnotation> symbolResponse = geneSymbolDtoValidator.validateGeneSymbolSlotAnnotationDTO(symbol, dto.getGeneSymbolDto());
			if (symbolResponse.hasErrors()) {
				geneResponse.addErrorMessage("gene_symbol_dto", symbolResponse.errorMessagesString());
			} else {
				symbol = symbolResponse.getEntity();
			}
		}

		GeneFullNameSlotAnnotation fullName = gene.getGeneFullName();
		if (fullName != null && dto.getGeneFullNameDto() == null) {
			fullName.setSingleGene(null);
			geneFullNameDAO.remove(fullName.getId());
		}

		if (dto.getGeneFullNameDto() != null) {
			ObjectResponse<GeneFullNameSlotAnnotation> fullNameResponse = geneFullNameDtoValidator.validateGeneFullNameSlotAnnotationDTO(fullName, dto.getGeneFullNameDto());
			if (fullNameResponse.hasErrors()) {
				geneResponse.addErrorMessage("gene_full_name_dto", fullNameResponse.errorMessagesString());
			} else {
				fullName = fullNameResponse.getEntity();
			}
		} else {
			fullName = null;
		}

		GeneSystematicNameSlotAnnotation systematicName = gene.getGeneSystematicName();
		if (systematicName != null && dto.getGeneSystematicNameDto() == null) {
			systematicName.setSingleGene(null);
			geneSystematicNameDAO.remove(systematicName.getId());
		}

		if (dto.getGeneSystematicNameDto() != null) {
			ObjectResponse<GeneSystematicNameSlotAnnotation> systematicNameResponse = geneSystematicNameDtoValidator.validateGeneSystematicNameSlotAnnotationDTO(systematicName, dto.getGeneSystematicNameDto());
			if (systematicNameResponse.hasErrors()) {
				geneResponse.addErrorMessage("gene_systematic_name_dto", systematicNameResponse.errorMessagesString());
			} else {
				systematicName = systematicNameResponse.getEntity();
			}
		} else {
			systematicName = null;
		}

		Map<String, GeneSynonymSlotAnnotation> existingSynonyms = new HashMap<>();
		if (CollectionUtils.isNotEmpty(gene.getGeneSynonyms())) {
			for (GeneSynonymSlotAnnotation existingSynonym : gene.getGeneSynonyms()) {
				existingSynonyms.put(SlotAnnotationIdentityHelper.nameSlotAnnotationIdentity(existingSynonym), existingSynonym);
			}
		}
		
		List<GeneSynonymSlotAnnotation> synonyms = new ArrayList<>();
		List<Long> synonymIdsToKeep = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getGeneSynonymDtos())) {
			for (NameSlotAnnotationDTO synonymDTO : dto.getGeneSynonymDtos()) {
				ObjectResponse<GeneSynonymSlotAnnotation> synonymResponse = geneSynonymDtoValidator.validateGeneSynonymSlotAnnotationDTO(existingSynonyms.get(identityHelper.nameSlotAnnotationDtoIdentity(synonymDTO)), synonymDTO);
				if (synonymResponse.hasErrors()) {
					geneResponse.addErrorMessage("gene_synonym_dtos", synonymResponse.errorMessagesString());
				} else {
					GeneSynonymSlotAnnotation synonym = synonymResponse.getEntity();
					synonyms.add(synonym);
					if (synonym.getId() != null)
						synonymIdsToKeep.add(synonym.getId());
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(gene.getGeneSynonyms())) {
			for (GeneSynonymSlotAnnotation syn : gene.getGeneSynonyms()) {
				if (!synonymIdsToKeep.contains(syn.getId())) {
					syn.setSingleGene(null);
					geneSynonymDAO.remove(syn.getId());
				}
			}
		}

		Map<String, GeneSecondaryIdSlotAnnotation> existingSecondaryIds = new HashMap<>();
		if (CollectionUtils.isNotEmpty(gene.getGeneSecondaryIds())) {
			for (GeneSecondaryIdSlotAnnotation existingSecondaryId : gene.getGeneSecondaryIds()) {
				existingSecondaryIds.put(SlotAnnotationIdentityHelper.secondaryIdIdentity(existingSecondaryId), existingSecondaryId);
			}
		}
		
		List<GeneSecondaryIdSlotAnnotation> secondaryIds = new ArrayList<>();
		List<Long> secondaryIdIdsToKeep = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getGeneSecondaryIdDtos())) {
			for (SecondaryIdSlotAnnotationDTO secondaryIdDTO : dto.getGeneSecondaryIdDtos()) {
				ObjectResponse<GeneSecondaryIdSlotAnnotation> secondaryIdResponse = geneSecondaryIdDtoValidator.validateGeneSecondaryIdSlotAnnotationDTO(existingSecondaryIds.get(identityHelper.secondaryIdDtoIdentity(secondaryIdDTO)), secondaryIdDTO);
				if (secondaryIdResponse.hasErrors()) {
					geneResponse.addErrorMessage("gene_secondary_id_dtos", secondaryIdResponse.errorMessagesString());
				} else {
					GeneSecondaryIdSlotAnnotation secondaryId = secondaryIdResponse.getEntity();
					secondaryIds.add(secondaryId);
					if (secondaryId.getId() != null)
						secondaryIdIdsToKeep.add(secondaryId.getId());
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(gene.getGeneSecondaryIds())) {
			for (GeneSecondaryIdSlotAnnotation sid : gene.getGeneSecondaryIds()) {
				if (!secondaryIdIdsToKeep.contains(sid.getId())) {
					sid.setSingleGene(null);
					geneSecondaryIdDAO.remove(sid.getId());
				}
			}
		}

		if (geneResponse.hasErrors())
			throw new ObjectValidationException(dto, geneResponse.errorMessagesString());

		gene = geneDAO.persist(gene);

		// Attach gene and persist SlotAnnotation objects

		if (symbol != null) {
			symbol.setSingleGene(gene);
			geneSymbolDAO.persist(symbol);
		}
		gene.setGeneSymbol(symbol);

		if (fullName != null) {
			fullName.setSingleGene(gene);
			geneFullNameDAO.persist(fullName);
		}
		gene.setGeneFullName(fullName);

		if (systematicName != null) {
			systematicName.setSingleGene(gene);
			geneSystematicNameDAO.persist(systematicName);
		}
		gene.setGeneSystematicName(systematicName);

		if (CollectionUtils.isNotEmpty(synonyms)) {
			for (GeneSynonymSlotAnnotation syn : synonyms) {
				syn.setSingleGene(gene);
				geneSynonymDAO.persist(syn);
			}
		}
		gene.setGeneSynonyms(synonyms);

		if (CollectionUtils.isNotEmpty(secondaryIds)) {
			for (GeneSecondaryIdSlotAnnotation sid : secondaryIds) {
				sid.setSingleGene(gene);
				geneSecondaryIdDAO.persist(sid);
			}
		}
		gene.setGeneSecondaryIds(secondaryIds);

		return gene;
	}
}
