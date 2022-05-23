import React from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

export const ReportHistoryTable = ({ history }) => {

  const pdfBody = rowData => <a href={rowData.pdfUrl} target="_blank">PDF</a>;
  const xlsBody = rowData => <a href={rowData.xlsUrl} target="_blank">XLS</a>;
  const htmlBody = rowData => <a href={rowData.htmlUrl} target="_blank">HTML</a>;

  if (!history) return <div>No history to show</div>; 

  return(
    <DataTable value={history}>
      <Column body={pdfBody} field="pdfUrl" header="PDF File" />
      <Column body={xlsBody} field="xlsUrl" header="XLS File" />
      <Column body={htmlBody} field="htmlUrl" header="HTML File" />
    </DataTable>
  )
};
