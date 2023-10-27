import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { RelatedNotesFormTable } from "../relatedNotes/RelatedNotesFormTable";
import { useRef } from "react";

export const RelatedNotesForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);

  const createNewRelatedNoteHandler = (e) => {
    e.preventDefault();
    const dataKey = state.allele.relatedNotes?.length;
    const newRelatedNote = {
      dataKey: dataKey,
      internal: false,
      obsolete: false,
      noteType: null,
      freeText: "",
      references: null
    }

    dispatch({
      type: "ADD_ROW", 
      row: newRelatedNote, 
      entityType: "relatedNotes", 
    })
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const noteTypeOnChangeHandler = (props, event) => {
    //todo -- add props.editorCallback() after PrimeReact upgrade 
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'relatedNotes', 
      index: props.rowIndex, 
      field: "noteType", 
      value: event.target.value
    });
  };
  
  const internalOnChangeHandler = (props, event) => {
    //todo -- add props.editorCallback() after PrimeReact upgrade 
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'relatedNotes', 
      index: props.rowIndex, 
      field: "internal", 
      value: event.target?.value?.name
    });
  };

  const referencesOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'relatedNotes', 
      index: props.rowIndex, 
      field: "references", 
      value: event.target.value
    });
  }

  const textOnChangeHandler = (rowIndex, event, field) => {
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'relatedNotes', 
      index: rowIndex, 
      field: field, 
      value: event.target.value
    });
  }

  const deletionHandler  = (e, index) => {
    e.preventDefault();
    dispatch({type: "DELETE_ROW", entityType: "relatedNotes", index: index});
    dispatch({type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "relatedNotes", errorMessages: []});
  };

  return (
    <FormTableWrapper
      table={
        <RelatedNotesFormTable
          relatedNotes={state.allele?.relatedNotes}
          editingRows={state.entityStates.relatedNotes.editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates.relatedNotes.errorMessages}
          noteTypeOnChangeHandler={noteTypeOnChangeHandler}
          textOnChangeHandler={textOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          referencesOnChangeHandler={referencesOnChangeHandler}
        />
      }
      tableName="Related Notes"
      showTable={state.entityStates.relatedNotes.show}
      button={<Button label="Add Related Note" onClick={createNewRelatedNoteHandler} className="w-6"/>}
    />
  );

};