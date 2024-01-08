import { Button } from "primereact/button";
import { FormTableWrapper } from "../../../components/FormTableWrapper";
import { useRef } from "react";
import { GermlineTransmissionStatusFormTable } from "./GermlineTransmissionStatusFormTable";

export const GermilineTransmissionStatusForm = ({ labelColumnSize, state, dispatch }) => {
  const tableRef = useRef(null);

  const germlineTransmissionStatusArray = [state.allele?.alleleGermlineTransmissionStatus];

  const createNewGermlineTransmissionStatus = (e) => {
    e.preventDefault();
    const newGermlineTransmissionStatus = {
      dataKey: 0,
      internal: false,
    };

    dispatch({
      type: "ADD_OBJECT",
      value: newGermlineTransmissionStatus,
      entityType: "alleleGermlineTransmissionStatus",
    });
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const internalOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value?.name);
    dispatch({
      type: 'EDIT_OBJECT',
      entityType: 'alleleGermlineTransmissionStatus',
      field: "internal",
      value: event.target?.value?.name
    });
  };

  const germlineTransmissionStatusOnChangeHandler = (props, event) => {
    props.editorCallback(event.target.value);
    dispatch({
      type: 'EDIT_OBJECT',
      entityType: 'alleleGermlineTransmissionStatus',
      field: "germlineTransmissionStatus",
      value: event.target.value
    });
  };

  const evidenceOnChangeHandler = (event, setFieldValue, props) => {
    //updates value in table input box
    setFieldValue(event.target.value);
    dispatch({
      type: 'EDIT_OBJECT',
      entityType: 'alleleGermlineTransmissionStatus',
      field: "evidence",
      value: event.target.value
    });
  };

  const deletionHandler = (e) => {
    e.preventDefault();
    dispatch({ type: "DELETE_OBJECT", entityType: "alleleGermlineTransmissionStatus" });
    dispatch({ type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: "alleleGermlineTransmissionStatus", errorMessages: [] });
  };

  return (
    <FormTableWrapper
      labelColumnSize={labelColumnSize}
      table={
        <GermlineTransmissionStatusFormTable
          name={germlineTransmissionStatusArray}
          editingRows={state.entityStates.alleleGermlineTransmissionStatus.editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          deletionHandler={deletionHandler}
          errorMessages={state.entityStates.alleleGermlineTransmissionStatus.errorMessages}
          germlineTransmissionStatusOnChangeHandler={germlineTransmissionStatusOnChangeHandler}
          internalOnChangeHandler={internalOnChangeHandler}
          evidenceOnChangeHandler={evidenceOnChangeHandler}
        />
      }
      tableName="Germline Transmission Status"
      showTable={state.entityStates.alleleGermlineTransmissionStatus.show}
      button={<Button label="Add Germline Transmission Status" onClick={createNewGermlineTransmissionStatus} disabled={state.allele?.alleleGermlineTransmissionStatus} className="w-6" />}
    />
  );

};