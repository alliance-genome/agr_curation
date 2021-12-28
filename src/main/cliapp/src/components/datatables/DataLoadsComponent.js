import React, {useRef, useState, useEffect} from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { classNames } from 'primereact/utils';
import { SearchService } from '../../service/SearchService';
import { Messages } from 'primereact/messages';


export const DataLoadsComponent = () => {

  const [groups, setGroups] = useState({});
  const [group, setGroup] = useState({});
  const [groupDialog, setGroupDialog] = useState(false);
  const [expandedGroupRows, setExpandedGroupRows] = useState(null);
  const [expandedLoadRows, setExpandedLoadRows] = useState(null);
  const [submitted, setSubmitted] = useState(false);
  const errorMessage = useRef(null);

  let emptyGroup = {

  };

  useEffect(() => {
    const searchService = new SearchService();
    searchService.find("bulkloadgroup", 100, 0, {}).then(res => {
      setGroups(res.results);
    });
  }, []);

  const openNewGroup = () => {
    setGroup(emptyGroup);
    setSubmitted(false);
    setGroupDialog(true);
  };

  const onInputChange = (e, name) => {
    const val = (e.target && e.target.value) || '';
    let _group = {...group};
    _group[`${name}`] = val;

    setGroup(_group);
  };

  const hideDialog = () => {
    setSubmitted(false);
    setGroupDialog(false);
  };

  const saveGroup = () => {
    setSubmitted(true);
    setGroupDialog(false);
  };

  const groupDialogFooter = (
    <React.Fragment>
      <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
      <Button label="Save" icon="pi pi-check" className="p-button-text" onClick={saveGroup} />
    </React.Fragment>
  );

  const urlTemplate = (rowData) => {
    return <a href={rowData.s3Url}>Download</a>
  };

  const loadTable = (load) => {
    return (
      <div className="card">
        <DataTable value={load.loadFiles} responsiveLayout="scroll">
          <Column field="md5Sum" header="MD5 Sum" />
          <Column field="fileSize" header="File Size" />
          <Column field="s3Url" header="S3 Url (Download)" body={urlTemplate} />
          <Column field="lastUpdated" header="Last Loaded" />
          <Column field="status" header="Status" />

        </DataTable>
      </div>
    );
  }

  const groupTable = (group) => {
    return (
      <div className="card">
        <DataTable value={group.loads} responsiveLayout="scroll"
            expandedRows={expandedLoadRows} onRowToggle={(e) => setExpandedLoadRows(e.data)}
            rowExpansionTemplate={loadTable} dataKey="id">
          <Column expander style={{ width: '3em' }} />
          <Column field="name" header="Load Name" />
          <Column field="status" header="Status" />

        </DataTable>
      </div>
    );
  }

  return (
    <div className="card">
      <h3>Data Loads Table</h3>
      <Button label="New" icon="pi pi-plus" className="p-button-success p-mr-2" onClick={openNewGroup} />
      <Messages ref={errorMessage}/>
      <DataTable
        value={groups} className="p-datatable-sm"
        expandedRows={expandedGroupRows} onRowToggle={(e) => setExpandedGroupRows(e.data)}
        rowExpansionTemplate={groupTable} dataKey="id">
        <Column expander style={{ width: '3em' }} />
        <Column field="name" header="Group Name" />
      </DataTable>

      <Dialog visible={groupDialog} style={{ width: '450px' }} header="Group Details" modal className="p-fluid" footer={groupDialogFooter} onHide={hideDialog}>
        <div className="p-field">
          <label htmlFor="name">Name</label>
          <InputText id="name" value={group.name} onChange={(e) => onInputChange(e, 'name')} required autoFocus className={classNames({ 'p-invalid': submitted && !group.name })} />
          {submitted && !group.name && <small className="p-error">Name is required.</small>}
        </div>
      </Dialog>

    </div>
  );
}
