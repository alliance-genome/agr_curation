import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { evidenceTemplate, evidenceEditorTemplate } from '../../../components/EvidenceComponent';
import { synonymScopeTemplate, nameTypeTemplate, synonymUrlTemplate, synonymUrlEditorTemplate, displayTextTemplate, displayTextEditorTemplate, formatTextTemplate, formatTextEditorTemplate } from '../../../components/NameSlotAnnotationComponent';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { TableInputTextEditor } from '../../../components/Editors/TableInputTextEditor';
import { SynonymScopeEditor } from '../../../components/Editors/SynonymScopeEditor';
import { NameTypeEditor } from '../../../components/Editors/NameTypeEditor';

export const SynonymsFormTable = ({
  synonyms,
  editingRows,
  onRowEditChange,
  tableRef,
  onRowEditCancel,
  onRowEditSave,
  deletionHandler,
  errorMessages,
  internalEditor,
  textOnChangeHandler,
  synonymScopeOnChangeHandler,
  nameTypeOnChangeHandler,
  internalTemplate
}) => {
  let headerGroup = HeaderGroup();

  return (
    <DataTable value={synonyms} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
      editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
      <Column editor={() => <DeleteAction deletionHandler={deletionHandler}/>} 
        style={{ maxWidth: '4rem' }} frozen headerClassName='surface-0' bodyStyle={{ textAlign: 'center' }} />
      <Column 
        editor={(props) => {
          return <TableInputTextEditor
            value={props.value} 
            rowIndex={props.rowIndex} 
            errorMessages={errorMessages}
            textOnChangeHandler={textOnChangeHandler}
            editorCallback={props.editorCallback}
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
            editorCallback={props.editorCallback}
            field="formatText"
        />}} 
        field="formatText" header="Format Text" headerClassName='surface-0' body={formatTextTemplate} />
      <Column 
        editor={(props) => {
          return <SynonymScopeEditor
            value={props.value} 
            rowIndex={props.rowIndex} 
            errorMessages={errorMessages}
            synonymScopeOnChangeHandler={synonymScopeOnChangeHandler}
            editorCallback={props.editorCallback}
        />}} 
        field="synonymScope" header="Synonym Scope" headerClassName='surface-0' body={synonymScopeTemplate} />
      <Column 
        editor={(props) => {
          return <NameTypeEditor
            value={props.value} 
            rowIndex={props.rowIndex} 
            errorMessages={errorMessages}
            nameTypeOnChangeHandler={nameTypeOnChangeHandler}
            editorCallback={props.editorCallback}
        />}} 
        field="nameType" header="Name Type" headerClassName='surface-0' body={nameTypeTemplate} />
      <Column 
        editor={(props) => {
          return <TableInputTextEditor
            value={props.value} 
            rowIndex={props.rowIndex} 
            errorMessages={errorMessages}
            textOnChangeHandler={textOnChangeHandler}
            editorCallback={props.editorCallback}
            field="synonymUrl"
        />}} 
        field="synonymUrl" header="Synonym URL" headerClassName='surface-0' body={synonymUrlTemplate} />
      <Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0' />
      <Column editor={(props) => evidenceEditorTemplate(props, errorMessages)} field="evidence.curie" header="Evidence" headerClassName='surface-0' body={(rowData) => evidenceTemplate(rowData)} />
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

