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
        for(const load in newLoad){
            if(!newLoad[load]){
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

    getBackendBulkLoadTypes() {
        return [
            "GENE_DTO", "ALLELE_DTO", "AGM_DTO", "DISEASE_ANNOTATION_DTO",
            "ONTOLOGY", "GENE", "ALLELE", "AGM", "DISEASE_ANNOTATION"
        ];
    }

    getLoadTypes() {
        return [
            "BulkFMSLoad", "BulkURLLoad", "BulkManualLoad"
        ];
    }

}
