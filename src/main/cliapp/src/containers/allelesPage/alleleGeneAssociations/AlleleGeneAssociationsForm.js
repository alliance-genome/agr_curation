import { useRef } from "react";
import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { AlleleGeneAssociationsFormTable } from "./AlleleGeneAssociationsFormTable";
import { generateCurieSearchField } from "../utils";
import { processOptionalField } from "../../../utils/utils";

export const AlleleGeneAssociationsForm = ({ labelColumnSize, state, dispatch }) => {
  const tableRef = useRef(null);
  const alleleGeneAssociations = global.structuredClone(state.allele?.alleleGeneAssociations);

  const createNewGeneAssociationHandler = (e) => {
    e.preventDefault();
    const updatedErrorMessages = global.structuredClone(state.entityStates.alleleGeneAssociations.errorMessages);
    updatedErrorMessages.unshift({});
    const dataKey = Math.floor(Math.random() * 10000);
    const newAlleleGeneAssociation = {
      dataKey: dataKey,
      subject: state.allele,
    };

    dispatch({
      type: "ADD_ROW",
      row: newAlleleGeneAssociation,
      entityType: "alleleGeneAssociations",
    });
    
    dispatch({ 
      type: "UPDATE_TABLE_ERROR_MESSAGES", 
      entityType: "alleleGeneAssociations", 
      errorMessages: updatedErrorMessages 
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
      field: "relation",
      value: event.target.value
    });
  };

  const relatedNoteOnChangeHandler = (rowIndex, value, rowProps) => {
    rowProps.editorCallback(value[0]);
    dispatch({
      type: 'EDIT_ROW',
      entityType: 'alleleGeneAssociations',
      index: rowIndex,
      field: "relatedNote",
      value: value[0]
    });
  };

  const geneOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({
      type: 'EDIT_ROW',
      entityType: 'alleleGeneAssociations',
      index: props.rowIndex,
      field: "object",
      value: event.target.value
    });
  };

  const evidenceCodeOnChangeHandler = (event, setFieldValue, props) => {
    const value = processOptionalField(event.target.value);
    setFieldValue(value);
    dispatch({
      type: 'EDIT_ROW',
      entityType: 'alleleGeneAssociations',
      index: props.rowIndex,
      field: "evidenceCode",
      value: value
    });
  };


  const evidenceOnChangeHandler = (event, setFieldValue, props) => {
    const newEvidence = event.target.value;
    const newAssociation = {
      ...alleleGeneAssociations[props.rowIndex],
      evidence: newEvidence,
      evidenceCurieSearchFilter: generateCurieSearchField(newEvidence),
    }


    //updates value in table input box
    setFieldValue(newEvidence);

    dispatch({
      type: 'REPLACE_ROW',
      entityType: 'alleleGeneAssociations',
      index: props.rowIndex,
      field: "evidence",
      value: newAssociation,
    });
  };

  const deletionHandler = (e, index) => {
    e.preventDefault();
    const updatedErrorMessages = global.structuredClone(state.entityStates.alleleGeneAssociations.errorMessages);
    const agaId = alleleGeneAssociations[index].id;
    updatedErrorMessages.splice(index,1);
    dispatch({ type: "DELETE_ROW", entityType: "alleleGeneAssociations", index: index, id: agaId });
    dispatch({ type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "alleleGeneAssociations", errorMessages: updatedErrorMessages });
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
          relatedNoteOnChangeHandler={relatedNoteOnChangeHandler}
          evidenceCodeOnChangeHandler={evidenceCodeOnChangeHandler}
          dispatch={dispatch}
        />
      }
      tableName="Allele Gene Associations"
      showTable={state.entityStates.alleleGeneAssociations.show}
      button={<Button label="Add Gene Association" onClick={createNewGeneAssociationHandler} className="w-6 p-button-text" />}
    />
  );

};