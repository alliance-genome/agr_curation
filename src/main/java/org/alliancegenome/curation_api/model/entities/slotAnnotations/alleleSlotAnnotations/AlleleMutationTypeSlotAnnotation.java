package org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotation.class })
@Schema(name = "AlleleMutationtTypeSlotAnnotation", description = "POJO representing an allele mutation type slot annotation")
@Table(indexes = { @Index(name = "allelemutationtype_singleallele_curie_index", columnList = "singleallele_curie"),
		@Index(name = "allelemutationtype_inheritancemode_id_index", columnList = "inheritancemode_id"),
		@Index(name = "allelemutationtype_phenotypeterm_curie_index", columnList = "phenotypeterm_curie") })
public class AlleleMutationTypeSlotAnnotation extends SlotAnnotation {

	@ManyToOne
	@JsonBackReference
	private Allele singleAllele;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = { @Index(name = "allelemutationtypeslotannotation_id_index", columnList = "allelemutationtypeslotannotation_id"),
		@Index(name = "allelemutationtypeslotannotation_mutationtypes_curie_index", columnList = "mutationtypes_curie"), })
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class })
	private List<SOTerm> mutationTypes;

}
