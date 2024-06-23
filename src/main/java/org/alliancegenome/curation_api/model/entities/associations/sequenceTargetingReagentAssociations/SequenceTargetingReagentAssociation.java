package org.alliancegenome.curation_api.model.entities.associations.sequenceTargetingReagentAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

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
@AGRCurationSchemaVersion(min = "2.3.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { EvidenceAssociation.class })
@Schema(name = "AlleleGenomicEntityAssociation", description = "Base class for SQTR associations")
@Table(
	indexes = {
			@Index(name = "sequencetargetingreagentassociation_subject_index", columnList = "sequencetargetingreagentassociationsubject_id"),

	}
)
public class SequenceTargetingReagentAssociation extends EvidenceAssociation {
	@IndexedEmbedded(includePaths = {"name", "synonyms", "secondaryIdentifiers"})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@Fetch(FetchMode.JOIN)
	private SequenceTargetingReagent sequenceTargetingReagentAssociationSubject;
}
