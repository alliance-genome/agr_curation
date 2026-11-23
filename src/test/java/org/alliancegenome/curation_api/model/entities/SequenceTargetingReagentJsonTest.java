package org.alliancegenome.curation_api.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.associations.SequenceTargetingReagentGeneAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.view.CurationView;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The search and detail views of an STR carry two computed fields, the targeted genes preview and
 * their count. Clients read that JSON back into SequenceTargetingReagent (the integration tests do,
 * an update sends it back), so the fields must be ignored on the way in.
 */
class SequenceTargetingReagentJsonTest {

	private final ObjectMapper mapper = new ObjectMapper();

	private SequenceTargetingReagent strTargetingOneGene() {
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setDisplayText("ptk2ab");
		Gene gene = new Gene();
		gene.setPrimaryExternalId("ZFIN:ZDB-GENE-020114-1");
		gene.setGeneSymbol(symbol);

		VocabularyTerm relation = new VocabularyTerm();
		relation.setName("targets");

		SequenceTargetingReagent str = new SequenceTargetingReagent();
		str.setPrimaryExternalId("ZFIN:ZDB-CRISPR-200805-5");
		str.setName("CRISPR3-ptk2ab");

		SequenceTargetingReagentGeneAssociation association = new SequenceTargetingReagentGeneAssociation();
		association.setSequenceTargetingReagentAssociationSubject(str);
		association.setRelation(relation);
		association.setSequenceTargetingReagentGeneAssociationObject(gene);
		str.setSequenceTargetingReagentGeneAssociations(List.of(association));
		return str;
	}

	@Test
	void detailViewCarriesTheComputedFieldsAndReadsBack() throws Exception {
		String json = mapper.writerWithView(CurationView.SequenceTargetingReagentDetailView.class)
			.writeValueAsString(strTargetingOneGene());

		assertTrue(json.contains("\"targetedGeneCount\":1"), json);
		assertTrue(json.contains("\"geneIdentifier\":\"ZFIN:ZDB-GENE-020114-1\""), json);

		SequenceTargetingReagent read = mapper.readValue(json, SequenceTargetingReagent.class);
		assertEquals("ZFIN:ZDB-CRISPR-200805-5", read.getPrimaryExternalId());
		assertEquals("CRISPR3-ptk2ab", read.getName());
	}
}
