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

        newLoad.group = { id: newLoad.group };
        for (const objectKey in newLoad) {
            if (!newLoad[objectKey]) {
                delete newLoad[objectKey];
            }
        }
        console.log("Creating: ");
        console.log(newLoad);
        return axios.post(`api/${endpoint}`, newLoad);
    }

    updateLoad(newLoad) {

        let endpoint = newLoad.type.toLowerCase();

        newLoad.group = { id: newLoad.group };
        for (const objectKey in newLoad) {
            if (!newLoad[objectKey]) {
                delete newLoad[objectKey];
            }
        }
        delete newLoad["loadFiles"];
        console.log("Saving: ");
        console.log(newLoad);
        return axios.put(`api/${endpoint}`, newLoad);
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

    deleteLoadFile(id) {
      console.log(id);
      return axios.delete(`api/bulkloadfile/${id}`);
    }

    getBackendBulkLoadTypes(loadType) {
        const bulkLoadTypes = {
            BulkFMSLoad: ["GENE_DTO", "ALLELE_DTO", "AGM_DTO", "DISEASE_ANNOTATION_DTO", "MOLECULE"],
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
            "ECO", "ZFA", "DO", "MA", "CHEBI", "XCO", "MP", "DAO", "ZECO", "WBBT", "EMAPA", "GO"
        ];
    }

    getDataTypes() {
        return [
            "RGD", "MGI", "SGD", "HUMAN", "ZFIN", "FB", "WB"
        ];
    }

}
