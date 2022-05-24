import React from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { StatusTemplate } from './StatusTemplate';

export const ReportTable = ({ curationReports, getService, setReport, setReportDialog, reportDispatch, setNewReportDialog, queryClient, toast }) => {

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
    }).catch((error) => {
        toast.current.show([
          { life: 7000, severity: 'error', summary: 'Update error: ', detail: error.response.data.details, sticky: false }
        ]);
      console.warn(error.response.data);
    });
  };

  const runReport = (rowData) => {
    getService().restartReport(rowData.id).then(() => {
      queryClient.invalidateQueries('reporttable');
    });
  };

  const PlayButton = ({ rowData }) => {
    if (!rowData.curationReportStatus || rowData.curationReportStatus === "FINISHED" || rowData.curationReportStatus === "FAILED" || rowData.curationReportStatus === "STOPPED") {
      return (
        <div className='col-2'>
          <Button key="run" icon="pi pi-play" className="p-button-rounded p-button-success mr-2" onClick={() => runReport(rowData)} />
        </div>
      );
    }else {
      return null;
    };
  };

  const reportActionBodyTemplate = (rowData) => {
    let buttons = [];

    buttons.push(<Button key="openDialog" icon="pi pi-search-plus" className="p-button-rounded p-button-info mr-2" onClick={() => handleReportOpen(rowData)} />);
    buttons.push(<Button key="edit" icon="pi pi-pencil" className="p-button-rounded p-button-warning mr-2" onClick={() => handleReportEdit(rowData)} />);

    if (!rowData.curationReportStatus || rowData.curationReportStatus === "FINISHED" || rowData.curationReportStatus === "FAILED" || rowData.curationReportStatus === "STOPPED") {
      buttons.push(<Button key="run" icon="pi pi-play" className="p-button-rounded p-button-success mr-2" onClick={() => runReport(rowData)} />);
    }

    if (!rowData.reportFiles || rowData.reportFiles.length === 0) {
      buttons.push(<Button key="delete" icon="pi pi-trash" className="p-button-rounded p-button-danger mr-2" onClick={() => deleteReport(rowData)} />);
    }

    return (
      <div className='grid'>
        <div className='col-2'>
          <Button key="openDialog" icon="pi pi-search-plus" className="p-button-rounded p-button-info mr-2" onClick={() => handleReportOpen(rowData)} />
        </div>
        <div className='col-2'>
          <Button key="edit" icon="pi pi-pencil" className="p-button-rounded p-button-warning mr-2" onClick={() => handleReportEdit(rowData)} />
        </div>
        <PlayButton rowData={rowData} />
        <div className='col-2'>
          <Button key="delete" icon="pi pi-trash" className="p-button-rounded p-button-danger mr-2" onClick={() => deleteReport(rowData)} />
        </div>
      </div>
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
          <Column field="curationReportStatus" body={(rowData) => <StatusTemplate rowData={rowData}/>} header="Status" />
          <Column field="birtReportFilePath" header="BIRT Report File Path" />
          <Column key="reportAction" body={reportActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
        </DataTable>
      </div>
    );
};
