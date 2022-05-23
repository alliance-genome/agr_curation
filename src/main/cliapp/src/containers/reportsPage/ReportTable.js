import React from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';

export const ReportTable = ({ curationReports, getService, setReport, setReportDialog, reportDispatch, setNewReportDialog, queryClient }) => {

  const handleReportOpen = async (rowData) => {
    getService().getReport(rowData.id).then((res) => {
      const { data: { entity } } = res;
      setReport(entity);
      setReportDialog(true);
    });
  };

  const handleReportEdit = (rowData) => {
    reportDispatch({ type: "EDIT", editReport: rowData });
    setNewReportDialog(true);
  };

  const deleteReport = (rowData) => {
    getService().deleteReport(rowData.id).then(() => {
      queryClient.invalidateQueries('reporttable');
    });
  };

  /* const runLoad = (rowData) => {
    getService().restartLoad(rowData.type, rowData.id).then(response => {
      queryClient.invalidateQueries('bulkloadtable');
    });
  }; */

  /* const runLoadFile = (rowData) => {
    getService().restartLoadFile(rowData.id).then(response => {
      queryClient.invalidateQueries('bulkloadtable');
    });
  }; */

  const reportActionBodyTemplate = (rowData) => {
    let buttons = [];

    buttons.push(<Button key="openDialog" icon="pi pi-search-plus" className="p-button-rounded p-button-info mr-2" onClick={() => handleReportOpen(rowData)} />);
    buttons.push(<Button key="edit" icon="pi pi-pencil" className="p-button-rounded p-button-warning mr-2" onClick={() => handleReportEdit(rowData)} />);

    // if (!rowData.curationReportStatus || rowData.curationReportStatus === "FINISHED" || rowData.curationReportStatus === "FAILED" || rowData.curationReportStatus === "STOPPED") {
      // buttons.push(<Button key="run" icon="pi pi-play" className="p-button-rounded p-button-success mr-2" onClick={() => runLoad(rowData)} />);
    // }

    if (!rowData.reportFiles || rowData.reportFiles.length === 0) {
      buttons.push(<Button key="delete" icon="pi pi-trash" className="p-button-rounded p-button-danger mr-2" onClick={() => deleteReport(rowData)} />);
    }

    return buttons;
  };

  const statusTemplate = (rowData) => {
    let styleClass = 'p-button-text p-button-plain';
    if (rowData.curationReportStatus === 'FAILED') { styleClass = "p-button-danger"; }
    if (rowData.status && (
      rowData.curationReportStatus.endsWith('STARTED') ||
      rowData.curationReportStatus.endsWith('RUNNING') ||
      rowData.curationReportStatus.endsWith('PENDING')
    )) { styleClass = "p-button-success"; }

    return (
      <Button label={rowData.curationReportStatus} tooltip={rowData.errorMessage} className={`p-button-rounded ${styleClass}`} />
    );
  };

  const scheduleActiveTemplate = (rowData) => {
    return (
      <>
        {rowData.scheduleActive ? "true" : "false"}
      </>
    );
  };
    return (
      <div className="card">
        <DataTable key="reportTable" value={curationReports} responsiveLayout="scroll"
        >
          <Column field="name" header="Name" />
          <Column key="scheduleActive" field="scheduleActive" header="Schedule Active" body={scheduleActiveTemplate} />
          <Column key="cronSchedule" field="cronSchedule" header="Cron Schedule" />
          <Column field="curationReportStatus" body={statusTemplate} header="Status" />
          <Column field="birtReportFilePath" header="BIRT Report File Path" />
          <Column key="reportAction" body={reportActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
        </DataTable>
      </div>
    );
};
