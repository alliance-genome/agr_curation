import React, { useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { GeneService } from '../service/GeneService';

export const GenesComponent = () => {

	const [genes, setGenes] = useState(null);
	const [multiSortMeta, setMultiSortMeta] = useState([]);
	const [filters, setFilters] = useState({});
	const [page, setPage] = useState(0);
	const [first, setFirst] = useState(0);
	const [rows, setRows] = useState(50);
	const [totalRecords, setTotalRecords] = useState(0);
	
	useEffect(() => {
		const geneService = new GeneService();
		geneService.getGenes(rows, page, multiSortMeta, filters).then(searchReults => {
			setGenes(searchReults.results);
			setTotalRecords(searchReults.totalResults);
		});
	}, [rows, page, multiSortMeta, filters]);

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
		var found = false;
		var newSort = [...multiSortMeta];

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

	const paginatorLeft = <Button type="button" icon="pi pi-refresh" className="p-button-text" />;
	const paginatorRight = <Button type="button" icon="pi pi-cloud" className="p-button-text" />;

	return (
			<div>
				<div className="card">
					<h3>Genes Table</h3>
					<DataTable value={genes} className="p-datatable-sm"
						sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
						first={first} onFilter={onFilter} filters={filters}
						paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy 
						paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
						currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
						paginatorLeft={paginatorLeft} paginatorRight={paginatorRight}>
						
						<Column field="curie" header="Curie" sortable filter></Column>
						<Column field="name" header="Name" sortable filter></Column>
						<Column field="symbol" header="Symbol" sortable filter></Column>
						<Column field="taxon" header="Taxon" sortable filter filterPlaceholder="Search by Taxon"></Column>
					</DataTable>
				</div>
			</div>
	)
}
