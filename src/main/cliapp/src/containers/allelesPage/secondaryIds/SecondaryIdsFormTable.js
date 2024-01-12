import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { TableInputTextEditor } from '../../../components/Editors/TableInputTextEditor';

export const SecondaryIdsFormTable = ({
  secondaryIds,
  editingRows,
  onRowEditChange,
  tableRef,
  deletionHandler,
  errorMessages,
  internalOnChangeHandler,
  textOnChangeHandler,
  evidenceOnChangeHandler,
}) => {

  let headerGroup = <ColumnGroup>
    <Row>
      <Column header="Actions" />
      <Column header="Secondary ID" />
      <Column header="Internal" />
      <Column header="Evidence" />
    </Row>
  </ColumnGroup>;


  return (
    <DataTable value={secondaryIds} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup} size='small'
      editingRows={editingRows} resizableColumns columnResizeMode="expand" onRowEditChange={onRowEditChange} ref={tableRef}>
      <Column editor={(props) => <DeleteAction deletionHandler={deletionHandler} id={props?.rowData?.dataKey} />}
        className='max-w-4rem' bodyClassName="text-center" headerClassName='surface-0' frozen />
      <Column 
        editor={(props) => {
          return <TableInputTextEditor
            value={props.value} 
            rowIndex={props.rowIndex} 
            errorMessages={errorMessages}
            dataKey={props?.rowData?.dataKey}
            textOnChangeHandler={textOnChangeHandler}
            field="secondaryId"
        />}} 
        field="secondaryId" header="Secondary ID" headerClassName='surface-0' className="w-4"/>
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

