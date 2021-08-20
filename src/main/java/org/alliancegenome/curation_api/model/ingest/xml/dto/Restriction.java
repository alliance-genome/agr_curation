package org.alliancegenome.curation_api.model.ingest.xml.dto;

import javax.xml.bind.annotation.XmlElement;

public class Restriction {

    @XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
    private String onProperty;
    
    @XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
    private SomeValueOf someValuesFrom;
    
    @XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
    private String minQualifiedCardinality;
    
    @XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
    private String qualifiedCardinality;
    
    @XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
    private OnClass onClass;

}
