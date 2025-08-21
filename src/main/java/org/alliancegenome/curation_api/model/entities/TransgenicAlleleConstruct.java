package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.view.View;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@JsonView(View.TransgenicAllelesDocumentView.class)
public class TransgenicAlleleConstruct {

	private Construct construct;
	private List<Gene> expressedGenes;
	private List<SequenceTargetingReagent> sequenceTargetingReagents;
	private List<Gene> regulatoryGenes;
	private List<Gene> nonBgiComponents;
}
