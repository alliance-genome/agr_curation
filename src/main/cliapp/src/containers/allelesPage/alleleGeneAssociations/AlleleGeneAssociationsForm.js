import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { useRef } from "react";
import { AlleleGeneAssociationsFormTable } from "./AlleleGeneAssociationsFormTable";

export const AlleleGeneAssociationsForm = ({ labelColumnSize, state, dispatch }) => {
  const tableRef = useRef(null);
  const alleleGeneAssociations = global.structuredClone(state.allele?.alleleGeneAssociations);

  const createNewGeneAssociationHandler = (e) => {
    e.preventDefault();
    const dataKey = state.allele.alleleGeneAssociations?.length;
    const newAlleleGeneAssociation = {
      dataKey: dataKey,
    };

    dispatch({
      type: "ADD_ROW",
      row: newAlleleGeneAssociation,
      entityType: "alleleGeneAssociations",
    });
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const alleleGeneRelationOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value);
    dispatch({
      type: 'EDIT_ROW',
      entityType: 'alleleGeneAssociations',
      index: props.rowIndex,
      field: "alleleGeneRelation",
      value: event.target.value
    });
  };

  const geneOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({
      type: 'EDIT_ROW',
      entityType: 'alleleGeneAssociations',
      index: props.rowIndex,
      field: "geneObject",
      value: event.target?.value?.curie
    });
  };

  const evidenceCodeOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({
      type: 'EDIT_ROW',
      entityType: 'alleleGeneAssociations',
      index: props.rowIndex,
      field: "evidenceCode",
      value: event.target.value
    });
  };


  const evidenceOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({
      type: 'EDIT_ROW',
      entityType: 'alleleGeneAssociations',
      index: props.rowIndex,
      field: "evidence",
      value: event.target.value
    });
  };

  const deletionHandler = (e, index) => {
    e.preventDefault();
    dispatch({ type: "DELETE_ROW", entityType: "alleleGeneAssociations", index: index });
    dispatch({ type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "alleleGeneAssociations", errorMessages: [] });
  };

  return (
    <FormTableWrapper
      labelColumnSize={labelColumnSize}
      table={
        <AlleleGeneAssociationsFormTable
          alleleGeneAssociations={alleleGeneAssociations}
          editingRows={state.entityStates.alleleGeneAssociations.editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates.alleleGeneAssociations.errorMessages}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
          alleleGeneRelationOnChangeHandler={alleleGeneRelationOnChangeHandler}
          geneOnChangeHandler={geneOnChangeHandler}
          evidenceCodeOnChangeHandler={evidenceCodeOnChangeHandler}
          dispatch={dispatch}
        />
      }
      tableName="Allele Gene Associations"
      showTable={state.entityStates.alleleGeneAssociations.show}
      button={<Button label="Add Gene Association" onClick={createNewGeneAssociationHandler} className="w-6" />}
    />
  );

};