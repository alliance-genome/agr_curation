import React, {useRef, useState} from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { GeneService } from '../service/GeneService';
import { useQuery } from 'react-query';
import { Messages } from 'primereact/messages';

export const GenesComponent = () => {

  const [genes, setGenes] = useState(null);
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [filters, setFilters] = useState({});
  const [page, setPage] = useState(0);
  const [first, setFirst] = useState(0);
  const [rows, setRows] = useState(50);
  const [totalRecords, setTotalRecords] = useState(0);

  const geneService = new GeneService();
  const errorMessage = useRef(null);

    useQuery(['genes', rows, page, multiSortMeta, filters],
    () => geneService.getGenes(rows, page, multiSortMeta, filters), {
        onSuccess: (data) => {
          setGenes(data.results);
          setTotalRecords(data.totalResults);
        },
        onError: (error) => {
            errorMessage.current.show([
                { severity: 'error', summary: 'Error', detail: error.message, sticky: true }
            ])
        },
         keepPreviousData:true
    });



  const onLazyLoad = (event) => {
    setRows(event.rows);
    setPage(event.page);
    setFirst(event.first);
  }

  const onFilter = (event) => {
    //console.log("On Filter: ");
    //console.log(event.filters);
    setFilters(event.filters);
  }

  const onSort = (event) => {
    //console.log("On Sort: ");
    //console.log(event);
    let found = false;
    const newSort = [...multiSortMeta];

    newSort.forEach((o) => {
      if(o.field === event.multiSortMeta[0].field) {
        o.order = event.multiSortMeta[0].order;
        found = true;
      }
    });

    if(!found) {
      setMultiSortMeta(newSort.concat(event.multiSortMeta));
    } else {
      setMultiSortMeta(newSort);
    }
  }


  return (
      <div>
        <div className="card">
          <h3>Genes Table</h3>
            <Messages ref={errorMessage}/>
          <DataTable value={genes} className="p-datatable-sm"
            sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
            first={first} onFilter={onFilter} filters={filters}
            paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
            paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
            currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
          >

            <Column field="curie" header="Curie" sortable filter></Column>
            <Column field="name" header="Name" sortable filter></Column>
            <Column field="symbol" header="Symbol" sortable filter></Column>
            <Column field="taxon" header="Taxon" sortable filter filterPlaceholder="Search by Taxon"></Column>
          </DataTable>

        </div>
      </div>
  )
}
