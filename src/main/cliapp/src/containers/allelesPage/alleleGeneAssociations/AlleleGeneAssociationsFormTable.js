import { useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { GeneEditor } from '../../../components/Editors/GeneEditor';
import { RelatedNoteEditor } from '../../../components/Editors/RelatedNoteEditor';
import { EvidenceCodeEditor } from '../../../components/Editors/EvidenceCodeEditor';
import { RelatedNotesDialogEditOnly } from '../../../components/RelatedNotesDialogEditOnly';
import { VocabularyTermSetEditor } from '../../../components/Editors/VocabularyTermSetEditor';
import { RelationshipFilterTemplate } from './RelationshipFilterTemplate';

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
  relatedNoteOnChangeHandler,
  dispatch,
}) => {
  const [rows, setRows] = useState(5);
  const [first, setFirst] = useState(0);

  const [relatedNotesData, setRelatedNotesData] = useState({
    relatedNotes: [],
    dialogIsVisible: false,
    rowIndex: null,
    mainRowProps: {},
    errorMessages,
  });
  const onPage = (options) => {
    setRows(options.rows);
    setFirst(options.first);
  }

  return (
    <>
      <DataTable value={alleleGeneAssociations} dataKey="dataKey" showGridlines editMode='row'
        removableSort filterDisplay='row' onPage={onPage}
        editingRows={editingRows} resizableColumns columnResizeMode="expand" onRowEditChange={onRowEditChange} ref={tableRef}
        paginator paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
        currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} first={first} rowsPerPageOptions={[5, 10, 20, 50]}
      >
        <Column editor={(props) => <DeleteAction deletionHandler={deletionHandler} index={props.rowIndex} />}
          className='max-w-4rem' bodyClassName="text-center" headerClassName='surface-0' frozen />
        <Column
          editor={(props) => {
            return <VocabularyTermSetEditor
              props={props}
              onChangeHandler={alleleGeneRelationOnChangeHandler}
              errorMessages={errorMessages}
              rowIndex={props.rowIndex}
              vocabType="allele_gene_relation"
              field="relation"
              showClear={false}
            />;
          }}
          field="relation"
          header="Relationship"
          headerClassName='surface-0'
          filter
          sortable
          filterField="relation.name"
          sortField="relation.name"
          filterMatchMode="in"
          showFilterMenu={false}
          filterElement={(props) => {
            return <RelationshipFilterTemplate options={props} alleleGeneAssociations={alleleGeneAssociations} />
          }}
        />
        <Column
          editor={(props) => {
            return <GeneEditor
              props={props}
              errorMessages={errorMessages}
              onChange={geneOnChangeHandler}
            />;
          }}
          field="object"
          header="Gene"
          headerClassName='surface-0'
          filter
          sortable
          filterField="object.modEntityId"
          sortField="object.modEntityId"
          showFilterMenu={false}
          filterMatchMode='contains'
        />
        <Column
          editor={(props) => {
            return <RelatedNoteEditor
              rowProps={props}
              relatedNote={props.rowData.relatedNote}
              errorMessages={errorMessages}
              rowIndex={props.rowIndex}
              rows={props.rows}
              setRelatedNotesData={setRelatedNotesData}
            />;
          }}
          field="relatedNote" header="Note" headerClassName='surface-0'
          filter
          filterField="relatedNote.freeText"
          filterMatchMode='contains'
          showFilterMenu={false}
          sortable
          sortField="relatedNote.freeText"
        />
        <Column
          editor={(props) => {
            return <EvidenceCodeEditor
              props={props}
              onChangeHandler={evidenceCodeOnChangeHandler}
              errorMessages={errorMessages}
            />;
          }}
          filter
          sortable
          filterField="evidenceCode.curie"
          sortField="evidenceCode.curie"
          showFilterMenu={false}
          filterMatchMode='contains'
          field="evidenceCode" header="Evidence Code" headerClassName='surface-0'
        />
        <Column
          editor={(props) => {
            return <EvidenceEditor
              props={props}
              errorMessages={errorMessages}
              onChange={evidenceOnChangeHandler}
            />;
          }}
          filter filterField='evidenceCurieSearchFilter' filterMatchMode='contains' showFilterMenu={false}
          field="evidence.curie" sortable sortField="evidenceCurieSearchFilter" header="Evidence" headerClassName='surface-0' />
        <Column field="updatedBy.uniqueId" header="Updated By" />
        <Column field="dateUpdated" header="Date Updated" />
      </DataTable>
      <RelatedNotesDialogEditOnly
        relatedNotesData={relatedNotesData}
        setRelatedNotesData={setRelatedNotesData}
        errorMessagesMainRow={errorMessages}
        dispatch={dispatch}
        singleValue={true}
        onChange={relatedNoteOnChangeHandler}
        defaultValues={{noteType: "comment"}}
        errorField='relatedNote'
      />
    </>
  );
};

