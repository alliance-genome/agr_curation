package org.alliancegenome.curation_api.model.entities.slotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.ontology.FBCVTerm;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * SCRUM-6535: the use(s) of a cassette (enhancer trap, protein trap, and so on) - FBcv terms in the
 * 'experimental_tool_descriptor' namespace, per the LinkML 'uses' slot.
 */
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotation.class })
@Schema(name = "CassetteUseSlotAnnotation", description = "CassetteUseSlotAnnotation: the uses of a cassette")
public class CassetteUseSlotAnnotation extends SlotAnnotation {

	@ManyToOne
	@JsonBackReference
	private Cassette singleCassette;

	@IndexedEmbedded(includePaths = { "curie", "name", "curie_keyword", "name_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(
		name = "cassetteuseslotannotation_ontologyterm",
		joinColumns = @JoinColumn(name = "slotannotation_id"),
		inverseJoinColumns = @JoinColumn(name = "uses_id"),
		indexes = {
			@Index(name = "cassetteuseslotannotation_slotannotation_index", columnList = "slotannotation_id"),
			@Index(name = "cassetteuseslotannotation_uses_index", columnList = "uses_id")
		}
	)
	@JsonView({ CurationView.FieldsAndLists.class , CurationView.CassetteView.class})
	private List<FBCVTerm> uses;

}
