package org.alliancegenome.curation_api.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ObjectListResponse", description = "ObjectListResponse: wraps a list of entities with error/warning messages. The 'entities' field contains the returned list.")
public class ObjectListResponse<E> extends APIResponse {

	@Schema(description = "The list of returned entity objects")
	@JsonView(CurationView.FieldsOnly.class)
	private List<E> entities;

	public ObjectListResponse(Set<E> set) {
		this(new ArrayList<E>(set));
	}

}
