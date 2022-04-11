import React from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';

export const RelatedNotesDialog = ({ relatedNotes, relatedNotesDialog, setRelatedNotesDialog }) => {

  const hideDialog = () => {
    setRelatedNotesDialog(false);
  };

  const internalTemplate = (rowData) => {
    return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
  };

  const textTemplate = (rowData) => {
    return <EllipsisTableCell>{rowData.freeText}</EllipsisTableCell>;
  };

  return (
    <Dialog visible={relatedNotesDialog} className='w-6' modal onHide={hideDialog}>
      <h3>Related Notes</h3>
      <DataTable value={relatedNotes} dataKey="id" showGridlines >
        <Column field="noteType.name" header="Note Type"></Column>
        <Column field="internal" header="Internal" body={internalTemplate}></Column>
        <Column field="freeText" header="Text" body={textTemplate}></Column>
      </DataTable>
    </Dialog>
  );
};

