package org.alliancegenome.curation_api.base;

import java.util.Map;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoader;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public abstract class BaseOntologyTermBulkController<S extends BaseOntologyTermService<T, D>, T extends OntologyTerm, D extends BaseDAO<T>> {

    private GenericOntologyLoader<T> loader;

    private BaseOntologyTermService<T, D> service;
    private Class<T> termClazz;

    protected void setService(S service, Class<T> termClazz) {
        this.service = service;
        this.termClazz = termClazz;
        loader = new GenericOntologyLoader<T>(termClazz);
    }

    public String updateTerms(String fullText) {

        try {
            Map<String, T> termMap = loader.load(fullText);

            ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
            ph.startProcess(termClazz.getSimpleName() + " Database Persistance", termMap.size());
            for(String termKey: termMap.keySet()) {
                service.processUpdate(termMap.get(termKey));
                ph.progressProcess();
            }
            ph.finishProcess();

        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }

        return "OK";
    }

}
