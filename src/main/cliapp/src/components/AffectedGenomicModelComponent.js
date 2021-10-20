import React, { useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { AffectedGenomicModelService } from '../service/AffectedGenomicModelService'

export const AffectedGenomicModelComponent = () => {

  const [agms, setAgms] = useState(null);

  const [page, setPage] = useState(0);
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [filters, setFilters] = useState({});
  const [rows, setRows] = useState(50);
  const [totalRecords, setTotalRecords] = useState(0);
  const [first, setFirst] = useState(0);

  useEffect(() => {

      const agmService = new AffectedGenomicModelService();
      agmService.getAgms(rows, page, multiSortMeta, filters).then(searchResults => {

          setAgms(searchResults.results);
          setTotalRecords(searchResults.totalResults);


      });

  }, [rows, page, multiSortMeta, filters]);

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
    var found = false;
    var newSort = [...multiSortMeta];

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

  const nameTemplate = (rowData) => {
    return <div dangerouslySetInnerHTML={{__html: rowData.name}} />
  }


  return (
      <div>
        <div className="card">
          <DataTable value={agms} className="p-datatable-md"
            sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
            first={first} onFilter={onFilter} filters={filters}
            paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
            paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
            currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
    >
            <Column field="curie" header="Curie" sortable filter></Column>
            <Column field="name" header="Name" body={nameTemplate} sortable filter></Column>
            <Column field="subtype" header="Sub Type" sortable filter></Column>
            <Column field="parental_population" header="Parental Population" sortable filter></Column>
            <Column field="taxon" header="Taxon" sortable filter></Column>
          </DataTable>
        </div>
      </div>
  )
}
