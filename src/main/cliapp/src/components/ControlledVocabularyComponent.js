import React, {useRef, useState} from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from 'primereact/messages';
import { FilterComponent } from './FilterComponent'

import { returnSorted } from '../utils/utils';

export const ControlledVocabularyComponent = () => {

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

    useQuery(['genes', rows, page, multiSortMeta, filters],
    () => searchService.search("vocabularyterm", rows, page, multiSortMeta, filters), {
        onSuccess: (data) => {
          setIsEnabled(true);
          setTerms(data.results);
          setTotalRecords(data.totalResults);
        },
        onError: (error) => {
            errorMessage.current.show([
                { severity: 'error', summary: 'Error:&nbsp;', detail: error.message, sticky: true }
            ])
        },
         keepPreviousData:true
    });



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
          <h3>Controlled Vocabulary Terms Table</h3>
            <Messages ref={errorMessage}/>
          <DataTable value={terms} className="p-datatable-sm"
            sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
            first={first} onFilter={onFilter} filters={filters}
            paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
            paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
            currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
          >

            <Column field="id" header="Id" />
            <Column field="name" header="Name" sortable={isEnabled} filter filterElement={filterComponentTemplate("nameFilter", ["name"])}></Column>
            <Column field="vocabulary.name" header="Vocabulary" sortable={isEnabled} filter filterElement={filterComponentTemplate("ivocabularyNameFilter", ["vocabulary.name"])} />
            <Column field="definition" header="Definition" sortable={isEnabled} filter filterElement={filterComponentTemplate("definitionFilter", ["definition"])} />
            <Column field="isObsolete" header="Obsolete" sortable={isEnabled} filter filterElement={filterComponentTemplate("isObsoleteFilter", ["isObsolete"])} />
            
          </DataTable>

        </div>
      </div>
  )
}
