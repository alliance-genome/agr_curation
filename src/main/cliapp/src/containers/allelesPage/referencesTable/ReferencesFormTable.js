import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { CrossReferenceTemplate } from '../../../components/Templates/reference/CrossReferenceTemplate';
import { ShortCitationTemplate } from '../../../components/Templates/reference/ShortCitationTemplate';

export const ReferencesFormTable = ({
  references,
  editingRows,
  onRowEditChange,
  tableRef,
  deletionHandler,
}) => {

  return (
    <DataTable value={references} dataKey="crossReferencesFilter" showGridlines editMode='row' removableSort filterDisplay='row' size='small'
      editingRows={editingRows} resizableColumns columnResizeMode="fit" onRowEditChange={onRowEditChange} ref={tableRef}
      paginator paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
      currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={5} rowsPerPageOptions={[5,10,20,50]}
    >
      <Column body={(data) => <DeleteAction deletionHandler={deletionHandler} id={data?.dataKey} />} header="Action"
        className='min-w-4rem max-w-4rem' bodyClassName="text-center" frozen />
      <Column field="curie" header="Curie" sortable filter showFilterMenu={false} filterMatchMode='contains'/>
      <Column field="crossReferences" header="Cross References" body={(data) => <CrossReferenceTemplate reference={data} />}
        filter filterField='crossReferencesFilter' filterMatchMode='contains' showFilterMenu={false} />
      <Column field="shortCitation"  sortable header="Short Citation" body={(data) => <ShortCitationTemplate rowData={data}/>} 
      filterField='shortCitation' filter showFilterMenu={false} filterMatchMode='contains'/>
    </DataTable>
  );
};


