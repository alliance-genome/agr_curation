package org.alliancegenome.curation_api.services.helpers.interactions;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.enums.PsiMiTabPrefixEnum;
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
		
		if (CollectionUtils.isEmpty(dto.getInteractionIds())) {
			return null;
		}
		
		for (String interactionId : dto.getInteractionIds()) {
			String displayName = PsiMiTabPrefixEnum.getAllianceIdentifier(interactionId);
			if (displayName != null) {
				if (CollectionUtils.isEmpty(dto.getInteractionXrefs())) {
					xrefs.add(createAllianceXref(displayName, displayName));
				} else {
					for (String xrefCurie : dto.getInteractionXrefs()) {
						String referencedCurie = PsiMiTabPrefixEnum.getAllianceIdentifier(xrefCurie);
						if (referencedCurie != null) {
							xrefs.add(createAllianceXref(displayName, referencedCurie));
						}
					}
				}
			}
		}

		if (CollectionUtils.isEmpty(xrefs)) {
			return null;
		}

		return xrefs;
	}

	private CrossReference createAllianceXref(String displayName, String referencedCurie) {
		String[] curieParts = referencedCurie.split(":");
		if (curieParts.length != 2) {
			return null;
		}
		ResourceDescriptorPage rdp = rdpService.getPageForResourceDescriptor(curieParts[0].toUpperCase(), "gene/interactions");
		if (rdp == null) {
			return null;
		}

		CrossReference xref = new CrossReference();
		xref.setDisplayName(displayName);
		xref.setReferencedCurie(referencedCurie);
		xref.setResourceDescriptorPage(rdp);

		return xref;
	}
}
