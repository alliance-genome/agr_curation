package org.alliancegenome.curation_api.model.document.es;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.view.View;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@Schema(name = "Publication", description = "POJO that represents the Publication")
public class Publication implements Comparable<Publication>, Serializable {

	@JsonView({ View.ExpressionDetail.class })
	private String primaryKey;
	@JsonView({ View.ExpressionDetail.class })
	private String pubMedId;
	@JsonView({ View.ExpressionDetail.class })
	private String pubMedUrl;
	@JsonView({ View.ExpressionDetail.class })
	private String pubModId;
	@JsonView({ View.ExpressionDetail.class })
	private String pubModUrl;
	@JsonView({ View.ExpressionDetail.class })
	private String pubId;
	@JsonView({ View.ExpressionDetail.class })
	private String pubUrl;
	@JsonView({ View.ExpressionDetail.class })
	private List<ECOTerm> evidence;

	public void setPubIdFromId() {
		if (StringUtils.isNotEmpty(pubMedId)) {
			pubId = pubMedId;
		} else {
			pubId = pubModId;
		}
	}

	@JsonView({ View.ExpressionDetail.class, View.DiseaseAnnotation.class })
	@JsonGetter("url")
	private String getPubUrl() {
		if (StringUtils.isNotEmpty(pubMedId)) {
			return pubMedUrl;
		} else {
			return pubModUrl;
		}
	}

	@JsonSetter("url")
	private void setPubUrl(String value) {
		pubMedUrl = value;
	}

	@Override
	public String toString() {
		return getPubId() + " : " + getPubUrl();
	}

	@Override
	public int compareTo(Publication o) {
		return getPubId().compareTo(o.getPubId());
	}

	@JsonView({ View.ExpressionDetail.class })
	@JsonGetter("id")
	public String getPubId() {
		if (StringUtils.isNotEmpty(pubMedId)) {
			return pubMedId;
		}
		return pubModId != null ? pubModId : "";

	}

	@JsonSetter("id")
	private void setPubId(String value) {
		pubMedId = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Publication that = (Publication) o;
		return Objects.equals(getPubId(), that.getPubId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPubId());
	}
}
