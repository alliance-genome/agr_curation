package org.alliancegenome.curation_api.model.ingest.xml.dto;

import javax.xml.bind.annotation.*;

import lombok.Data;

@Data
@XmlRootElement(name = "Axiom", namespace = "http://www.w3.org/2002/07/owl#")
@XmlAccessorType (XmlAccessType.FIELD)
public class Axiom {
    
    @XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
    private String annotatedSource;
    
    @XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
    private String annotatedProperty;
    
    @XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
    private String annotatedTarget;
    
    @XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
    private String hasDbXref;
    
    @XmlElement(namespace = "http://www.w3.org/2000/01/rdf-schema#")
    private String comment;
    
    //@XmlElement(namespace = "http://www.w3.org/2000/01/rdf-schema#")
    //private String label;
    
    //@XmlElement(namespace = "http://www.w3.org/2000/01/rdf-schema#")
    //private String subPropertyOf;
    
    @XmlElement(name = "type", namespace = "http://purl.org/dc/elements/1.1/")
    private String[] types;

}
