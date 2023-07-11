package org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotation.class })
@Schema(name = "AlleleMutationtTypeSlotAnnotation", description = "POJO representing an allele mutation type slot annotation")
@Table(indexes = { @Index(name = "allelemutationtype_singleallele_curie_index", columnList = "singleallele_curie") })
public class AlleleMutationTypeSlotAnnotation extends SlotAnnotation {

	@ManyToOne
	@JsonBackReference
	@Fetch(FetchMode.JOIN)
	private Allele singleAllele;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = { @Index(name = "allelemutationtypeslotannotation_id_index", columnList = "allelemutationtypeslotannotation_id"),
		@Index(name = "allelemutationtypeslotannotation_mutationtypes_curie_index", columnList = "mutationtypes_curie"), })
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<SOTerm> mutationTypes;

}
