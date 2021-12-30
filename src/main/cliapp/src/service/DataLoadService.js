import axios from 'axios';

export class DataLoadService {

    createGroup(newGroup) {
        return axios.post(`api/bulkloadgroup`, newGroup);
    }

    deleteGroup(id) {
        return axios.delete(`api/bulkloadgroup/${id}`);
    }

    createLoad(newLoad) {
        let endpoint = newLoad.type.toLowerCase();
        newLoad.group = { id: newLoad.group.id };
        newLoad.scheduleActive = newLoad.scheduleActive.name;
        for (const load in newLoad) {
            if (!newLoad[load]) {
                delete newLoad[load];
            }
        }
        return axios.post(`api/${endpoint}`, newLoad);
    }

    deleteLoad(loadType, id) {
        let endpoint = loadType.toLowerCase();
        return axios.delete(`api/${endpoint}/${id}`);
    }

    restartLoad(loadType, id) {
        let endpoint = loadType.toLowerCase();
        return axios.get(`api/${endpoint}/restart/${id}`);
    }

    restartLoadFile(id) {
        return axios.get(`api/bulkloadfile/restart/${id}`);
    }

    getBackendBulkLoadTypes(loadType) {
        const bulkLoadTypes = {
            BulkFMSLoad: ["GENE_DTO", "ALLELE_DTO", "AGM_DTO", "DISEASE_ANNOTATION_DTO"],
            BulkURLLoad: ["GENE_DTO", "ALLELE_DTO", "AGM_DTO", "DISEASE_ANNOTATION_DTO", "ONTOLOGY", "GENE", "ALLELE", "AGM", "DISEASE_ANNOTATION"],
            BulkManualLoad: ["GENE", "ALLELE", "AGM", "DISEASE_ANNOTATION"]
        };
        return bulkLoadTypes[loadType];
    }

    getLoadTypes() {
        return [
            "BulkFMSLoad", "BulkURLLoad", "BulkManualLoad"
        ];
    }

    getOntologyTypes() {
        return [
            "ECO","ZFA","DO","MA", "CHEBI", "XCO", "MP", "DAO", "ZECO", "WBBT", "EMAPA", "GO"
        ];
    }

}
