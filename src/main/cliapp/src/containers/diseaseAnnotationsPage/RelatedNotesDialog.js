import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { InputTextAreaEditor } from '../../components/InputTextAreaEditor';
import { ErrorMessageComponent } from '../../components/ErrorMessageComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ValidationService } from '../../service/ValidationService';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';

export const RelatedNotesDialog = ({
  relatedNotesData,
  setRelatedNotesData,
  relatedNotesRef,
  authState
}) => {
  const { relatedNotes, isInEdit, dialog, rowIndex, mainRowProps } = relatedNotesData;
  const [editingRows, setEditingRows] = useState({});
  const [errorMessages, setErrorMessages] = useState([]);
  const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
  const noteTypeTerms = useControlledVocabularyService('Disease annotation note types');
  const validationService = new ValidationService(authState);

  const tableRef = useRef(null);

  const showDialogHandler = () => {
    if (isInEdit) {
      let rowsObject = {};

      relatedNotes.forEach((note) => {
        rowsObject[`${note.id}`] = true;
      });

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
    setRelatedNotesData((relatedNotesData) => {
      return {
        ...relatedNotesData,
        dialog: false,
      };
    });
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
    const resultsArray = await validateNotes(relatedNotesRef.current);
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
      mainRowProps.rowData.relatedNotes = relatedNotesRef.current;
      let updatedAnnotations = [...mainRowProps.props.value];
      updatedAnnotations[rowIndex].relatedNotes = relatedNotesRef.current;
      keepDialogOpen = false;
    };

    setRelatedNotesData((relatedNotesData) => {
      return {
        ...relatedNotesData,
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


  const onInternalEditorValueChange = (props, event) => {
    relatedNotesRef.current[props.rowIndex].internal = event.value.name;
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
    relatedNotesRef.current[props.rowIndex].noteType = event.value;
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
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"noteType"} />
      </>
    );
  };

  const freeTextEditor = (props, fieldname, setRelatedNotesData, errorMessages, relatedNotesRef) => {
    if (errorMessages) {
      errorMessages.severity = "error";
    }
    return (
      <>
        <InputTextAreaEditor
          rowProps={props}
          fieldName={fieldname}
          setRelatedNotesData={setRelatedNotesData}
          relatedNotesRef={relatedNotesRef}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={fieldname} />
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
    <Dialog visible={dialog} className='w-6' modal onHide={hideDialog}
      closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
      <h3>Related Notes</h3>
      <DataTable value={relatedNotes} dataKey="id" showGridlines editMode='row'
        editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef}
      >
        <Column editor={noteTypeEditor} field="noteType.name" header="Note Type" />
        <Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} />
        <Column
          editor={(props) => freeTextEditor(props, "freeText", setRelatedNotesData, errorMessages, relatedNotesRef)}
          field="freeText"
          header="Text"
          body={textTemplate}
        />
      </DataTable>
    </Dialog>
  );
};
