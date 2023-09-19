import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { MutationTypesEditor } from '../../../components/Editors/MutationTypesEditor';

export const MutationTypesFormTable = ({
  mutationTypes,
  editingRows,
  onRowEditChange,
  tableRef,
  deletionHandler,
  errorMessages,
  internalOnChangeHandler,
  mutationTypesOnChangeHandler,
  evidenceOnChangeHandler,
}) => {

  let headerGroup = <ColumnGroup>
    <Row>
      <Column header="Actions" />
      <Column header="Mutation Types" />
      <Column header="Internal" />
      <Column header="Evidence" />
    </Row>
  </ColumnGroup>;


  return (
    <DataTable value={mutationTypes} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
      editingRows={editingRows} resizableColumns columnResizeMode="expand" onRowEditChange={onRowEditChange} ref={tableRef}>
      <Column editor={(props) => <DeleteAction deletionHandler={deletionHandler} index={props.rowIndex} />}
        style={{ maxWidth: '4rem' }} frozen headerClassName='surface-0' bodyStyle={{ textAlign: 'center' }} />
      <Column
        editor={(props) => {
          return <MutationTypesEditor
            props={props}
            errorMessages={errorMessages}
            onChange={mutationTypesOnChangeHandler}
          />;
        }}
        field="mutationTypes" header="Mutation Types" headerClassName='surface-0' />
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
          return <EvidenceEditor
            props={props}
            errorMessages={errorMessages}
            onChange={evidenceOnChangeHandler}
          />;
        }}
        field="evidence.curie" header="Evidence" headerClassName='surface-0' />
    </DataTable>
  );
};

