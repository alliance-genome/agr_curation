import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { ReferencesFormTable } from "./ReferencesFormTable";
import { useRef, useState } from "react";
import { generateCrossRefSearchField } from "../utils";
import { SingleReferenceFormEditor } from "../../../components/Editors/references/SingleReferenceFormEditor";

export const ReferencesForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);


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
          deletionHandler={deletionHandler}
        />
      }
      tableName=""
      showTable={state.entityStates.references.show}
      button={
          <ReferenceFieldAndButton
            state={state}
            dispatch={dispatch}
          />
      }
    />
  );
};

function ReferenceFieldAndButton({ state, dispatch }) {
  const [reference, setReference] = useState(null);

  const createNewReferenceHandler = (event) => {
    event.preventDefault();

    if (!reference || typeof reference === 'string'){

      const errorMessages = {
        references: "Must select reference from dropdown",
      };
      dispatch({
        type: "UPDATE_TABLE_ERROR_MESSAGES",
        entityType: "references",
        errorMessages,
      });

      return;
    } 

    const dataKey = state.allele.references?.length;
    const searchString = generateCrossRefSearchField(reference);

    const newReference = {
      ...reference,
      shortCitation: reference.short_citation,
      dataKey: dataKey,
      crossReferencesFilter: searchString
    };

    dispatch({
      type: "ADD_ROW",
      row: newReference,
      entityType: "references",
    });
    setReference(null);
    dispatch({
      type: "UPDATE_TABLE_ERROR_MESSAGES",
      entityType: "references",
      errorMessages: {},
    });
  };


  const referencesOnChangeHandler = (event) => {
    setReference(event.target.value);
  };
  return (
    <>
      <SingleReferenceFormEditor
        reference={reference}
        onReferenceValueChange={referencesOnChangeHandler}
        errorMessages={state.entityStates.references.errorMessages} />
      <Button label="Add Reference" onClick={createNewReferenceHandler} className="w-6" />
    </>
  );
}
