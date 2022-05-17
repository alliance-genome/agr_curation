import React, { useState } from 'react';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { classNames } from 'primereact/utils';
import { ReportService } from '../../service/ReportService';
import { useOktaAuth } from '@okta/okta-react';

import { useMutation, useQueryClient } from 'react-query';

export const NewReportGroupForm = ({ reportGroupDialog, setReportGroupDialog }) => {

  const { authState } = useOktaAuth();

  const [group, setGroup] = useState({});

  const [submitted, setSubmitted] = useState(false);
  let reportService = null;

  const mutation = useMutation(newGroupName => {
    return getService().createGroup(newGroupName);
  });

  const queryClient = useQueryClient();

  let emptyGroup = {};

  const onChange = (event, field) => {
    const val = (event.target && event.target.value) || '';
    let _group = { ...group };
    _group[field] = val;
    setGroup(_group);
  };

  const getService = () => {
    if(!reportService) {
      reportService = new ReportService(authState);
    }
    return reportService;
  }

  const hideDialog = () => {
    setReportGroupDialog(false);
    setSubmitted(false);
    setGroup(emptyGroup);
  };

  const saveGroup = (event) => {
    event.preventDefault();
    setSubmitted(true);

    // Go other stuff
    if (group.name && group.name.trim()) {
      console.log(event);
      mutation.mutate(group, {
        onSuccess: () => {
          queryClient.invalidateQueries('reporttable');
          setSubmitted(false);
          setReportGroupDialog(false);
          setGroup(emptyGroup);
        }
      });
    }

  };

  const groupDialogFooter = (
    <React.Fragment>
      <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
      <Button label="Save" icon="pi pi-check" className="p-button-text" onClick={saveGroup} />
    </React.Fragment>
  );


  return (
    <div>
      <Dialog visible={reportGroupDialog} style={{ width: '450px' }} header="Group Details" modal className="p-fluid" footer={groupDialogFooter} onHide={hideDialog}>
        <div className="field">
          <label htmlFor="name">Group Name</label>

          <InputText id="name" value={group.name} onChange={(e) => onChange(e, 'name')} required autoFocus className={classNames({ 'p-invalid': submitted && !group.name })} />
          {submitted && !group.name && <small className="p-error">Name is required.</small>}
        </div>
      </Dialog>
    </div>
  );
};

