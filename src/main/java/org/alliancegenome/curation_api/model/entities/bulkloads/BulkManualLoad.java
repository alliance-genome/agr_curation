package org.alliancegenome.curation_api.model.entities.bulkloads;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.2.4", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { BulkLoad.class })
@Schema(name = "BulkManualLoad", description = "BulkManualLoad: a bulk manual load")
@JsonTypeName
@Table(indexes = {
	@Index(name = "bulkmanualload_species_index", columnList = "species_id")
})
public class BulkManualLoad extends BulkLoad {

	@JsonView({ CurationView.FieldsOnly.class })
	@ManyToOne
	private Species species;
}
