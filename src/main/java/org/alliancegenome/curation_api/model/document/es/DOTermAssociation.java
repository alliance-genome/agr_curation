package org.alliancegenome.curation_api.model.document.es;

import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.view.View;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonView({View.ModelDocument.class})
public class DOTermAssociation {
	private DOTerm disease;
	private String associationType;
	private String diseaseModel;
}
