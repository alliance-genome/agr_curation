package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.model.ingest.dto.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationDTOValidator;
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
	GeneSymbolSlotAnnotationDTOValidator geneSymbolDtoValidator;
	@Inject
	GeneFullNameSlotAnnotationDTOValidator geneFullNameDtoValidator;
	@Inject
	GeneSystematicNameSlotAnnotationDTOValidator geneSystematicNameDtoValidator;
	@Inject
	GeneSynonymSlotAnnotationDTOValidator geneSynonymDtoValidator;

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

		if (gene.getGeneSymbol() != null) {
			GeneSymbolSlotAnnotation symbol = gene.getGeneSymbol();
			symbol.setSingleGene(null);
			geneSymbolDAO.remove(symbol.getId());
		}

		GeneSymbolSlotAnnotation symbol = null;
		if (dto.getGeneSymbolDto() == null) {
			geneResponse.addErrorMessage("gene_symbol_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<GeneSymbolSlotAnnotation> symbolResponse = geneSymbolDtoValidator.validateGeneSymbolSlotAnnotationDTO(dto.getGeneSymbolDto());
			if (symbolResponse.hasErrors()) {
				geneResponse.addErrorMessage("gene_symbol_dto", symbolResponse.errorMessagesString());
			} else {
				symbol = symbolResponse.getEntity();
			}
		}

		if (gene.getGeneFullName() != null) {
			GeneFullNameSlotAnnotation fullName = gene.getGeneFullName();
			fullName.setSingleGene(null);
			geneFullNameDAO.remove(fullName.getId());
		}

		GeneFullNameSlotAnnotation fullName = null;
		if (dto.getGeneFullNameDto() != null) {
			ObjectResponse<GeneFullNameSlotAnnotation> fullNameResponse = geneFullNameDtoValidator.validateGeneFullNameSlotAnnotationDTO(dto.getGeneFullNameDto());
			if (fullNameResponse.hasErrors()) {
				geneResponse.addErrorMessage("gene_full_name_dto", fullNameResponse.errorMessagesString());
			} else {
				fullName = fullNameResponse.getEntity();
			}
		}

		if (gene.getGeneSystematicName() != null) {
			GeneSystematicNameSlotAnnotation systematicName = gene.getGeneSystematicName();
			systematicName.setSingleGene(null);
			geneSystematicNameDAO.remove(systematicName.getId());
		}

		GeneSystematicNameSlotAnnotation systematicName = null;
		if (dto.getGeneSystematicNameDto() != null) {
			ObjectResponse<GeneSystematicNameSlotAnnotation> systematicNameResponse = geneSystematicNameDtoValidator.validateGeneSystematicNameSlotAnnotationDTO(dto.getGeneSystematicNameDto());
			if (systematicNameResponse.hasErrors()) {
				geneResponse.addErrorMessage("gene_full_name_dto", systematicNameResponse.errorMessagesString());
			} else {
				systematicName = systematicNameResponse.getEntity();
			}
		}

		if (CollectionUtils.isNotEmpty(gene.getGeneSynonyms())) {
			gene.getGeneSynonyms().forEach(gs -> {
				gs.setSingleGene(null);
				geneSynonymDAO.remove(gs.getId());
			});
		}

		List<GeneSynonymSlotAnnotation> synonyms = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getGeneSynonymDtos())) {
			for (NameSlotAnnotationDTO synonymDTO : dto.getGeneSynonymDtos()) {
				ObjectResponse<GeneSynonymSlotAnnotation> synonymResponse = geneSynonymDtoValidator.validateGeneSynonymSlotAnnotationDTO(synonymDTO);
				if (synonymResponse.hasErrors()) {
					geneResponse.addErrorMessage("gene_synonym_dtos", synonymResponse.errorMessagesString());
				} else {
					GeneSynonymSlotAnnotation synonym = synonymResponse.getEntity();
					synonyms.add(synonym);
				}
			}
		}

		if (geneResponse.hasErrors()) {
			throw new ObjectValidationException(dto, geneResponse.errorMessagesString());
		}

		gene = geneDAO.persist(gene);

		// Attach gene and persist SlotAnnotation objects

		if (symbol != null) {
			symbol.setSingleGene(gene);
			geneSymbolDAO.persist(symbol);
		}

		if (fullName != null) {
			fullName.setSingleGene(gene);
			geneFullNameDAO.persist(fullName);
		}

		if (systematicName != null) {
			systematicName.setSingleGene(gene);
			geneSystematicNameDAO.persist(systematicName);
		}

		if (CollectionUtils.isNotEmpty(synonyms)) {
			for (GeneSynonymSlotAnnotation syn : synonyms) {
				syn.setSingleGene(gene);
				geneSynonymDAO.persist(syn);
			}
		}

		return gene;
	}
}
