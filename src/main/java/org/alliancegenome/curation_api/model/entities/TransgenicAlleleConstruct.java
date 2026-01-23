package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.view.CurationView;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@JsonView(CurationView.TransgenicAllelesDocument.class)
public class TransgenicAlleleConstruct {

	private Construct construct;
	private List<Gene> expressedGenes;
	private List<SequenceTargetingReagent> sequenceTargetingReagents;
	private List<Gene> targetedGenes;
	private List<Gene> regulatoryGenes;
	private List<Gene> nonBgiComponents;
}
