package org.alliancegenome.curation_api.services.helpers.interactions;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class InteractionCrossReferenceHelper {
	
	@Inject ResourceDescriptorPageService rdpService;
	
	public List<CrossReference> createAllianceXrefs(PsiMiTabDTO dto) {
		List<CrossReference> xrefs = new ArrayList<>();
		List<String> xrefStrings = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getInteractionIds()))
			xrefStrings.addAll(dto.getInteractionIds());
		if (CollectionUtils.isNotEmpty(dto.getInteractionXrefs()))
			xrefStrings.addAll(dto.getInteractionXrefs());

		if (CollectionUtils.isEmpty(xrefStrings))
			return null;
			
		for (String xrefString : xrefStrings) {
			String xrefCurie = InteractionStringHelper.getAllianceCurie(xrefString);
			if (xrefCurie != null) {
				CrossReference xref = createAllianceXref(xrefCurie);
				if (xref != null)
					xrefs.add(xref);
			}
		}
		
		if (CollectionUtils.isEmpty(xrefs))
			return null;
		
		return xrefs;
	}
	
	private CrossReference createAllianceXref(String curie) {
		String[] curieParts = curie.split(":");
		if (curieParts.length != 2)
			return null;
		ResourceDescriptorPage rdp = rdpService.getPageForResourceDescriptor(curieParts[0], "gene/interactions");
		if (rdp == null)
			return null;
		
		CrossReference xref = new CrossReference();
		xref.setDisplayName(curie);
		xref.setReferencedCurie(curie);
		xref.setResourceDescriptorPage(rdp);
		
		return xref;
	}
}
