import React from 'react';
import { Button } from 'primereact/button';

export const TopButtons = ({ reportDispatch, setNewReportDialog, setReportGroupDialog, queryClient }) => {

  const handleNewReportGroupOpen = () => {
    setReportGroupDialog(true);
  };

  const refresh = () => {
    queryClient.invalidateQueries('reporttable');
  };

  const handleNewReportOpen = () => {
    reportDispatch({ type: "RESET" });
    setNewReportDialog(true);
  };

  return (
    <>
      <Button label="New Group" icon="pi pi-plus" className="p-button-success mr-2" onClick={handleNewReportGroupOpen} />
      <Button label="New Report" icon="pi pi-plus" className="p-button-success mr-2" onClick={handleNewReportOpen} />
      <Button label="Refresh Data" icon="pi pi-plus" className="p-button-success mr-2" onClick={refresh} />
    </>
  );
}
