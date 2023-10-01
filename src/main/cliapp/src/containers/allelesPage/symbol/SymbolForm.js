import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { useRef } from "react";
import { SymbolFormTable } from "./SymbolFormTable";

export const SymbolForm = ({ labelColumnSize, state, dispatch }) => {
  const tableRef = useRef(null);

  const symbols = [state.allele?.alleleSymbol];
  
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

  return (
    <FormTableWrapper
      labelColumnSize={labelColumnSize}
      table={
        <SymbolFormTable
          name={symbols}
          editingRows={state.entityStates.alleleSymbol.editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
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
    />
  );
};