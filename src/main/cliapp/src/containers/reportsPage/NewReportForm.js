import React, { useRef, useState, useEffect } from 'react';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { CronFields } from './CronFields';
import { Dropdown } from "primereact/dropdown";
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { useMutation, useQueryClient } from 'react-query';
import { ReportService } from '../../service/ReportService';
import { InputText } from 'primereact/inputtext';

export const NewReportForm = ({ reportDialog, setReportDialog, groups, newReport, reportDispatch, disableFormFields, setDisableFormFields, reportService }) => {
  const booleanTerms = useControlledVocabularyService('generic_boolean_terms');

  const queryClient = useQueryClient();

  // const [backendBulkLoadTypes, setBackendLoadTypes] = useState();

  const mutation = useMutation(report => {
    if (report.id) {
      return getService().updateReport(report);
    } else {
      return getService().createReport(report);
    }
  });

  const onChange = (e) => {
    reportDispatch({
      field: e.target.name,
      value: e.target.value
    });
  };

  const getService = () => {
    if(!reportService) {
      reportService = new ReportService();
    }
    return reportService;
  }

  const hideDialog = () => {
    reportDispatch({ type: "RESET" });
    setReportDialog(false);
    setDisableFormFields(false);
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    mutation.mutate(newReport, {
      onSuccess: () => {
        queryClient.invalidateQueries('reporttable');
        reportDispatch({ type: "RESET" });
        setReportDialog(false);
        setDisableFormFields(false);
      },
      onError: () => {
        // lookup group and set 
      }
    });
  };

  const newBulkLoadDialogFooter = (
    <>
      <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDialog} />
      <Button label="Save" icon="pi pi-check" className="p-button-text" onClick={handleSubmit} />
    </>
  );


  return (
    <Dialog visible={reportDialog} style={{ width: '450px' }} header="Add Bulk Load" modal className="p-fluid" footer={newBulkLoadDialogFooter} onHide={hideDialog} resizeable >
      <div className='p-justify-center'>
        <form>

          <div className="field">
            <label htmlFor="name">Name</label>
            <InputText
              id="name"
              name="name"
              placeholder={"Name"}
              value={newReport.name}
              onChange={onChange}
            />
          </div>

          <div className="field">
            <label htmlFor="group">Group Name</label>
            <Dropdown
              id="group"
              options={groups}
              value={newReport.curationReportGroup}
              onChange={onChange}
              placeholder={"Select Group"}
              className='p-col-12'
              name='curationReportGroup'
              optionLabel='name'
              optionValue='id'
            />
          </div>
          <CronFields
              newItem={newReport}
              onChange={onChange}
          />
        </form>
      </div>
    </Dialog>
  );
};
