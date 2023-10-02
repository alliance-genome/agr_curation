import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { CrossReferencesTemplate } from '../../../components/Templates/CrossReferenceTemplate';

export const ReferencesFormTable = ({
  references,
  tableRef,
  deletionHandler,
}) => {

  let headerGroup = <ColumnGroup>
    <Row>
      <Column header="Actions" />
      <Column header="Curie" />
      <Column header="Cross References" />
      <Column header="Short Citation" />
    </Row>
  </ColumnGroup>;


  return (
    <DataTable value={references} dataKey="dataKey" showGridlines headerColumnGroup={headerGroup}
      resizableColumns columnResizeMode="expand" ref={tableRef} editMode='row'>
      <Column body={(data, props) => <DeleteAction deletionHandler={deletionHandler} index={props.rowIndex} />}
        className='max-w-4rem' bodyClassName="text-center" headerClassName='surface-0' frozen />
      <Column field="curie" header="Curie" />
      <Column field="cross_references" header="Cross References" body={(data) => <CrossReferencesTemplate rowData={data} />} />
      <Column field="short_citation" header="Short Citation" />
    </DataTable>
  );
};

