package org.alliancegenome.curation_api.model.entities.associations.codingSequenceAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.entities.LocationAssociation;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@AGRCurationSchemaVersion(min = "2.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { LocationAssociation.class })
@Schema(name = "CodingSequenceGenomicLocationAssociation", description = "POJO representing an association between a CDS and a genomic location")
@Table(
	indexes = {
		@Index(name = "codingsequencelocationassociation_relation_index", columnList = "relation_id"),
		@Index(name = "codingsequencelocationassociation_subject_index", columnList = "codingsequenceassociationsubject_id"),
		@Index(name = "codingsequencelocationassociation_object_index", columnList = "codingsequencegenomiclocationassociationobject_id")
	}
)
public class CodingSequenceGenomicLocationAssociation extends LocationAssociation {

	@IndexedEmbedded(includePaths = {
		"curie", "curie_keyword", "modEntityId", "modEntityId_keyword",
		"modInternalId", "modInternalId_keyword", "name", "name_keyword"
	})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties("codingSequenceGenomicLocationAssociations")
	@Fetch(FetchMode.JOIN)
	private CodingSequence codingSequenceAssociationSubject;

	@IndexedEmbedded(includePaths = {
		"curie", "curie_keyword", "modEntityId", "modEntityId_keyword",
		"modInternalId", "modInternalId_keyword", "name", "name_keyword"
	})
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@JsonIgnoreProperties("codingSequenceGenomicLocationAssociations")
	@Fetch(FetchMode.JOIN)
	private AssemblyComponent codingSequenceGenomicLocationAssociationObject;	
}
