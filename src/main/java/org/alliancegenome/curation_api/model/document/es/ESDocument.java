package org.alliancegenome.curation_api.model.document.es;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public abstract class ESDocument implements Serializable {
	// Only needed to retrieve the category value. It's not meant to change this variable!
	// The variable should be final, but Lombok does not support final variables with @Setter.
	@Setter(AccessLevel.NONE)
	public String category;
	protected Boolean searchable = false;
}
