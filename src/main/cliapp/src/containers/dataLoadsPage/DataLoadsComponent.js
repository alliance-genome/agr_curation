import React, { useReducer, useRef, useState } from 'react';
import { useQuery } from 'react-query';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

import { SearchService } from '../../service/SearchService';
import { DataLoadService } from '../../service/DataLoadService';
import { Messages } from 'primereact/messages';
import { Button } from 'primereact/button';
import { NewBulkLoadForm } from './NewBulkLoadForm';
import { NewBulkLoadGroupForm } from './NewBulkLoadGroupForm';
import { useQueryClient } from 'react-query';


export const DataLoadsComponent = () => {

  const bulkLoadReducer = (state, action) => {
    switch (action.type) {
      case 'EDIT':
        return { ...action.editBulkLoad };
      case 'RESET':
        return { name: "" };
      default:
        return { ...state, [action.field]: action.value };
    }
  };

  const [groups, setGroups] = useState({});
  const [bulkLoadGroupDialog, setBulkLoadGroupDialog] = useState(false);
  const [bulkLoadDialog, setBulkLoadDialog] = useState(false);
  const [expandedGroupRows, setExpandedGroupRows] = useState(null);
  const [expandedLoadRows, setExpandedLoadRows] = useState(null);
  const errorMessage = useRef(null);
  const searchService = new SearchService();
  const dataLoadService = new DataLoadService();

  const [newBulkLoad, bulkLoadDispatch] = useReducer(bulkLoadReducer, {});

  const queryClient = useQueryClient();

  const handleNewBulkLoadGroupOpen = (event) => {
    setBulkLoadGroupDialog(true);
  };

  const handleNewBulkLoadOpen = (event) => {
    bulkLoadDispatch({ type: "RESET" });
    setBulkLoadDialog(true);
  };

  useQuery(['bulkloadtable'],
    () => searchService.find('bulkloadgroup', 100, 0, {}), {
    onSuccess: (data) => {
      for (let group of data.results) {
        if (group.loads) {
          for (let load of group.loads) {
            load.group = group.id;
          }
        }
      }
      setGroups(data.results);
    },
    keepPreviousData: true,
    refetchOnWindowFocus: false
  });

  const urlTemplate = (rowData) => {
    return <a href={rowData.s3Url}>Download</a>;
  };

  const refresh = () => {
    queryClient.invalidateQueries('bulkloadtable');
  };

  const runLoad = (rowData) => {
    dataLoadService.restartLoad(rowData.type, rowData.id).then(response => {
      queryClient.invalidateQueries('bulkloadtable');
    });
  };

  const runLoadFile = (rowData) => {
    dataLoadService.restartLoadFile(rowData.id).then(response => {
      queryClient.invalidateQueries('bulkloadtable');
    });
  };

  const editLoad = (rowData) => {
    bulkLoadDispatch({ type: "EDIT", editBulkLoad: rowData });
    setBulkLoadDialog(true);
  };

  const deleteLoadFile = (rowData) => {
    dataLoadService.deleteLoadFile(rowData.id).then(response => {
      queryClient.invalidateQueries('bulkloadtable');
    });
  };

  const deleteLoad = (rowData) => {
    dataLoadService.deleteLoad(rowData.type, rowData.id).then(response => {
      queryClient.invalidateQueries('bulkloadtable');
    });
  };

  const deleteGroup = (rowData) => {
    dataLoadService.deleteGroup(rowData.id).then(response => {
      queryClient.invalidateQueries('bulkloadtable');
    });
  };

  const loadFileActionBodyTemplate = (rowData) => {
    let ret = [];

    if(!rowData.status || rowData.status === "FINISHED" || rowData.status === "FAILED") {
      ret.push(<Button icon="pi pi-play" className="p-button-rounded p-button-success p-mr-2" onClick={() => runLoadFile(rowData)} />);
    }
    if(!rowData.status || rowData.status === "FINISHED" || rowData.status === "FAILED") {
      ret.push(<Button icon="pi pi-trash" className="p-button-rounded p-button-danger p-mr-2" onClick={() => deleteLoadFile(rowData)} />);
    }

    return ret;

  };

  const loadActionBodyTemplate = (rowData) => {
    let ret = [];

    ret.push(<Button icon="pi pi-pencil" className="p-button-rounded p-button-warning p-mr-2" onClick={() => editLoad(rowData)} />);

    if (!rowData.status || rowData.status === "FINISHED" || rowData.status === "FAILED") {
      ret.push(<Button icon="pi pi-play" className="p-button-rounded p-button-success p-mr-2" onClick={() => runLoad(rowData)} />);
    }

    if (!rowData.loadFiles || rowData.loadFiles.length === 0) {
      ret.push(<Button icon="pi pi-trash" className="p-button-rounded p-button-danger p-mr-2" onClick={() => deleteLoad(rowData)} />);
    }

    return ret;
  };

  const groupActionBodyTemplate = (rowData) => {
    if (!rowData.loads || rowData.loads.length === 0) {
      return (<Button icon="pi pi-trash" className="p-button-rounded p-button-danger p-mr-2" onClick={() => deleteGroup(rowData)} />);
    }
  };

  const nameBodyTemplate = (rowData) => {
    return (
      <React.Fragment>
        {rowData.name}
      </React.Fragment>
    );
  };

  const urlBodyTemplate = (rowData) => {
    if (rowData.url) {
      return <a href={rowData.url}>Source Download URL</a>;
    }
  };

  const backendBulkLoadTypeTemplate = (rowData) => {
    if (rowData.backendBulkLoadType === 'ONTOLOGY') {
      return rowData.backendBulkLoadType + "(" + rowData.ontologyType + ")";
    } else {
      return rowData.backendBulkLoadType;
    }
  };

  const scheduleActiveTemplate = (rowData) => {
    return (
      <>
        {rowData.scheduleActive ? "true" : "false"}
      </>
    );
  };



  const fileTable = (load) => {
    return (
      <div className="card">
        <DataTable key="fileTable" value={load.loadFiles} responsiveLayout="scroll">
          <Column field="md5Sum" header="MD5 Sum" />
          <Column field="fileSize" header="File Size" />
          <Column field="recordCount" header="Record Count" />
          <Column field="s3Url" header="S3 Url (Download)" body={urlTemplate} />
          <Column field="lastUpdated" header="Last Loaded" />
          <Column field="status" body={statusTemplate} header="Status" />
          <Column body={loadFileActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
        </DataTable>
      </div>
    );
  };

  const dynamicColumns = (loads) => {

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
      ret.push(<Column key="dataType" field="dataType" header="Load Data Type" />);
    }

    return ret;
  };

  const statusTemplate = (rowData) => {
    let styleClass = 'p-button-text p-button-plain';
    if (rowData.status === 'FAILED') { styleClass = "p-button-danger"; }
    if (rowData.status === 'STARTED' || rowData.status === 'RUNNING') { styleClass = "p-button-success"; }

    return (
      <Button label={rowData.status} className={`p-button-rounded ${styleClass}`} />
    );
  };

  const loadTable = (group) => {
    return (
      <div className="card">
        <DataTable key="loadTable" value={group.loads} responsiveLayout="scroll"
          expandedRows={expandedLoadRows} onRowToggle={(e) => setExpandedLoadRows(e.data)}
          rowExpansionTemplate={fileTable} dataKey="id">
          <Column expander style={{ width: '3em' }} />
          <Column body={nameBodyTemplate} header="Load Name" />
          <Column field="type" header="Bulk Load Type" />
          <Column field="backendBulkLoadType" body={backendBulkLoadTypeTemplate} header="Backend Bulk Load Type" />
          {dynamicColumns(group.loads)}
          <Column field="status" body={statusTemplate} header="Status" />
          <Column key="loadAction" body={loadActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
        </DataTable>
      </div>
    );
  };

  return (
    <div className="card">
      <Button label="New Group" icon="pi pi-plus" className="p-button-success p-mr-2" onClick={handleNewBulkLoadGroupOpen} />
      <Button label="New Bulk Load" icon="pi pi-plus" className="p-button-success p-mr-2" onClick={handleNewBulkLoadOpen} />
      <Button label="Refresh Data" icon="pi pi-plus" className="p-button-success p-mr-2" onClick={refresh} />
      <h3>Data Loads Table</h3>
      <Messages ref={errorMessage} />
      <DataTable key="groupTable"
        value={groups} className="p-datatable-sm"
        expandedRows={expandedGroupRows} onRowToggle={(e) => setExpandedGroupRows(e.data)}
        rowExpansionTemplate={loadTable} dataKey="id">
        <Column expander style={{ width: '3em' }} />
        <Column body={nameBodyTemplate} header="Group Name" />
        <Column body={groupActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
      </DataTable>
      <NewBulkLoadForm
        bulkLoadDialog={bulkLoadDialog}
        setBulkLoadDialog={setBulkLoadDialog}
        newBulkLoad={newBulkLoad}
        bulkLoadDispatch={bulkLoadDispatch}
        groups={groups}
      />
      <NewBulkLoadGroupForm
        bulkLoadGroupDialog={bulkLoadGroupDialog}
        setBulkLoadGroupDialog={setBulkLoadGroupDialog}
      />
    </div>
  );
};
