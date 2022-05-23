import React, { useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';

export const GroupTable = ({ getService, queryClient, groups, reportTable }) => {
  const [expandedGroupRows, setExpandedGroupRows] = useState(null);

  const deleteGroup = (rowData) => {
    getService().deleteGroup(rowData.id).then(() => {
      queryClient.invalidateQueries('reporttable');
    });
  };

  const groupActionBodyTemplate = (rowData) => {
    if (!rowData.curationReports || rowData.curationReports.length === 0) {
      return (<Button icon="pi pi-trash" className="p-button-rounded p-button-danger mr-2" onClick={() => deleteGroup(rowData)} />);
    }
  };

  return (
      <DataTable key="groupTable"
        value={groups} className="p-datatable-sm"
        expandedRows={expandedGroupRows} onRowToggle={(e) => setExpandedGroupRows(e.data)}
        rowExpansionTemplate={reportTable}
        dataKey="id">
        <Column expander style={{ width: '3em' }} />
        <Column field="name" header="Group Name" />
        <Column body={groupActionBodyTemplate} exportable={false} style={{ minWidth: '8rem' }}></Column>
      </DataTable>
  );
};
