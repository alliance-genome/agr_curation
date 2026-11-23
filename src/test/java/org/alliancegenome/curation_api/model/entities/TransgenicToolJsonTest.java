package org.alliancegenome.curation_api.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.associations.TransgenicToolTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSynonymSlotAnnotation;
import org.alliancegenome.curation_api.view.CurationView;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * Which parts of a transgenic tool and its tool - tool associations each view serializes. Two tools
 * submitted as compatible with each other (A -> B and B -> A) loop if a tool nested in an association
 * carries its own associations; the views alone prevent that, no properties are ignored:
 * - the tool views (the table, a tool's own endpoints): the associated tools reduced to relation,
 *   symbol and identifier, not the associations
 * - FieldsAndLists: no associations
 * - the association view (/find and /findBy): both tools with their fields only
 */
class TransgenicToolJsonTest {

	// as the application maps JSON: properties without a view are not in any view
	private final ObjectMapper mapper = JsonMapper.builder().disable(MapperFeature.DEFAULT_VIEW_INCLUSION).build();

	private TransgenicTool tool(String id, String symbolText, String synonymText) {
		TransgenicToolSymbolSlotAnnotation symbol = new TransgenicToolSymbolSlotAnnotation();
		symbol.setDisplayText(symbolText);
		TransgenicToolSynonymSlotAnnotation synonym = new TransgenicToolSynonymSlotAnnotation();
		synonym.setDisplayText(synonymText);

		TransgenicTool tool = new TransgenicTool();
		tool.setPrimaryExternalId(id);
		tool.setTransgenicToolSymbol(symbol);
		tool.setTransgenicToolSynonyms(List.of(synonym));
		return tool;
	}

	private TransgenicToolTransgenicToolAssociation association(TransgenicTool subject, TransgenicTool object) {
		VocabularyTerm relation = new VocabularyTerm();
		relation.setName("is_compatible_with");

		TransgenicToolTransgenicToolAssociation association = new TransgenicToolTransgenicToolAssociation();
		association.setTransgenicToolAssociationSubject(subject);
		association.setRelation(relation);
		association.setTransgenicToolTransgenicToolAssociationObject(object);
		return association;
	}

	// FLP <-> FRT, each submitted as compatible with the other
	private TransgenicToolTransgenicToolAssociation mutualPair() {
		TransgenicTool flp = tool("FB:FBto0000001", "FLP", "flippase");
		TransgenicTool frt = tool("FB:FBto0000002", "FRT", "FLP recombination target");
		TransgenicToolTransgenicToolAssociation flpToFrt = association(flp, frt);
		flp.setTransgenicToolTransgenicToolAssociations(List.of(flpToFrt));
		frt.setTransgenicToolTransgenicToolAssociations(List.of(association(frt, flp)));
		return flpToFrt;
	}

	@Test
	void toolViewHasTheAssociatedToolsNotTheAssociations() throws Exception {
		TransgenicTool flp = mutualPair().getTransgenicToolAssociationSubject();

		String json = mapper.writerWithView(CurationView.TransgenicToolView.class).writeValueAsString(flp);

		assertTrue(json.contains("\"associatedTransgenicTools\":[{\"relation\":\"is_compatible_with\",\"symbol\":\"FRT\",\"identifier\":\"FB:FBto0000002\"}]"), json);
		assertFalse(json.contains("transgenicToolTransgenicToolAssociations"), json);
		assertTrue(json.contains("\"flippase\""), json);
	}

	@Test
	void detailViewDoesNotLoop() throws Exception {
		TransgenicTool flp = mutualPair().getTransgenicToolAssociationSubject();

		String json = mapper.writerWithView(CurationView.TransgenicToolDetailView.class).writeValueAsString(flp);

		assertTrue(json.contains("\"identifier\":\"FB:FBto0000002\""), json);
		assertFalse(json.contains("transgenicToolTransgenicToolAssociations"), json);
	}

	@Test
	void fieldsAndListsHasNoAssociations() throws Exception {
		TransgenicTool flp = mutualPair().getTransgenicToolAssociationSubject();

		String json = mapper.writerWithView(CurationView.FieldsAndLists.class).writeValueAsString(flp);

		assertFalse(json.contains("transgenicToolTransgenicToolAssociations"), json);
		assertFalse(json.contains("is_compatible_with"), json);
		assertFalse(json.contains("transgenicToolAssociationSubject"), json);
		assertFalse(json.contains("transgenicToolTransgenicToolAssociationObject"), json);
		assertFalse(json.contains("FB:FBto0000002"), json);
	}

	@Test
	void associationViewHasBothToolsWithTheirFieldsOnly() throws Exception {
		String json = mapper.writerWithView(CurationView.TransgenicToolTransgenicToolAssociationView.class).writeValueAsString(mutualPair());

		assertTrue(json.contains("\"transgenicToolAssociationSubject\":{"), json);
		assertTrue(json.contains("\"transgenicToolTransgenicToolAssociationObject\":{"), json);
		assertTrue(json.contains("\"FB:FBto0000001\""), json);
		assertTrue(json.contains("\"FRT\""), json);
		assertTrue(json.contains("\"is_compatible_with\""), json);
		// the tools' lists are in the tool views only
		assertFalse(json.contains("transgenicToolTransgenicToolAssociations"), json);
		assertFalse(json.contains("associatedTransgenicTools"), json);
		assertFalse(json.contains("flippase"), json);
	}

	@Test
	void toolViewReadsBack() throws Exception {
		TransgenicTool flp = mutualPair().getTransgenicToolAssociationSubject();
		String json = mapper.writerWithView(CurationView.TransgenicToolView.class).writeValueAsString(flp);

		// as the application reads JSON: the computed associatedTransgenicTools is read-only
		TransgenicTool read = new ObjectMapper().readValue(json, TransgenicTool.class);
		assertEquals("FB:FBto0000001", read.getPrimaryExternalId());
	}
}
