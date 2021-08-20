package org.alliancegenome.curation_api.model.ingest.xml.dto;

import javax.xml.bind.annotation.XmlElement;

public class OnClass {

    @XmlElement(name = "Class", namespace = "http://www.w3.org/2002/07/owl#")
    private RDFClass rdfClass;
}
