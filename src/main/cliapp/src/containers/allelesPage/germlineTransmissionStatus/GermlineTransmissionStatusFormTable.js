import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { ControlledVocabularyEditor } from '../../../components/Editors/ControlledVocabularyEditor';

export const GermlineTransmissionStatusFormTable = ({
  name,
  editingRows,
  onRowEditChange,
  tableRef,
  deletionHandler,
  errorMessages,
  germlineTransmissionStatusOnChangeHandler,
  internalOnChangeHandler,
  evidenceOnChangeHandler,
}) => {


  let headerGroup =
    <ColumnGroup>
      <Row>
        <Column header="Actions" />
        <Column header="Germline Transmission Status" />
        <Column header="Internal" />
        <Column header="Evidence" />
      </Row>
    </ColumnGroup>;

  return (
    <DataTable value={name} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup} size='small'
      editingRows={editingRows} resizableColumns columnResizeMode="expand" onRowEditChange={onRowEditChange} ref={tableRef}>
      <Column editor={(props) => <DeleteAction deletionHandler={deletionHandler} index={props.rowIndex} />}
        className='max-w-4rem' bodyClassName="text-center" headerClassName='surface-0' frozen />
      <Column
        editor={(props) => {
          return <ControlledVocabularyEditor
            props={props}
            onChangeHandler={germlineTransmissionStatusOnChangeHandler}
            errorMessages={errorMessages}
            dataKey={props?.rowData?.dataKey}
            rowIndex={props.rowIndex}
            vocabType="allele_germline_transmission_status"
            field="germlineTransmissionStatus"
          />;
        }}
        field="germlineTransmissionStatus" header="Germiline Transmission Status" headerClassName='surface-0' />
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

