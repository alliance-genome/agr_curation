package org.alliancegenome.curation_api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("2000 - ReferenceIdentity")
@Order(2000)
class ReferenceIdentity {

	@Test
	public void testUniqueness() {
		Set<Reference> referenceSet = new HashSet<>();
		Reference reference = getReference("ref1", "xref1");
		referenceSet.add(reference);
		referenceSet.add(reference);
		Reference reference1 = getReference("ref1", "xref1");
		Reference reference2 = getReference("ref1", "xref2");
		referenceSet.add(reference1);
		referenceSet.add(reference2);
		// make sure identical references are considered equals (in a collection)
		assertEquals(referenceSet.size(), 2);
	}

	@NotNull
	private static Reference getReference(String refCurie, String xrefCurie) {
		Reference reference1 = new Reference();
		reference1.setCurie(refCurie);
		CrossReference crossReference = new CrossReference();
		crossReference.setReferencedCurie(xrefCurie);
		crossReference.setDisplayName(xrefCurie);
		
		reference1.setCrossReferences(List.of(crossReference));
		return reference1;
	}
}