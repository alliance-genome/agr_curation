import { BaseAuthService } from './BaseAuthService';

export class ReportService extends BaseAuthService {
    createGroup(newGroup) {
        return this.api.post(`/curationreportgroup`, newGroup);
    }

    deleteGroup(id) {
        return this.api.delete(`/curationreportgroup/${id}`);
    }

    createReport(newReport) {
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
        newReport.curationReportGroup = { id: newReport.curationReportGroup };
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
  
    restartReport(id) {
      return this.api.get(`/curationreport/restart/${id}`);
    }

    getReport(id) {
      return this.api.get(`/curationreport/${id}`);
    }

    getFileHistoryFile(id) {
      return this.api.get(`/curationreporthistory/${id}`);
    }

}
