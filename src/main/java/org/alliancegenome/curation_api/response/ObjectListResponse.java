package org.alliancegenome.curation_api.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ObjectListResponse", description = "POJO that represents the Object List Response")
public class ObjectListResponse<E> extends APIResponse {

	@JsonView(View.FieldsOnly.class)
	private List<E> entities;

	public ObjectListResponse(Set<E> set) {
		this(new ArrayList<E>(set));
	}

}
