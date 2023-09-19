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
      showType: "showMutationTypes", 
      row: newMutationType, 
      tableType: "alleleMutationTypes", 
      editingRowsType: "mutationTypesEditingRows" 
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
      tableType: 'alleleMutationTypes', 
      index: props.rowIndex, 
      field: "mutationTypes", 
      value: event.target.value
    });
  }

  const internalOnChangeHandler = (props, event) => {
    dispatch({ 
      type: 'EDIT_ROW', 
      tableType: 'alleleMutationTypes', 
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
      tableType: 'alleleMutationTypes', 
      index: props.rowIndex, 
      field: "evidence", 
      value: event.target.value
    });
  }

  const deletionHandler  = (e, index) => {
    e.preventDefault();
    dispatch({type: "DELETE_ROW", tableType: "alleleMutationTypes", showType: "showMutationTypes", index: index});
    dispatch({type: "UPDATE_ERROR_MESSAGES", errorType: "mutationTypesErrorMessages", errorMessages: []});
  };

  return (
    <FormTableWrapper
      table={
        <MutationTypesFormTable
          mutationTypes={state.allele?.alleleMutationTypes}
          editingRows={state.mutationTypesEditingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.mutationTypesErrorMessages}
          mutationTypesOnChangeHandler={mutationTypesOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Mutation Types"
      showTable={state.showMutationTypes}
      button={<Button label="Add Mutation Type" onClick={createNewMutationTypeHandler} className="w-6"/>}
    />
  );

};