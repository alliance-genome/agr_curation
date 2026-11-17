package org.alliancegenome.curation_api.services.helpers;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class VariantUniqueIdHelper {

	public static String getVariantUniqueId(Variant variant) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		if (variant.getTaxon() != null) {
			uniqueId.add(variant.getTaxon().getCurie());
		}
		if (variant.getVariantType() != null) {
			uniqueId.add(variant.getVariantType().getCurie());
		}
		if (variant.getDataProvider() != null) {
			uniqueId.add(variant.getDataProvider().getAbbreviation());
		}
		if (CollectionUtils.isNotEmpty(variant.getCuratedVariantGenomicLocations())) {
			List<String> locationIds = variant.getCuratedVariantGenomicLocations().stream()
				.filter(location -> !Boolean.TRUE.equals(location.getObsolete()))
				.map(VariantUniqueIdHelper::getLocationUniqueId)
				.sorted()
				.toList();
			for (String locationId : locationIds) {
				uniqueId.add(locationId);
			}
		}
		return uniqueId.getUniqueId();
	}

	private static String getLocationUniqueId(CuratedVariantGenomicLocationAssociation location) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		AssemblyComponent assemblyComponent = location.getVariantGenomicLocationAssociationObject();
		if (assemblyComponent != null) {
			uniqueId.add(StringUtils.isNotBlank(assemblyComponent.getPrimaryExternalId()) ? assemblyComponent.getPrimaryExternalId() : assemblyComponent.getName());
			if (assemblyComponent.getGenomeAssembly() != null) {
				uniqueId.add(assemblyComponent.getGenomeAssembly().getPrimaryExternalId());
			}
		}
		uniqueId.add(location.getStart());
		uniqueId.add(location.getEnd());
		uniqueId.add(location.getVariantSequence());
		return uniqueId.getUniqueId();
	}

}
