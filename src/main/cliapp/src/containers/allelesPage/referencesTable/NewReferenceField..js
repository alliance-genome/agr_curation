import { useState } from "react";
import { Button } from "primereact/button";
import { generateCrossRefSearchField } from "../utils";
import { SingleReferenceFormEditor } from "../../../components/Editors/references/SingleReferenceFormEditor";

export const NewReferenceField = ({ state, dispatch }) => {
  const [reference, setReference] = useState(null);

  const createNewReferenceHandler = (event) => {
    event.preventDefault();

    if (!reference || typeof reference === 'string') {

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
};
