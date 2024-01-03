import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { useRef } from "react";
import { FunctionalImpactsFormTable } from "./FunctionalImpactsFormTable";
import { processOptionalField } from "../../../utils/utils";
import { addDataKey } from "../utils";

export const FunctionalImpactsForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);
  const entityType = "alleleFunctionalImpacts";
  const functionalImpacts = global.structuredClone(state.allele?.[entityType]);

  const createNewFunctionalImpactsHandler = (e) => {
    e.preventDefault();
    const newFunctionalImpact = {
      phenotypeStatement: "",
      internal: false,
    };

    addDataKey(newFunctionalImpact);

    dispatch({
      type: "ADD_ROW",
      row: newFunctionalImpact,
      entityType: entityType,
    });
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const functionalImpactsOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({
      type: 'EDIT_ROW',
      entityType: entityType,
      index: props.rowIndex,
      field: "functionalImpacts",
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
      value: value,
    });
  };

  const phenotypeStatementOnChangeHandler = (rowIndex, event) => {
    dispatch({
      type: 'EDIT_ROW',
      entityType: entityType,
      index: rowIndex,
      field: "phenotypeStatement",
      value: event.target.value
    });
  };
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
  };

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
        <FunctionalImpactsFormTable
          functionalImpacts={functionalImpacts}
          editingRows={state.entityStates[entityType].editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates[entityType].errorMessages}
          functionalImpactsOnChangeHandler={functionalImpactsOnChangeHandler}
          phenotypeTermOnChangeHandler={phenotypeTermOnChangeHandler}
          phenotypeStatementOnChangeHandler={phenotypeStatementOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Functional Impacts"
      showTable={state.entityStates[entityType].show}
      button={<Button label="Add Functional Impact" onClick={createNewFunctionalImpactsHandler} className="w-4 p-button-text" />}
    />
  );

};