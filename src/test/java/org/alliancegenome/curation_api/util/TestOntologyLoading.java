package org.alliancegenome.curation_api.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.alliancegenome.curation_api.model.entities.ontology.BTOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadHelper;

public class TestOntologyLoading {

	public static void main(String[] args) throws Exception {
//		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
//		config.setLoadOnlyIRIPrefix("ATP");
//		GenericOntologyLoadHelper<ATPTerm> loader = new GenericOntologyLoadHelper<>(ATPTerm.class, config);
//		Map<String, ATPTerm> map = loader.load(new FileInputStream(new File("/Users/olinblodgett/Desktop/FMS/ATP.owl")));

//		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
//		config.getAltNameSpaces().add("xenopus_anatomy");
//		config.getAltNameSpaces().add("xenopus_anatomy_in_vitro");
//		GenericOntologyLoadHelper<XBATerm> loader = new GenericOntologyLoadHelper<>(XBATerm.class, config);
//		Map<String, XBATerm> map = loader.load(new FileInputStream(new File("/Users/olinblodgett/Desktop/Ontology/XB.owl")));

		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		GenericOntologyLoadHelper<BTOTerm> loader = new GenericOntologyLoadHelper<>(BTOTerm.class, config);
		Map<String, BTOTerm> map = loader.load(new FileInputStream(new File("/Users/olinblodgett/Desktop/FMS/BTO.owl")));
		
		
		for (Entry<String, ? extends OntologyTerm> entry : map.entrySet()) {
			//if(entry.getKey().equals("BTO:0000030")) {
				//String ancestorsString = entry.getValue().getAncestors().stream().map(t -> { return t.getClosureSubject().getCurie() + " " + t.getClosureTypes() + "(" + t.getDistance() + ") " + t.getClosureObject().getCurie() + "\n"; }).toList() + "";
				//System.out.println("Ancestors: " + ancestorsString);

				System.out.println(entry.getKey() + " " + entry.getValue().getAncestors().size());
			//}
		}
	}
}
