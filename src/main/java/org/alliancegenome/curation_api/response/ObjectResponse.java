package org.alliancegenome.curation_api.response;

import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
@Schema(name="ObjectResponse", description="POJO that represents the ObjectResponse")
public class ObjectResponse<E> extends APIResponse {

	@JsonView({View.FieldsOnly.class, View.PersonSettingView.class})
	private E entity;

}
