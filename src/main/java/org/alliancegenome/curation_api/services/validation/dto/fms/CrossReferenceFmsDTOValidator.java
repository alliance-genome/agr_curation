package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CrossReferenceFmsDTOValidator {

    @Inject ResourceDescriptorPageService resourceDescriptorPageService;

    public ObjectListResponse<CrossReference> validateCrossReferenceFmsDTO(CrossReferenceFmsDTO dto) throws ObjectValidationException{
        List<CrossReference> xrefs = new ArrayList<>();
        ObjectListResponse<CrossReference> crResponse = new ObjectListResponse<>();
        if (CollectionUtils.isNotEmpty(dto.getPages())) {
            String prefix = getPrefix(dto.getId());
            for(String xrefPage : dto.getPages()) {
                CrossReference xref = new CrossReference();
                ResourceDescriptorPage rdp = resourceDescriptorPageService.getPageForResourceDescriptor(prefix, xrefPage);
                if (rdp == null) {
                    crResponse.addErrorMessage("page", ValidationConstants.INVALID_MESSAGE + " (" + xrefPage + ")");
                }
                else {
                    xref.setReferencedCurie(dto.getId());
                    xref.setResourceDescriptorPage(rdp);
                }
                xrefs.add(xref);
            }
            crResponse.setEntities(xrefs);
        } else {
            crResponse.addErrorMessage("page", ValidationConstants.REQUIRED_MESSAGE);
        }
        return crResponse;
    }

    private String getPrefix(String id){
        return id.indexOf(":") == -1 ? id : id.substring(0, id.indexOf(":"));
    }
    
}
