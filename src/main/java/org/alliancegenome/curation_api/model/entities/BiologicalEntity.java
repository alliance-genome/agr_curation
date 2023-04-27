package org.alliancegenome.curation_api.model.entities;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.BiologicalEntityTypeBridge;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.CurieAuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.TypeBinderRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.TypeBinding;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = AffectedGenomicModel.class, name = "AffectedGenomicModel"), @JsonSubTypes.Type(value = Allele.class, name = "Allele"),
	@JsonSubTypes.Type(value = Gene.class, name = "Gene") })
@Audited
@Entity
//@TypeBinding(binder = @TypeBinderRef(type = BiologicalEntityTypeBridge.class))
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.7.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(indexes = { @Index(name = "biologicalentity_createdby_index", columnList = "createdBy_id"), @Index(name = "biologicalentity_updatedby_index", columnList = "updatedBy_id"),
	@Index(name = "biologicalentity_taxon_index", columnList = "taxon_curie"), @Index(name = "biologicalentity_dataprovider_index", columnList = "dataprovider_id")})
public class BiologicalEntity extends CurieAuditedObject {

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private NCBITaxonTerm taxon;
	
	@IndexedEmbedded(includePaths = {"sourceOrganization.abbreviation", "sourceOrganization.fullName", "sourceOrganization.shortName", "crossReference.displayName", "crossReference.referencedCurie",
			"sourceOrganization.abbreviation_keyword", "sourceOrganization.fullName_keyword", "sourceOrganization.shortName_keyword", "crossReference.displayName_keyword", "crossReference.referencedCurie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private DataProvider dataProvider;

}
