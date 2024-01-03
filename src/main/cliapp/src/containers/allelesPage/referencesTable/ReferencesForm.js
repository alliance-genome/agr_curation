import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { ReferencesFormTable } from "./ReferencesFormTable";
import { useRef } from "react";
import { NewReferenceField } from "./NewReferenceField";

export const ReferencesForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);
  const entityType = "references";


  const onRowEditChange = (e) => {
    return null;
  };

  const deletionHandler = (e, dataKey) => {
    e.preventDefault();
    dispatch({ type: "DELETE_ROW", entityType: entityType, dataKey });
  };

  return (
    <FormTableWrapper
      table={
        <ReferencesFormTable
          references={state.allele[entityType]}
          editingRows={state.entityStates[entityType].editingRows}
          errorMessages={state.entityStates[entityType].errorMessages}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          className="w-1"
        />
      }
      tableName="References"
      showTable={state.entityStates[entityType].show}
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

