import React, {useRef, useState} from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { DiseaseAnnotationService } from '../service/DiseaseAnnotationService'
import { useQuery } from 'react-query';
import {Messages} from "primereact/messages";


export const DiseaseAnnotationsComponent = () => {

  const [diseaseAnnotations, setDiseaseAnnotations] = useState(null);

  const [page, setPage] = useState(0);
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [filters, setFilters] = useState({});
  const [rows, setRows] = useState(50);
  const [totalRecords, setTotalRecords] = useState(0);
  const [first, setFirst] = useState(0);
  const [expandedRows, setExpandedRows] = useState(null);

  const diseaseAnnotationService = new DiseaseAnnotationService();
  const errorMessage = useRef(null);


  useQuery(['diseaseAnnotations', rows, page, multiSortMeta, filters],
    () => diseaseAnnotationService.getDiseaseAnnotations(rows, page, multiSortMeta, filters), {
    onSuccess: (data) => {
      setDiseaseAnnotations(data.results);
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


  const onFilter = (event) => {
    setFilters(event.filters);
  }

  const onSort = (event) => {
    let found = false;
    const newSort = [...multiSortMeta];

    newSort.forEach((o) => {
      if (o.field === event.multiSortMeta[0].field) {
        o.order = event.multiSortMeta[0].order;
        found = true;
      }
    });

    if (!found) {
      setMultiSortMeta(newSort.concat(event.multiSortMeta));
    } else {
      setMultiSortMeta(newSort);
    }
  }

  const publicationTemplate = (rowData) => {
    if (rowData) {
      return <div>{rowData.referenceList.map(a => a.curie)}</div>
    }
  };

  const paginatorLeft = <Button type="button" icon="pi pi-refresh" className="p-button-text" />;
  const paginatorRight = <Button type="button" icon="pi pi-cloud" className="p-button-text" />;

  return (
    <div>
      <div className="card">
        <h3>Disease Annotations Table</h3>
        <Messages ref={errorMessage}/>
        <DataTable value={diseaseAnnotations} className="p-datatable-md"
          sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
          first={first} onFilter={onFilter} filters={filters}
          dataKey="id" expandedRows={expandedRows} onRowToggle={(e) => setExpandedRows(e.data)}
          paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
          paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
          paginatorLeft={paginatorLeft} paginatorRight={paginatorRight}>
          <Column field="id" header="Id" ></Column>
          <Column field="subject.curie" header="Subject" sortable filter ></Column>
          <Column field="object.curie" header="Disease" sortable filter ></Column>
          <Column field="referenceList.curie" header="Reference" body={publicationTemplate} sortable filter ></Column>
        </DataTable>
      </div>
    </div>
  )
}
