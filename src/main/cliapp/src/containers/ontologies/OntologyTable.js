import React, { useRef, useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from "primereact/messages";
import { FilterComponent } from '../../components/FilterComponent'
import { MultiSelect } from 'primereact/multiselect';

import { returnSorted } from '../../utils/utils';

export const OntologyTable = ({ endpoint, ontologyAbbreviation, columnMap: columns  }) => {

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
    const columnNames = columns.map((col) => {
      return col.header;
    }); 

    const [selectedColumnNames, setSelectedColumnNames] = useState(columnNames);

    useQuery(['terms', rows, page, multiSortMeta, filters],
        () => searchService.search(endpoint, rows, page, multiSortMeta, filters), {
        onSuccess: (data) => {
            setIsEnabled(true);
            setTerms(data.results);
            setTotalRecords(data.totalResults);
        },
        onError: (error) => {
            errorMessage.current.show([
                { severity: 'error', summary: 'Error', detail: error.message, sticky: true }
            ])
        },
        keepPreviousData: true,
        refetchOnWindowFocus: false
    });



    const onLazyLoad = (event) => {
        setRows(event.rows);
        setPage(event.page);
        setFirst(event.first);
    }

    const onFilter = (filtersCopy) => {
        setFilters({ ...filtersCopy });
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

  const obsoleteTemplate = (rowData) => {
        if(rowData && rowData.obsolete !== null && rowData.obsolete !== undefined){
            return <div>{JSON.stringify(rowData.obsolete)}</div>
        }
    }; 

  const header = (
    <div style={{ textAlign: 'left' }}>
      <MultiSelect
        value={selectedColumnNames}
        options={columnNames}
        onChange={e => setSelectedColumnNames(e.value)}
        style={{ width: '20em' }}
        disabled={!isEnabled}
      />
    </div>
  );

  const filteredColumns = columns.filter((col) => {
    return selectedColumnNames.includes(col.header);
  });


  const columnMap = filteredColumns.map((col, i) => {
      if (col.field === 'obsolete') {
        return <Column
          key={col.field}
          field={col.field}
          header={col.header}
          sortable={isEnabled}
          body={obsoleteTemplate}
          filter
          filterElement={filterComponentTemplate(col.field + "Filter", [col.field])}
          />;
      }
      return <Column
        key={col.field}
        field={col.field}
        header={col.header}
        sortable={isEnabled}
        filter
        filterElement={filterComponentTemplate(col.field + "Filter", [col.field])}
        />;
    });

    return (
        <div>
            <div className="card">
                <h3>{ontologyAbbreviation} Table</h3>
                <Messages ref={errorMessage} />
                <DataTable value={terms} className="p-datatable-sm" header={header} 
                    sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
                    paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy first={first}
                    paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
                    currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
                    resizableColumns columnResizeMode="fit" showGridlines
                >
                    {columnMap}
                    
                </DataTable>
            </div>
        </div>
    )
}
