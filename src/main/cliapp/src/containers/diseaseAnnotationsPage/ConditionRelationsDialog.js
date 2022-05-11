import React from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ListTableCell } from '../../components/ListTableCell';

export const ConditionRelationsDialog = ({ conditonRelations, conditionRelationsDialog, setConditionRelationsDialog }) => {

  const hideDialog = () => {
    setConditionRelationsDialog(false);
  };

  const conditionStatementTemplate = (rowData) => {
    const listTemplate = (item) => {
        return (
          <EllipsisTableCell>
            {item.conditionStatement}
          </EllipsisTableCell>
        );
      };
    return <ListTableCell template={listTemplate} listData={rowData.conditions} />
  };
  
  const internalTemplate = (rowData) => {
    return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
  };

  return (
    <Dialog visible={conditionRelationsDialog} className='w-6' modal onHide={hideDialog}>
      <h3>Experimental Conditions</h3>
      <DataTable value={conditonRelations} dataKey="id" showGridlines >
        <Column field="conditionRelationType.name" header="Relation"></Column>
        <Column field="handle" header="Handle"></Column>
        <Column field="conditions.conditionStatement" header="Statement" body={conditionStatementTemplate}></Column>
        <Column field="internal" header="Internal" body={internalTemplate}></Column>
      </DataTable>
    </Dialog>
  );
};

