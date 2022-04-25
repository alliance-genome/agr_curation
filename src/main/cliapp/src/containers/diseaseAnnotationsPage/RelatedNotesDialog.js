import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { InputTextarea } from "primereact/inputtextarea";
import { InputTextAreaEditor } from '../../components/InputTextAreaEditor';
import { ErrorMessageComponent } from '../../components/ErrorMessageComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ValidationService } from '../../service/ValidationService';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';

export const RelatedNotesDialog = ({
  originalRelatedNotesData,
  setOriginalRelatedNotesData,
  authState,
  errorMessagesMainRow,
  setErrorMessagesMainRow
}) => {
  const [ dialogRelatedNotes, setDialogRelatedNotes] = useState(null);
  const { originalRelatedNotes, isInEdit, dialog, rowIndex, mainRowProps } = originalRelatedNotesData;
  const [localRelateNotes, setLocalRelateNotes] = useState(null) ;
  const [editingRows, setEditingRows] = useState({});
  const [errorMessages, setErrorMessages] = useState([]);
  const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
  const noteTypeTerms = useControlledVocabularyService('Disease annotation note types');
  const validationService = new ValidationService(authState);
  const [undoEditsButton, setUndoEditsButton] = useState(false);
  const tableRef = useRef(null);
  const [externalUpdate, setExternalUpdate] = useState();

  const showDialogHandler = () => {
     setUndoEditsButton(true);
     let temp = global.structuredClone(originalRelatedNotes);
   setLocalRelateNotes(temp);

    if (isInEdit) {
      let rowsObject = {};
      if(originalRelatedNotes) {
      originalRelatedNotes.forEach((note) => {
        rowsObject[`${note.id}`] = true;
      });
    }
      setEditingRows(rowsObject);
    } else {
      setEditingRows({});
    }
  };

  const onRowEditChange = (e) => {
    setEditingRows(e.data);
  }

  const hideDialog = () => {
    setErrorMessages([]);
    setOriginalRelatedNotesData((originalRelatedNotesData) => {
      return {
        ...originalRelatedNotesData,
        dialog: false,
      };
    });
   let temp = global.structuredClone(originalRelatedNotes);
    setLocalRelateNotes(temp);
    setUndoEditsButton(false);
  };

  const validateNotes = async (notes) => {
    const validationResultsArray = [];
    for (const note of notes) {
      const result = await validationService.validate('note', note);
      validationResultsArray.push(result);
    }
    return validationResultsArray;
  };

  const saveDataHandler = async () => {
    const resultsArray = await validateNotes(localRelateNotes);
    const errorMessagesCopy = [...errorMessages];
    let keepDialogOpen = false;
    let anyErrors = false;

    resultsArray.forEach((result, index) => {
      const { isError, data } = result;
      if (isError) {
        errorMessagesCopy[index] = {};
        Object.keys(data).forEach((field) => {
          let messageObject = {
            severity: "error",
            message: data[field]
          };
          errorMessagesCopy[index][field] = messageObject;
          setErrorMessages(errorMessagesCopy);
          keepDialogOpen = true;
          anyErrors = true;
        });
      }
    });

    if (!anyErrors) {
      setErrorMessages([]);
      mainRowProps.rowData.relatedNotes = localRelateNotes;
      let updatedAnnotations = [...mainRowProps.props.value];
      updatedAnnotations[rowIndex].relatedNotes = localRelateNotes;
      keepDialogOpen = false;

      if(undoEditsButton) {
      const errorMessagesCopy = errorMessagesMainRow;
      let messageObject = {
        severity: "warn",
        message: "Pending Edits!"
      };
      errorMessagesCopy[mainRowProps.rowIndex] = {};
      errorMessagesCopy[mainRowProps.rowIndex]["relatedNotes.freeText"] = messageObject;
      setErrorMessagesMainRow({...errorMessagesCopy});
    }
    };

    setOriginalRelatedNotesData((originalRelatedNotesData) => {
      return {
        ...originalRelatedNotesData,
        dialog: keepDialogOpen,
      }
    }
    );
  };

  const internalTemplate = (rowData) => {
    return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
  };

  const textTemplate = (rowData) => {
    return <EllipsisTableCell>{rowData.freeText}</EllipsisTableCell>;
  };

  const undoEditsTemplate = (rowData) => {
      if(!isInEdit)
        return null;
  }

  const onInternalEditorValueChange = (props, event) => {
     let _localRelateNotes = [...localRelateNotes];
     _localRelateNotes[props.rowIndex].internal = event.value.name;
     setLocalRelateNotes(_localRelateNotes);
    setUndoEditsButton(true);
  }

  const internalEditor = (props) => {
    return (
      <>
        <TrueFalseDropdown
          options={booleanTerms}
          editorChange={onInternalEditorValueChange}
          props={props}
          field={"internal"}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"internal"} />
      </>
    );
  };


  const onNoteTypeEditorValueChange = (props, event) => {
    let _localRelateNotes = [...localRelateNotes];
    _localRelateNotes[props.rowIndex].noteType = event.value;
    setLocalRelateNotes(_localRelateNotes);
    setUndoEditsButton(true);
  };

  const noteTypeEditor = (props) => {
    return (
      <>
        <ControlledVocabularyDropdown
          field="noteType"
          options={noteTypeTerms}
          editorChange={onNoteTypeEditorValueChange}
          props={props}
          showClear={false}
       externalUpdate={externalUpdate}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"noteType"} />
      </>
    );
  };

  const onFreeTextEditorValueChange = (props, event) => {
    let _localRelateNotes = [...localRelateNotes];
    _localRelateNotes[props.rowIndex].freeText = event.target.value;
    setLocalRelateNotes(_localRelateNotes);
    setUndoEditsButton(true);
  };

  const freeTextEditor = (props, fieldName, errorMessages) => {
    if (errorMessages) {
      errorMessages.severity = "error";
    }
    return (
      <>
      <InputTextarea
        value={props.rowData[fieldName] ? props.rowData[fieldName] : ''}
        onChange={(e) => onFreeTextEditorValueChange(props, e)}
        style={{ width: '100%' }}
      />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={fieldName} />
      </>
    );
  };

  const manageUndoEditsOperation = (event, props) => {
      let hasEdited = false;
    if(props.rowData.noteType.name !== originalRelatedNotes[props.rowIndex].noteType.name){
      hasEdited = true;
    }
    if(props.rowData.internal !== originalRelatedNotes[props.rowIndex].internal){
      hasEdited = true;
    }
    if(props.rowData.freeText !== originalRelatedNotes[props.rowIndex].freeText){
      hasEdited = true;
    }
      if(hasEdited && undoEditsButton){
        //setUndoEditsButton(false);
      }
     let _localRelateNotes = [...localRelateNotes];
     _localRelateNotes[props.rowIndex] = global.structuredClone(originalRelatedNotes[props.rowIndex]);
     setLocalRelateNotes(_localRelateNotes);
    //const _localRelateNotes = localRelateNotes.map( x => ({...originalRelatedNotes}) );
    //setLocalRelateNotes(_localRelateNotes);
  };

  const undoEditsButtonShow = (rowData) => {
    if (!isInEdit) {
      return null;
    };
      return (
      <Button className="p-button-text" label="Undo Edits" disabled={!undoEditsButton}
            onClick={ (event) => { manageUndoEditsOperation(event, rowData) } } >
      </Button>
    )
  };

  const footerTemplate = () => {
    if (!isInEdit) {
      return null;
    };
    return (
      <div>
        <Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
        <Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} />
      </div>
    );
  }

  return (
    <Dialog visible={dialog} className='w-6' modal onHide={hideDialog}
      closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
      <h3>Related Notes</h3>
      <DataTable value={localRelateNotes} dataKey="id" showGridlines editMode='row'
        editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef}
      >
        <Column editor={noteTypeEditor} field="noteType.name" header="Note Type" />
        <Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} />
        <Column
          editor={(props) => freeTextEditor(props, "freeText", errorMessages)}
          field="freeText"
          header="Text"
          body={textTemplate}
        />
        <Column
        editor={(props => undoEditsButtonShow(props))}
        field="undoEditsButton"
       header=""
       body={undoEditsTemplate}
      />
      </DataTable>
     {JSON.stringify(localRelateNotes)}
     <hr/>
     {JSON.stringify(originalRelatedNotes)}
    </Dialog>
  );
};
