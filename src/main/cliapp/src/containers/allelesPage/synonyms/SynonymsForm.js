import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { SynonymsFormTable } from "./SynonymsFormTable";
import { useRef } from "react";

export const SynonymsForm = ({ labelColumnSize, state, dispatch }) => {
  const tableRef = useRef(null);

  const createNewSynonymHandler = (e) => {
    e.preventDefault();
    const dataKey = state.allele.alleleSynonyms?.length;
    const newSynonym = {
      dataKey: dataKey,
      synonymUrl: "",
      internal: false,
      obsolete: false,
      nameType: null,
      formatText: "",
      displayText: ""
    }

    dispatch({
      type: "ADD_ROW", 
      row: newSynonym, 
      entityType: "alleleSynonyms", 
    })
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const nameTypeOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleSynonyms', 
      index: props.rowIndex, 
      field: "nameType", 
      value: event.target.value
    });
  };

  const internalOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value?.name);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleSynonyms', 
      index: props.rowIndex, 
      field: "internal", 
      value: event.target?.value?.name
    });
  };

  const synonymScopeOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleSynonyms', 
      index: props.rowIndex, 
      field: "synonymScope", 
      value: event.target.value
    });
  };

  const textOnChangeHandler = (rowIndex, event, field) => {
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleSynonyms', 
      index: rowIndex, 
      field: field, 
      value: event.target.value
    });
  }

  const evidenceOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleSynonyms', 
      index: props.rowIndex, 
      field: "evidence", 
      value: event.target.value
    });
  }

  const deletionHandler  = (e, index) => {
    e.preventDefault();
    dispatch({type: "DELETE_ROW", entityType: "alleleSynonyms", index: index});
    dispatch({type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "alleleSynonyms", errorMessages: []});
  };

  return (
    <FormTableWrapper
      labelColumnSize={labelColumnSize}
      table={
        <SynonymsFormTable
          synonyms={state.allele?.alleleSynonyms}
          editingRows={state.entityStates.alleleSynonyms.editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates.alleleSynonyms.errorMessages}
          textOnChangeHandler={textOnChangeHandler}
          synonymScopeOnChangeHandler={synonymScopeOnChangeHandler}
          nameTypeOnChangeHandler={nameTypeOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Synonyms"
      showTable={state.entityStates.alleleSynonyms.show}
      button={<Button label="Add Synonym" onClick={createNewSynonymHandler} className="w-6"/>}
    />
  );

};