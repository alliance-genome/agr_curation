import React from 'react';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { DialogTop } from './DialogTop';
import { ReportHistoryTable } from './ReportHistoryTable';

export const ReportDialog = ({ reportDialog, setReportDialog, report }) => {
  const hideDialog = () => {
    setReportDialog(false);
  };

  const reportDialogFooter = (
    <>
      <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
    </>
  );

  return (          
    <Dialog visible={reportDialog} footer={reportDialogFooter} closable onHide={hideDialog}>
      <DialogTop report={report}/>
      <ReportHistoryTable history={report.curationReportHistory} />
    </Dialog>
  )
}

