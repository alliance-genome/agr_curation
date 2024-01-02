import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { ObsoleteEditor } from '../../../components/Editors/ObsoleteEditor';
import { ControlledVocabularyEditor } from '../../../components/Editors/ControlledVocabularyEditor';

export const NomenclatureEventsFormTable = ({
  nomenclatureEvents,
  editingRows,
  onRowEditChange,
  tableRef,
  deletionHandler,
  errorMessages,
  internalOnChangeHandler,
  obsoleteOnChangeHandler,
  nomenclatureEventOnChangeHandler,
  evidenceOnChangeHandler,
}) => {

  let headerGroup = <ColumnGroup>
    <Row>
    <Column header="Actions" />
        <Column header="Nomenclature Event" />
        <Column header="Evidence" />
        <Column header="Internal" />
        <Column header="Obsolete" />
        <Column header="Updated By" />
        <Column header="Date Updated" />
        <Column header="Created By" />
        <Column header="Date Created" />
    </Row>
  </ColumnGroup>;


  return (
    <DataTable value={nomenclatureEvents} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
      editingRows={editingRows} resizableColumns columnResizeMode="expand" onRowEditChange={onRowEditChange} ref={tableRef}>
      <Column editor={(props) => <DeleteAction deletionHandler={deletionHandler} index={props.rowIndex} />}
        className='max-w-4rem' bodyClassName="text-center" headerClassName='surface-0' frozen />
      <Column
        editor={(props) => {
          return <ControlledVocabularyEditor
            props={props}
            onChangeHandler={nomenclatureEventOnChangeHandler}
            errorMessages={errorMessages}
            rowIndex={props.rowIndex}
            dataKey={props?.rowData?.dataKey}
            vocabType="allele_nomenclature_event"
            field="nomenclatureEvent"
            showClear={false}
          />;
        }}
        field="nomenclatureEvent" header="Nomenclature Event" headerClassName='surface-0' />
      <Column
        editor={(props) => {
          return <EvidenceEditor
            props={props}
            errorMessages={errorMessages}
            onChange={evidenceOnChangeHandler}
          />;
        }}
        field="evidence.curie" header="Evidence" headerClassName='surface-0' />
      <Column
        editor={(props) => {
          return <InternalEditor
            props={props}
            rowIndex={props.rowIndex}
            errorMessages={errorMessages}
            dataKey={props?.rowData?.dataKey}
            internalOnChangeHandler={internalOnChangeHandler}
          />;
        }}
        field="internal" header="Internal" headerClassName='surface-0' />
      <Column
        editor={(props) => {
          return <ObsoleteEditor
            props={props}
            rowIndex={props.rowIndex}
            errorMessages={errorMessages}
            dataKey={props?.rowData?.dataKey}
            obsoleteOnChangeHandler={obsoleteOnChangeHandler}
          />;
        }}
        field="obsolete" header="Obsolete" headerClassName='surface-0' />
      <Column field="updatedBy.uniqueId" header="Updated By" />
      <Column field="dateUpdated" header="Date Updated" />
      <Column field="createdBy.uniqueId" header="Created By" />
      <Column field="dateCreated" header="Date Created" />
    </DataTable>
  );
};

