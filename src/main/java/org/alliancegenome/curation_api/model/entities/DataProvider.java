package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "DataProvider", description = "POJO that represents the data provider")
@AGRCurationSchemaVersion(min = "1.6.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(indexes = {
		@Index(name = "dataprovider_createdby_index", columnList = "createdBy_id"),
		@Index(name = "dataprovider_updatedby_index", columnList = "updatedBy_id"),
		@Index(name = "dataprovider_crossreference_index", columnList = "crossreference_id"),
		@Index(name = "dataprovider_sourceorganization_index", columnList = "sourceorganization_id")
})
public class DataProvider extends AuditedObject {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private Organization sourceOrganization;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private CrossReference crossReference;

	public String getSourceUrl() {
		List<String> urlExpceptionHandler = List.of("MGI", "SGD", "OMIM");
		if (crossReference != null) {
			String urlTemplate = crossReference.getResourceDescriptorPage().getUrlTemplate();
			if (urlExpceptionHandler.contains(sourceOrganization.getAbbreviation())) {
				// remove the prefix in the template as the prefix is already in the curie.
				urlTemplate = urlTemplate.replace(sourceOrganization.getAbbreviation() + ":", "");
			}
			return urlTemplate.replace("[%s]", crossReference.getReferencedCurie());
		}
		return null;
	}
}
