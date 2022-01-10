import React, { useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService'
import { FilterComponent } from '../../components/FilterComponent'
import { MultiSelect } from 'primereact/multiselect';

import { returnSorted } from '../../utils/utils';

export const AffectedGenomicModelTable = () => {

  const [agms, setAgms] = useState(null);

  const [page, setPage] = useState(0);
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [filters, setFilters] = useState({});
  const [rows, setRows] = useState(50);
  const [totalRecords, setTotalRecords] = useState(0);
  const [first, setFirst] = useState(0);
  const columnNames = ["Curie", "Name", "Sub Type", "Parental Population", "Taxon"];

  const [selectedColumnNames, setSelectedColumnNames] = useState(columnNames);
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
 

  const columns = [
    {
      field:"curie",
      header:"Curie",
      sortable: isEnabled, 
      filter: true,
      filterElement: filterComponentTemplate("curieFilter", ["curie"])
    }, 
    {
      field:"name",
      header:"Name",
      body: nameTemplate, 
      sortable: isEnabled,  
      filter: false,
      filterElement: filterComponentTemplate("nameFilter", ["name"])
    }, 
    {
      field:"subtype",
      header:"Sub Type",
      sortable: isEnabled, 
      filter : true, 
      filterElement: filterComponentTemplate("subtypeFilter", ["subtype"])
    },
    {
      field:"parental_population",
      header:"Parental Population",
      sortable: isEnabled,  
      filter: true,
      filterElement: filterComponentTemplate("parental_populationFilter", ["parental_population"])
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
      key={col.field}
      field={col.field}
      header={col.header}
      sortable={isEnabled}
      filter={col.filter}
      filterElement={col.filterElement}
      body={col.body}
    />;
  })
  return (
      <div>
        <div className="card">
          <DataTable value={agms} className="p-datatable-md" header={header}
            sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
            first={first} resizableColumns columnResizeMode="fit" showGridlines
            paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
            paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
            currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
    >
          {columnMap}
          </DataTable>
        </div>
      </div>
  )
}
