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
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';

export const RelatedNotesDialog = ({
  relatedNotesData,
  setRelatedNotesData,
  relatedNotesRef,
}) => {
  const { relatedNotes, isInEdit, dialog, rowIndex, mainRowProps } = relatedNotesData;
  const [editingRows, setEditingRows] = useState({});
  const [errorMessages, setErrorMessages] = useState({});
  const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
  const noteTypeTerms = useControlledVocabularyService('Disease annotation note types');

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
    setRelatedNotesData((relatedNotesData) => {
      return {
        ...relatedNotesData,
        dialog: false,
      };
    });
  };

  const saveDataHandler = () => {
    mainRowProps.rowData.relatedNotes = relatedNotesRef.current;
    setRelatedNotesData((relatedNotesData) => {
      return {
        ...relatedNotesData,
        dialog: false,
      }
    }
    );
  };

  const internalTemplate = (rowData) => {
    return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
  };


  const onInternalEditorValueChange = (props, event) => {
    relatedNotesRef.current[props.rowIndex].internal = event.value;
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

  const textTemplate = (rowData) => {
    return <EllipsisTableCell>{rowData.freeText}</EllipsisTableCell>;
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
          showClear={true}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"noteType"} />
      </>
    );
  };

  const freeTextEditor = (props, fieldname, setRelatedNotesData, errorMessages, relatedNotesRef) => {
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

  const footerTemplate = (name) => {
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
      closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate}>
      <h3>Related Notes</h3>
      <DataTable value={relatedNotes} dataKey="id" showGridlines editMode='row'
        editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef}
      >
        <Column editor={noteTypeEditor} field="noteType.name" header="Note Type"></Column>
        <Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate}></Column>
        <Column editor={(props) => freeTextEditor(props, 'freeText', setRelatedNotesData, errorMessages, relatedNotesRef)} field="freeText" header="Text" body={textTemplate}></Column>
      </DataTable>
    </Dialog>
  );
};

