package org.alliancegenome.curation_api.model.document;

import java.util.List;

import org.alliancegenome.curation_api.document.base.BaseDocument;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@JsonPropertyOrder({"curie", "title", "volume", "citation", "short_citation", "cross_references", "abstract"})
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

	@JsonProperty("short_citation")
	@JsonView({ View.FieldsOnly.class })
	public String citationShort;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("cross_references")
	public List<LiteratureCrossReference> crossReferences;

}
