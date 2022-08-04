package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;

import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.*;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Indexed
@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Schema(name = "Gene_Disease_Annotation", description = "Annotation class representing a gene disease annotation")
@JsonTypeName("GeneDiseaseAnnotation")
@OnDelete(action = OnDeleteAction.CASCADE)
public class GeneDiseaseAnnotation extends DiseaseAnnotation {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
	@JoinColumn(foreignKey = @ForeignKey(name="fk_genedasubject"))
	@JsonView({View.FieldsOnly.class})
	private Gene subject;
	
	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private AffectedGenomicModel sgdStrainBackground;
	
}