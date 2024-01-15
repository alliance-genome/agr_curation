import { useRef } from "react";
import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { AlleleGeneAssociationsFormTable } from "./AlleleGeneAssociationsFormTable";
import { addDataKey, generateCurieSearchField } from "../utils";
import { processOptionalField } from "../../../utils/utils";

export const AlleleGeneAssociationsForm = ({ labelColumnSize, state, dispatch }) => {
  const tableRef = useRef(null);
  const entityType = "alleleGeneAssociations";
  const alleleGeneAssociations = global.structuredClone(state.allele?.[entityType]);

  const createNewGeneAssociationHandler = (e) => {
    e.preventDefault();
    const updatedErrorMessages = global.structuredClone(state.entityStates[entityType].errorMessages);
    const newAlleleGeneAssociation = {
      subject: state.allele,
    };

    addDataKey(newAlleleGeneAssociation);
    updatedErrorMessages[newAlleleGeneAssociation.dataKey] = {};

    dispatch({
      type: "ADD_ROW",
      row: newAlleleGeneAssociation,
      entityType: entityType,
    });

    dispatch({
      type: "UPDATE_TABLE_ERROR_MESSAGES",
      entityType: entityType,
      errorMessages: updatedErrorMessages
    });
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const alleleGeneRelationOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value);
    dispatch({
      type: 'EDIT_FILTERABLE_ROW',
      entityType: entityType,
      dataKey: props.rowData?.dataKey,
      field: "relation",
      value: event.target.value
    });
  };

  const relatedNoteOnChangeHandler = (value, props) => {
    props.editorCallback(value[0]);
    dispatch({
      type: 'EDIT_FILTERABLE_ROW',
      entityType: entityType,
      dataKey: props.rowData?.dataKey,
      field: "relatedNote",
      value: value[0]
    });
  };

  const geneOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({
      type: 'EDIT_FILTERABLE_ROW',
      entityType: entityType,
      dataKey: props.rowData?.dataKey,
      field: "objectGene",
      value: event.target.value
    });
  };

  const evidenceCodeOnChangeHandler = (event, setFieldValue, props) => {
    const value = processOptionalField(event.target.value);
    setFieldValue(value);
    dispatch({
      type: 'EDIT_FILTERABLE_ROW',
      entityType: entityType,
      dataKey: props.rowData?.dataKey,
      field: "evidenceCode",
      value: value
    });
  };


  const evidenceOnChangeHandler = (event, setFieldValue, props) => {
    const newEvidence = event.target.value;
    const dataKey = props.rowData?.dataKey;
    const aga = alleleGeneAssociations.find((aga) =>  aga.dataKey === dataKey );
    const newAssociation = {
      ...aga,
      evidence: newEvidence,
      evidenceCurieSearchFilter: generateCurieSearchField(newEvidence),
    };

    //updates value in table input box
    setFieldValue(newEvidence);

    dispatch({
      type: 'REPLACE_ROW',
      entityType: entityType,
      dataKey: dataKey,
      field: "evidence",
      newRow: newAssociation,
    });
  };

  const deletionHandler = (e, dataKey) => {
    e.preventDefault();
    const aga = alleleGeneAssociations.find((aga) =>  aga.dataKey === dataKey );
    const updatedErrorMessages = global.structuredClone(state.entityStates[entityType].errorMessages);
    delete updatedErrorMessages[dataKey];
    dispatch({ type: "DELETE_ROW", entityType: entityType, dataKey, id: aga.id });
    dispatch({ type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: entityType, errorMessages: updatedErrorMessages });
  };

  return (
    <FormTableWrapper
      labelColumnSize={labelColumnSize}
      table={
        <AlleleGeneAssociationsFormTable
          alleleGeneAssociations={alleleGeneAssociations}
          editingRows={state.entityStates[entityType].editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates[entityType].errorMessages}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
          alleleGeneRelationOnChangeHandler={alleleGeneRelationOnChangeHandler}
          geneOnChangeHandler={geneOnChangeHandler}
          relatedNoteOnChangeHandler={relatedNoteOnChangeHandler}
          evidenceCodeOnChangeHandler={evidenceCodeOnChangeHandler}
          dispatch={dispatch}
        />
      }
      tableName="Allele Gene Associations"
      showTable={state.entityStates[entityType].show}
      button={<Button label="Add Gene Association" onClick={createNewGeneAssociationHandler} className="w-6 p-button-text" />}
    />
  );

};