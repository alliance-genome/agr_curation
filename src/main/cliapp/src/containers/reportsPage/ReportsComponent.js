import React, { useReducer, useRef, useState } from 'react';
import { useQuery } from 'react-query';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

import { SearchService } from '../../service/SearchService';
import { ReportService } from '../../service/ReportService';
import { Messages } from 'primereact/messages';
import { Button } from 'primereact/button';
import { NewReportForm } from './NewReportForm';
import { NewReportGroupForm } from './NewReportGroupForm';
import { HistoryDialog } from './HistoryDialog';
import { useQueryClient } from 'react-query';

export const ReportsComponent = () => {

  const reportReducer = (state, action) => {
    switch (action.type) {
      case 'EDIT':
        return { ...action.editReport };
      case 'RESET':
        return { name: "", cronSchedule: "" };
      default:
        return { ...state, [action.field]: action.value };
    }
  };

  const [groups, setGroups] = useState({});
  const [history, setHistory] = useState({ id: 0 });
  const [reportGroupDialog, setReportGroupDialog] = useState(false);
  const [historyDialog, setHistoryDialog] = useState(false);
  const [reportDialog, setReportDialog] = useState(false);
  const [expandedGroupRows, setExpandedGroupRows] = useState(null);
  const [expandedLoadRows, setExpandedLoadRows] = useState(null);
  const [expandedFileRows, setExpandedFileRows] = useState(null);
  const [disableFormFields, setDisableFormFields] = useState(false);
  const errorMessage = useRef(null);
  const searchService = new SearchService();

  const [newReport, reportDispatch] = useReducer(reportReducer, {});

  const queryClient = useQueryClient();

  let reportService = null;

  const handleNewReportGroupOpen = () => {
    setReportGroupDialog(true);
  };

  const handleNewReportOpen = () => {
    reportDispatch({ type: "RESET" });
    setReportDialog(true);
  };

  useQuery(['reporttable'],
    () => searchService.find('curationreportgroup', 100, 0, {}), {
    onSuccess: (data) => {
      if (data.results) {
        for (let group of data.results) {
          if (group.curationReports) {
            for (let report of group.curationReports) {
              report.curationReportGroup = group.id;
            }
          }
        }
        setGroups(data.results);
      }
    },
    keepPreviousData: true,
    refetchOnWindowFocus: false
  });

  const getService = () => {
    if (!reportService) {
      reportService = new ReportService();
    }
    return reportService;
  }

  /* const urlTemplate = (rowData) => {
    return <a href={rowData.s3Url}>Download</a>;
  };
 */
  const refresh = () => {
    queryClient.invalidateQueries('bulkloadtable');
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

  const editReport = (rowData) => {
    reportDispatch({ type: "EDIT", editReport: rowData });
    setReportDialog(true);
    setDisableFormFields(true);
  };

  /* const deleteLoadFile = (rowData) => {
    getService().deleteLoadFile(rowData.id).then(response => {
      queryClient.invalidateQueries('bulkloadtable');
    });
  }; */

  const deleteReport = (rowData) => {
    getService().deleteReport(rowData.id).then(() => {
      queryClient.invalidateQueries('reporttable');
    });
  };

  const deleteGroup = (rowData) => {
    getService().deleteGroup(rowData.id).then(() => {
      queryClient.invalidateQueries('reporttable');
    });
  };

  /* const showHistory = (rowData) => {
    setHistory(rowData);
    setHistoryDialog(true);
  }; */


  /* const historyActionBodyTemplate = (rowData) => {
    return <Button icon="pi pi-search-plus" className="p-button-rounded p-button-info mr-2" onClick={() => showHistory(rowData)} />
  };

  const loadFileActionBodyTemplate = (rowData) => {
    let ret = [];

    if (!rowData.status || rowData.status === "FINISHED" || rowData.status === "FAILED" || rowData.status === "STOPPED") {
      ret.push(<Button key="run" icon="pi pi-play" className="p-button-rounded p-button-success mr-2" onClick={() => runLoadFile(rowData)} />);
    }
    if (!rowData.status || rowData.status === "FINISHED" || rowData.status === "FAILED" || rowData.status === "STOPPED") {
      ret.push(<Button key="delete" icon="pi pi-trash" className="p-button-rounded p-button-danger mr-2" onClick={() => deleteLoadFile(rowData)} />);
    }

    return ret;

  }; */

  const reportActionBodyTemplate = (rowData) => {
    let buttons = [];

    buttons.push(<Button key="edit" icon="pi pi-pencil" className="p-button-rounded p-button-warning mr-2" onClick={() => editReport(rowData)} />);

    //not sure how status will be handled in the reports or if these are valid states
    // if (!rowData.curationReportStatus || rowData.curationReportStatus === "FINISHED" || rowData.curationReportStatus === "FAILED" || rowData.curationReportStatus === "STOPPED") {
      // buttons.push(<Button key="run" icon="pi pi-play" className="p-button-rounded p-button-success mr-2" onClick={() => runLoad(rowData)} />);
    // }

    if (!rowData.reportFiles || rowData.reportFiles.length === 0) {
      buttons.push(<Button key="delete" icon="pi pi-trash" className="p-button-rounded p-button-danger mr-2" onClick={() => deleteReport(rowData)} />);
    }

    return buttons;
  };

  const groupActionBodyTemplate = (rowData) => {
    if (!rowData.curationReports || rowData.curationReports.length === 0) {
      return (<Button icon="pi pi-trash" className="p-button-rounded p-button-danger mr-2" onClick={() => deleteGroup(rowData)} />);
    }
  };

  const nameBodyTemplate = (rowData) => {
    return (
      <React.Fragment>
        {rowData.name}
      </React.Fragment>
    );
  };

  /* const urlBodyTemplate = (rowData) => {
    if (rowData.url) {
      return <a href={rowData.url}>Source Download URL</a>;
    }
  }; */

  /* const backendBulkLoadTypeTemplate = (rowData) => {
    if (rowData.backendBulkLoadType === 'ONTOLOGY') {
      return rowData.backendBulkLoadType + "(" + rowData.ontologyType + ")";
    } else {
      return rowData.backendBulkLoadType;
    }
  }; */

  const scheduleActiveTemplate = (rowData) => {
    return (
      <div>
        {rowData.scheduleActive ? "true" : "false"}
      </div>
    );
  };




  /* const dynamicColumns = (loads) => {

    let showFMSLoad = false;
    let showURLLoad = false;
    let showManualLoad = false;

    let ret = [];

    if (loads) {
      for (const load of loads) {
        if (load.type === "BulkFMSLoad") showFMSLoad = true;
        if (load.type === "BulkURLLoad") showURLLoad = true;
        if (load.type === "BulkManualLoad") showManualLoad = true;
      }
    }

    if (showFMSLoad || showURLLoad) {
      ret.push(<Column key="scheduleActive" field="scheduleActive" header="Schedule Active" body={scheduleActiveTemplate} />);
      ret.push(<Column key="cronSchedule" field="cronSchedule" header="Cron Schedule" />);
      ret.push(<Column key="nextRun" field="nextRun" header="Next Run" />);
      if (showFMSLoad) {
        ret.push(<Column key="dataType" field="dataType" header="FMS Data Type" />);
        ret.push(<Column key="dataSubType" field="dataSubType" header="FMS Data Sub Type" />);
      }
      if (showURLLoad) {
        ret.push(<Column key="url" body={urlBodyTemplate} header="Data URL" />);
      }
    }
    if (showManualLoad) {
      ret.push(<Column key="dataType2" field="dataType" header="Load Data Type" />);
    }

    return ret;
  }; */

  const statusTemplate = (rowData) => {
    let styleClass = 'p-button-text p-button-plain';
    if (rowData.status === 'FAILED') { styleClass = "p-button-danger"; }
    if (rowData.status && (
      rowData.status.endsWith('STARTED') ||
      rowData.status.endsWith('RUNNING') ||
      rowData.status.endsWith('PENDING')
    )) { styleClass = "p-button-success"; }

    return (
      <Button label={rowData.status} tooltip={rowData.errorMessage} className={`p-button-rounded ${styleClass}`} />
    );
  };

  /* const hisotryTable = (file) => {
    return (
      <div className="card">
        <DataTable key="historyTable" value={file.history} responsiveLayout="scroll">

          <Column field="loadStarted" header="Load Started" />
          <Column field="loadFinished" header="Load Finished" />
          <Column field="completedRecords" header="Records Completed" />
          <Column field="failedRecords" header="Records Failed" />
          <Column field="totalRecords" header="Total Records" />
          <Column body={historyActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
        </DataTable>
      </div>
    );
  }; */

  /* const fileTable = (load) => {
    return (
      <div className="card">
        <DataTable key="fileTable" value={load.loadFiles} responsiveLayout="scroll"
          expandedRows={expandedFileRows} onRowToggle={(e) => setExpandedFileRows(e.data)}
          rowExpansionTemplate={hisotryTable} dataKey="id">
          <Column expander style={{ width: '3em' }} />
          <Column field="md5Sum" header="MD5 Sum" />
          <Column field="fileSize" header="Compressed File Size" />
          <Column field="recordCount" header="Record Count" />
          <Column field="s3Url" header="S3 Url (Download)" body={urlTemplate} />
          <Column field="dateUpdated" header="Last Loaded" />
          <Column field="status" body={statusTemplate} header="Status" />
          <Column body={loadFileActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
        </DataTable>
      </div>
    );
  }; */


  const reportTable = (group) => {
    return (
      <div className="card">
        <DataTable key="reportTable" value={group.curationReports} responsiveLayout="scroll"
        // expandedRows={expandedLoadRows} onRowToggle={(e) => setExpandedLoadRows(e.data)}
        // rowExpansionTemplate={fileTable} dataKey="id"
        >
          <Column expander style={{ width: '3em' }} />
          <Column field="name" header="Name" />
          <Column key="scheduleActive" field="scheduleActive" header="Schedule Active" body={scheduleActiveTemplate} />
          <Column key="cronSchedule" field="cronSchedule" header="Cron Schedule" />
          <Column field="status" body={statusTemplate} header="Status" />
          <Column key="reportAction" body={reportActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
      {/* <Column field="curationreportgroup.name" header="Curation Report Group" />
            /*dynamicColumns(group.reports)*/}
        </DataTable>
      </div>
    );
  };

  return (
    <div className="card">
      <Button label="New Group" icon="pi pi-plus" className="p-button-success mr-2" onClick={handleNewReportGroupOpen} />
      <Button label="New Report" icon="pi pi-plus" className="p-button-success mr-2" onClick={handleNewReportOpen} />
      <Button label="Refresh Data" icon="pi pi-plus" className="p-button-success mr-2" onClick={refresh} />
      <h3>Reports Table</h3>
      <Messages ref={errorMessage} />
      <DataTable key="groupTable"
        value={groups} className="p-datatable-sm"
        expandedRows={expandedGroupRows} onRowToggle={(e) => setExpandedGroupRows(e.data)}
        rowExpansionTemplate={reportTable}
        dataKey="id">
        <Column expander style={{ width: '3em' }} />
        <Column body={nameBodyTemplate} header="Group Name" />
        <Column body={groupActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
      </DataTable>
      <NewReportForm
        reportDialog={reportDialog}
        setReportDialog={setReportDialog}
        newReport={newReport}
        reportDispatch={reportDispatch}
        groups={groups}
        disableFormFields={disableFormFields}
        setDisableFormFields={setDisableFormFields}
        reportService={reportService}
      />
      <NewReportGroupForm
        reportGroupDialog={reportGroupDialog}
        setReportGroupDialog={setReportGroupDialog}
      />
      <HistoryDialog
        historyDialog={historyDialog}
        setHistoryDialog={setHistoryDialog}
        dataLoadService={getService()}
        history={history}
      />
    </div>
  );
};
