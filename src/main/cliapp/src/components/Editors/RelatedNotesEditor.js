import React, { useRef, useState } from 'react';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { Button } from 'primereact/button';
import { EditMessageTooltip } from '../../components/EditMessageTooltip';

export const RelatedNotesEditor = ({ rowProps, errorMessages, setRelatedNotesData }) => {
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;
  console.log("props in RelatedNotesEditor", rowProps);

  //todo: may need to move these up a level

  const handleRelatedNotesOpenInEdit = (event, rows, rowIndex) => {
    event.preventDefault();
    const index = rowIndex % rows;
    let _relatedNotesData = {};
    _relatedNotesData["originalRelatedNotes"] = [rowProps?.rowData.relatedNote];
    _relatedNotesData["dialogIsVisible"] = true;
    _relatedNotesData["rowIndex"] = index;
    _relatedNotesData["errorMessages"] = errorMessages;
    // _relatedNotesData["mainRowProps"] = rowProps;
    setRelatedNotesData(() => ({
      ..._relatedNotesData
    }));
  };

  if (rowProps.rowData.relatedNote) {
    return (
      <>
        <div>
          <Button className="p-button-text"
            onClick={(event) => { handleRelatedNotesOpenInEdit(event, rowProps.props.rows, rowProps.rowIndex); }} >
						<span style={{ textDecoration: 'underline' }}>
							{rowProps.rowData.relatedNote.freeText}
              <i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
            </span>&nbsp;&nbsp;&nbsp;&nbsp;
            <EditMessageTooltip object="allele" />
          </Button>
        </div>
        <ErrorMessageComponent errorMessages={errorMessagesRef.current[rowProps.rowIndex]} errorField={"relatedNotes"} style={{ 'fontSize': '1em' }} />
      </>
    );
  } else {
    return (
      <>
        <div>
          <Button className="p-button-text"
            onClick={(event) => { handleRelatedNotesOpenInEdit(event, rowProps.props.rows, rowProps.rowIndex); }} >
            <span style={{ textDecoration: 'underline' }}>
              Add Note
              <i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
            </span>&nbsp;&nbsp;&nbsp;&nbsp;
            <EditMessageTooltip />
          </Button>
        </div>
        <ErrorMessageComponent errorMessages={errorMessagesRef.current[rowProps.rowIndex]} errorField={"relatedNotes"} style={{ 'fontSize': '1em' }} />
      </>
    );
  }
};