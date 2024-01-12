import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { MutationTypesFormTable } from "./MutationTypesFormTable";
import { useRef } from "react";
import { addDataKey } from "../utils";

export const MutationTypesForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);
  const entityType = "alleleMutationTypes";
  const mutationTypes = global.structuredClone(state.allele?.[entityType]);

  const createNewMutationTypeHandler = (e) => {
    e.preventDefault();
    const newMutationType = {
      internal: false,
    }

    addDataKey(newMutationType);

    dispatch({
      type: "ADD_ROW", 
      row: newMutationType, 
      entityType: entityType, 
    })
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const mutationTypesOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: entityType, 
      index: props.rowIndex, 
      field: "mutationTypes", 
      value: event.target.value
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
        <MutationTypesFormTable
          mutationTypes={mutationTypes}
          editingRows={state.entityStates[entityType].editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates[entityType].errorMessages}
          mutationTypesOnChangeHandler={mutationTypesOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Mutation Types"
      showTable={state.entityStates[entityType].show}
      button={<Button label="Add Mutation Type" onClick={createNewMutationTypeHandler} className="w-4  p-button-text"/>}
    />
  );

};