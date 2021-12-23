import React, {useRef, useState} from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { useQuery } from 'react-query';
import { Messages } from 'primereact/messages';
import { FilterComponent } from '../../components/FilterComponent'

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

  return (
      <div>
        <div className="card">
          <h3>Molecules Table</h3>
            <Messages ref={errorMessage}/>
          <DataTable value={molecules} className="p-datatable-sm"
            sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
            first={first}
            paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
            paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
            currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
          >

            <Column field="curie" header="Curie" sortable={isEnabled} filter filterElement={filterComponentTemplate("curieFilter", ["curie"])} />
            <Column field="name" header="Name" sortable={isEnabled} filter filterElement={filterComponentTemplate("nameFilter", ["name"])} />
            <Column field="inchi" header="InChi" sortable={isEnabled} filter filterElement={filterComponentTemplate("inchiFilter", ["inchi"])} />
            <Column field="inchi_key" header="InChiKey" sortable={isEnabled} filter filterElement={filterComponentTemplate("inchi_keyFilter", ["inchi_key"])} />
            <Column field="iupac" header="IUPAC" sortable={isEnabled} filter filterElement={filterComponentTemplate("iupacFilter", ["iupac"])} />
            <Column field="formula" header="Formula" sortable={isEnabled} filter filterElement={filterComponentTemplate("formulaFilter", ["formula"])} />
            <Column field="smiles" header="SMILES" sortable={isEnabled} filter filterElement={filterComponentTemplate("smilesFilter", ["smiles"])} />
          </DataTable>

        </div>
      </div>
  )
}
