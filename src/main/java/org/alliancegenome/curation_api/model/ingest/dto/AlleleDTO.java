package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@AGRCurationSchemaVersion(min="1.5.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={GenomicEntityDTO.class, AlleleMutationTypeSlotAnnotationDTO.class, NameSlotAnnotationDTO.class}, submitted=true)
public class AlleleDTO extends GenomicEntityDTO {

	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("reference_curies")
	private List<String> referenceCuries;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("inheritance_mode_name")
	private String inheritanceModeName;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("in_collection_name")
	private String inCollectionName;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("is_extinct")
	private Boolean isExtinct;

	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("allele_mutation_type_dtos")
	private List<AlleleMutationTypeSlotAnnotationDTO> alleleMutationTypeDtos;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("allele_symbol_dto")
	private NameSlotAnnotationDTO alleleSymbolDto;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("allele_full_name_dto")
	private NameSlotAnnotationDTO alleleFullNameDto;
	
	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("allele_synonym_dtos")
	private List<NameSlotAnnotationDTO> alleleSynonymDtos;
	
}
