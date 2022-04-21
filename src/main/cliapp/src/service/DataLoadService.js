import axios from 'axios';
import { BaseAuthService } from './BaseAuthService';

export class DataLoadService extends BaseAuthService {
    //eslint-disable-next-line
    constructor(authState) {
      super(authState);
    }

    createGroup(newGroup) {
        return axios.post(`api/bulkloadgroup`, newGroup, this.apiAuthHeader);
    }

    deleteGroup(id) {
        return axios.delete(`api/bulkloadgroup/${id}`, this.apiAuthHeader);
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
        return axios.post(`api/${endpoint}`, newLoad, this.apiAuthHeader);
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
        return axios.put(`api/${endpoint}`, newLoad, this.apiAuthHeader);
    }

    deleteLoad(loadType, id) {
        let endpoint = loadType.toLowerCase();
        return axios.delete(`api/${endpoint}/${id}`, this.apiAuthHeader);
    }

    restartLoad(loadType, id) {
        let endpoint = loadType.toLowerCase();
        return axios.get(`api/${endpoint}/restart/${id}`, this.apiAuthHeader);
    }

    restartLoadFile(id) {
      return axios.get(`api/bulkloadfile/restart/${id}`, this.apiAuthHeader);
    }

    getFileHistoryFile(id) {
      return axios.get(`api/bulkloadfilehistory/${id}`);
    }

    deleteLoadFile(id) {
      return axios.delete(`api/bulkloadfile/${id}`, this.apiAuthHeader);
    }

    getBackendBulkLoadTypes(loadType) {
        const bulkLoadTypes = {
            BulkFMSLoad: ["GENE_DTO", "ALLELE_DTO", "AGM_DTO", "DISEASE_ANNOTATION_DTO", "MOLECULE"],
            BulkURLLoad: ["GENE_DTO", "ALLELE_DTO", "AGM_DTO", "DISEASE_ANNOTATION_DTO", "ONTOLOGY", "GENE", "ALLELE", "AGM", "DISEASE_ANNOTATION"],
            BulkManualLoad: ["FULL_INGEST", "DISEASE_ANNOTATION", "GENE_DISEASE_ANNOTATION", "ALLELE_DISEASE_ANNOTATION", "AGM_DISEASE_ANNOTATION", "GENE", "ALLELE", "AGM" ]
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
            "ECO", "ZFA", "DO", "MA", "CHEBI", "XCO", "MP", "DAO", "ZECO", "WBBT", "EMAPA", "GO", "SO", "WBLS", "FBDV", "MMUSDV", "ZFS", "XBA", "XBS", "XPO", "XBED", "XSMO"
        ];
    }

    getDataTypes() {
        return [
            "RGD", "MGI", "SGD", "HUMAN", "ZFIN", "FB", "WB"
        ];
    }


}
