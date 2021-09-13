package org.alliancegenome.curation_api.services;

import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseModelAnnotationDTO;

import java.util.HashMap;
import java.util.Map;

@JBossLog
public abstract class DiseaseAnnotationCurie {

    public static final String DELIMITER = "|";

    public abstract String getCurieID(DiseaseModelAnnotationDTO annotationDTO);

}
