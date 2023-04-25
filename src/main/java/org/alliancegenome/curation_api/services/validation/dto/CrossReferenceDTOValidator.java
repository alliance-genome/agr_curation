package org.alliancegenome.curation_api.services.validation.dto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ResourceDescriptorDAO;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.ingest.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class CrossReferenceDTOValidator extends BaseDTOValidator {

	@Inject
	ResourceDescriptorDAO resourceDescriptorDAO;
	@Inject
	ResourceDescriptorPageDAO resourceDescriptorPageDAO;
	
	public ObjectResponse<CrossReference> validateCrossReferenceDTO(CrossReferenceDTO dto) {
		CrossReference xref = new CrossReference();
		
		ObjectResponse<CrossReference> crResponse = validateAuditedObjectDTO(xref, dto);

		xref = crResponse.getEntity();

		if (StringUtils.isBlank(dto.getPrefix())) {
			crResponse.addErrorMessage("prefix", ValidationConstants.REQUIRED_MESSAGE); 
		} else {
			SearchResponse<ResourceDescriptor> rdResponse = resourceDescriptorDAO.findByField("prefix", dto.getPrefix());
			if (rdResponse == null || rdResponse.getSingleResult() == null)
				crResponse.addErrorMessage("prefix", ValidationConstants.INVALID_MESSAGE + " (" + dto.getPrefix() + ")");
		}
			
		if (StringUtils.isBlank(dto.getReferencedCurie()))
			crResponse.addErrorMessage("reference_curie", ValidationConstants.REQUIRED_MESSAGE); 
		xref.setReferencedCurie(dto.getReferencedCurie());
		
		if (StringUtils.isBlank(dto.getDisplayName()))
			crResponse.addErrorMessage("display_name", ValidationConstants.REQUIRED_MESSAGE); 
		xref.setDisplayName(dto.getDisplayName());
		
		if (StringUtils.isBlank(dto.getPageArea())) {
			crResponse.addErrorMessage("page_area", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ResourceDescriptorPage page = resourceDescriptorPageDAO.getPageForResourceDescriptor(dto.getPrefix(), dto.getPageArea());
			if (page == null)
				crResponse.addErrorMessage("page_area", ValidationConstants.INVALID_MESSAGE + " (" + dto.getPageArea() + ")");
			xref.setResourceDescriptorPage(page);
		}
		
		crResponse.setEntity(xref);

		return crResponse;
	}

}
