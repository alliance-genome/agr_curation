import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { ReferencesEditor } from '../../../components/Editors/ReferencesEditor';
import { VocabularyTermSetEditor } from '../../../components/Editors/VocabularyTermSetEditor';
import { TableInputTextAreaEditor } from '../../../components/Editors/TableInputTextAreaEditor';

export const RelatedNotesFormTable = ({
  relatedNotes,
  editingRows,
  onRowEditChange,
  tableRef,
  deletionHandler,
  errorMessages,
  internalOnChangeHandler,
  noteTypeOnChangeHandler,
  textOnChangeHandler,
  referencesOnChangeHandler,
  isLoading
}) => {

  let headerGroup = <ColumnGroup>
    <Row>
      <Column header="Actions" />
      <Column header="Note Type" />
      <Column header="Text" />
      <Column header="Internal" />
      <Column header="Evidence" />
    </Row>
  </ColumnGroup>;


  return (
    <DataTable value={relatedNotes} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup} loading={isLoading}
      editingRows={editingRows} resizableColumns columnResizeMode="expand" onRowEditChange={onRowEditChange} ref={tableRef}>
      <Column editor={(props) => <DeleteAction deletionHandler={deletionHandler} index={props.rowIndex} />}
        className='max-w-4rem' bodyClassName="text-center" headerClassName='surface-0' frozen />
      <Column
        editor={(props) => {
          return <VocabularyTermSetEditor
            props={props}
            onChangeHandler={noteTypeOnChangeHandler}
            errorMessages={errorMessages}
            rowIndex={props.rowIndex}
            vocabType="allele_note_type"
            field="noteType"
            showClear={false}
          />;
        }}
        field="noteType" header="Note Type" headerClassName='surface-0' />
      <Column 
        editor={(props) => {
          return <TableInputTextAreaEditor
            value={props.value} 
            rowIndex={props.rowIndex} 
            errorMessages={errorMessages}
            textOnChangeHandler={textOnChangeHandler}
            field="freeText"
            rows={5}
            columns={30}
        />}} 
        field="freeText" header="Text" headerClassName='surface-0' className="w-4"/>
      <Column
        editor={(props) => {
          return <InternalEditor
            props={props}
            rowIndex={props.rowIndex}
            errorMessages={errorMessages}
            internalOnChangeHandler={internalOnChangeHandler}
          />;
        }}
        field="internal" header="Internal" headerClassName='surface-0' />
      <Column
        editor={(props) => {
          return <ReferencesEditor
            props={props}
            errorMessages={errorMessages}
            onChange={referencesOnChangeHandler}
          />;
        }}
        field="references.curie" header="Evidence" headerClassName='surface-0' />
    </DataTable>
  );
};

