import React, { useRef, useState } from 'react';
import { useQuery } from 'react-query';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

import { SearchService } from '../../service/SearchService';
import { Messages } from 'primereact/messages';
import { Button } from 'primereact/button';
import NewBulkLoadForm from './NewBulkLoadForm';
import NewBulkLoadGroupForm from './NewBulkLoadGroupForm';


export const DataLoadsComponent = () => {

  const [groups, setGroups] = useState({});
  const [bulkLoadGroupDialog, setBulkLoadGroupDialog] = useState(false);
  const [bulkLoadDialog, setBulkLoadDialog] = useState(false);
  const [expandedGroupRows, setExpandedGroupRows] = useState(null);
  const [expandedLoadRows, setExpandedLoadRows] = useState(null);
  const errorMessage = useRef(null);
  const searchService = new SearchService();

  const handleNewBulkLoadGroupOpen = (event) => {
    setBulkLoadGroupDialog(true);
  }

  const handleNewBulkLoadOpen = (event) => {
    setBulkLoadDialog(true);
  }

  useQuery(['bulkloadgroup'],
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

  const loadTable = (load) => {
    return (
      <div className="card">
        <DataTable value={load.loadFiles} responsiveLayout="scroll">
          <Column field="md5Sum" header="MD5 Sum" />
          <Column field="fileSize" header="File Size" />
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
          <Column field="name" header="Load Name" />
          <Column field="status" header="Status" />

        </DataTable>
      </div>
    );
  }

  return (
    <div className="card">
      <Button icon="pi pi-plus" className="p-button-success p-mr-2" onClick={handleNewBulkLoadGroupOpen}>New Group</Button>
      <Button icon="pi pi-plus" className="p-button-success p-mr-2" onClick={handleNewBulkLoadOpen}>New Bulk Load</Button>
      <h3>Data Loads Table</h3>
      <Messages ref={errorMessage} />
      <DataTable
        value={groups} className="p-datatable-sm"
        expandedRows={expandedGroupRows} onRowToggle={(e) => setExpandedGroupRows(e.data)}
        rowExpansionTemplate={groupTable} dataKey="id">
        <Column expander style={{ width: '3em' }} />
        <Column field="name" header="Group Name" />
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
