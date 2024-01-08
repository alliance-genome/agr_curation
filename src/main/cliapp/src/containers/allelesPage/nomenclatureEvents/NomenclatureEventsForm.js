import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { NomenclatureEventsFormTable } from "../nomenclatureEvents/NomenclatureEventsFormTable";
import { useRef } from "react";

export const NomenclatureEventsForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);
  const nomenclatureEvents = global.structuredClone(state.allele?.alleleNomenclatureEvents);

  const createNewNomenclatureEventHandler = (e) => {
    e.preventDefault();
    const dataKey = state.allele.alleleNomenclatureEvents?.length;
    const newNomenclatureEvent = {
      dataKey: dataKey,
      internal: false,
      obsolete: false,
      nomenclatureEvent: null
    }

    dispatch({
      type: "ADD_ROW", 
      row: newNomenclatureEvent, 
      entityType: "alleleNomenclatureEvents", 
    })
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const nomenclatureEventOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleNomenclatureEvents', 
      index: props.rowIndex, 
      field: "nomenclatureEvent", 
      value: event.target.value
    });
  };
  
  const internalOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value?.name);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleNomenclatureEvents', 
      index: props.rowIndex, 
      field: "internal", 
      value: event.target?.value?.name
    });
  };

  const obsoleteOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value?.name);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleNomenclatureEvents', 
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
      entityType: 'alleleNomenclatureEvents', 
      index: props.rowIndex, 
      field: "evidence", 
      value: event.target.value
    });
  }

  const deletionHandler  = (e, index) => {
    e.preventDefault();
    dispatch({type: "DELETE_ROW", entityType: "alleleNomenclatureEvents", index: index});
    dispatch({type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "alleleNomenclatureEvents", errorMessages: []});
  };

  return (
    <FormTableWrapper
      table={
        <NomenclatureEventsFormTable
          nomenclatureEvents={nomenclatureEvents}
          editingRows={state.entityStates.alleleNomenclatureEvents.editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates.alleleNomenclatureEvents.errorMessages}
          nomenclatureEventOnChangeHandler={nomenclatureEventOnChangeHandler}
          obsoleteOnChangeHandler={obsoleteOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Nomenclature Events"
      showTable={state.entityStates.alleleNomenclatureEvents.show}
      button={<Button label="Add Nomenclature Event" onClick={createNewNomenclatureEventHandler} className="w-6"/>}
    />
  );

};