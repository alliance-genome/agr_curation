import { useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { ControlledVocabularyEditor } from '../../../components/Editors/ControlledVocabularyEditor';
import { GeneEditor } from '../../../components/Editors/GeneEditor';
import { RelatedNotesEditor } from '../../../components/Editors/RelatedNotesEditor';
import { EvidenceCodeEditor } from '../../../components/Editors/EvidenceCodeEditor';
import { RelatedNotesDialogEditOnly } from '../../../components/RelatedNotesDialogEditOnly';

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
  dispatch,
}) => {

  const [relatedNotesData, setRelatedNotesData] = useState({
    relatedNotes: [],
    dialogIsVisible: false,
    rowIndex: null,
    mainRowProps: {},
    errorMessages,
  });

  return (
    <>
      <DataTable value={alleleGeneAssociations} dataKey="dataKey" showGridlines editMode='row'
        removableSort filterDisplay='row'
        editingRows={editingRows} resizableColumns columnResizeMode="expand" onRowEditChange={onRowEditChange} ref={tableRef}
        paginator paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
        currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={5} rowsPerPageOptions={[5, 10, 20, 50]}
      >
        <Column editor={(props) => <DeleteAction deletionHandler={deletionHandler} index={props.rowIndex} />}
          className='max-w-4rem' bodyClassName="text-center" headerClassName='surface-0' frozen />
        <Column
          editor={(props) => {
            // does this need to be a term set editor instead?
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
          field="relation.name" header="Relationship" headerClassName='surface-0' />
        <Column
          editor={(props) => {
            return <GeneEditor
              props={props}
              errorMessages={errorMessages}
              onChange={geneOnChangeHandler}
            />;
          }}
          field="objectGene"
          header="Gene"
          headerClassName='surface-0'
          filter
          sortable
          filterField="objectGene.curie"
          sortField="objectGene.curie"
          showFilterMenu={false}
          filterMatchMode='contains'
        />
        <Column
          editor={(props) => {
            return <RelatedNotesEditor
              rowProps={props}
              relatedNotes={props.relatedNotes}
              errorMessages={errorMessages}
              rowIndex={props.rowIndex}
              rows={props.rows}
              setRelatedNotesData={setRelatedNotesData}
            />;
          }}
          field="relatedNotes" header="Notes" headerClassName='surface-0'
          filter
          sortable
          showFilterMenu={false}
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
          filter filterField='crossReferencesFilter' filterMatchMode='contains' showFilterMenu={false}
          field="evidence.curie" header="Evidence" headerClassName='surface-0' />
        <Column field="updatedBy.uniqueId" header="Updated By" />
        <Column field="dateUpdated" header="Date Updated" />
      </DataTable>
      <RelatedNotesDialogEditOnly
        relatedNotesData={relatedNotesData}
        setRelatedNotesData={setRelatedNotesData}
        errorMessagesMainRow={errorMessages}
        dispatch={dispatch}
      />
    </>
  );
};

