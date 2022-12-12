package org.alliancegenome.curation_api.model.document;

import java.util.List;

import org.alliancegenome.curation_api.document.base.BaseDocument;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(callSuper = true)
public class LiteratureReference extends BaseDocument {

	@JsonView({ View.FieldsOnly.class })
	public String curie;

	@JsonView({ View.FieldsOnly.class })
	public String title;

	@JsonView({ View.FieldsOnly.class })
	public String pages;

	@JsonView({ View.FieldsOnly.class })
	public String volume;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("abstract")
	public String referenceAbstract;

	@JsonView({ View.FieldsOnly.class })
	public String citation;

	@JsonView({ View.FieldsOnly.class })
	public List<LiteratureCrossReference> cross_references;

}
