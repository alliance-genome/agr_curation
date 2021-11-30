import React, { useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService'
import { FilterComponent } from '../FilterComponent'

import { returnSorted } from '../../utils/utils';

export const AffectedGenomicModelComponent = () => {

  const [agms, setAgms] = useState(null);

  const [page, setPage] = useState(0);
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [filters, setFilters] = useState({});
  const [rows, setRows] = useState(50);
  const [totalRecords, setTotalRecords] = useState(0);
  const [first, setFirst] = useState(0);
  const [isEnabled, setIsEnabled] = useState(true);

  useEffect(() => {

      const searchService = new SearchService();
      searchService.search("agm", rows, page, multiSortMeta, filters).then(searchResults => {
        setIsEnabled(true);
        setAgms(searchResults.results);
        setTotalRecords(searchResults.totalResults);
      });

  }, [rows, page, multiSortMeta, filters]);

  const onLazyLoad = (event) => {
      setRows(event.rows);
      setPage(event.page);
      setFirst(event.first);

  }


  const onFilter = (filtersCopy) => { 
      setFilters({...filtersCopy});
  };

  const onSort = (event) => {
      setMultiSortMeta(
          returnSorted(event, multiSortMeta)
      )
  };

  const nameTemplate = (rowData) => {
    return <div dangerouslySetInnerHTML={{__html: rowData.name}} />
  }

  const filterComponentTemplate = (filterName, fields) => {
    return (<FilterComponent 
          isEnabled={isEnabled} 
          fields={fields} 
          filterName={filterName}
          currentFilters={filters}
          onFilter={onFilter}
      />);
  };

  return (
      <div>
        <div className="card">
          <DataTable value={agms} className="p-datatable-md"
            sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
            first={first}
            paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
            paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
            currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
    >
            <Column field="curie" header="Curie" sortable={isEnabled} filter filterElement={filterComponentTemplate("curieFilter", ["curie"])} />
            <Column field="name" header="Name" body={nameTemplate} sortable={isEnabled} filter filterElement={filterComponentTemplate("nameFilter", ["name"])} />
            <Column field="subtype" header="Sub Type" sortable={isEnabled} filter filterElement={filterComponentTemplate("subtypeFilter", ["subtype"])} />
            <Column field="parental_population" header="Parental Population" sortable={isEnabled} filter filterElement={filterComponentTemplate("parental_populationFilter", ["parental_population"])} />
            <Column field="taxon" header="Taxon" sortable={isEnabled} filter filterElement={filterComponentTemplate("taxonFilter", ["taxon"])}></Column>
          </DataTable>
        </div>
      </div>
  )
}
