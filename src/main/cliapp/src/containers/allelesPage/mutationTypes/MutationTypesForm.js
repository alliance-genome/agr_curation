import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { MutationTypesFormTable } from "./MutationTypesFormTable";
import { useRef } from "react";

export const MutationTypesForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);

  const createNewMutationTypeHandler = (e) => {
    e.preventDefault();
    const dataKey = state.allele.alleleMutationTypes?.length;
    const newMutationType = {
      dataKey: dataKey,
      internal: false,
    }

    dispatch({
      type: "ADD_ROW", 
      row: newMutationType, 
      entityType: "alleleMutationTypes", 
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
      entityType: 'alleleMutationTypes', 
      index: props.rowIndex, 
      field: "mutationTypes", 
      value: event.target.value
    });
  }

  const internalOnChangeHandler = (props, event) => {
    //termporary solution -- replace with props.editorCallback() after PrimeReact upgrade 
    props.rowData.internal = event.target.value;
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleMutationTypes', 
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
      entityType: 'alleleMutationTypes', 
      index: props.rowIndex, 
      field: "evidence", 
      value: event.target.value
    });
  }

  const deletionHandler  = (e, index) => {
    e.preventDefault();
    dispatch({type: "DELETE_ROW", entityType: "alleleMutationTypes", index: index});
    dispatch({type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "alleleMutationTypes", errorMessages: []});
  };

  return (
    <FormTableWrapper
      table={
        <MutationTypesFormTable
          mutationTypes={state.allele?.alleleMutationTypes}
          editingRows={state.entityStates.alleleMutationTypes.editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates.alleleMutationTypes.errorMessages}
          mutationTypesOnChangeHandler={mutationTypesOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Mutation Types"
      showTable={state.entityStates.alleleMutationTypes.show}
      button={<Button label="Add Mutation Type" onClick={createNewMutationTypeHandler} className="w-6"/>}
    />
  );

};