import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { ReferencesFormTable } from "./ReferencesFormTable";
import { useRef } from "react";
import { NewReferenceField } from "./NewReferenceField.";

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
          className="w-1"
        />
      }
      tableName="References"
      showTable={state.entityStates.references.show}
      button={
          <NewReferenceField
            state={state}
            dispatch={dispatch}
          />
      }
      includeField={true}
    />
  );
};

