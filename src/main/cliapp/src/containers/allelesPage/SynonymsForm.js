import { Button } from "primereact/button";
import { FormTableWrapper } from "../../components/FormTableWrapper";
import { SynonymsFormTable } from "./synonyms/SynonymsFormTable";
import { useRef } from "react";

export const SynonymsForm = ({ labelColumnSize, state, dispatch }) => {
  const tableRef = useRef(null);

  const createNewSynonymHandler = (e) => {
    e.preventDefault();
    const dataKey = state.alleleSynonyms?.length;
    const newSynonym = {
      dataKey: dataKey,
      synonymUrl: "",
      updatedBy: {},
      dateUpdated: "",
      internal: false,
      obsolete: false,
      dbDateCreated: "",
      dbDateUpdated: "",
      nameType: {},
      formatText: "",
      displayText: ""
    }

    dispatch({
      type: "ADD_ROW", 
      showType: "showSynonyms", 
      row: newSynonym, 
      tableType: "alleleSynonyms", 
      editingRowsType: "synonymsEditingRows" 
    })
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const nameTypeOnChangeHandler = (event, rowIndex, field) => {
    dispatch({ 
      type: 'EDIT_ROW', 
      tableType: 'alleleSynonyms', 
      index: rowIndex, 
      field: field, 
      value: event.target.value
    });
  };

  const internalOnChangeHandler = (event, rowIndex, field) => {
    dispatch({ 
      type: 'EDIT_ROW', 
      tableType: 'alleleSynonyms', 
      index: rowIndex, 
      field: field, 
      value: event.target.value.name
    });
  };

  const synonymScopeOnChangeHandler = (event, rowIndex, field) => {
    dispatch({ 
      type: 'EDIT_ROW', 
      tableType: 'alleleSynonyms', 
      index: rowIndex, 
      field: field, 
      value: event.target.value
    });
  };

  const textOnChangeHandler = (rowIndex, event, field) => {
    dispatch({ 
      type: 'EDIT_ROW', 
      tableType: 'alleleSynonyms', 
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
      tableType: 'alleleSynonyms', 
      index: props.rowIndex, 
      field: "evidence", 
      value: event.target.value
    });
  }

  const nameTypeEditor = (e) => {
    return null;
  };

  const onRowEditCancel = (e) => {
    return null;
  };

  const onRowEditSave = (e) => {
    return null;
  };

  const deletionHandler  = (e, index) => {
    e.preventDefault();
    dispatch({type: "DELETE_ROW", tableType: "alleleSynonyms", showType: "showSynonyms", index: index});
  };

  return (
    <FormTableWrapper
      labelColumnSize={labelColumnSize}
      table={
        <SynonymsFormTable
          synonyms={state.allele?.alleleSynonyms}
          editingRows={state.synonymsEditingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          onRowEditCancel={onRowEditCancel}
          onRowEditSave={onRowEditSave}
          deletionHandler={deletionHandler}
          errorMessages={state.synonymsErrorMessages}
          nameTypeEditor={nameTypeEditor}
          textOnChangeHandler={textOnChangeHandler}
          synonymScopeOnChangeHandler={synonymScopeOnChangeHandler}
          nameTypeOnChangeHandler={nameTypeOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Synonyms"
      showTable={state.showSynonyms}
      button={<Button label="Add Synonym" onClick={createNewSynonymHandler} style={{ width: "50%" }} />}
    />
  );

};