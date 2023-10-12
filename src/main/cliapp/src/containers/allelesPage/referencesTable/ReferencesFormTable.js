import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { CrossReferencesTemplate } from '../../../components/Templates/CrossReferenceTemplate';
import { ShortCitationTemplate } from '../../../components/Templates/ShortCitationTemplate';
import { SingleReferenceTableEditor } from '../../../components/Editors/references/SingleReferenceTableEditor';

export const ReferencesFormTable = ({
  references,
  editingRows,
  onRowEditChange,
  errorMessages,
  tableRef,
  referencesOnChangeHandler,
  deletionHandler,
}) => {


  return (
    <DataTable value={references} dataKey="dataKey" showGridlines editMode='row' removableSort filterDisplay='row'
      editingRows={editingRows} resizableColumns columnResizeMode="expand" onRowEditChange={onRowEditChange} ref={tableRef}
      paginator paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
      currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={10} rowsPerPageOptions={[5,10,20,50]}
    >
      <Column editor={(props) => <DeleteAction deletionHandler={deletionHandler} index={props.rowIndex} />} header="Action"
        className='max-w-4rem' bodyClassName="text-center" frozen />
      <Column field="curie" editor={(props) => {
        return <SingleReferenceTableEditor
          props={props}
          errorMessages={errorMessages}
          onChange={referencesOnChangeHandler}
        />;
      }} header="Select" />
      <Column field="curie" header="Curie" sortable filter showFilterMenu={false} filterMatchMode='contains'/>
      <Column field="crossReferences" header="Cross References" body={(data) => <CrossReferencesTemplate rowData={data} />}
        filter filterField='crossReferencesFilter' filterMatchMode='contains' showFilterMenu={false} />
      <Column field="shortCitation"  sortable header="Short Citation" body={(data) => <ShortCitationTemplate rowData={data}/>} 
      filterField='shortCitation' filter showFilterMenu={false} filterMatchMode='contains'/>
    </DataTable>
  );
};


