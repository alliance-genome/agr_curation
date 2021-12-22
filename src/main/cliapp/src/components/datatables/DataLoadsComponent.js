import React, {useRef, useState, useEffect} from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { Messages } from 'primereact/messages';


export const DataLoadsComponent = () => {

  const [groups, setGroups] = useState({});
  const [expandedGroupRows, setExpandedGroupRows] = useState(null);
  const [expandedLoadRows, setExpandedLoadRows] = useState(null);
  const errorMessage = useRef(null);

   useEffect(() => {
    const searchService = new SearchService();
    searchService.find("bulkloadgroup", 100, 0, {}).then(res => {
      setGroups(res.results);
    });
   }, []);

  const loadTable = (load) => {
    return (
      <div className="card">
        <DataTable value={load.loadFiles} responsiveLayout="scroll">
          <Column field="md5Sum" header="MD5 Sum" />
          <Column field="fileSize" header="File Size" />
          <Column field="s3Url" header="S3 Url" />
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
      <h3>Data Loads Table</h3>
      <Messages ref={errorMessage}/>
      <DataTable
        value={groups} className="p-datatable-sm"
        expandedRows={expandedGroupRows} onRowToggle={(e) => setExpandedGroupRows(e.data)}
        rowExpansionTemplate={groupTable} dataKey="id">
        <Column expander style={{ width: '3em' }} />
        <Column field="name" header="Group Name" />
      </DataTable>
    </div>
  );
}
