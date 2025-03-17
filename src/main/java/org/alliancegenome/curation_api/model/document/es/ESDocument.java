package org.alliancegenome.curation_api.model.document.es;

import java.io.Serializable;

import lombok.Data;

@Data
public abstract class ESDocument implements Serializable {
	protected String category;
}
