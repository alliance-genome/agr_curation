import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { FullNameFormTable } from "./FullNameFormTable";
import { useRef } from "react";

export const FullNameForm = ({ labelColumnSize, state, dispatch }) => {
  const tableRef = useRef(null);

  const fullNameArray = [state.allele?.alleleFullName];

  const createNewFullNameHandler = (e) => {
    e.preventDefault();
    const newFullName = {
      dataKey: 0,
      synonymUrl: "",
      internal: false,
      nameType: null,
      formatText: "",
      displayText: ""
    }

    dispatch({
      type: "ADD_OBJECT", 
      showType: "showFullName", 
      value: newFullName, 
      objectType: "alleleFullName", 
      editingRowsType: "fullNameEditingRows" 
    })
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const nameTypeOnChangeHandler = (props, event) => {
    dispatch({ 
      type: 'EDIT_OBJECT', 
      objectType: 'alleleFullName', 
      field: "nameType", 
      value: event.target.value
    });
  };

  const internalOnChangeHandler = (props, event) => {
    dispatch({ 
      type: 'EDIT_OBJECT', 
      objectType: 'alleleFullName', 
      field: "internal", 
      value: event.target?.value?.name
    });
  };

  const synonymScopeOnChangeHandler = (props, event) => {
    dispatch({ 
      type: 'EDIT_OBJECT', 
      objectType: 'alleleFullName', 
      field: "synonymScope", 
      value: event.target.value
    });
  };

  const textOnChangeHandler = (rowIndex, event, field) => {
    dispatch({ 
      type: 'EDIT_OBJECT', 
      objectType: 'alleleFullName', 
      field: field, 
      value: event.target.value
    });
  }

  const evidenceOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({ 
      type: 'EDIT_OBJECT', 
      objectType: 'alleleFullName', 
      field: "evidence", 
      value: event.target.value
    });
  }

  const deletionHandler  = (e) => {
    e.preventDefault();
    dispatch({type: "DELETE_OBJECT", objectType: "alleleFullName", showType: "showFullName"});
    dispatch({type: "UPDATE_ERROR_MESSAGES", errorType: "fullNameErrorMessages", errorMessages: []});
  };

  return (
    <FormTableWrapper
      labelColumnSize={labelColumnSize}
      table={
        <FullNameFormTable
          name={fullNameArray}
          editingRows={state.fullNameEditingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.fullNameErrorMessages}
          textOnChangeHandler={textOnChangeHandler}
          synonymScopeOnChangeHandler={synonymScopeOnChangeHandler}
          nameTypeOnChangeHandler={nameTypeOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Full Name"
      showTable={state.showFullName}
      button={<Button label="Add Full Name" onClick={createNewFullNameHandler} disabled={state.allele?.alleleFullName} className="w-6"/>}
    />
  );

};