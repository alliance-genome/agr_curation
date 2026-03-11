package org.alliancegenome.curation_api.response;

import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ObjectResponse", description = "ObjectResponse: wraps a single entity with error/warning messages. The 'entity' field contains the returned object.")
public class ObjectResponse<E> extends APIResponse {

	@Schema(description = "The returned entity object")
	@JsonView({ CurationView.FieldsOnly.class, CurationView.PersonSettingView.class })
	private E entity;
	
}
