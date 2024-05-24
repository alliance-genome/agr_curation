package org.alliancegenome.curation_api.services;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.GeneExpressionAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;
import org.alliancegenome.curation_api.services.ontology.MmoTermService;
import org.alliancegenome.curation_api.services.ontology.OntologyTermService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestScoped
public class GeneExpressionAnnotationService extends BaseAnnotationCrudService<GeneExpressionAnnotation, GeneExpressionAnnotationDAO> implements BaseUpsertServiceInterface <GeneExpressionAnnotation, GeneExpressionFmsDTO>{

	@Inject	GeneExpressionAnnotationDAO geneExpressionAnnotationDAO;

	@Inject GeneService geneService;

	@Inject MmoTermService mmoTermService;

	@Inject OntologyTermService ontologyTermService;

	@Inject	ReferenceService referenceService;

	@Override
	protected void init() {
		setSQLDao(geneExpressionAnnotationDAO);
	}

	@Override
	public GeneExpressionAnnotation deprecateOrDeleteAnnotationAndNotes(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		return null;
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);

		if(StringUtils.equals(dataProvider.sourceOrganization, "RGD") || StringUtils.equals(dataProvider.sourceOrganization, "XB"))
			params.put(EntityFieldConstants.PA_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);

		List<Long> annotationIds = geneExpressionAnnotationDAO.findFilteredIds(params);

		return annotationIds;
	}

	@Override
	public GeneExpressionAnnotation upsert(GeneExpressionFmsDTO geneExpressionFmsDTO) throws ObjectUpdateException {
		return null;
	}

	@Override
	public GeneExpressionAnnotation upsert(GeneExpressionFmsDTO geneExpressionFmsDTO, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		if (isExpressionAnnotationValid(geneExpressionFmsDTO)) {
			GeneExpressionAnnotation geneExpressionAnnotation = new GeneExpressionAnnotation();
			String uniqueId = generateUniqueId(geneExpressionFmsDTO);
			geneExpressionAnnotation.setUniqueId(generateUniqueId(geneExpressionFmsDTO));
			Gene expressionAnnotationSubject = geneService.findByIdentifierString(geneExpressionFmsDTO.getGeneId());
			geneExpressionAnnotation.setExpressionAnnotationSubject(expressionAnnotationSubject);
			Reference singleReference = referenceService.retrieveFromDbOrLiteratureService(geneExpressionFmsDTO.getEvidence().getPublicationId());
			geneExpressionAnnotation.setSingleReference(singleReference);
			MMOTerm expression_assay_used = mmoTermService.findByCurie(geneExpressionFmsDTO.getAssay());
			geneExpressionAnnotation.setExpressionAssayUsed(expression_assay_used);
			geneExpressionAnnotationDAO.persist(geneExpressionAnnotation);
			return geneExpressionAnnotation;
		}
		return null;
	}

	public boolean isExpressionAnnotationValid(GeneExpressionFmsDTO geneExpressionFmsDTO) throws ObjectUpdateException {
		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getGeneId())) {
			throw new ObjectValidationException(geneExpressionFmsDTO, "geneId - " + ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getGeneId() + ")");
		}
		if (geneService.findByIdentifierString(geneExpressionFmsDTO.getGeneId()) == null) {
			throw new ObjectValidationException(geneExpressionFmsDTO, "geneId - " + ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getGeneId() + ")");
		}
		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getDateAssigned())) {
			throw new ObjectValidationException(geneExpressionFmsDTO, "dateAssigned - " + ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getDateAssigned() + ")");
		}
		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getAssay())) {
			throw new ObjectValidationException(geneExpressionFmsDTO, "assay - " + ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getAssay() + ")");
		}
		if (mmoTermService.findByCurie(geneExpressionFmsDTO.getAssay()) == null) {
			throw new ObjectValidationException(geneExpressionFmsDTO, "assay - " + ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getAssay() + ")");
		}
		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getEvidence())) {
			throw new ObjectValidationException(geneExpressionFmsDTO, "evidence - " + ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence() + ")");
		}
		if (ObjectUtils.isEmpty(geneExpressionFmsDTO.getEvidence().getCrossReference())) {
			throw new ObjectValidationException(geneExpressionFmsDTO, "evidence - crossreference" + ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getCrossReference() + ")");
		}
		if (StringUtils.isEmpty(geneExpressionFmsDTO.getEvidence().getPublicationId()) || StringUtils.isBlank(geneExpressionFmsDTO.getEvidence().getPublicationId())) {
			throw new ObjectValidationException(geneExpressionFmsDTO, "evidence - publicationId" + ValidationConstants.REQUIRED_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getPublicationId() + ")");
		}
		if (referenceService.retrieveFromDbOrLiteratureService(geneExpressionFmsDTO.getEvidence().getPublicationId()) == null) {
			throw new ObjectValidationException(geneExpressionFmsDTO, "evidence - publicationId" + ValidationConstants.INVALID_MESSAGE + " (" + geneExpressionFmsDTO.getEvidence().getPublicationId() + ")");
		}
		return true;
	}

	//	UniqueID  = assayId | evidenceCrosseferenceId | geneId | stageTermId | stageName | whereExpressedStatement | anatomicalStructureTermId | cellularComponentTermId
	private String generateUniqueId(GeneExpressionFmsDTO geneExpressionFmsDTO) {
		StringBuffer buffer = new StringBuffer(convertNull(geneExpressionFmsDTO.getAssay()));
		buffer.append("|");
		buffer.append(convertNull(geneExpressionFmsDTO.getGeneId()));
		buffer.append("|");
		buffer.append(convertNull(geneExpressionFmsDTO.getEvidence().getCrossReference().getId()));
		buffer.append("|");
		buffer.append(convertNull(geneExpressionFmsDTO.getWhenExpressed().getStageTermId()));
		buffer.append("|");
		buffer.append(convertNull(geneExpressionFmsDTO.getWhenExpressed().getStageName()));
		buffer.append("|");
		buffer.append(convertNull(geneExpressionFmsDTO.getWhereExpressed().getWhereExpressedStatement()));
		buffer.append("|");
		buffer.append(convertNull(geneExpressionFmsDTO.getWhereExpressed().getAnatomicalStructureTermId()));
		buffer.append("|");
		buffer.append(convertNull(geneExpressionFmsDTO.getWhereExpressed().getCellularComponentTermId()));
		buffer.append("|");
		return buffer.toString();
	}

	private String convertNull(String input) {
		return input == null ? "na" : input;
	}
}
