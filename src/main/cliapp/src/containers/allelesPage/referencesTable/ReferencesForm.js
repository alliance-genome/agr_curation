import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { ReferencesFormTable } from "./ReferencesFormTable";
import { useRef } from "react";
import { generateCrossRefSearchField } from "../utils";

export const ReferencesForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);

  // dispatch({type: 'EDIT', value: filterableReferences});



  const createNewReferenceHandler = (e) => {
    e.preventDefault();
    const dataKey = state.allele.references?.length;
    //todo: add filter field in here
    const newReference = {
      dataKey: dataKey,
    };

    dispatch({
      type: "ADD_ROW",
      row: newReference,
      entityType: "references",
    });
  };


  const referencesOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    //todo: add filter field in here

    if (typeof event.target.value === 'string') return;
    const searchString = generateCrossRefSearchField(event.target.value);

    const newReference = {
      ...event.target.value,
      shortCitation: event.target.value.short_citation,
      dataKey: props.rowIndex,
      crossReferencesFilter: searchString
    };
    console.log("in onChange", newReference);

    dispatch({
      type: 'REPLACE_ROW',
      entityType: 'references',
      index: props.rowIndex,
      value: newReference
    });
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const deletionHandler = (e, index) => {
    e.preventDefault();
    dispatch({ type: "DELETE_ROW", entityType: "references", index: index });
    dispatch({ type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "references", errorMessages: [] });
  };

  return (
    <FormTableWrapper
      table={
        <ReferencesFormTable
          references={state.allele.references}
          editingRows={state.entityStates.references.editingRows}
          errorMessages={state.entityStates.references.errorMessages}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          referencesOnChangeHandler={referencesOnChangeHandler}
          deletionHandler={deletionHandler}
        />
      }
      tableName=""
      showTable={state.entityStates.references.show}
      button={<Button label="Add Reference" onClick={createNewReferenceHandler} className="w-6" />}
    />
  );
};