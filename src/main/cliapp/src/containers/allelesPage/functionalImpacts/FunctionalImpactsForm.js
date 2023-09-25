import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { useRef } from "react";
import { FunctionalImpactsFormTable } from "./FunctionalImpactsFormTable";

export const FunctionalImpactsForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);

  const createNewFunctionalImpactsHandler = (e) => {
    e.preventDefault();
    const dataKey = state.allele.alleleFunctionalImpacts?.length;
    const newFunctionalImpact = {
      dataKey: dataKey,
      phenotypeStatement: "",
      internal: false,
    };

    dispatch({
      type: "ADD_ROW",
      row: newFunctionalImpact,
      entityType: "alleleFunctionalImpacts",
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
      entityType: 'alleleFunctionalImpacts',
      index: props.rowIndex,
      field: "functionalImpacts",
      value: event.target.value
    });
  };

  const phenotypeTermOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({
      type: 'EDIT_ROW',
      entityType: 'alleleFunctionalImpacts',
      index: props.rowIndex,
      field: "phenotypeTerm",
      value: event.target.value
    });
  };

  const phenotypeStatementOnChangeHandler = (rowIndex, event) => {
    dispatch({
      type: 'EDIT_ROW',
      entityType: 'alleleFunctionalImpacts',
      index: rowIndex,
      field: "phenotypeStatement",
      value: event.target.value
    });
  };
  const internalOnChangeHandler = (props, event) => {
    dispatch({
      type: 'EDIT_ROW',
      entityType: 'alleleFunctionalImacts',
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
      entityType: 'alleleFunctionalImpacts',
      index: props.rowIndex,
      field: "evidence",
      value: event.target.value
    });
  };

  const deletionHandler = (event, index) => {
    event.preventDefault();
    dispatch({ type: "DELETE_ROW", entityType: "alleleFunctionalImpacts", index: index });
    dispatch({ type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "alleleFunctionalImpacts", errorMessages: [] });
  };

  return (
    <FormTableWrapper
      table={
        <FunctionalImpactsFormTable
          functionalImpacts={state.allele?.alleleFunctionalImpacts}
          editingRows={state.entityStates.alleleFunctionalImpacts.editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates.alleleFunctionalImpacts.errorMessages}
          functionalImpactsOnChangeHandler={functionalImpactsOnChangeHandler}
          phenotypeTermOnChangeHandler={phenotypeTermOnChangeHandler}
          phenotypeStatementOnChangeHandler={phenotypeStatementOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Functional Impacts"
      showTable={state.entityStates.alleleFunctionalImpacts.show}
      button={<Button label="Add Functional Impact" onClick={createNewFunctionalImpactsHandler} className="w-6" />}
    />
  );

};