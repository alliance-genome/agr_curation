package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "AssociationType", length = 96)
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "association", description = "POJO that represents an association")
@Table(indexes = {
	@Index(name = "association_curie_index", columnList = "curie"),
	@Index(name = "association_modEntityId_index", columnList = "modEntityId"),
	@Index(name = "association_modInternalId_index", columnList = "modInternalId"),
	@Index(name = "association_uniqueId_index", columnList = "uniqueid"),
	@Index(name = "association_whenexpressedstagename_index ", columnList = "whenexpressedstagename"),
	@Index(name = "association_whereexpressedstatement_index", columnList = "whereexpressedstatement"),
	@Index(name = "association_interactionId_index", columnList = "interactionid"),
	@Index(name = "association_construct_subject_index", columnList = "constructassociationsubject_id"),
	@Index(name = "association_construct_object_index", columnList = "constructgenomicentityassociationobject_id"),
	@Index(name = "association_relation_index", columnList = "relation_id"),
	@Index(name = "association_singlereference_index", columnList = "singleReference_id"),
	@Index(name = "association_dataProvider_index", columnList = "dataProvider_id"),
	@Index(name = "association_crossreference_index", columnList = "crossreference_id"),
	@Index(name = "association_assertedAllele_index", columnList = "assertedAllele_id"),
	@Index(name = "association_inferredAllele_index", columnList = "inferredAllele_id"),
	@Index(name = "association_inferredGene_index", columnList = "inferredGene_id"),
	@Index(name = "association_PhenotypeAnnotationSubject_index", columnList = "phenotypeAnnotationSubject_id"),
	@Index(name = "association_sgdstrainbackground_index", columnList = "sgdstrainbackground_id"),
	@Index(name = "association_expressionpattern_index", columnList = "expressionpattern_id"),
	@Index(name = "association_expression_annotation_subject_index", columnList = "expressionannotationsubject_id"),
	@Index(name = "association_expression_assay_used_index", columnList = "expressionassayused_id"),
	@Index(name = "association_sqtr_subject_index", columnList = "sequencetargetingreagentassociationsubject_id"),
	@Index(name = "association_sqtr_object_index", columnList = "sequencetargetingreagentgeneassociationobject_id"),
	@Index(name = "association_evidencecode_index", columnList = "evidencecode_id"),
	@Index(name = "association_relatednote_index", columnList = "relatednote_id"),
	@Index(name = "association_geneassociationsubject_index", columnList = "geneassociationsubject_id"),
	@Index(name = "association_genegeneassociationobject_index", columnList = "genegeneassociationobject_id"),
	@Index(name = "association_transcript_subject_index", columnList = "transcriptassociationsubject_id"),
	@Index(name = "association_transcript_gene_object_index", columnList = "transcriptgeneassociationobject_id"),
	@Index(name = "association_exon_subject_index", columnList = "exonassociationsubject_id"),
	@Index(name = "association_exon_object_index", columnList = "exongenomiclocationassociationobject_id"),
	@Index(name = "association_annotationType_index", columnList = "annotationType_id"),
	@Index(name = "association_DiseaseAnnotationObject_index", columnList = "diseaseAnnotationObject_id"),
	@Index(name = "association_diseaseGeneticModifierRelation_index", columnList = "diseaseGeneticModifierRelation_id"),
	@Index(name = "association_geneticSex_index", columnList = "geneticSex_id"),
	@Index(name = "association_secondaryDataProvider_index", columnList = "secondaryDataProvider_id"),
	@Index(name = "association_DiseaseAnnotationSubject_index", columnList = "diseaseAnnotationSubject_id"),
	@Index(name = "association_interactionsource_index", columnList = "interactionsource_id"),
	@Index(name = "association_interactiontype_index", columnList = "interactiontype_id"),
	@Index(name = "association_interactorarole_index", columnList = "interactorarole_id"),
	@Index(name = "association_interactorbrole_index", columnList = "interactorbrole_id"),
	@Index(name = "association_interactoratype_index", columnList = "interactoratype_id"),
	@Index(name = "association_interactorbtype_index", columnList = "interactorbtype_id"),
	@Index(name = "association_interactorageneticperturbarion_index", columnList = "interactorageneticperturbation_id"),
	@Index(name = "association_interactorbgeneticperturbarion_index", columnList = "interactorbgeneticperturbation_id"),
	@Index(name = "association_alleleassociationsubject_index", columnList = "alleleassociationsubject_id"),
	@Index(name = "association_allelegeneassociationobject_index", columnList = "allelegeneassociationobject_id"),
	@Index(name = "association_transcript_exon_object_index", columnList = "transcriptexonassociationobject_id"),
	@Index(name = "association_transcript_object_index", columnList = "transcriptgenomiclocationassociationobject_id"),
	@Index(name = "association_cds_subject_index", columnList = "codingsequenceassociationsubject_id"),
	@Index(name = "association_cds_object_index", columnList = "codingsequencegenomiclocationassociationobject_id"),
	@Index(name = "association_aggregationdatabase_index", columnList = "aggregationdatabase_id"),
	@Index(name = "association_detectionmethod_index", columnList = "detectionmethod_id"),
	@Index(name = "association_transcript_cds_object_index", columnList = "transcriptcodingsequenceassociationobject_id"),

	@Index(name = "association_associationtype_index", columnList = "associationtype"),

	@Index(name = "association_createdby_index", columnList = "createdBy_id"),
	@Index(name = "association_updatedby_index", columnList = "updatedBy_id")
})

@AGRCurationSchemaVersion(min = "1.9.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
public class Association extends AuditedObject {
	
}
