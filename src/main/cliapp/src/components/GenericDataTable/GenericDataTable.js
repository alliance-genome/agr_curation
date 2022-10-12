import React, { useRef, useEffect, useState } from 'react';

import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { MultiSelect } from 'primereact/multiselect';

import { FilterComponent } from './FilterComponent'
import { DataTableHeaderFooterTemplate } from "../DataTableHeaderFooterTemplate";


import {filterColumns, orderColumns} from '../../utils/utils';
import { useGenericDataTable } from "./useGenericDataTable";

export const GenericDataTable = (props) => {

	const { tableName, isEnabled, aggregationFields, endpoint, columns, headerButtons, deletionEnabled } = props;

	const {
		setSelectedColumnNames,
		tableStateConfirm,
		onFilter,
		setColumnList,
		columnWidths,
		entities,
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
		handleDeletion,
		tableState,
		defaultColumnNames,
	} = useGenericDataTable(props);

	const toast_topright = useRef(null);
	const [deleteDialog, setDeleteDialog] = useState(false);
	const [errorDialog, setErrorDialog] = useState(false);
	const [idToDelete, setIdToDelete] = useState(null);
	const [entityToDelete, setEntityToDelete] = useState(null);
	const [deletionErrorMessage, setDeletionErrorMessage] = useState(null);

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
				onclickEvent = {tableStateConfirm}
				isEnabled = {isEnabled}
		/>
	);

	const filterComponentTemplate = ({ type, filterName, fields, nonNullFields, nullFields, options, optionField, useKeywordFields, annotationsAggregations }) => {
		return (
			<FilterComponent
				type={type}
				filterName={filterName}
				fields={fields}
				nonNullFields={nonNullFields}
				nullFields={nullFields}
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
				} else {
					return null;
				}

			})
		);
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [tableState, isEnabled, columnWidths]);

	const rowEditorFilterNameHeader = (options) => {
		return <div className="p-column-header-content"><span className="p-column-title">Filters</span></div>
	}

	const showDeleteDialog = (props) => {
		let _idToDelete = props.rowData ? props.rowData.id : props.id;
		setIdToDelete(_idToDelete);
		setEntityToDelete(props);
		setDeleteDialog(true);
	}

	const deleteRow = async (idToDelete, entityToDelete) => {
		setDeleteDialog(false);
		let _deletionErrorMessage = await handleDeletion(idToDelete, entityToDelete);
		setDeletionErrorMessage(_deletionErrorMessage);
		if (_deletionErrorMessage !== null) {
			setErrorDialog(true);
		}
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={() => showDeleteDialog(props)}/>
		);
	}

	const hideDeleteDialog = () => {
		setDeleteDialog(false);
	};

	const hideErrorDialog = () => {
		setErrorDialog(false);
	};

	const deleteDialogFooter = () => {
		return (
        	<React.Fragment>
            	<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDeleteDialog} />
            	<Button label="Confirm" icon="pi pi-check" className="p-button-text" onClick={() => deleteRow(idToDelete, entityToDelete)} />
        	</React.Fragment>
    	);
	}

	const errorDialogFooter = () => {
		return (
        	<React.Fragment>
            	<Button label="OK" icon="pi pi-times" className="p-button-text" onClick={hideErrorDialog} />
            </React.Fragment>
    	);
	}
	return (
			<div className="card">
				<Toast ref={toast_topright} position="top-right" />
				<DataTable dataKey='id' value={entities} header={header} ref={dataTable}
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
					{deletionEnabled &&
						<Column field="delete" editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} filterElement={rowEditorFilterNameHeader}
						showFilterMenu={false} style={{maxWidth: '4rem', minWidth: '4rem', display: props.isEditable ? 'visible' : 'none' }} headerStyle={{ width: '4rem', position: 'sticky' }} bodyStyle={{textAlign: 'center'}}
						frozen headerClassName='surface-0'/>
					}
					{columnList}
				</DataTable>

			 <Dialog visible={deleteDialog} style={{ width: '450px' }} header="Confirm Deletion" modal footer={deleteDialogFooter} onHide={hideDeleteDialog}>
                <div className="confirmation-content">
                    <i className="pi pi-exclamation-triangle mr-3" style={{ fontSize: '2rem'}} />
                    {<span>Warning: You are about to delete this data object from the database. This cannot be undone. Please confirm deletion or cancel.</span>}
                </div>
            </Dialog>
			<Dialog visible={errorDialog} style={{ width: '450px' }} header="Deletion Error" modal footer={errorDialogFooter} onHide={hideErrorDialog}>
                <div className="error-message-dialog">
                    <i className="pi pi-ban mr-3" style={{ fontSize: '2rem'}} />
                    {<span>ERROR: The data object you are trying to delete is in use by other data objects. Remove data connections to all other data objects and try to delete again.</span>}
                </div>
				<hr/>
				<div className="error-message-detail">
					{<span style={{fontSize: '0.85rem'}}>{deletionErrorMessage}</span>}
				</div>
            </Dialog>
			</div>
	)
}

