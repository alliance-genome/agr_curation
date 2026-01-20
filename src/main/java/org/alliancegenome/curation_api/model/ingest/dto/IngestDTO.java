package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.associations.AgmAgmAssociationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AgmAlleleAssociationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AgmSequenceTargetingReagentAssociationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AlleleConstructAssociationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AlleleGeneAssociationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AlleleVariantAssociationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.ConstructGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class IngestDTO {

	@JsonView({CurationView.FieldsOnly.class})
	@JsonProperty("linkml_version")
	private String linkMLVersion;

	@JsonView({CurationView.FieldsOnly.class})
	@JsonProperty("alliance_member_release_version")
	private String allianceMemberReleaseVersion;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("agm_ingest_set")
	private List<AffectedGenomicModelDTO> agmIngestSet;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("allele_ingest_set")
	private List<AlleleDTO> alleleIngestSet;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("disease_agm_ingest_set")
	private List<AGMDiseaseAnnotationDTO> diseaseAgmIngestSet;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("disease_allele_ingest_set")
	private List<AlleleDiseaseAnnotationDTO> diseaseAlleleIngestSet;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("disease_gene_ingest_set")
	private List<GeneDiseaseAnnotationDTO> diseaseGeneIngestSet;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("gene_ingest_set")
	private List<GeneDTO> geneIngestSet;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("construct_ingest_set")
	private List<ConstructDTO> constructIngestSet;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("variant_ingest_set")
	private List<VariantDTO> variantIngestSet;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("allele_construct_association_ingest_set")
	private List<AlleleConstructAssociationDTO> alleleConstructAssociationIngestSet;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("allele_gene_association_ingest_set")
	private List<AlleleGeneAssociationDTO> alleleGeneAssociationIngestSet;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("allele_variant_association_ingest_set")
	private List<AlleleVariantAssociationDTO> alleleVariantAssociationIngestSet;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("construct_genomic_entity_association_ingest_set")
	private List<ConstructGenomicEntityAssociationDTO> constructGenomicEntityAssociationIngestSet;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("agm_sequence_targeting_reagent_association_ingest_set")
	private List<AgmSequenceTargetingReagentAssociationDTO> agmStrAssociationIngestSet;

	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("agm_allele_association_ingest_set")
	private List<AgmAlleleAssociationDTO> agmAlleleAssociationIngestSet;
	
	@JsonView({CurationView.FieldsAndLists.class})
	@JsonProperty("agm_agm_association_ingest_set")
	private List<AgmAgmAssociationDTO> agmAgmAssociationIngestSet;
}

