import React, { useState } from 'react';
import { useQuery } from 'react-query';
import Moment from 'react-moment';
import moment from 'moment';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ScrollPanel } from 'primereact/scrollpanel';

export const HistoryDialog = ({ historyDialog, setHistoryDialog, history, dataLoadService }) => {

  const [expandedRows, setExpandedRows] = useState(null);
  const [fullHistory, setFullHistory] = useState({});

  useQuery(['bulkLoadFullHistory', history],
      () => dataLoadService.getFileHistoryFile(history.id), {
      onSuccess: (res) => {
        if(res.data.entity) {
          setFullHistory(res.data.entity);
        }
      },
      onError: (error) => {
        console.log(error);
      },
      keepPreviousData: true,
      refetchOnWindowFocus: false
    }
  );

  const hideDialog = () => {
    setHistoryDialog(false);
    setFullHistory({});
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
                <span className="count visitors"><Moment format="HH:mm:ss" duration={fullHistory.loadStarted} date={fullHistory.loadFinished} /></span>
              </div>
            </div>
            <div className="p-col-12 p-lg-4">
              <div className="card summary">
                <span className="title">Rate</span>
                <span className="detail">How many records per second to the database</span>
                <span className="count purchases">{Math.round(fullHistory.completedRecords / (moment(fullHistory.loadFinished) - moment(fullHistory.loadStarted)) * 10000) / 10} r/s</span>
              </div>
            </div>
            <div className="p-col-12 p-lg-4">
              <div className="card summary">
                <span className="title">Completed</span>
                <span className="detail">How much of the load was successful</span>
                <span className="count revenue">{fullHistory.completedRecords} of {fullHistory.totalRecords} = {Math.round(fullHistory.completedRecords / fullHistory.totalRecords * 1000) / 10}%</span>
              </div>
            </div>
            
          </div>

          <div className="card">
            <DataTable key="exceptionTable" value={fullHistory.exceptions} responsiveLayout="scroll"
              expandedRows={expandedRows} onRowToggle={(e) => setExpandedRows(e.data)} rowExpansionTemplate={jsonObjectTemplate} dataKey="message">
              <Column expander style={{ width: '3em' }} />
              <Column field="message" header={`Exception Messages (${fullHistory.exceptions ? fullHistory.exceptions.length : 0})`} />
            </DataTable>
          </div>

        </div>
      </Dialog>
    </div>
  );
};

