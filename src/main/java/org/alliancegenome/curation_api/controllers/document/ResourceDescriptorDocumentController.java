package org.alliancegenome.curation_api.controllers.document;

import org.alliancegenome.curation_api.dao.ResourceDescriptorDAO;
import org.alliancegenome.curation_api.interfaces.document.ResourceDescriptorDocumentInterface;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.response.SearchResponse;

import jakarta.inject.Inject;

public class ResourceDescriptorDocumentController implements ResourceDescriptorDocumentInterface {

	@Inject ResourceDescriptorDAO resourceDescriptorDAO;

	@Override
	public SearchResponse<ResourceDescriptor> findAll() {
		return resourceDescriptorDAO.findAllWithPages();
	}
}
