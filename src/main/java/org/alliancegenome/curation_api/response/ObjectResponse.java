package org.alliancegenome.curation_api.response;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectResponse<E> extends APIResponse {

	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class })
	private E entity;

}
