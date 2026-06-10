package org.alliancegenome.curation_api.model.mati;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentifiersRange {
	private Identifier first;
	private Identifier last;
}
