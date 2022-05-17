import { BaseAuthService } from './BaseAuthService';

export class ReportService extends BaseAuthService {
    createGroup(newGroup) {
        return this.api.post(`/curationreportgroup`, newGroup);
    }

    deleteGroup(id) {
        return this.api.delete(`/curationreportgroup/${id}`);
    }

    createReport(newReport) {
        // let endpoint = newReport.type.toLowerCase();

        newReport.curationReportGroup = { id: newReport.curationReportGroup };
        for (const objectKey in newReport) {
            if (!newReport[objectKey]) {
                delete newReport[objectKey];
            }
        }
        console.log("Creating: ");
        console.log(newReport);
        return this.api.post(`/curationreport`, newReport);
    }

    updateReport(newReport) {


        newReport.group = { id: newReport.group };
        for (const objectKey in newReport) {
            if (!newReport[objectKey]) {
                delete newReport[objectKey];
            }
        }
        console.log("Saving: ");
        console.log(newReport);
        return this.api.put(`/curationreport`, newReport);
    }

    deleteReport(id) {
        return this.api.delete(`/curationreport/${id}`);
    }
  
  //not sure this is needed for reports
    /* restartLoad(loadType, id) {
        let endpoint = loadType.toLowerCase();
        return this.api.get(`/${endpoint}/restart/${id}`);
    }
    restartLoadFile(id) {
      return this.api.get(`/bulkloadfile/restart/${id}`);
    }
 */
    getFileHistoryFile(id) {
      return this.api.get(`/curationreporthistory/${id}`);
    }

    deleteLoadFile(id) {
      return this.api.delete(`/bulkloadfile/${id}`);
    }

  //Do the reports need these as well?
    getBackendBulkLoadTypes(loadType) {
        const bulkLoadTypes = {
            BulkFMSLoad: ["MOLECULE"],
            BulkURLLoad: ["ONTOLOGY", "GENE", "ALLELE", "AGM", "DISEASE_ANNOTATION"],
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
