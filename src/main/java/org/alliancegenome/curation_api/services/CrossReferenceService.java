package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class CrossReferenceService extends BaseEntityCrudService<CrossReference, CrossReferenceDAO> {

	@Inject
	CrossReferenceDAO crossReferenceDAO;
	@Inject
	ResourceDescriptorPageDAO resourceDescriptorPageDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(crossReferenceDAO);
	}

	public List<CrossReference> handleFmsDtoUpdate(List<CrossReferenceFmsDTO> fmsCrossReferences, List<CrossReference> existingCrossReferences) {
		Map<String, CrossReference> incomingXrefMap = new HashedMap<>();
		if (CollectionUtils.isNotEmpty(fmsCrossReferences)) {
			for (CrossReferenceFmsDTO fmsXref : fmsCrossReferences) {
				String xrefCurie = fmsXref.getCurie();
				if (CollectionUtils.isNotEmpty(fmsXref.getPages())) {
					for (String xrefPage : fmsXref.getPages()) {
						CrossReference xref = new CrossReference();
						xref.setReferencedCurie(xrefCurie);
						xref.setDisplayName(xrefCurie);
						String prefix = xrefCurie.indexOf(":") == -1 ? xrefCurie :
							xrefCurie.substring(0, xrefCurie.indexOf(":"));
						xref.setResourceDescriptorPage(resourceDescriptorPageDAO.getPageForResourceDescriptor(prefix, xrefPage));
						incomingXrefMap.put(getCrossReferenceUniqueId(xref), xref);
					}
				} else {
					CrossReference xref = new CrossReference();
					xref.setReferencedCurie(xrefCurie);
					xref.setDisplayName(xrefCurie);
					incomingXrefMap.put(getCrossReferenceUniqueId(xref), xref);
				}
			}
		}
		
		return handleUpdate(incomingXrefMap, existingCrossReferences);
	}
	
	public List<CrossReference> handleUpdate(List<CrossReference> incomingCrossReferences, List<CrossReference> existingCrossReferences) {
		Map<String, CrossReference> incomingXrefMap = new HashedMap<>();
		if (CollectionUtils.isNotEmpty(incomingCrossReferences)) {
			for (CrossReference incomingCrossReference : incomingCrossReferences) {
				incomingXrefMap.put(getCrossReferenceUniqueId(incomingCrossReference), incomingCrossReference);
			}
		}
		
		return handleUpdate(incomingXrefMap, existingCrossReferences);	
	}
	
	public CrossReference handleUpdate(CrossReference incomingCrossReference, CrossReference existingCrossReference) {
		List<CrossReference> existingCrossReferences = new ArrayList<>();
		if (existingCrossReference != null)
			existingCrossReferences.add(existingCrossReference);
		
		Map<String, CrossReference> incomingXrefMap = new HashedMap<>();
		if (incomingCrossReference != null)
			incomingXrefMap.put(getCrossReferenceUniqueId(incomingCrossReference), incomingCrossReference);
		
		List<CrossReference> updatedXrefs = handleUpdate(incomingXrefMap, existingCrossReferences);
		if (CollectionUtils.isEmpty(updatedXrefs))
			return null;
		
		return updatedXrefs.get(0);
	}
	
	@Transactional
	public List<CrossReference> handleUpdate(Map<String, CrossReference> incomingXrefMap, List<CrossReference> existingCrossReferences) {
		Map<String, CrossReference> currentXrefMap = new HashedMap<>();
		if (CollectionUtils.isNotEmpty(existingCrossReferences)) {
			for (CrossReference xref : existingCrossReferences) {
				currentXrefMap.put(getCrossReferenceUniqueId(xref), xref);
			}
		}
		
		List<CrossReference> finalXrefs = new ArrayList<>();
		incomingXrefMap.forEach((k, v) -> {
			if (currentXrefMap.containsKey(k)) {
				finalXrefs.add(updateCrossReference(currentXrefMap.get(k), v));
			} else {
				finalXrefs.add(crossReferenceDAO.persist(v));
			}
		});
		
		currentXrefMap.forEach((k,v) -> {
			if (incomingXrefMap.containsKey(k)) {
				crossReferenceDAO.remove(v.getId());
			}
		});
		
		return finalXrefs;
	}
	
	public CrossReference updateCrossReference(CrossReference oldXref, CrossReference newXref) {
		oldXref.setDisplayName(newXref.getDisplayName());
		oldXref.setCreatedBy(newXref.getCreatedBy());
		oldXref.setUpdatedBy(newXref.getUpdatedBy());
		oldXref.setDateCreated(newXref.getDateCreated());
		oldXref.setDateUpdated(newXref.getDateUpdated());
		oldXref.setInternal(newXref.getInternal());
		oldXref.setObsolete(newXref.getObsolete());
		
		return oldXref;
	}
	
	public String getCrossReferenceUniqueId(CrossReference xref) {
		if (xref == null)
			return null;
		if (xref.getResourceDescriptorPage() == null)
			return xref.getReferencedCurie();
		return StringUtils.join(
				List.of(xref.getReferencedCurie(), xref.getResourceDescriptorPage().getId()), "|");
	}
}
