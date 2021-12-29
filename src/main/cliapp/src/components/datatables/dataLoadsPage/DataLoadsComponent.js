import React, { useRef, useState } from 'react';
import { useQuery } from 'react-query';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

import { SearchService } from '../../../service/SearchService';
import { DataLoadService } from '../../../service/DataLoadService';
import { Messages } from 'primereact/messages';
import { Button } from 'primereact/button';
import NewBulkLoadForm from './NewBulkLoadForm';
import NewBulkLoadGroupForm from './NewBulkLoadGroupForm';
import { useQueryClient } from 'react-query';

export const DataLoadsComponent = () => {

  const [groups, setGroups] = useState({});
  const [bulkLoadGroupDialog, setBulkLoadGroupDialog] = useState(false);
  const [bulkLoadDialog, setBulkLoadDialog] = useState(false);
  const [expandedGroupRows, setExpandedGroupRows] = useState(null);
  const [expandedLoadRows, setExpandedLoadRows] = useState(null);
  const errorMessage = useRef(null);
  const searchService = new SearchService();
  const dataLoadService = new DataLoadService();
  
  const queryClient = useQueryClient();

  const handleNewBulkLoadGroupOpen = (event) => {
    setBulkLoadGroupDialog(true);
  }

  const handleNewBulkLoadOpen = (event) => {
    setBulkLoadDialog(true);
  }

  useQuery(['bulkloadtable'],
    () => searchService.find('bulkloadgroup', 100, 0, {}), {
    onSuccess: (data) => {
      setGroups(data.results);
    },
    keepPreviousData: true,
    refetchOnWindowFocus: false
  })

  const urlTemplate = (rowData) => {
    return <a href={rowData.s3Url}>Download</a>
  };

  const refresh = () => {
    queryClient.invalidateQueries('bulkloadtable');
  };

  const runLoad = (rowData) => {
    dataLoadService.restartLoad(rowData.type, rowData.id);
  };

  const actionBodyTemplate = (rowData) => {
    return (
      <React.Fragment>
          <Button icon="pi pi-play" className="p-button-rounded p-button-success p-mr-2" onClick={() => runLoad(rowData)} />
      </React.Fragment>
    );
  }

  const nameBodyTemplate = (rowData) => {
    return (
      <React.Fragment>
          {rowData.name} ({rowData.id})
      </React.Fragment>
    );
  }

  const loadTable = (load) => {
    return (
      <div className="card">
        <DataTable value={load.loadFiles} responsiveLayout="scroll">
          <Column field="md5Sum" header="MD5 Sum" />
          <Column field="fileSize" header="File Size" />
          <Column field="recordCount" header="Record Count" />
          <Column field="s3Url" header="S3 Url (Download)" body={urlTemplate} />
          <Column field="lastUpdated" header="Last Loaded" />
          <Column field="status" header="Status" />

        </DataTable>
      </div>
    );
  }

  const groupTable = (group) => {
    return (
      <div className="card">
        <DataTable value={group.loads} responsiveLayout="scroll"
          expandedRows={expandedLoadRows} onRowToggle={(e) => setExpandedLoadRows(e.data)}
          rowExpansionTemplate={loadTable} dataKey="id">
          <Column expander style={{ width: '3em' }} />
          <Column body={nameBodyTemplate} header="Load Name" />
          <Column field="backendBulkLoadType" header="Backend Bulk Load Type" />
          <Column field="scheduleActive" header="Schedule Active" />
          <Column field="cronSchedule" header="Cron Schedule" />
          <Column field="nextRun" header="Next Run" />
          <Column field="status" header="Status" />
          <Column body={actionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
        </DataTable>
      </div>
    );
  }

  return (
    <div className="card">
      <Button label="New Group" icon="pi pi-plus" className="p-button-success p-mr-2" onClick={handleNewBulkLoadGroupOpen} />
      <Button label="New Bulk Load" icon="pi pi-plus" className="p-button-success p-mr-2" onClick={handleNewBulkLoadOpen} />
      <Button label="Refresh Data" icon="pi pi-plus" className="p-button-success p-mr-2" onClick={refresh} />
      <h3>Data Loads Table</h3>
      <Messages ref={errorMessage} />
      <DataTable
        value={groups} className="p-datatable-sm"
        expandedRows={expandedGroupRows} onRowToggle={(e) => setExpandedGroupRows(e.data)}
        rowExpansionTemplate={groupTable} dataKey="id">
        <Column expander style={{ width: '3em' }} />
        <Column body={nameBodyTemplate} header="Group Name" />
      </DataTable>
      <NewBulkLoadForm
        bulkLoadDialog={bulkLoadDialog}
        setBulkLoadDialog={setBulkLoadDialog}
      />
      <NewBulkLoadGroupForm
        bulkLoadGroupDialog={bulkLoadGroupDialog}
        setBulkLoadGroupDialog={setBulkLoadGroupDialog}
      />
    </div>
  );
}
