import React, { useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { AlleleService } from '../service/AlleleService';
import { useQuery } from 'react-query';

export const AllelesComponent = () => {

  const [alleles, setAlleles] = useState(null);
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [filters, setFilters] = useState({});
  const [page, setPage] = useState(0);
  const [first, setFirst] = useState(0);
  const [rows, setRows] = useState(50);
  const [totalRecords, setTotalRecords] = useState(0);

  const alleleService = new AlleleService();

  const { isError } = useQuery(['alleles', rows, page, multiSortMeta, filters],
    () => alleleService.getAlleles(rows, page, multiSortMeta, filters), {
    onSuccess: (data) => {
      setAlleles(data.results);
      setTotalRecords(data.totalResults);
    },
    keepPreviousData: true
  })

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

  const symbolTemplate = (rowData) => {
    return <div dangerouslySetInnerHTML={{__html: rowData.symbol}} />
  }

  const paginatorLeft = <Button type="button" icon="pi pi-refresh" className="p-button-text" />;
  const paginatorRight = <Button type="button" icon="pi pi-cloud" className="p-button-text" />;

  return (
      <div>
        <div className="card">
          <DataTable value={alleles} className="p-datatable-sm"
            paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy first={first} 
            sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
            onFilter={onFilter} filters={filters}
            paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
            currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
            paginatorLeft={paginatorLeft} paginatorRight={paginatorRight}>

            <Column field="curie" header="Curie" sortable filter></Column>
            <Column field="description" header="Description" sortable filter></Column>
            <Column field="symbol" header="Symbol" body={symbolTemplate} sortable filter></Column>
            <Column field="taxon" header="Taxon" sortable filter></Column>
          </DataTable>
        </div>
      </div>
  )
}
