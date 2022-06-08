package org.alliancegenome.curation_api.model.ingest.xml.dto;

import javax.xml.bind.annotation.XmlElement;

public class Members {

	@XmlElement(name = "Description", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
	private Description[] descriptions;
}
