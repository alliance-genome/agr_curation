import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { InheritanceModesFormTable } from "../inheritanceModes/InheritanceModesFormTable";
import { useRef } from "react";
import { processOptionalField } from "../../../utils/utils";
import { addDataKey } from "../utils";

export const InheritanceModesForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);
  const entityType = "alleleInheritanceModes";
  const inheritanceModes = global.structuredClone(state.allele?.[entityType]);

  const createNewInheritanceModeHandler = (e) => {
    e.preventDefault();
    const newInheritanceMode = {
      internal: false,
      obsolete: false,
      inheritanceMode: null,
      phenotypeTerm: null,
      phenotypeStatement: ""
    }

    addDataKey(newInheritanceMode);

    dispatch({
      type: "ADD_ROW", 
      row: newInheritanceMode, 
      entityType: entityType, 
    })
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const inheritanceModeOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: entityType, 
      index: props.rowIndex, 
      field: "inheritanceMode", 
      value: event.target.value
    });
  };
  
  const phenotypeTermOnChangeHandler = (event, setFieldValue, props) => {
    const value = processOptionalField(event.target.value);

    //updates value in table input box
    setFieldValue(value);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: entityType, 
      index: props.rowIndex, 
      field: "phenotypeTerm", 
      value: value
    });
  }

  const internalOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value?.name);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: entityType, 
      index: props.rowIndex, 
      field: "internal", 
      value: event.target?.value?.name
    });
  };

  const evidenceOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: entityType, 
      index: props.rowIndex, 
      field: "evidence", 
      value: event.target.value
    });
  }

  const textOnChangeHandler = (rowIndex, event, field) => {
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: entityType, 
      index: rowIndex, 
      field: field, 
      value: event.target.value
    });
  }

  const deletionHandler = (e, dataKey) => {
    e.preventDefault();
    const updatedErrorMessages = global.structuredClone(state.entityStates[entityType].errorMessages);
    delete updatedErrorMessages[dataKey];
    dispatch({ type: "DELETE_ROW", entityType: entityType, dataKey });
    dispatch({ type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: entityType, errorMessages: updatedErrorMessages });
  };

  return (
    <FormTableWrapper
      table={
        <InheritanceModesFormTable
          inheritanceModes={inheritanceModes}
          editingRows={state.entityStates[entityType].editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates[entityType].errorMessages}
          inheritanceModeOnChangeHandler={inheritanceModeOnChangeHandler}
          phenotypeTermOnChangeHandler={phenotypeTermOnChangeHandler}
          textOnChangeHandler={textOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Inheritance Modes"
      showTable={state.entityStates[entityType].show}
      button={<Button label="Add Inheritance Mode" onClick={createNewInheritanceModeHandler} className="w-4  p-button-text"/>}
    />
  );

};