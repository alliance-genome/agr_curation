package org.alliancegenome.curation_api.model.entities.associations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.view.CurationView;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * Which parts of an STR gene association each view serializes; the views alone keep the
 * serialization from looping, no properties are ignored:
 * - the association view (/sqtrgeneassociation/find and /findBy): STR, gene and evidence, without lists
 * - FieldsAndLists (a gene's own associations): the STR without its associations, not the gene
 * - the STR detail view (an STR's own associations): the gene, not the STR
 */
class SequenceTargetingReagentGeneAssociationJsonTest {

	// as the application maps JSON: properties without a view are not in any view
	private final ObjectMapper mapper = JsonMapper.builder().disable(MapperFeature.DEFAULT_VIEW_INCLUSION).build();

	private CrossReference crossReference(String curie) {
		CrossReference crossReference = new CrossReference();
		crossReference.setReferencedCurie(curie);
		return crossReference;
	}

	private SequenceTargetingReagentGeneAssociation association() {
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setDisplayText("ptk2ab");
		Gene gene = new Gene();
		gene.setPrimaryExternalId("ZFIN:ZDB-GENE-020114-1");
		gene.setGeneSymbol(symbol);
		gene.setGeneGenomicLocationAssociations(List.of(new GeneGenomicLocationAssociation()));
		gene.setCrossReferences(List.of(crossReference("ENSEMBL:ENSDARG00000000001")));

		SequenceTargetingReagent str = new SequenceTargetingReagent();
		str.setPrimaryExternalId("ZFIN:ZDB-CRISPR-200805-5");
		str.setSynonyms(List.of("CRISPR3"));
		str.setCrossReferences(List.of(crossReference("ZFIN:STR-XREF-1")));

		Reference reference = new Reference();
		reference.setCurie("AGRKB:101000000000001");

		VocabularyTerm relation = new VocabularyTerm();
		relation.setName("targets");

		SequenceTargetingReagentGeneAssociation association = new SequenceTargetingReagentGeneAssociation();
		association.setSequenceTargetingReagentAssociationSubject(str);
		association.setRelation(relation);
		association.setSequenceTargetingReagentGeneAssociationObject(gene);
		association.setEvidence(List.of(reference));
		return association;
	}

	@Test
	void associationViewHasStrGeneAndEvidenceWithoutLists() throws Exception {
		String json = mapper.writerWithView(CurationView.SequenceTargetingReagentGeneAssociationView.class).writeValueAsString(association());

		assertTrue(json.contains("\"sequenceTargetingReagentAssociationSubject\":{"), json);
		assertTrue(json.contains("\"ZFIN:ZDB-CRISPR-200805-5\""), json);
		assertFalse(json.contains("\"sequenceTargetingReagentGeneAssociations\""), json);

		assertTrue(json.contains("\"evidence\":[{"), json);
		assertTrue(json.contains("\"AGRKB:101000000000001\""), json);
		assertTrue(json.contains("\"ptk2ab\""), json);
		assertTrue(json.contains("\"ZFIN:ZDB-GENE-020114-1\""), json);
		assertTrue(json.contains("\"targets\""), json);
		assertFalse(json.contains("geneGenomicLocationAssociations"), json);
		// the gene's cross references are not in the STR views, the STR's own are
		assertFalse(json.contains("ENSEMBL:ENSDARG00000000001"), json);
		assertTrue(json.contains("ZFIN:STR-XREF-1"), json);
	}

	@Test
	void fieldsAndListsHasTheStrWithoutItsAssociationsAndNotTheGene() throws Exception {
		String json = mapper.writerWithView(CurationView.FieldsAndLists.class).writeValueAsString(association());

		assertTrue(json.contains("\"sequenceTargetingReagentAssociationSubject\":{"), json);
		assertFalse(json.contains("\"sequenceTargetingReagentGeneAssociations\""), json);
		assertFalse(json.contains("sequenceTargetingReagentGeneAssociationObject"), json);
		assertTrue(json.contains("\"targets\""), json);
	}

	@Test
	void strDetailViewHasTheStrsAssociationsWithTheirGeneButNotTheStrAgain() throws Exception {
		SequenceTargetingReagentGeneAssociation association = association();
		SequenceTargetingReagent str = association.getSequenceTargetingReagentAssociationSubject();
		str.setSequenceTargetingReagentGeneAssociations(List.of(association));

		String json = mapper.writerWithView(CurationView.SequenceTargetingReagentDetailView.class).writeValueAsString(str);

		assertTrue(json.contains("\"sequenceTargetingReagentGeneAssociations\":[{"), json);
		assertTrue(json.contains("\"ZFIN:ZDB-GENE-020114-1\""), json);
		assertFalse(json.contains("sequenceTargetingReagentAssociationSubject"), json);
	}
}
