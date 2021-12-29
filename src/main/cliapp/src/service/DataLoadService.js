import axios from 'axios';

export class DataLoadService {

    createGroup(newGroup) {
        return axios.post(`api/bulkloadgroup`, newGroup);
    }

    createLoad(newLoad) {
        const loadType = newLoad.loadType;
        delete newLoad.loadType;
        newLoad.group = {id: newLoad.group.id};
        return axios.post(`api/bulk${loadType}load`, newLoad);
    }


    getBackendBulkLoadTypes() {
        return [
            "ONTOLOGY_DTO", "GENE_DTO", "ALLELE_DTO", "AGM_DTO", "DISEASE_ANNOTATION_DTO",
            "ONTOLOGY", "GENE", "ALLELE", "AGM", "DISEASE_ANNOTATION"
        ];
    }

    
    getLoadTypes() {
        return [
            "fms", "url", "manual"
        ];
    }
}
