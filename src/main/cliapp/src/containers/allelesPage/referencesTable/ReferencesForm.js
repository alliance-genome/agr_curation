import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { ReferencesFormTable } from "./ReferencesFormTable";
import { useRef } from "react";

export const ReferencesForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);

  const deletionHandler  = (e, index) => {
    e.preventDefault();
    dispatch({type: "DELETE_ROW", entityType: "references", index: index});
  };

  return (
    <FormTableWrapper
      table={
        <ReferencesFormTable
          references={state.allele?.references}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
        />
      }
      tableName=""
      showTable={state.entityStates.references.show}
      button={null}
    />
  );

};