package org.alliancegenome.curation_api.model.document.es;

import org.alliancegenome.curation_api.view.View;
import org.jetbrains.annotations.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Setter;

@Setter
public class Stage implements Comparable<Stage> {

	@JsonView({View.ExpressionDetail.class })
	@JsonProperty("stageID")
	private String primaryKey;
	@JsonView(value = {View.ExpressionDetail.class})
	private String name;

	@Override
	public String toString() {
		return primaryKey;
	}

	@Override
	public int compareTo(@NotNull Stage o) {
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Stage stage = (Stage) o;
		return java.util.Objects.equals(primaryKey, stage.primaryKey);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(primaryKey);
	}
}
