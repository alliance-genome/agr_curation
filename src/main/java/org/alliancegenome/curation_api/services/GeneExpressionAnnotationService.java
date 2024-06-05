package org.alliancegenome.curation_api.services;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ExpressionAnnotationDAO;
import org.alliancegenome.curation_api.dao.GeneExpressionAnnotationDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.GeneExpressionFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;
import org.alliancegenome.curation_api.services.helpers.annotations.GeneExpressionAnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.services.ontology.MmoTermService;
import org.alliancegenome.curation_api.services.validation.dto.fms.GeneExpressionAnnotationFmsDTOValidator;
import org.apache.commons.lang.StringUtils;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestScoped
@JBossLog
public class GeneExpressionAnnotationService extends BaseAnnotationCrudService<GeneExpressionAnnotation, GeneExpressionAnnotationDAO> implements BaseUpsertServiceInterface<GeneExpressionAnnotation, GeneExpressionFmsDTO> {

	@Inject	ExpressionAnnotationDAO expressionAnnotationDAO;

	@Inject	GeneExpressionAnnotationDAO geneExpressionAnnotationDAO;

	@Inject GeneService geneService;

	@Inject MmoTermService mmoTermService;

	@Inject	ReferenceService referenceService;

	@Inject GeneExpressionAnnotationUniqueIdHelper geneExpressionAnnotationUniqueIdHelper;

	@Inject	GeneExpressionAnnotationFmsDTOValidator geneExpressionAnnotationFmsDTOValidator;

	@Inject DataProviderService dataProviderService;

	@Inject VocabularyTermService vocabularyTermService;

	@Inject	PersonDAO personDAO;

	@Inject PersonService personService;

	@Override
	protected void init() {
		setSQLDao(geneExpressionAnnotationDAO);
	}

	@Override
	public GeneExpressionAnnotation deprecateOrDeleteAnnotationAndNotes(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		GeneExpressionAnnotation geneExpressionAnnotation = geneExpressionAnnotationDAO.find(id);

		if (geneExpressionAnnotation == null) {
			String errorMessage = "Could not find Gene Expression Annotation with id: " + id;
			if (throwApiError) {
				ObjectResponse<GeneExpressionAnnotation> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			log.error(errorMessage);
			return null;
		}
		if (deprecate) {
			if (!geneExpressionAnnotation.getObsolete()) {
				geneExpressionAnnotation.setObsolete(true);
				if (authenticatedPerson.getId() != null) {
					geneExpressionAnnotation.setUpdatedBy(personDAO.find(authenticatedPerson.getId()));
				} else {
					geneExpressionAnnotation.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
				}
				geneExpressionAnnotation.setDateUpdated(OffsetDateTime.now());
				return geneExpressionAnnotationDAO.persist(geneExpressionAnnotation);
			} else {
				return geneExpressionAnnotation;
			}
		} else {
			geneExpressionAnnotationDAO.remove(id);
		}
		return null;
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD") || StringUtils.equals(dataProvider.sourceOrganization, "XB")) {
			params.put(EntityFieldConstants.PA_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> annotationIds = geneExpressionAnnotationDAO.findIdsByParams(params);
		return annotationIds;
	}

	@Transactional
	@Override
	public GeneExpressionAnnotation upsert(GeneExpressionFmsDTO geneExpressionFmsDTO, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		if (geneExpressionAnnotationFmsDTOValidator.isExpressionAnnotationValid(geneExpressionFmsDTO)) {
			GeneExpressionAnnotation geneExpressionAnnotation = new GeneExpressionAnnotation();
			String uniqueId = geneExpressionAnnotationUniqueIdHelper.generateUniqueId(geneExpressionFmsDTO);
			SearchResponse<GeneExpressionAnnotation> annotationDB = geneExpressionAnnotationDAO.findByField("uniqueId", uniqueId);
			if (annotationDB != null && annotationDB.getSingleResult() != null) {
				geneExpressionAnnotation = annotationDB.getSingleResult();
			}
			geneExpressionAnnotation.setUniqueId(uniqueId);
			Gene expressionAnnotationSubject = geneService.findByIdentifierString(geneExpressionFmsDTO.getGeneId());
			geneExpressionAnnotation.setExpressionAnnotationSubject(expressionAnnotationSubject);
			Reference singleReference = referenceService.retrieveFromDbOrLiteratureService(geneExpressionFmsDTO.getEvidence().getPublicationId());
			geneExpressionAnnotation.setSingleReference(singleReference);
			MMOTerm expressionAssayUsed = mmoTermService.findByCurie(geneExpressionFmsDTO.getAssay());
			geneExpressionAnnotation.setExpressionAssayUsed(expressionAssayUsed);
			geneExpressionAnnotation.setWhereExpressedStatement(geneExpressionFmsDTO.getWhereExpressed().getWhereExpressedStatement());
			geneExpressionAnnotation.setWhenExpressedStageName(geneExpressionFmsDTO.getWhenExpressed().getStageName());
			geneExpressionAnnotation.setDataProvider(dataProviderService.createOrganizationDataProvider(dataProvider.sourceOrganization));
			geneExpressionAnnotation.setRelation(vocabularyTermService.getTermInVocabulary(VocabularyConstants.GENE_EXPRESSION_VOCABULARY, VocabularyConstants.GENE_EXPRESSION_RELATION_TERM).getEntity());
			geneExpressionAnnotation.setObsolete(false);
			geneExpressionAnnotation.setInternal(false);
			OffsetDateTime creationDate = OffsetDateTime.parse(geneExpressionFmsDTO.getDateAssigned());
			geneExpressionAnnotation.setDateCreated(creationDate);
			return geneExpressionAnnotationDAO.persist(geneExpressionAnnotation);
		}
		return null;
	}
}
