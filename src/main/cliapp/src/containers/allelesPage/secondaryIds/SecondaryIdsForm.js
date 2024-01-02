import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { SecondaryIdsFormTable } from "../secondaryIds/SecondaryIdsFormTable";
import { useRef } from "react";
import { addDataKey } from "../utils";

export const SecondaryIdsForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);
  const secondaryIds = global.structuredClone(state.allele?.alleleSecondaryIds);

  const createNewSecondaryIdHandler = (e) => {
    e.preventDefault();

    const newSecondaryId = {
      internal: false,
      obsolete: false,
      secondaryId: ""
    }

    addDataKey(newSecondaryId);

    dispatch({
      type: "ADD_ROW", 
      row: newSecondaryId, 
      entityType: "alleleSecondaryIds", 
    })
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const internalOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value?.name);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleSecondaryIds', 
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
      entityType: 'alleleSecondaryIds', 
      index: props.rowIndex, 
      field: "evidence", 
      value: event.target.value
    });
  }

  const textOnChangeHandler = (rowIndex, event, field) => {
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleSecondaryIds', 
      index: rowIndex, 
      field: field, 
      value: event.target.value
    });
  }

  const deletionHandler  = (e, index) => {
    e.preventDefault();
    dispatch({type: "DELETE_ROW", entityType: "alleleSecondaryIds", index: index});
    dispatch({type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "alleleSecondaryIds", errorMessages: []});
  };

  return (
    <FormTableWrapper
      table={
        <SecondaryIdsFormTable
          secondaryIds={secondaryIds}
          editingRows={state.entityStates.alleleSecondaryIds.editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates.alleleSecondaryIds.errorMessages}
          textOnChangeHandler={textOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Secondary IDs"
      showTable={state.entityStates.alleleSecondaryIds.show}
      button={<Button label="Add Secondary ID" onClick={createNewSecondaryIdHandler} className="w-4 p-button-text"/>}
    />
  );

};