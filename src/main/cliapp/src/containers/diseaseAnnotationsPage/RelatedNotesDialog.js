import React from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

export const RelatedNotesDialog = ({ relatedNotes, relatedNotesDialog, setRelatedNotesDialog }) => {

  const hideDialog = () => {
    setRelatedNotesDialog(false);
  };

  return (
    <Dialog
      visible={relatedNotesDialog}
      style={{ width: '450px' }}
      modal className="p-fluid"
      onHide={hideDialog}>
      <h3>Related Notes</h3>
      <DataTable value={relatedNotes}>
        <Column field="noteType" header="Note Type"></Column>
        <Column field="internal" header="Internal"></Column>
        <Column field="text" header="Text"></Column>
      </DataTable>
    </Dialog>
  );
};

