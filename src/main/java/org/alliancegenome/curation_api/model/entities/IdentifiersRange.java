package org.alliancegenome.curation_api.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IdentifiersRange {
	private Identifier first;
	private Identifier last;
}
