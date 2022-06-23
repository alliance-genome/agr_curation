import React, { useEffect } from 'react';

import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { MultiSelect } from 'primereact/multiselect';

import { FilterComponent } from './FilterComponent'
import { DataTableHeaderFooterTemplate } from "../DataTableHeaderFooterTemplate";


import { filterColumns, orderColumns } from '../../utils/utils';
import { useGenericDataTable } from "./useGenericDataTable";

export const GenericDataTable = (props) => {

	const { tableName, isEnabled, aggregationFields, endpoint, columns, headerButtons } = props;

	const {
		setSelectedColumnNames,
		defaultColumnNames,
		tableState,
		resetTableState,
		onFilter,
		setColumnList,
		columnWidths,
		entity,
		dataTable,
		editingRows,
		onRowEditInit,
		onRowEditCancel,
		onRowEditSave,
		onRowEditChange,
		onSort,
		colReorderHandler,
		handleColumnResizeEnd,
		totalRecords,
		onLazyLoad,
		columnList,
	} = useGenericDataTable(props);

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
				title = {tableName + " Table"}
				tableState = {tableState}
				defaultColumnNames = {defaultColumnNames}
				multiselectComponent = {createMultiselectComponent(tableState,defaultColumnNames,isEnabled)}
				buttons = {headerButtons ? headerButtons() : undefined}
				onclickEvent = {(event) => resetTableState(event)}
				isEnabled = {isEnabled}
		/>
	);

	const filterComponentTemplate = ({ type, filterName, fields, options, optionField, useKeywordFields, annotationsAggregations }) => {
		return (
			<FilterComponent
				type={type}
				filterName={filterName}
				fields={fields}
				isEnabled={isEnabled}
				onFilter={onFilter}
				options={options}
				optionField={optionField}
				useKeywordFields = {useKeywordFields}
				aggregationFields={aggregationFields}
				tableState={tableState}
				annotationsAggregations={annotationsAggregations}
				endpoint={endpoint}
			/>
		);
	};

	useEffect(() => {
		const filteredColumns = filterColumns(columns, tableState.selectedColumnNames);
		const orderedColumns = orderColumns(filteredColumns, tableState.selectedColumnNames);
		setColumnList(
			orderedColumns.map((col) => {
				if(col){
					return <Column
						style={{'minWidth':`${columnWidths[col.field]}vw`, 'maxWidth': `${columnWidths[col.field]}vw`}}
						headerClassName='surface-0'
						columnKey={col.field}
						key={col.field}
						field={col.field}
						header={col.header}
						body={col.body}
						sortable={isEnabled}
						filter
						editor={col.editor}
						showFilterMenu={false}
						filterElement={() => filterComponentTemplate(col.filterElement)}
					/>;
				}
			})
		);
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [tableState, isEnabled, columnWidths]);

	const rowEditorFilterNameHeader = (options) => {
		return <div className="p-column-header-content"><span className="p-column-title">Filters</span></div>
	}

	return (
			<div className="card">
				<DataTable dataKey='id' value={entity} header={header}	ref={dataTable}
					filterDisplay="row" scrollHeight="62vh" scrollable= {true} tableClassName='p-datatable-md'
					editMode= "row" onRowEditInit= {onRowEditInit} onRowEditCancel= {onRowEditCancel}
					onRowEditSave= {onRowEditSave} editingRows={editingRows} onRowEditChange={onRowEditChange}
					sortMode="multiple" removableSort={true} onSort={onSort} multiSortMeta={tableState.multiSortMeta}
					onColReorder={colReorderHandler} reorderableColumns= {true}
					resizableColumns= {true} columnResizeMode="expand" showGridlines= {true} onColumnResizeEnd={handleColumnResizeEnd}
					paginator= {true} totalRecords={totalRecords} onPage={onLazyLoad} lazy= {true} first={tableState.first}
					paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
					currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
					rows={tableState.rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]} >
					{props.isEditable &&
						<Column field='rowEditor' rowEditor style={{maxWidth: '7rem', minWidth: '7rem'}} filter filterElement={rowEditorFilterNameHeader} showFilterMenu={false}
							headerStyle={{ width: '7rem', position: 'sticky' }} bodyStyle={{ textAlign: 'center' }} frozen headerClassName='surface-0'/>
					}
					{columnList}
				</DataTable>
			</div>
	)
}

