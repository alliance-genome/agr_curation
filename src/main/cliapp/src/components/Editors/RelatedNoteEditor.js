import React, { useRef } from 'react';
import { ErrorMessageComponent } from '../Error/ErrorMessageComponent';
import { Button } from 'primereact/button';
import { EditMessageTooltip } from '../EditMessageTooltip';

export const RelatedNoteEditor = ({ rowProps, relatedNote, errorMessages, setRelatedNotesData }) => {
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

  const handleRelatedNotesOpenInEdit = (event, rows, rowIndex) => {
    event.preventDefault();
    const index = rowIndex % rows;
    let _relatedNotesData = {};
    _relatedNotesData["originalRelatedNotes"] = relatedNote ? [relatedNote] : undefined;
    _relatedNotesData["dialogIsVisible"] = true;
    _relatedNotesData["rowIndex"] = index;
    _relatedNotesData["errorMessages"] = errorMessages;
    setRelatedNotesData(() => ({
      ..._relatedNotesData
    }));
  };

  if (relatedNote) {
    return (
      <>
        <div>
          <Button className="p-button-text"
            onClick={(event) => { handleRelatedNotesOpenInEdit(event, rowProps.props.rows, rowProps.rowIndex); }} >
						<span style={{ textDecoration: 'underline' }}>
							{relatedNote.freeText}
              <i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
            </span>&nbsp;&nbsp;&nbsp;&nbsp;
            <EditMessageTooltip object="allele" />
          </Button>
        </div>
        <ErrorMessageComponent errorMessages={errorMessagesRef.current[rowProps.rowIndex]} errorField={"relatedNote"} style={{ 'fontSize': '1em' }} />
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
        <ErrorMessageComponent errorMessages={errorMessagesRef.current[rowProps.rowIndex]} errorField={"relatedNote"} style={{ 'fontSize': '1em' }} />
      </>
    );
  }
};