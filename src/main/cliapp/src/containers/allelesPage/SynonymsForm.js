import { Button } from "primereact/button";
import { FormTableWrapper } from "../../components/FormTableWrapper";
import { SynonymsFormTable } from "./synonyms/SynonymsFormTable";
import { useRef } from "react";

export const SynonymsForm = ({ labelColumnSize, state, dispatch }) => {
  const tableRef = useRef(null);
  const createNewSynonymHandler = (e) => {
    e.preventDefault();
    dispatch({type: "ADD_ROW", showType: "showSynonyms", row: {}})
    return null;
  };

  const onRowEditChange = (e) => {
    return null;
  };

  const synonymScopeEditor = (e) => {
    return null;
  };

  const nameTypeEditor = (e) => {
    return null;
  };

  const internalTemplate = (e) => {
    return null;
  };

  const internalEditor = (e) => {
    return null;
  };

  const onRowEditCancel = (e) => {
    return null;
  };

  const onRowEditSave = (e) => {
    return null;
  };

  const deleteAction = (e) => {
    return null;
  };

  return (
    <FormTableWrapper
      labelColumnSize={labelColumnSize}
      table={
        <SynonymsFormTable
          synonyms={state.synonyms}
          editingRows={state.editingRows}
          onRowEditChange={onRowEditChange}
          tableRef={tableRef}
          onRowEditCancel={onRowEditCancel}
          onRowEditSave={onRowEditSave}
          deleteAction={deleteAction}
          errorMessages={state.errorMessages}
          synonymScopeEditor={synonymScopeEditor}
          nameTypeEditor={nameTypeEditor}
          internalEditor={internalEditor}
          internalTemplate={internalTemplate}

        />
      }
      tableName="Synonyms"
      showTable={state.showSynonyms}
      button={<Button label="Add Synonym" onClick={createNewSynonymHandler} style={{ width: "50%" }} />}
    />
  );

};