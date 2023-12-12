import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from './Actions/DeletionAction';
import { InputTextAreaEditor } from './InputTextAreaEditor';
import { DialogErrorMessageComponent } from './Error/DialogErrorMessageComponent';
import { InternalEditor } from './Editors/InternalEditor';
import { useVocabularyTermSetService } from '../service/useVocabularyTermSetService';
import { ValidationService } from '../service/ValidationService';
import { ControlledVocabularyDropdown } from './ControlledVocabularySelector';
import { validate } from '../utils/utils';

export const RelatedNotesDialogEditOnly = ({
  relatedNotesData,
  errorMessagesMainRow,
  dispatch,
  setRelatedNotesData,
  singleValue = false,
  onChange,
  errorField = "relatedNotes"
}) => {
  const {
    originalRelatedNotes,
    dialogIsVisible,
    rowIndex,
  } = relatedNotesData;
  const [errorMessages, setErrorMessages] = useState([]);
  const validationService = new ValidationService();
  const tableRef = useRef(null);
  const [editingRows, setEditingRows] = useState({});
  const noteTypeTerms = useVocabularyTermSetService("allele_genomic_entity_association_note_type");
  const [localRelatedNotes, setLocalRelatedNotes] = useState([]);

  const cloneNotes = (clonableNotes) => {
    if (!clonableNotes) return [{ dataKey: 0 }];
    let _clonableNotes = global.structuredClone(clonableNotes);
    _clonableNotes.forEach((note, index) => {
      note.dataKey = index;
    });
    return _clonableNotes;
  };

  const showDialogHandler = () => {
    let _localRelatedNotes = cloneNotes(originalRelatedNotes);
    setLocalRelatedNotes(_localRelatedNotes);

    let rowsObject = {};
    _localRelatedNotes.forEach((note) => {
      rowsObject[`${note.dataKey}`] = true;
    });
    setEditingRows(rowsObject);
  };

  const deletionHandler = (e, index) => {
    e.preventDefault();
    let _localRelatedNotes = global.structuredClone(localRelatedNotes);
    _localRelatedNotes.splice(index, 1);
    let _errorMessages = global.structuredClone(errorMessages);
    _errorMessages.splice(index, 1);
    setLocalRelatedNotes(_localRelatedNotes);
    setErrorMessages(_errorMessages);
  };

  const updateLocalRelatedNotes = (index, field, value) => {
    const _localRelatedNotes = global.structuredClone(localRelatedNotes);
    _localRelatedNotes[index][field] = value;
    setLocalRelatedNotes(_localRelatedNotes);
  };
  const onNoteTypeEditorValueChange = (props, event) => {
    props.editorCallback(event.target.value);
    updateLocalRelatedNotes(props.rowIndex, "noteType", event.target.value);
  };

  const noteTypeEditor = (props) => {
    return (
      <>
        <ControlledVocabularyDropdown
          field="noteType"
          options={noteTypeTerms.sort((a, b) => (a.name > b.name) ? 1 : -1)}
          editorChange={onNoteTypeEditorValueChange}
          props={props}
          showClear={false}
          dataKey='id'
        />
        <DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"noteType"} />
      </>
    );
  };

  const onFreeTextEditorValueChange = (event, props) => {
    props.editorCallback(event.target.value);
    updateLocalRelatedNotes(props.rowIndex, "freeText", event.target.value);
  };

  const freeTextEditor = (props, fieldName, errorMessages) => {
    return (
      <>
        <InputTextAreaEditor
          initalValue={props.value}
          editorChange={(e) => onFreeTextEditorValueChange(e, props)}
          rows={5}
          columns={30}
        />
        <DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={fieldName} />
      </>
    );
  };

  const internalOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value);
    updateLocalRelatedNotes(props.rowIndex, "internal", event.target.value.name);
  };

  const validateTable = async () => {
    const results = await validate(localRelatedNotes, 'note', validationService);
    const errors = [];
    let anyErrors = false;
    results.forEach((result, index) => {
      const { isError, data } = result;
      if (isError) {
        errors[index] = {};
        if (!data) return;
        Object.keys(data).forEach((field) => {
          errors[index][field] = {
            severity: "error",
            message: data[field]
          };
        });
        anyErrors = true;
      }
    });
    setErrorMessages(errors);
    return anyErrors;
  };

  const saveDataHandler = async () => {
    setErrorMessages([]);
    const anyErrors = await validateTable();
    if (anyErrors) return;
    onChange(rowIndex, localRelatedNotes, relatedNotesData.rowProps);

    const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
    let messageObject = {
      severity: "warn",
      message: "Pending Edits!"
    };
    errorMessagesCopy[rowIndex] = {};
    errorMessagesCopy[rowIndex][errorField] = messageObject;
    dispatch({ type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "alleleGeneAssociations", errorMessages: errorMessagesCopy });
    setRelatedNotesData((relatedNotesData) => {
      return {
        ...relatedNotesData,
        dialogIsVisible: false,
      };
    });
    setLocalRelatedNotes([]);
  };

  const createNewNoteHandler = () => {
    let cnt = localRelatedNotes ? localRelatedNotes.length : 0;
    localRelatedNotes.push({
      dataKey: cnt,
      noteType: {
        name: ""
      },
      internal: false
    });
    let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
    setEditingRows(_editingRows);
  };

  const hideDialog = () => {
    setErrorMessages([]);
    setRelatedNotesData((relatedNotesData) => {
      return {
        ...relatedNotesData,
        dialogIsVisible: false,
      };
    });
    let _localRelatedNotes = [];
    setLocalRelatedNotes(_localRelatedNotes);
  };

  const footerTemplate = () => {
    return (
      <div>
        <Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
        <Button label="New Note" icon="pi pi-plus" onClick={createNewNoteHandler}
          disabled={singleValue && localRelatedNotes.length > 0} />
        <Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} />
      </div>
    );
  };

  let headerGroup = <ColumnGroup>
    <Row>
      <Column header="Actions" />
      <Column header="Note Type" />
      <Column header="Text" />
      <Column header="Internal" />
    </Row>
  </ColumnGroup>;

  const onRowEditChange = (e) => {
    return null;
  };


  return (
    <div>
      <Dialog visible={dialogIsVisible} className='w-8' modal onHide={hideDialog} closable onShow={showDialogHandler} footer={footerTemplate}>
        <h3>Related Notes</h3>
        <DataTable value={localRelatedNotes} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
          editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef}>
          <Column editor={(props) => <DeleteAction deletionHandler={deletionHandler} index={props.rowIndex} />}
            className='max-w-4rem' bodyClassName="text-center" headerClassName='surface-0' frozen />
          <Column editor={noteTypeEditor} field="noteType.name" header="Note Type" headerClassName='surface-0' />
          <Column
            editor={(props) => freeTextEditor(props, "freeText", errorMessages)}
            field="freeText"
            header="Text"
            headerClassName='surface-0'
            className='wrap-word max-w-35rem'
          />
          <Column
            editor={(props) => {
              return <InternalEditor
                props={props}
                errorMessages={errorMessages}
                internalOnChangeHandler={internalOnChangeHandler}
                rowIndex={props.rowData.rowIndex}
              />;
            }}
            field="internal" header="Internal" headerClassName='surface-0' />
        </DataTable>
      </Dialog>
    </div>
  );

};
