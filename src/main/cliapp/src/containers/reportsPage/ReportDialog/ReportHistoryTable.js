import React from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { StatusTemplate } from '../StatusTemplate';

export const ReportHistoryTable = ({ history }) => {

  const pdfBody = rowData => <a href={rowData.pdfUrl} rel="noopener noreferrer" target="_blank">PDF</a>;
  const xlsBody = rowData => <a href={rowData.xlsUrl} rel="noopener noreferrer" target="_blank">XLS</a>;
  const htmlBody = rowData => <a href={rowData.htmlUrl} rel="noopener noreferrer" target="_blank">HTML</a>;

  if (!history) return <div>No history to show</div>; 

  return(
    <DataTable value={history}>
      <Column body={pdfBody} field="pdfUrl" header="PDF File" />
      <Column body={xlsBody} field="xlsUrl" header="XLS File" />
      <Column body={htmlBody} field="htmlUrl" header="HTML File" />
      <Column body={(rowData) => <StatusTemplate rowData={rowData}/>} header="Status" />
    </DataTable>
  )
};
