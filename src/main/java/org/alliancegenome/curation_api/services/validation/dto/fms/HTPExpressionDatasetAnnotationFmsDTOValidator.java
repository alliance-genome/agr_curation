package org.alliancegenome.curation_api.services.validation.dto.fms;

import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPExpressionDatasetAnnotationFmsDTO;

import com.arjuna.ats.jta.exceptions.NotImplementedException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;

@RequestScoped
public class HTPExpressionDatasetAnnotationFmsDTOValidator {

	@Transactional
	public HTPExpressionDatasetAnnotation validateHTPExpressionDatasetAnnotationFmsDTO(HTPExpressionDatasetAnnotationFmsDTO dto) throws ObjectValidationException, NotImplementedException {
		throw new NotImplementedException();
	}
}
