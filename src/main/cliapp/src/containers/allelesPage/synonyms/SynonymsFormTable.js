import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { evidenceTemplate, evidenceEditorTemplate } from '../../../components/EvidenceComponent';
import { synonymScopeTemplate, nameTypeTemplate, synonymUrlTemplate, synonymUrlEditorTemplate, displayTextTemplate, displayTextEditorTemplate, formatTextTemplate, formatTextEditorTemplate } from '../../../components/NameSlotAnnotationComponent';

export const SynonymsFormTable = ({
  synonyms,
  editingRows,
  onRowEditChange,
  tableRef,
  onRowEditCancel,
  onRowEditSave,
  deleteAction,
  errorMessages,
  synonymScopeEditor,
  nameTypeEditor,
  internalEditor,
  internalTemplate
}) => {
  let headerGroup = HeaderGroup();

  return (
    <DataTable value={synonyms} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
      editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
      <Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem' }} frozen headerClassName='surface-0' bodyStyle={{ textAlign: 'center' }} />
      <Column editor={(props) => displayTextEditorTemplate(props, errorMessages)} field="displayText" header="Display Text" headerClassName='surface-0' body={displayTextTemplate} />
      <Column editor={(props) => formatTextEditorTemplate(props, errorMessages)} field="formatText" header="Format Text" headerClassName='surface-0' body={formatTextTemplate} />
      <Column editor={synonymScopeEditor} field="synonymScope" header="Synonym Scope" headerClassName='surface-0' body={synonymScopeTemplate} />
      <Column editor={nameTypeEditor} field="nameType" header="Name Type" headerClassName='surface-0' body={nameTypeTemplate} />
      <Column editor={(props) => synonymUrlEditorTemplate(props, errorMessages)} field="synonymUrl" header="Synonym URL" headerClassName='surface-0' body={synonymUrlTemplate} />
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
      <Column header="Actions" colSpan={2} />
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

