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
  const tableRef = useRef(null);
  const rowsInEdit = useRef(0);
  const [editedRows, setEditedRows] = useState({});
  const [externalUpdate, setExternalUpdate] = useState();
  const hasEdited = useRef(false);

  const showDialogHandler = () => {
    let temp = global.structuredClone(originalRelatedNotes);
    setLocalRelateNotes(temp);

    if(isInEdit){
      let rowsObject = {};
      let cnt = 0;
      if(originalRelatedNotes) {
        originalRelatedNotes.forEach((note) => {
          rowsObject[`${note.id}`] = true;
        });
        editedRows[cnt] = { ...temp[cnt] };
        cnt++;
      }
      setEditingRows(rowsObject);
      setEditedRows(editedRows);
      rowsInEdit.current++;
    }else{
      setEditingRows({});
    }
  };

  const onRowEditChange = (e) => {
    setEditingRows(e.data);
  }

  const onRowEditCancel = (event) => {
    rowsInEdit.current--;
    let relatedNotes = [...localRelateNotes];
    relatedNotes[event.index] = editedRows[event.index];
    delete editedRows[event.index];
    setEditedRows(editedRows);
    setLocalRelateNotes(relatedNotes);

    let _localRelateNotes = [...localRelateNotes];
    _localRelateNotes[event.index] = global.structuredClone(originalRelatedNotes[event.index]);
    setLocalRelateNotes(_localRelateNotes);

    const errorMessagesCopy = errorMessages;
    errorMessagesCopy[event.index] = {};
    setErrorMessages(errorMessagesCopy);

    hasEdited.current = false;
  };

  const compareChangesInNotes = (data,index) => {
    if(data.noteType.name !== originalRelatedNotes[index].noteType.name){
      hasEdited.current = true;
    }
    if(data.internal !== originalRelatedNotes[index].internal){
      hasEdited.current = true;
    }
    if(data.freeText !== originalRelatedNotes[index].freeText){
      hasEdited.current = true;
    }
  };

  const onRowEditSave = (event) => {
    rowsInEdit.current--;
    let _localRelateNotes = [...localRelateNotes];
    _localRelateNotes[event.index] = event.data;
    setLocalRelateNotes(_localRelateNotes);
    compareChangesInNotes(event.data,event.index);
  };

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

      for(let i in localRelateNotes)
        compareChangesInNotes(localRelateNotes[i],i);

      if(hasEdited){
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

  const onFreeTextEditorValueChange = (event, props) => {
    let _localRelateNotes = [...localRelateNotes];
    _localRelateNotes[props.rowIndex].freeText = event.target.value;
    setLocalRelateNotes(_localRelateNotes);
  };

  const freeTextEditor = (props, fieldName, errorMessages) => {
    if (errorMessages) {
      errorMessages.severity = "error";
    }
    return (
      <>
      <InputTextAreaEditor
        initalValue={props.value}
        editorChange={(e) => onFreeTextEditorValueChange(e, props)}
        rows={5}
        columns={30}
      />
        {/*<InputTextarea
          ref={(input) => {input && input.focus()}}
          value={props.value}
          onChange={(e) => onFreeTextEditorValueChange(e, props)}
          style={{ width: '100%' }}
          rows={5}
          cols={30}
        />*/}
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={fieldName} />
      </>
    );
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
    <Dialog visible={dialog} className='w-6' modal onHide={hideDialog} closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
      <h3>Related Notes</h3>
      <DataTable value={localRelateNotes} dataKey="id" showGridlines editMode='row'
        editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
        <Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
              bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0'/>
        <Column editor={noteTypeEditor} field="noteType.name" header="Note Type" headerClassName='surface-0'/>
        <Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
        <Column
          editor={(props) => freeTextEditor(props, "freeText", errorMessages)}
          field="freeText"
          header="Text"
          body={textTemplate}
          headerClassName='surface-0'
        />
      </DataTable>
    </Dialog>
  );
};
