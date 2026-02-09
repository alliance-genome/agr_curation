package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.AlleleDatabaseStatusSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.AlleleFunctionalImpactSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.AlleleGermlineTransmissionStatusSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.AlleleInheritanceModeSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.AlleleMutationTypeSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.AlleleNomenclatureEventSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.7.3", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GenomicEntityDTO.class, AlleleMutationTypeSlotAnnotationDTO.class,
	NameSlotAnnotationDTO.class, SecondaryIdSlotAnnotationDTO.class, AlleleInheritanceModeSlotAnnotationDTO.class, AlleleFunctionalImpactSlotAnnotationDTO.class,
	AlleleGermlineTransmissionStatusSlotAnnotationDTO.class, AlleleDatabaseStatusSlotAnnotationDTO.class, AlleleNomenclatureEventSlotAnnotationDTO.class, NoteDTO.class }, submitted = true)
public class AlleleDTO extends GenomicEntityDTO {

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("reference_curies")
	private List<String> referenceCuries;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("in_collection_name")
	private String inCollectionName;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("is_extinct")
	private Boolean isExtinct;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("allele_mutation_type_dtos")
	private List<AlleleMutationTypeSlotAnnotationDTO> alleleMutationTypeDtos;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("allele_inheritance_mode_dtos")
	private List<AlleleInheritanceModeSlotAnnotationDTO> alleleInheritanceModeDtos;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("allele_symbol_dto")
	private NameSlotAnnotationDTO alleleSymbolDto;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("allele_full_name_dto")
	private NameSlotAnnotationDTO alleleFullNameDto;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("allele_synonym_dtos")
	private List<NameSlotAnnotationDTO> alleleSynonymDtos;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("allele_secondary_id_dtos")
	private List<SecondaryIdSlotAnnotationDTO> alleleSecondaryIdDtos;
	
	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("allele_germline_transmission_status_dto")
	private AlleleGermlineTransmissionStatusSlotAnnotationDTO alleleGermlineTransmissionStatusDto;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("allele_functional_impact_dtos")
	private List<AlleleFunctionalImpactSlotAnnotationDTO> alleleFunctionalImpactDtos;
	
	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("allele_database_status_dto")
	private AlleleDatabaseStatusSlotAnnotationDTO alleleDatabaseStatusDto;
	
	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("allele_nomenclature_event_dtos")
	private List<AlleleNomenclatureEventSlotAnnotationDTO> alleleNomenclatureEventDtos;

}
