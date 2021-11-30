import React, {useRef, useState} from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from "primereact/messages";
import { FilterComponent } from '../FilterComponent'

import { returnSorted } from '../../utils/utils';

export const DiseaseOntologyComponent = () => {

  const [terms, setTerms] = useState(null);
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [filters, setFilters] = useState({});
  const [page, setPage] = useState(0);
  const [first, setFirst] = useState(0);
  const [rows, setRows] = useState(50);
  const [totalRecords, setTotalRecords] = useState(0);
  const [isEnabled, setIsEnabled] = useState(true);
  const searchService = new SearchService();
  const errorMessage = useRef(null);

  useQuery(['terms', rows, page, multiSortMeta, filters],
    () => searchService.search('doterm', rows, page, multiSortMeta, filters), {
    onSuccess: (data) => {
      setTerms(data.results);
      setTotalRecords(data.totalResults);
    },
      onError: (error) => {
          errorMessage.current.show([
              { severity: 'error', summary: 'Error', detail: error.message, sticky: true }
          ])
      },
    keepPreviousData: true
  });

  const onLazyLoad = (event) => {
    setRows(event.rows);
    setPage(event.page);
    setFirst(event.first);
  };

  const onFilter = (filtersCopy) => { 
      setFilters({...filtersCopy});
  };

  const onSort = (event) => {
      setMultiSortMeta(
          returnSorted(event, multiSortMeta)
      )
  };

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
            <h3>Diseases Table</h3>
            <Messages ref={errorMessage}/>
          <DataTable value={terms} className="p-datatable-sm"
            sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
            paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy first={first}
            paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
            currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
          >
            <Column field="curie" header="Curie" sortable={isEnabled} filter filterElement={filterComponentTemplate("curieFilter", ["curie"])} />
            <Column field="name" header="Name" sortable={isEnabled} filter filterElement={filterComponentTemplate("nameFilter", ["name"])} />
            <Column field="definition" header="Definition" sortable={isEnabled} filter filterElement={filterComponentTemplate("definitionFilter", ["definition"])} />
          </DataTable>
        </div>
      </div>
  )
}
