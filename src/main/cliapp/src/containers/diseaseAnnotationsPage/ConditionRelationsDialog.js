import React from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';

export const ConditionRelationsDialog = ({ conditonRelations, conditionRelationsDialog, setConditionRelationsDialog }) => {

  const hideDialog = () => {
    setConditionRelationsDialog(false);
  };

  const conditionStatementTemplate = (rowData) => {
    return <>
        <ul style={{ listStyleType: 'none' }}>
          {rowData?.conditions?.map((condition, index) =>
            <li key={index}>
              <EllipsisTableCell>
                {condition.conditionStatement}
              </EllipsisTableCell>
            </li>
          )}
        </ul>
      </>;
  };

  return (
    <Dialog
      visible={conditionRelationsDialog}
      style={{ width: '90%' }}
      modal className="p-fluid"
      onHide={hideDialog}>
      <h3>Related Notes</h3>
      <DataTable
        value={conditonRelations}
        dataKey="id"
        showGridlines
      >
        <Column field="conditionRelationType.name" header="Relation"></Column>
        <Column field="handle" header="Handle"></Column>
        <Column field="conditions.conditionStatement" header="Statement" body={conditionStatementTemplate}></Column>
      </DataTable>
    </Dialog>
  );
};
 
