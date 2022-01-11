import React, {useRef, useState} from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from 'primereact/messages';
import { FilterComponent } from '../../components/FilterComponent';
import { MultiSelect } from 'primereact/multiselect';

import { returnSorted } from '../../utils/utils';

export const AllelesTable = () => {

  const [alleles, setAlleles] = useState(null);
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [filters, setFilters] = useState({});
  const [page, setPage] = useState(0);
  const [first, setFirst] = useState(0);
  const [rows, setRows] = useState(50);
  const [totalRecords, setTotalRecords] = useState(0);
  const [isEnabled, setIsEnabled] = useState(true);
  const searchService = new SearchService();
  const errorMessage = useRef(null);

  const columnNames = ["Curie", "Description", "Symbol", "Taxon"];

  const [selectedColumnNames, setSelectedColumnNames] = useState(columnNames);

  useQuery(['alleles', rows, page, multiSortMeta, filters],
    () => searchService.search("allele", rows, page, multiSortMeta, filters), {
    onSuccess: (data) => {
      setIsEnabled(true);
      setAlleles(data.results);
      setTotalRecords(data.totalResults);
    },
      onError: (error) => {
          errorMessage.current.show([
              { severity: 'error', summary: 'Error', detail: error.message, sticky: true }
          ])
      },
    keepPreviousData: true
  })


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

  const symbolTemplate = (rowData) => {
    return <div dangerouslySetInnerHTML={{__html: rowData.symbol}} />
  }

  const filterComponentTemplate = (filterName, fields) => {
    return (<FilterComponent 
          isEnabled={isEnabled} 
          fields={fields} 
          filterName={filterName}
          currentFilters={filters}
          onFilter={onFilter}
      />);}
 
  const columns = [
    {
      field:"curie",
      header:"Curie",
      sortable:{isEnabled},
      filter: true,
      filterElement: filterComponentTemplate("curieFilter", ["curie"])
    }, 
    {
      field:"description",
      header:"Description",
      sortable: isEnabled, 
      filter: true,
      filterelement: filterComponentTemplate("descriptionFilter", ["description"])
    }, 
    {
      field:"symbol",
      header:"Symbol",
      body: symbolTemplate,
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("symbolFilter", ["symbol"])
    },
    {
      field:"taxon",
      header:"Taxon",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("taxonFilter", ["taxon"])
    } 
  ];
  
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

  const columnMap = filteredColumns.map((col) => {
    return <Column
      columnKey={col.field}
      key={col.field}
      field={col.field}
      header={col.header}
      sortable={isEnabled}
      filter={col.filter}
      filterElement={col.filterElement}
      body={col.body}
    />;
  });

  return (
      <div>
        <div className="card">
            <h3>Alleles Table</h3>
            <Messages ref={errorMessage}/>
          <DataTable value={alleles} className="p-datatable-sm" header={header} reorderableColumns  
            paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy first={first}
            sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
            paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
            resizableColumns columnResizeMode="fit" showGridlines
            currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
          >
            {columnMap}
          </DataTable>
        </div>
      </div>
  )
}
