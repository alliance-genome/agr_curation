package org.alliancegenome.curation_api.model.dto.xml;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.*;

@Data
@ToString
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@XmlRootElement(name = "Class", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
@XmlAccessorType (XmlAccessType.FIELD)
public class RDFClass {
	
	private String about;
	
	@XmlElement(name = "subClassOf", namespace = "http://www.w3.org/2000/01/rdf-schema#")
	private SubClass[] subClassOfs;
	
	@XmlElement(namespace = "http://purl.obolibrary.org/obo/")
	private String IAO_0000115;

	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String id;
	
	@XmlElement(namespace = "http://www.w3.org/2000/01/rdf-schema#")
	private String label;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String[] hasAlternativeId;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String hasDbXref;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String hasExactSynonym;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String hasRelatedSynonym;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String hasBroadSynonym;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String hasNarrowSynonym;
	
	@XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
	private IntersctionOf intersectionOf;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String hasOBONamespace;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String inSubset;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String created_by;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String creation_date;
	
	@XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
	private String exactMatch;
	
	@XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
	private String deprecated;
	
	@XmlElement(namespace = "http://www.w3.org/2000/01/rdf-schema#")
	private String comment;
	
	@XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
	private EquivalentClass equivalentClass;
	
	@XmlElement(namespace = "http://purl.obolibrary.org/obo/")
	private String OBI_9991118;
	
	@XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
	private String narrowMatch;
	
	@XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
	private String broadMatch;
	
	@XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
	private String disjointWith;
	
	@XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
	private UnionOf unionOf;
	
	
	
}
