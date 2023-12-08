import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from './Actions/DeletionAction';
import { InputTextAreaEditor } from './InputTextAreaEditor';
import { DialogErrorMessageComponent } from './Error/DialogErrorMessageComponent';
import { InternalEditor } from './Editors/InternalEditor';
import { EllipsisTableCell } from './EllipsisTableCell';
import { TrueFalseDropdown } from './TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../service/useControlledVocabularyService';
import { useVocabularyTermSetService } from '../service/useVocabularyTermSetService';
import { ValidationService } from '../service/ValidationService';
import { ControlledVocabularyDropdown } from './ControlledVocabularySelector';
import { autocompleteSearch, buildAutocompleteFilter, multipleAutocompleteOnChange, getRefStrings } from '../utils/utils';
import { LiteratureAutocompleteTemplate } from './Autocomplete/LiteratureAutocompleteTemplate';
import { ListTableCell } from './ListTableCell';
import { AutocompleteMultiEditor } from './Autocomplete/AutocompleteMultiEditor';
import { SearchService } from '../service/SearchService';

export const RelatedNotesDialogEditOnly = ({
  relatedNotesData,
  errorMessagesMainRow,
  setErrorMessagesMainRow,
  setRelatedNotesData,
}) => {
  const {
    originalRelatedNotes,
    dialogIsVisible,
    rowIndex,
  } = relatedNotesData;
	const [errorMessages, setErrorMessages] = useState([]);
  const tableRef = useRef(null);
  //todo: may need to move or change to state object
  const [editingRows, setEditingRows] = useState({});
  //todo: may need to change
  const noteTypeTerms = useVocabularyTermSetService("allele_genomic_entity_association_note_type");
  //todo: may need to move or change to state object
  const [localRelatedNotes, setLocalRelatedNotes] = useState([]);

  //todo: pull out into a util method? Is this used elsewhere?
  const cloneNotes = (clonableNotes) => {
    let _clonableNotes = global.structuredClone(clonableNotes) || [{ dataKey: 0 }];
    _clonableNotes.forEach((note, index) => {
      note.dataKey = index;
    });
    return _clonableNotes;
  };

  const showDialogHandler = () => {
    let _localRelatedNotes = cloneNotes(originalRelatedNotes);
    setLocalRelatedNotes(_localRelatedNotes);
    console.log("_localRelatedNotes", _localRelatedNotes);

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
		setLocalRelatedNotes(_localRelatedNotes);
  };

  const updateLocalRelatedNotes = (index, field, value) => {
    const _localRelatedNotes = global.structuredClone(localRelatedNotes);
    _localRelatedNotes[index][field] = value;
    setLocalRelatedNotes(_localRelatedNotes);
  }
//Editors section
  const onNoteTypeEditorValueChange = (props, event) => {
    props.editorCallback(event.target.value);
    //todo: props.index exist?
    //todo: noteType correct?
    updateLocalRelatedNotes(props.index, "noteType", event.target.value);
  };

  //todo: pull into it's own component?
  //is there one already?
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
    //todo: props.index exist?
    //todo: freeText correct?
    updateLocalRelatedNotes(props.index, "freeText", event.target.value);
	};

  //todo: is this or could this be it's own component?
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
    //todo: props.index exist?
    //todo: internal correct?
    updateLocalRelatedNotes(props.index, "internal", event.target.value);
	}

  const saveDataHandler = () => {
    //todo:
    //will call the reducer update method in here if there are no errors
    // setErrorMessages([]);
    // for (const note of localRelatedNotes) {
    // 	delete note.dataKey;
    // }
    // mainRowProps.rowData.relatedNotes = localRelatedNotes;
    // let updatedAnnotations = [...mainRowProps.props.value];
    // updatedAnnotations[rowIndex].relatedNotes = localRelatedNotes;

    // const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
    // let messageObject = {
    // 	severity: "warn",
    // 	message: "Pending Edits!"
    // };
    // errorMessagesCopy[rowIndex] = {};
    // errorMessagesCopy[rowIndex]["relatedNotes"] = messageObject;
    // dispatch({ type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "alleleGeneAssociations", errorMessages: [] });
    // setErrorMessagesMainRow({...errorMessagesCopy});

    // setOriginalRelatedNotesData((originalRelatedNotesData) => {
    // 		return {
    // 			...originalRelatedNotesData,
    // 			dialog: false,
    // 		}
    // 	}
    // );
  };

  //todo: this will need to be updated
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


  console.log("editingRows", editingRows);
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
