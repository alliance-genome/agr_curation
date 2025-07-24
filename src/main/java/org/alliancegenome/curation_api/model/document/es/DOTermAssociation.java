package org.alliancegenome.curation_api.model.document.es;

import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.view.View;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonView({View.FieldsOnly.class, View.ForPublic.class, View.ModelDocumentView.class})
public class DOTermAssociation {
	private DOTerm disease;
	private String associationType;
	private String diseaseModel;
}
