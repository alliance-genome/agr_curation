import axios from 'axios';

export class DataLoadService {

    createGroup(newGroup) {
        return axios.post(`api/bulkloadgroup`, newGroup)
    }

    createLoad(newLoad) {
        return axios.post(`api/bulk${newLoad.loadType}load`, newLoad)
    }


    getBackendBulkLoadTypes() {
        return [
            "ONTOLOGY", "GENE", "ALLELE", "AGM", "DISEASE_ANNOTATION"
        ];
    }

    
    getLoadTypes() {
        return [
            "fms", "url", "manual"
        ];
    }
}
