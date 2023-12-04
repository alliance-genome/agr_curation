import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { ControlledVocabularyEditor } from '../../../components/Editors/ControlledVocabularyEditor';
import { GeneEditor } from '../../../components/Editors/GeneEditor';
import { RelatedNotesEditor } from '../../../components/Editors/RelatedNotesEditor';
import { EvidenceCodeEditor } from '../../../components/Editors/EvidenceCodeEditor';

export const AlleleGeneAssociationsFormTable = ({
  alleleGeneAssociations,
  editingRows,
  onRowEditChange,
  tableRef,
  deletionHandler,
  errorMessages,
  evidenceOnChangeHandler,
  alleleGeneRelationOnChangeHandler,
  geneOnChangeHandler,
  evidenceCodeOnChangeHandler,
}) => {
  let headerGroup = (
    <ColumnGroup>
      <Row>
        <Column header="Relationship" />
        <Column header="Gene" />
        <Column header="Note" />
        <Column header="Evidence Code" />
        <Column header="Evidence" />
        <Column header="Updated By" />
        <Column header="Date Updated" />
      </Row>
    </ColumnGroup>
  );

  return (
    <DataTable value={alleleGeneAssociations} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
      editingRows={editingRows} resizableColumns columnResizeMode="expand" onRowEditChange={onRowEditChange} ref={tableRef}>
      <Column editor={(props) => <DeleteAction deletionHandler={deletionHandler} index={props.rowIndex} />}
        className='max-w-4rem' bodyClassName="text-center" headerClassName='surface-0' frozen />
      <Column
        editor={(props) => {
          return <ControlledVocabularyEditor
            props={props}
            onChangeHandler={alleleGeneRelationOnChangeHandler}
            errorMessages={errorMessages}
            rowIndex={props.rowIndex}
            vocabType="allele_gene_relation"
            field="relation"
            showClear={false}
          />;
        }}
        field="displayText" header="Display Text" headerClassName='surface-0' />
      <Column
        editor={(props) => {
          return <GeneEditor
            props={props}
            errorMessages={errorMessages}
            onChange={geneOnChangeHandler}
          />;
        }}
        field="object.curie" header="Gene" headerClassName='surface-0' />
      <Column
        editor={(props) => {
          return <RelatedNotesEditor
            relatedNotes={props.relatedNotes}
            errorMessages={errorMessages}
            rowIndex={props.rowIndex}
            rows={props.rows}
          />;
        }}
        field="relatedNotes" header="Notes" headerClassName='surface-0' />
      <Column
        editor={(props) => {
          return <EvidenceCodeEditor
            props={props}
            onChangeHandler={evidenceCodeOnChangeHandler}
            errorMessages={errorMessages}
          />;
        }}
        field="nameType" header="Name Type" headerClassName='surface-0' />
      <Column
        editor={(props) => {
          return <EvidenceEditor
            props={props}
            errorMessages={errorMessages}
            onChange={evidenceOnChangeHandler}
          />;
        }}
        field="evidence.curie" header="Evidence" headerClassName='surface-0' />
      <Column field="updatedBy.uniqueId" header="Updated By" />
      <Column field="dateUpdated" header="Date Updated" />
    </DataTable>
  );
};

