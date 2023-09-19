import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { MutationTypesFormTable } from "./MutationTypesFormTable";
import { useRef } from "react";

export const SynonymsForm = ({ labelColumnSize, state, dispatch }) => {
  const tableRef = useRef(null);

  const createNewMutationTypeHandler = (e) => {
    e.preventDefault();
    const dataKey = state.alleleMutationTypes?.length;
    const newSynonym = {
      dataKey: dataKey,
      internal: false,
    }

    dispatch({
      type: "ADD_ROW", 
      showType: "showMutationTypes", 
      row: newSynonym, 
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
      field: "evidence", 
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
  };

  return (
    <FormTableWrapper
      labelColumnSize={labelColumnSize}
      table={
        <MutationTypesFormTable
          synonyms={state.allele?.alleleSynonyms}
          editingRows={state.synonymsEditingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.synonymsErrorMessages}
          mutationTypesOnChangeHandler={mutationTypesOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Mutation Types"
      showTable={state.showSynonyms}
      button={<Button label="Add Mutation Type" onClick={createNewMutationTypeHandler} className="w-6"/>}
    />
  );

};