import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { useRef } from "react";
import { SymbolFormTable } from "./SymbolFormTable";

export const SymbolForm = ({ labelColumnSize, state, dispatch }) => {
  const tableRef = useRef(null);

  const symbols = [state.allele?.alleleSymbol];

  const createNewSymbolHandler = (e) => {
    e.preventDefault();
    const newSymbol = {
      dataKey: 0,
      synonymUrl: "",
      internal: false,
      nameType: null,
      formatText: "",
      displayText: ""
    }

    dispatch({
      type: "ADD_OBJECT", 
      value: newSymbol, 
      entityType: "alleleSymbol", 
    })
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const nameTypeOnChangeHandler = (props, event) => {
     // TODO -- replace with props.editorCallback() after PrimeReact upgrade
     props.rowData.nameType = event.target.value;
    dispatch({ 
      type: 'EDIT_OBJECT', 
      entityType: 'alleleSymbol', 
      field: "nameType", 
      value: event.target.value
    });
  };

  const internalOnChangeHandler = (props, event) => {
     // TODO -- replace with props.editorCallback() after PrimeReact upgrade
     props.rowData.internal = event.target.value;
    dispatch({ 
      type: 'EDIT_OBJECT', 
      entityType: 'alleleSymbol', 
      field: "internal", 
      value: event.target?.value?.name
    });
  };

  const synonymScopeOnChangeHandler = (props, event) => {
    // TODO -- replace with props.editorCallback() after PrimeReact upgrade
    props.rowData.synonymScope = event.target.value;
    dispatch({ 
      type: 'EDIT_OBJECT', 
      entityType: 'alleleSymbol', 
      field: "synonymScope", 
      value: event.target.value
    });
  };

  const textOnChangeHandler = (rowIndex, event, field) => {
    dispatch({ 
      type: 'EDIT_OBJECT', 
      entityType: 'alleleSymbol', 
      field: field, 
      value: event.target.value
    });
  }

  const evidenceOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({ 
      type: 'EDIT_OBJECT', 
      entityType: 'alleleSymbol', 
      field: "evidence", 
      value: event.target.value
    });
  }

  const deletionHandler  = (e) => {
    e.preventDefault();
    dispatch({type: "DELETE_OBJECT", entityType: "alleleSymbol"});
    dispatch({type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "alleleSymbol", errorMessages: []});
  };

  return (
    <FormTableWrapper
      labelColumnSize={labelColumnSize}
      table={
        <SymbolFormTable
          name={symbols}
          editingRows={state.entityStates.alleleSymbol.editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates.alleleSymbol.errorMessages}
          textOnChangeHandler={textOnChangeHandler}
          synonymScopeOnChangeHandler={synonymScopeOnChangeHandler}
          nameTypeOnChangeHandler={nameTypeOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Symbol"
      showTable={state.entityStates.alleleSymbol.show}
      button={<Button label="Add Symbol" onClick={createNewSymbolHandler} disabled={state.allele?.alleleSymbol} className="w-6"/>}
    />
  );

};