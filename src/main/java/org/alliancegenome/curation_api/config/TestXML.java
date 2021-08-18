package org.alliancegenome.curation_api.config;

import java.io.*;

import javax.xml.bind.*;

import org.alliancegenome.curation_api.model.ingest.xml.dto.*;

public class TestXML {

    public static void main(String[] args) throws Exception {
        File file = new File("/Users/olinblodgett/Desktop/FMS/doid.owl");
        
        
        JAXBContext context = JAXBContext.newInstance(RDF.class);
        Unmarshaller um = context.createUnmarshaller();
        um.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
        RDF rdf = (RDF) um.unmarshal(new FileInputStream(file));
        
        
        
//      JacksonXmlModule module = new JacksonXmlModule();
//      module.setDefaultUseWrapper(false);
//      XmlMapper mapper = new XmlMapper(module);



        //mapper.setSerializationInclusion(Include.NON_NULL);
        //mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        //String xml = inputStreamToString(new FileInputStream(file));
        //RDF rdf = mapper.readValue(new File("/Users/olinblodgett/Desktop/FMS/doid.owl"), RDF.class);
        
        //System.out.println(rdf);
        
        int sum = 0;
        for(RDFClass c: rdf.getClasses()) {
            if(c.getHasAlternativeId() != null && c.getHasAlternativeId().length > 0) {
                System.out.println(c.getId());
                sum += c.getHasAlternativeId().length;
            }
        }
        
        System.out.println(sum);
        System.out.println(rdf.getClasses().length);
        //System.out.println(rdf.getAxioms().size());
        //System.out.println(rdf.getObjectProperties().size());
        

    }

}
