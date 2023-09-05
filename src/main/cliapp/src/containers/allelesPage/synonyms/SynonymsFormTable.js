import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { TableInputTextEditor } from '../../../components/Editors/TableInputTextEditor';
import { SynonymScopeEditor } from '../../../components/Editors/SynonymScopeEditor';
import { NameTypeEditor } from '../../../components/Editors/NameTypeEditor';
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';

export const SynonymsFormTable = ({
  synonyms,
  editingRows,
  onRowEditChange,
  tableRef,
  deletionHandler,
  errorMessages,
  textOnChangeHandler,
  synonymScopeOnChangeHandler,
  nameTypeOnChangeHandler,
  internalOnChangeHandler,
  evidenceOnChangeHandler,
}) => {
  let headerGroup = HeaderGroup();

  return (
    <DataTable value={synonyms} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
      editingRows={editingRows} resizableColumns columnResizeMode="expand" onRowEditChange={onRowEditChange} ref={tableRef}>
      <Column editor={(props) => <DeleteAction deletionHandler={deletionHandler} index={props.rowIndex}/>} 
        style={{ maxWidth: '4rem' }} frozen headerClassName='surface-0' bodyStyle={{ textAlign: 'center' }} />
      <Column 
        editor={(props) => {
          return <TableInputTextEditor
            value={props.value} 
            rowIndex={props.rowIndex} 
            errorMessages={errorMessages}
            textOnChangeHandler={textOnChangeHandler}
            field="displayText"
        />}} 
        field="displayText" header="Display Text" headerClassName='surface-0' />
      <Column 
        editor={(props) => {
          return <TableInputTextEditor
            value={props.value} 
            rowIndex={props.rowIndex} 
            errorMessages={errorMessages}
            textOnChangeHandler={textOnChangeHandler}
            field="formatText"
        />}} 
        field="formatText" header="Format Text" headerClassName='surface-0'/>
      <Column 
        editor={(props) => {
          return <SynonymScopeEditor
            value={props.value} 
            rowIndex={props.rowIndex} 
            errorMessages={errorMessages}
            synonymScopeOnChangeHandler={synonymScopeOnChangeHandler}
        />}} 
        field="synonymScope" header="Synonym Scope" headerClassName='surface-0' />
      <Column 
        editor={(props) => {
          return <NameTypeEditor
            value={props.value} 
            rowIndex={props.rowIndex} 
            errorMessages={errorMessages}
            nameTypeOnChangeHandler={nameTypeOnChangeHandler}
        />}} 
        field="nameType" header="Name Type" headerClassName='surface-0' />
      <Column 
        editor={(props) => {
          return <TableInputTextEditor
            value={props.value} 
            rowIndex={props.rowIndex} 
            errorMessages={errorMessages}
            textOnChangeHandler={textOnChangeHandler}
            field="synonymUrl"
        />}} 
        field="synonymUrl" header="Synonym URL" headerClassName='surface-0' />
      <Column 
        editor={(props) => {
          return <InternalEditor
            value={props.value} 
            rowIndex={props.rowIndex} 
            errorMessages={errorMessages}
            internalOnChangeHandler={internalOnChangeHandler}
        />}} 
        field="internal" header="Internal" headerClassName='surface-0' />
      <Column 
        editor={(props) => {
          return <EvidenceEditor
            props={props} 
            errorMessages={errorMessages}
            onChange={evidenceOnChangeHandler}
        />}} 
        field="evidence.curie" header="Evidence" headerClassName='surface-0' />
      <Column field="updatedBy.uniqueId" header="Updated By" />
      <Column field="dateUpdated" header="Date Updated" />
    </DataTable>
  );
};
function HeaderGroup() {
  return <ColumnGroup>
    <Row>
      <Column header="Actions" />
      <Column header="Display Text" />
      <Column header="Format Text" />
      <Column header="Synonym Scope" />
      <Column header="Name Type" />
      <Column header="Synonym URL" />
      <Column header="Internal" />
      <Column header="Evidence" />
      <Column header="Updated By" />
      <Column header="Date Updated" />
    </Row>
  </ColumnGroup>;
}

