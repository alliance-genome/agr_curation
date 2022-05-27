import React from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { useGenericDataTable } from "./useGenericDataTable";

export const GenericDataTable = (props) => {

  const { tableProps, columnList } = useGenericDataTable(props);

  return (
      <div className="card">
        <DataTable {...tableProps} >
          <Column field='rowEditor' rowEditor style={{maxWidth: '7rem', minWidth: '7rem'}} 
            headerStyle={{ width: '7rem', position: 'sticky' }} bodyStyle={{ textAlign: 'center' }} frozen headerClassName='surface-0'/>
          {columnList}
        </DataTable>
      </div>
  )
}

