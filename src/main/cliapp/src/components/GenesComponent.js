import React, { useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { GeneService } from '../service/GeneService';

export const GenesComponent = () => {

    const [genes, setGenes] = useState(null);
    const [page, setPage] = useState(0);
    const [first, setFirst] = useState(0);
    const [rows, setRows] = useState(50);
    const [totalRecords, setTotalRecords] = useState(0);
    
    useEffect(() => {
        const geneService = new GeneService();
        geneService.getGenes(rows, page).then(searchReults => {
            setGenes(searchReults.results);
            setTotalRecords(searchReults.totalResults);
        });
    }, [rows, page]);

    const onLazyLoad = (event) => {
        setRows(event.rows);
        setPage(event.page);
        setFirst(event.first);
    }

    const paginatorLeft = <Button type="button" icon="pi pi-refresh" className="p-button-text" />;
    const paginatorRight = <Button type="button" icon="pi pi-cloud" className="p-button-text" />;

    return (
            <div>
                <div className="card">
                    <h3>Genes Table</h3>
                    <DataTable value={genes} className="p-datatable-sm"
                        paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy first={first}
                        paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
                        currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
                        paginatorLeft={paginatorLeft} paginatorRight={paginatorRight}>
                        <Column field="curie" header="Curie"></Column>
                        <Column field="name" header="Name"></Column>
                        <Column field="symbol" header="Symbol"></Column>
                        <Column field="taxon" header="Taxon"></Column>
                    </DataTable>
                </div>
            </div>
    )
}
