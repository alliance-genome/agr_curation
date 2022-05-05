import { BaseAuthService } from './BaseAuthService';

export class DataLoadService extends BaseAuthService {
    //eslint-disable-next-line
    constructor(authState) {
      super(authState);
    }

    createGroup(newGroup) {
        return this.api.post(`/bulkloadgroup`, newGroup);
    }

    deleteGroup(id) {
        return this.api.delete(`api/bulkloadgroup/${id}`);
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
        return this.api.post(`api/${endpoint}`, newLoad);
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
        return this.api.put(`api/${endpoint}`, newLoad);
    }

    deleteLoad(loadType, id) {
        let endpoint = loadType.toLowerCase();
        return this.api.delete(`api/${endpoint}/${id}`);
    }

    restartLoad(loadType, id) {
        let endpoint = loadType.toLowerCase();
        return this.api.get(`api/${endpoint}/restart/${id}`);
    }

    restartLoadFile(id) {
      return this.api.get(`api/bulkloadfile/restart/${id}`);
    }

    getFileHistoryFile(id) {
      return this.api.get(`api/bulkloadfilehistory/${id}`);
    }

    deleteLoadFile(id) {
      return this.api.delete(`api/bulkloadfile/${id}`);
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
            "ECO", "ZFA", "DO", "MA", "CHEBI", "XCO", "MP", "DAO", "ZECO", "WBBT", "EMAPA", "GO", "SO", "WBLS", "FBDV", "MMUSDV", "ZFS", "XBA_XBS", "XPO", "XBED", "XSMO"
        ];
    }

    getDataTypes() {
        return [
            "RGD", "MGI", "SGD", "HUMAN", "ZFIN", "FB", "WB"
        ];
    }


}
