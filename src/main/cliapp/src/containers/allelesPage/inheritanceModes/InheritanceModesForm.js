import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { InheritanceModesFormTable } from "../inheritanceModes/InheritanceModesFormTable";
import { useRef } from "react";

export const InheritanceModesForm = ({ state, dispatch }) => {
  const tableRef = useRef(null);

  const createNewInheritanceModeHandler = (e) => {
    e.preventDefault();
    const dataKey = state.allele.alleleInheritanceModes?.length;
    const newInheritanceMode = {
      dataKey: dataKey,
      internal: false,
      obsolete: false,
      inheritanceMode: null,
      phenotypeTerm: null,
      phenotypeStatement: ""
    }

    dispatch({
      type: "ADD_ROW", 
      row: newInheritanceMode, 
      entityType: "alleleInheritanceModes", 
    })
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const inheritanceModeOnChangeHandler = (props, event) => {
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleInheritanceModes', 
      index: props.rowIndex, 
      field: "inheritanceMode", 
      value: event.target.value
    });
  };
  
  const phenotypeTermOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    let value = null;
    if (event.target.value !== "") {
      if (!event.target.value?.curie) {
        value = {curie: event.target.value};
      } else {
        value = event.target.value;
      }

    }
    setFieldValue(value);
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleInheritanceModes', 
      index: props.rowIndex, 
      field: "phenotypeTerm", 
      value: value
    });
  }

  const internalOnChangeHandler = (props, event) => {
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleInheritanceModes', 
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
      entityType: 'alleleInheritanceModes', 
      index: props.rowIndex, 
      field: "evidence", 
      value: event.target.value
    });
  }

  const textOnChangeHandler = (rowIndex, event, field) => {
    dispatch({ 
      type: 'EDIT_ROW', 
      entityType: 'alleleInheritanceModes', 
      index: rowIndex, 
      field: field, 
      value: event.target.value
    });
  }

  const deletionHandler  = (e, index) => {
    e.preventDefault();
    dispatch({type: "DELETE_ROW", entityType: "alleleInheritanceModes", index: index});
    dispatch({type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "alleleInheritanceModes", errorMessages: []});
  };

  return (
    <FormTableWrapper
      table={
        <InheritanceModesFormTable
          inheritanceModes={state.allele?.alleleInheritanceModes}
          editingRows={state.entityStates.alleleInheritanceModes.editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates.alleleInheritanceModes.errorMessages}
          inheritanceModeOnChangeHandler={inheritanceModeOnChangeHandler}
          phenotypeTermOnChangeHandler={phenotypeTermOnChangeHandler}
          textOnChangeHandler={textOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Inheritance Modes"
      showTable={state.entityStates.alleleInheritanceModes.show}
      button={<Button label="Add Inheritance Mode" onClick={createNewInheritanceModeHandler} className="w-6"/>}
    />
  );

};