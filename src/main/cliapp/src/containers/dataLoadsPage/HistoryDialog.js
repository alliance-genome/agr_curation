import React, { useState } from 'react';
import Moment from 'react-moment';
import moment from 'moment';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ScrollPanel } from 'primereact/scrollpanel';
import { classNames } from 'primereact/utils';
import { DataLoadService } from '../../service/DataLoadService';
import { useOktaAuth } from '@okta/okta-react';


import { useMutation, useQueryClient } from 'react-query';


export const HistoryDialog = ({ historyDialog, setHistoryDialog, history }) => {

  const [expandedRows, setExpandedRows] = useState(null);

  const hideDialog = () => {
    setHistoryDialog(false);
  };

  const jsonObjectTemplate = (rowData) => {
    if (rowData.jsonObject) {
      return (
        <div class="card">
          <h2>JSON Object</h2>
          <ScrollPanel style={{width: '100%'}}>
            <pre>{JSON.stringify(JSON.parse(rowData.jsonObject), null, 2) }</pre>
          </ScrollPanel>
        </div>
      );
    }
  };

  return (
    <div>
      <Dialog visible={historyDialog} style={{ width: '70vw' }} header="History Information" modal className="p-fluid" onHide={hideDialog}>
        <div className="p-field">
          <div className="p-grid p-fluid dashboard">
            <div className="p-col-12 p-lg-3">
              <div className="card summary">
                <span className="title">Duration</span>
                <span className="detail">How long the load took</span>
                <span className="count visitors"><Moment format="HH:mm:ss" duration={history.loadStarted} date={history.loadFinished} /></span>
              </div>
            </div>
            <div className="p-col-12 p-lg-4">
              <div className="card summary">
                <span className="title">Rate</span>
                <span className="detail">How many records per second to the database</span>
                <span className="count purchases">{Math.round(history.completedRecords / (moment(history.loadFinished) - moment(history.loadStarted)) * 10000) / 10} r/s</span>
              </div>
            </div>
            <div className="p-col-12 p-lg-4">
              <div className="card summary">
                <span className="title">Completed</span>
                <span className="detail">How much of the load was successful</span>
                <span className="count revenue">{history.completedRecords} of {history.totalRecords} = {Math.round(history.completedRecords / history.totalRecords * 1000) / 10}%</span>
              </div>
            </div>
            
          </div>

          <div className="card">
            <DataTable key="exceptionTable" value={history.exceptions} responsiveLayout="scroll"
              expandedRows={expandedRows} onRowToggle={(e) => setExpandedRows(e.data)} rowExpansionTemplate={jsonObjectTemplate} dataKey="message">
              <Column expander style={{ width: '3em' }} />
              <Column field="message" header="Exception Message" />
            </DataTable>
          </div>

        </div>
      </Dialog>
    </div>
  );
};

