package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@AGRCurationSchemaVersion(min="1.5.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={GenomicEntityDTO.class, NameSlotAnnotationDTO.class}, submitted=true)
public class GeneDTO extends GenomicEntityDTO {

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("allele_symbol_dto")
	private NameSlotAnnotationDTO alleleSymbolDto;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("allele_full_name_dto")
	private NameSlotAnnotationDTO alleleFullNameDto;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("allele_systematic_name_dto")
	private NameSlotAnnotationDTO alleleSystematicNameDto;
	
	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("allele_synonyms_dto")
	private List<NameSlotAnnotationDTO> alleleSynonymDtos;
}
