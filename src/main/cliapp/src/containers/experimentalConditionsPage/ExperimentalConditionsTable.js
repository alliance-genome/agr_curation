import React, { useRef, useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from 'primereact/messages';
import { FilterComponent } from '../../components/FilterComponent'
import { MultiSelect } from 'primereact/multiselect';

import { returnSorted } from '../../utils/utils';

export const ExperimentalConditionsTable = () => {

  const [experimentalConditions, setExperimentalConditions] = useState(null);
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [filters, setFilters] = useState({});
  const [page, setPage] = useState(0);
  const [first, setFirst] = useState(0);
  const [rows, setRows] = useState(50);
  const [totalRecords, setTotalRecords] = useState(0);
  const [isEnabled, setIsEnabled] = useState(true);
  const searchService = new SearchService();
  const errorMessage = useRef(null);
  const columnNames = ["Unique ID", "Statement", "Class", "ID", "Gene Ontology", "Chemical", "Anatomy", "Condition Taxon", "Quantity"];

  const [selectedColumnNames, setSelectedColumnNames] = useState(columnNames);

  useQuery(['experimentalConditions', rows, page, multiSortMeta, filters],
    () => searchService.search('experimental-condition', rows, page, multiSortMeta, filters), {
    onSuccess: (data) => {
      setIsEnabled(true);
      setExperimentalConditions(data.results);
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

  const columns = [
    {
      field: "uniqueId",
      header: "Unique ID",
      style: { whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word' },
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("uniqueIdFilter", ["uniqueId"])
    },
    {
      field:"conditionStatement",
      header:"Statement",
	  style: { whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word' },
      sortable: isEnabled,  
      filter: true,
      filterElement: filterComponentTemplate("conditionStatment", ["conditionStatement"])
    }, 
    {
      field:"conditionClass",
      header:"Class",
      sortable: isEnabled, 
      filter : true, 
      filterElement: filterComponentTemplate("conditionClass", ["conditionClass.curie", "conditionClass.name"])
    },
    {
      field:"conditionId",
      header:"ID",
      sortable: isEnabled,
      filter: true, 
      filterElement: filterComponentTemplate("conditionId", ["conditionId.curie", "conditionId.name"])
    },
    {
      field:"conditionGeneOntology",
      header:"Gene Ontology",
      sortable: isEnabled,
      filter: true, 
      filterElement: filterComponentTemplate("conditionId", ["conditionGeneOntology.curie", "conditionGeneOntology.name"])
    },
    {
      field:"conditionChemical",
      header:"Chemical",
      sortable: isEnabled,
      filter: true, 
      filterElement: filterComponentTemplate("conditionId", ["conditionChemical.curie", "conditionChemical.name"])
    },
    {
      field:"conditionAnatomy",
      header:"Anatomy",
      sortable: isEnabled,
      filter: true, 
      filterElement: filterComponentTemplate("conditionId", ["conditionAnatomy.curie", "conditionAnatomy.name"])
    },
    {
      field:"conditionTaxon",
      header:"Condition Taxon",
      sortable: isEnabled,
      filter: true, 
      filterElement: filterComponentTemplate("conditionId", ["conditionTaxon.curie", "conditionTaxon.name"])
    },
    {
      field:"conditionQuantity",
      header:"Quantity",
      sortable: isEnabled,
      filter: true, 
      filterElement: filterComponentTemplate("conditionId", ["conditionQuantity"])
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
      style={col.style}
    />;
  })                       

  return (
    <div>
      <div className="card">
        <h3>Genes Table</h3>
        <Messages ref={errorMessage} />
        <DataTable value={experimentalConditions} className="p-datatable-sm" header={header} reorderableColumns  
          sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
          first={first}
          resizableColumns columnResizeMode="fit" showGridlines
          paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
          paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
        >
            {columnMap}
        </DataTable>

      </div>
    </div>
  )
}
