import React, {useRef, useState} from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { MoleculeService } from '../service/MoleculeService';
import { useQuery } from 'react-query';
import { Messages } from 'primereact/messages';

export const MoleculesComponent = () => {

  const [molecules, setMolecules] = useState(null);
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [filters, setFilters] = useState({});
  const [page, setPage] = useState(0);
  const [first, setFirst] = useState(0);
  const [rows, setRows] = useState(50);
  const [totalRecords, setTotalRecords] = useState(0);

  const moleculeService = new MoleculeService();
  const errorMessage = useRef(null);

    useQuery(['molecules', rows, page, multiSortMeta, filters],
    () => moleculeService.getMolecules(rows, page, multiSortMeta, filters), {
        onSuccess: (data) => {
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

  const onFilter = (event) => {
    //console.log("On Filter: ");
    //console.log(event.filters);
    setFilters(event.filters);
  }

  const onSort = (event) => {
    //console.log("On Sort: ");
    //console.log(event);
    let found = false;
    const newSort = [...multiSortMeta];

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


  return (
      <div>
        <div className="card">
          <h3>Molecules Table</h3>
            <Messages ref={errorMessage}/>
          <DataTable value={molecules} className="p-datatable-sm"
            sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
            first={first} onFilter={onFilter} filters={filters}
            paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
            paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
            currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
          >

            <Column field="curie" header="Curie" sortable filter></Column>
            <Column field="name" header="Name" sortable filter></Column>
            <Column field="inchi" header="InChi" sortable filter></Column>
            <Column field="inchi_key" header="InChiKey" sortable filter></Column>
            <Column field="iupac" header="IUPAC" sortable filter></Column>
            <Column field="formula" header="Formula" sortable filter></Column>
            <Column field="smiles" header="SMILES" sortable filter></Column>
          </DataTable>

        </div>
      </div>
  )
}
