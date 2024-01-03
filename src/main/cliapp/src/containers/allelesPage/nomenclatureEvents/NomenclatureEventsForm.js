import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { NomenclatureEventsFormTable } from "../nomenclatureEvents/NomenclatureEventsFormTable";
import { useRef } from "react";
import { addDataKey } from "../utils";

export const NomenclatureEventsForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);
  const entityType = "alleleNomenclatureEvents";
  const nomenclatureEvents = global.structuredClone(state.allele?.[entityType]);

  const createNewNomenclatureEventHandler = (e) => {
    e.preventDefault();
    const newNomenclatureEvent = {
      internal: false,
      obsolete: false,
      nomenclatureEvent: null
    }

    addDataKey(newNomenclatureEvent);

    dispatch({
      type: "ADD_ROW", 
      row: newNomenclatureEvent, 
      entityType: entityType, 
    })
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const nomenclatureEventOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: entityType, 
      index: props.rowIndex, 
      field: "nomenclatureEvent", 
      value: event.target.value
    });
  };
  
  const internalOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value?.name);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: entityType, 
      index: props.rowIndex, 
      field: "internal", 
      value: event.target?.value?.name
    });
  };

  const obsoleteOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value?.name);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: entityType, 
      index: props.rowIndex, 
      field: "obsolete", 
      value: event.target?.value?.name
    });
  };

  const evidenceOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: entityType, 
      index: props.rowIndex, 
      field: "evidence", 
      value: event.target.value
    });
  }

  const deletionHandler = (e, dataKey) => {
    e.preventDefault();
    const updatedErrorMessages = global.structuredClone(state.entityStates[entityType].errorMessages);
    delete updatedErrorMessages[dataKey];
    dispatch({ type: "DELETE_ROW", entityType: entityType, dataKey });
    dispatch({ type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: entityType, errorMessages: updatedErrorMessages });
  };

  return (
    <FormTableWrapper
      table={
        <NomenclatureEventsFormTable
          nomenclatureEvents={nomenclatureEvents}
          editingRows={state.entityStates[entityType].editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates[entityType].errorMessages}
          nomenclatureEventOnChangeHandler={nomenclatureEventOnChangeHandler}
          obsoleteOnChangeHandler={obsoleteOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Nomenclature Events"
      showTable={state.entityStates[entityType].show}
      button={<Button label="Add Nomenclature Event" onClick={createNewNomenclatureEventHandler} className="w-4 p-button-text"/>}
    />
  );

};