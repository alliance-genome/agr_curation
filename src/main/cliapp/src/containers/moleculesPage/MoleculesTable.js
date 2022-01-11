import React, {useRef, useState} from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from 'primereact/messages';
import { FilterComponent } from '../../components/FilterComponent'
import { MultiSelect } from 'primereact/multiselect';

import { returnSorted } from '../../utils/utils';

export const MoleculesTable = () => {

  const [molecules, setMolecules] = useState(null);
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [filters, setFilters] = useState({});
  const [page, setPage] = useState(0);
  const [first, setFirst] = useState(0);
  const [rows, setRows] = useState(50);
  const [totalRecords, setTotalRecords] = useState(0);
  const [isEnabled, setIsEnabled] = useState(true);
  const searchService = new SearchService();
  const errorMessage = useRef(null);
  const columnNames = ["Curie", "Name", "InChi", "InChiKey", "IUPAC", "Formula", "SMILES"];

  const [selectedColumnNames, setSelectedColumnNames] = useState(columnNames);

    useQuery(['molecules', rows, page, multiSortMeta, filters],
    () => searchService.search("molecule", rows, page, multiSortMeta, filters), {
        onSuccess: (data) => {
          setIsEnabled(true);
          setMolecules(data.results);
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
      field:"curie",
      header:"Curie",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("curieFilter", ["curie"])
    }, 
    {
      field:"name",
      header:"Name",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("nameFilter", ["name"])
    }, 
    {
      field:"inchi",
      header:"InChi",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("inchiFilter", ["inchi"])
    }, 
    {
      field:"inchi_key",
      header:"InChiKey",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("inchi_keyFilter", ["inchi_key"])
    }, 
    {
      field:"iupac",
      header:"IUPAC",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("iupacFilter", ["iupac"]),
    },
    {
      field:"formula",
      header:"Formula",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("formulaFilter", ["formula"])
    },
    {
      field:"smiles",
      header:"SMILES",
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("smilesFilter", ["smiles"])
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
    />;
  });                                                            

  return (
      <div>
        <div className="card">
          <h3>Molecules Table</h3>
            <Messages ref={errorMessage}/>
          <DataTable value={molecules} className="p-datatable-sm" header={header} 
            sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
            first={first}
            paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
            paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
            currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
            resizableColumns columnResizeMode="fit" showGridlines
          >
            {columnMap}
          </DataTable>

        </div>
      </div>
  )
}
