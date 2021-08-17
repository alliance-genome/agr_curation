import React, { useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { DiseaseAnnotationService } from '../service/DiseaseAnnotationService'

export const DiseaseAnnotations = () => {

    const [diseaseAnnotations, setDiseaseAnnotations] = useState(null);
    const [multiSortMeta, setMultiSortMeta] = useState([]);
    const [page, setPage] = useState(0);
    const [first, setFirst] = useState(0);
    const [rows, setRows] = useState(50);
    const [totalRecords, setTotalRecords] = useState(0);
    const [filters, setFilters] = useState();
    const [expandedRows, setExpandedRows] = useState(null);


    useEffect(() => {

        const diseaseAnnotationService = new DiseaseAnnotationService();
        diseaseAnnotationService.getDiseaseAnnotations(rows, page).then(searchResults => {
            setDiseaseAnnotations(searchResults.results);
            setTotalRecords(searchResults.totalResults);


        });

    }, [rows, page]);

    const onLazyLoad = (event) => {
        setRows(event.rows);
        setPage(event.page);
        setFirst(event.first);
    }

    const onSort = (event) => {
        setMultiSortMeta(event.multiSortMeta)
    }

    const onFilter = (event) => {
        //console.log("On Filter: ");
        //console.log(event.filters);
        setFilters(event.filters);
      }



    const publicationTemplate = (rowData) => {
        console.log(rowData);
        if(rowData)
            {return <div dangerouslySetInnerHTML={{__html: rowData.referenceList[0].curie}} />
        }
        
      }



    const paginatorLeft = <Button type="button" icon="pi pi-refresh" className="p-button-text" />;
    const paginatorRight = <Button type="button" icon="pi pi-cloud" className="p-button-text" />;

    return (
            <div>
                <div className="card">
                    <DataTable value={diseaseAnnotations} className="p-datatable-md" 
            
                        dataKey="id" expandedRows={expandedRows} onRowToggle={(e) => setExpandedRows(e.data)}
                        paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy 
                        paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
                        currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
                        paginatorLeft={paginatorLeft} paginatorRight={paginatorRight}>
                        <Column field="id" header="Curie" sortable ></Column>
                        <Column field="subject.curie" header="Gene" sortable ></Column>
                        <Column field="object.curie" header="Disease" sortable ></Column>
                        <Column field="referenceList" header="Reference" body={publicationTemplate} sortable ></Column>

                    </DataTable>
                </div>
            </div>
    )
}
