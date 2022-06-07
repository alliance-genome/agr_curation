import React, { useEffect, useRef, useState } from 'react';
import { useSessionStorage } from '../../service/useSessionStorage';
import { useSetDefaultColumnOrder } from '../../utils/useSetDefaultColumnOrder';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { SearchService } from '../../service/SearchService';
import { FilterComponentInputText } from '../../components/FilterComponentInputText'
import { MultiSelect } from 'primereact/multiselect';
import { Tooltip } from 'primereact/tooltip';

import { returnSorted, filterColumns, orderColumns, reorderArray, setDefaultColumnOrder } from '../../utils/utils';
import { DataTableHeaderFooterTemplate } from "../../components/DataTableHeaderFooterTemplate";
import {EllipsisTableCell} from "../../components/EllipsisTableCell";

export const AffectedGenomicModelTable = () => {
	const defaultColumnNames = ["Curie", "Name", "Sub Type", "Parental Population", "Taxon"];
	const defaultVisibleColumns = ["Curie", "Name", "Sub Type", "Taxon"];
	let initialTableState = {
		page: 0,
		first: 0,
		rows: 50,
		multiSortMeta: [],
		selectedColumnNames: defaultVisibleColumns,
		filters: {},
		isFirst: true,
	}
	const [tableState, setTableState] = useSessionStorage("agmTableSettings", initialTableState);

	const [agms, setAgms] = useState(null);
	const [totalRecords, setTotalRecords] = useState(0);
	const [isEnabled, setIsEnabled] = useState(true);
	const [columnList, setColumnList] = useState([]);

	const dataTable = useRef(null);

	useEffect(() => {
		const searchService = new SearchService();
		searchService.search("agm", tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters).then(searchResults => {
			setIsEnabled(true);
			setAgms(searchResults.results);
			setTotalRecords(searchResults.totalResults);
		});

	}, [tableState]);

	const setIsFirst = (value) => {
		let _tableState = {
			...tableState,
			isFirst: value,
		};

		setTableState(_tableState);
	}

	const onLazyLoad = (event) => {
		let _tableState = {
			...tableState,
			rows: event.rows,
			page: event.page,
			first: event.first
		};

		setTableState(_tableState);
	}

	const onSort = (event) => {
		let _tableState = {
			...tableState,
			multiSortMeta: returnSorted(event, tableState.multiSortMeta)
		}
		setTableState(_tableState);
	};

	const onFilter = (filtersCopy) => {
		let _tableState = {
			...tableState,
			filters: { ...filtersCopy }
		}
		setTableState(_tableState);
	};

	const setSelectedColumnNames = (newValue) => {
		let _tableState = {
			...tableState,
			selectedColumnNames: newValue
		};

		setTableState(_tableState);
	};

		const createMultiselectComponent = (tableState,defaultColumnNames,isEnabled) => {
				return (<MultiSelect
						value={tableState.selectedColumnNames}
						options={defaultColumnNames}
						onChange={e => setSelectedColumnNames(e.value)}
						style={{ width: '20em', textAlign: 'center' }}
						disabled={!isEnabled}
				/>);
		};

	const header = (
			<DataTableHeaderFooterTemplate
					title = {"Affected Genomic Models Table"}
					tableState = {tableState}
					defaultColumnNames = {defaultColumnNames}
					multiselectComponent = {createMultiselectComponent(tableState,defaultColumnNames,isEnabled)}
					onclickEvent = {(event) => resetTableState(event)}
					isEnabled = {isEnabled}
			/>
	);

	const filterComponentTemplate = (filterName, fields) => {
		return (<FilterComponentInputText
			isEnabled={isEnabled}
			fields={fields}
			filterName={filterName}
			currentFilters={tableState.filters}
			onFilter={onFilter}
		/>);
	};

	const nameTemplate = (rowData) => {
		return (
			<>
				<div className={`overflow-hidden text-overflow-ellipsis ${rowData.curie.replace(':', '')}`} dangerouslySetInnerHTML={{ __html: rowData.name }} />
				<Tooltip target={`.${rowData.curie.replace(':', '')}`}>
					<div dangerouslySetInnerHTML={{ __html: rowData.name }} />
				</Tooltip>
			</>
		)
	}

	const taxonBodyTemplate = (rowData) => {
		if (rowData.taxon) {
				return (
						<>
								<EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${rowData.taxon.curie.replace(':', '')}`}>
										{rowData.taxon.name} ({rowData.taxon.curie})
								</EllipsisTableCell>
								<Tooltip target={`.${"TAXON_NAME_"}${rowData.taxon.curie.replace(':', '')}`} content= {`${rowData.taxon.name} (${rowData.taxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
						</>
				);
		}
	};
	
	const columns = [
		{
			field: "curie",
			header: "Curie",
			sortable: isEnabled,
			filter: true,
			filterElement: filterComponentTemplate("curieFilter", ["curie"])
		},
		{
			field: "name",
			header: "Name",
			body: nameTemplate,
			sortable: isEnabled,
			filter: true,
			filterElement: filterComponentTemplate("nameFilter", ["name"])
		},
		{
			field: "subtype",
			header: "Sub Type",
			sortable: isEnabled,
			filter: true,
			filterElement: filterComponentTemplate("subtypeFilter", ["subtype"])
		},
		{
			field: "parental_population",
			header: "Parental Population",
			sortable: isEnabled,
			filter: true,
			filterElement: filterComponentTemplate("parental_populationFilter", ["parental_population"])
		},
		{
			field: "taxon.name",
			header: "Taxon",
			sortable: isEnabled,
			body: taxonBodyTemplate,
			filter: true,
			filterElement: filterComponentTemplate("taxonFilter", ["taxon.curie","taxon.name"])
		}
	];

	useSetDefaultColumnOrder(columns, dataTable, defaultColumnNames, setIsFirst, tableState.isFirst);

	const [columnWidths, setColumnWidths] = useState(() => {
		const width = 100 / columns.length;

		const widthsObject = {};

		columns.forEach((col) => {
			widthsObject[col.field] = width;
		});

		return widthsObject;
	});

	useEffect(() => {
		const filteredColumns = filterColumns(columns, tableState.selectedColumnNames);
		const orderedColumns = orderColumns(filteredColumns, tableState.selectedColumnNames);
		setColumnList(
			orderedColumns.map((col) => {
				return <Column
					style={{'minWidth':`${columnWidths[col.field]}vw`, 'maxWidth': `${columnWidths[col.field]}vw`}}
					headerClassName='surface-0'
					columnKey={col.field}
					key={col.field}
					field={col.field}
					header={col.header}
					sortable={isEnabled}
					filter={col.filter}
					showFilterMenu={false}
					filterElement={col.filterElement}
					body={col.body}
				/>;
			})
		);
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [tableState, isEnabled, columnWidths]);

	const resetTableState = () => {
		let _tableState = {
			...initialTableState,
			isFirst: false,
		};

		setTableState(_tableState);
		setDefaultColumnOrder(columns, dataTable, defaultColumnNames);
		const _columnWidths = { ...columnWidths };

		Object.keys(_columnWidths).map((key) => {
			return _columnWidths[key] = 100 / columns.length;
		});

		setColumnWidths(_columnWidths);
		dataTable.current.el.children[1].scrollLeft = 0;
	}

	const colReorderHandler = (event) => {
		let _columnNames = [...tableState.selectedColumnNames];
		_columnNames = reorderArray(_columnNames, event.dragIndex, event.dropIndex);
		setSelectedColumnNames(_columnNames);
	};

	const handleColumnResizeEnd = (event) => {
		const currentWidth = event.element.clientWidth;
		const delta = event.delta;
		const newWidth = Math.floor(((currentWidth + delta) / window.innerWidth) * 100);
		const field = event.column.props.field;

		const _columnWidths = {...columnWidths};

		_columnWidths[field] = newWidth;
		setColumnWidths(_columnWidths);
	};

	return (
			<div className="card">
				<DataTable value={agms} className="p-datatable-md" header={header} reorderableColumns
					ref={dataTable} filterDisplay="row" scrollHeight="62vh" scrollable
					tableClassName='w-12 p-datatable-md'
					sortMode="multiple" removableSort onSort={onSort} multiSortMeta={tableState.multiSortMeta}
					first={tableState.first} resizableColumns columnResizeMode="expand" showGridlines
					paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
					onColReorder={colReorderHandler} onColumnResizeEnd={handleColumnResizeEnd}
					paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
					currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={tableState.rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
				>
					{columnList}
				</DataTable>
			</div>
	)
}
