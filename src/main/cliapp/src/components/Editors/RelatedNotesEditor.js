import React, { useRef, useState } from 'react';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { Button } from 'primereact/button';
import { EditMessageTooltip } from '../../components/EditMessageTooltip';

export const RelatedNotesEditor = ({ relatedNotes, errorMessages, rowIndex, rows }) => {
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

  //may need to move these up a level
  const [relatedNotesData, setRelatedNotesData] = useState({
    relatedNotes: [],
    isInEdit: false,
    dialog: false,
    rowIndex: null,
    mainRowProps: {},
  });

  const handleRelatedNotesOpenInEdit = (event, rows, rowIndex, isInEdit) => {
    const index = rowIndex % rows;
    let _relatedNotesData = {};
    _relatedNotesData["originalRelatedNotes"] = relatedNotes;
    _relatedNotesData["dialog"] = true;
    _relatedNotesData["isInEdit"] = isInEdit;
    _relatedNotesData["rowIndex"] = index;
    // _relatedNotesData["mainRowProps"] = rowProps;
    setRelatedNotesData(() => ({
      ..._relatedNotesData
    }));
  };

  if (relatedNotes) {
    return (
      <>
        <div>
          <Button className="p-button-text"
            onClick={(event) => { handleRelatedNotesOpenInEdit(event, rows, rowIndex); }} >
						<span style={{ textDecoration: 'underline' }}>
              <i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
            </span>&nbsp;&nbsp;&nbsp;&nbsp;
            <EditMessageTooltip object="allele" />
          </Button>
        </div>
        <ErrorMessageComponent errorMessages={errorMessagesRef.current[rowIndex]} errorField={"relatedNotes"} style={{ 'fontSize': '1em' }} />
      </>
    );
  } else {
    return (
      <>
        <div>
          <Button className="p-button-text"
            onClick={(event) => { handleRelatedNotesOpenInEdit(event, rows, rowIndex); }} >
            <span style={{ textDecoration: 'underline' }}>
              Add Note
              <i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
            </span>&nbsp;&nbsp;&nbsp;&nbsp;
            <EditMessageTooltip />
          </Button>
        </div>
        <ErrorMessageComponent errorMessages={errorMessagesRef.current[rowIndex]} errorField={"relatedNotes"} style={{ 'fontSize': '1em' }} />
      </>
    );
  }
};