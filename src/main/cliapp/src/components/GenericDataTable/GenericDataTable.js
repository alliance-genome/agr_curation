import React, { useRef, useEffect, useState } from 'react';

import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { MultiSelect } from 'primereact/multiselect';
import { Checkbox } from 'primereact/checkbox';

import { FilterComponent } from '../Filters/FilterComponent'
import { DataTableHeaderFooterTemplate } from "../DataTableHeaderFooterTemplate";


import { filterColumns, orderColumns } from '../../utils/utils';
import { useGenericDataTable } from "./useGenericDataTable";

export const GenericDataTable = (props) => {

	const { 
		tableName, 
		isEnabled, 
		aggregationFields, 
		endpoint, 
		columns, 
		headerButtons, 
		deletionEnabled, 
		dataKey = 'id', 
		deprecateOption = false,
		modReset = false,
		highlightObsolete = true 
	} = props;

	const {
		setSelectedColumnNames,
		tableStateConfirm,
		onFilter,
		setColumnList,
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
		handleDeprecation,
		tableState,
		defaultColumnNames,
		exceptionDialog,
		setExceptionDialog,
		setToModDefault,
		resetTableState,
		exceptionMessage,
	} = useGenericDataTable(props);

	const toast_topright = useRef(null);
	const [deleteDialog, setDeleteDialog] = useState(false);
	const [deprecateDialog, setDeprecateDialog] = useState(false);
	const [errorDialog, setErrorDialog] = useState(false);
	const [idToDelete, setIdToDelete] = useState(null);
	const [entityToDelete, setEntityToDelete] = useState(null);
	const [deletionErrorMessage, setDeletionErrorMessage] = useState(null);
	const [allowDelete, setAllowDelete] = useState(false);

	const createMultiselectComponent = (tableState,defaultColumnNames,isEnabled) => {
		return (<MultiSelect
				aria-label='columnToggle'
				value={tableState.selectedColumnNames}
				options={defaultColumnNames}
				filter
				resetFilterOnHide
				onChange={e => {
					let orderedSelectedColumnNames = tableState.orderedColumnNames.filter((columnName) => {
						return e.value.some(selectedColumn => selectedColumn === columnName);
					});

					setSelectedColumnNames(orderedSelectedColumnNames)
				}
			}
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
				tableStateConfirm = {tableStateConfirm}
				setToModDefault = {setToModDefault}
				resetTableState = {resetTableState}
				isEnabled = {isEnabled}
				modReset={modReset}
		/>
	);

	const filterComponentTemplate = (config) => {
		return (
			<FilterComponent
				filterConfig={config}
				isEnabled={isEnabled}
				onFilter={onFilter}
				aggregationFields={aggregationFields}
				tableState={tableState}
				endpoint={endpoint}
			/>
		);
	};
	//This is needed so column order is tracked properly
	useEffect(() => dataTable.current.resetColumnOrder() );

	useEffect(() => {
		const orderedColumns = orderColumns(columns, tableState.orderedColumnNames);
		const filteredColumns = filterColumns(orderedColumns, tableState.selectedColumnNames);
		setColumnList(() => {
			return filteredColumns.map((col) => {
				if(col){
					return <Column
						style={{'minWidth':`${tableState.columnWidths[col.field]}vw`, 'maxWidth': `${tableState.columnWidths[col.field]}vw`}}
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
						filterElement={() => filterComponentTemplate(col.filterConfig)}
					/>;
				} else {
					return null;
				}
			})
		});
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [tableState, isEnabled]);

	const rowEditorFilterNameHeader = (options) => {
		return <div className="p-column-header-content"><span className="p-column-title">Filters</span></div>
	}

	const showDeleteOrDeprecateDialog = (props) => {
		let _idToDelete = props.rowData ? props.rowData[dataKey] : props[dataKey];
		let isPublic = true; // TODO: check field in props when populated
		setIdToDelete(_idToDelete);
		setEntityToDelete(props);
		if (deprecateOption && isPublic) {
			setDeprecateDialog(true);
		} else {
			setDeleteDialog(true);
		}
	}

	const deleteOrDeprecateRow = async (idToDelete, entityToDelete, deprecateOnly) => {
		setDeleteDialog(false);
		setDeprecateDialog(false);
		if (deprecateOnly) {
			handleDeprecation(entityToDelete);
		} else {
			let _deletionErrorMessage = await handleDeletion(idToDelete, entityToDelete);
			setDeletionErrorMessage(_deletionErrorMessage);
			if (_deletionErrorMessage !== null) {
				setErrorDialog(true);
			}
		}
		setAllowDelete(false);
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={() => showDeleteOrDeprecateDialog(props)}/>
		);
	}

	const hideDeprecateDialog = () => {
		setDeprecateDialog(false);
		setAllowDelete(false);
	} ;

	const hideDeleteDialog = () => {
		setDeleteDialog(false);
	};

	const hideErrorDialog = () => {
		setErrorDialog(false);
	};

	const hideExceptionDialog = () => {
		setExceptionDialog(false);
	};

	const deleteDialogFooter = () => {
		return (
					<React.Fragment>
							<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDeleteDialog} />
							<Button label="Confirm" icon="pi pi-check" className="p-button-text" onClick={() => deleteOrDeprecateRow(idToDelete, entityToDelete, false)} />
					</React.Fragment>
			);
	}

	const deprecateDialogFooter = () => {
		return (
					<React.Fragment>
							<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={hideDeprecateDialog} />
							<Button label="Deprecate" icon="pi pi-check" className="p-button-text" onClick={() => deleteOrDeprecateRow(idToDelete, entityToDelete, true)} />
							<Button label="Delete" icon="pi pi-check" className="p-button-text" onClick={() => deleteOrDeprecateRow(idToDelete, entityToDelete, false)} disabled={!allowDelete}/>
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

	const exceptionDialogFooter = () => {
		return (
			<React.Fragment>
				<Button label="OK" icon="pi pi-times" className="p-button-text" onClick={hideExceptionDialog} />
			</React.Fragment>
		);
	}

	const getRowClass = (props) => {
		if (props?.obsolete && highlightObsolete) {
			return 'bg-gray-500 text-white'
		};
		return null;
	}

	return (
			<div className="card">
				<Toast ref={toast_topright} position="top-right" />
				<DataTable dataKey={dataKey} value={entities} header={header} ref={dataTable}
					filterDisplay="row" scrollHeight="62vh" scrollable= {true} tableClassName='p-datatable-md'
					editMode= "row" onRowEditInit= {onRowEditInit} onRowEditCancel= {onRowEditCancel}
					onRowEditSave= {onRowEditSave} editingRows={editingRows} onRowEditChange={onRowEditChange}
					sortMode="multiple" removableSort={true} onSort={onSort} multiSortMeta={tableState.multiSortMeta}
					onColReorder={colReorderHandler} reorderableColumns= {true} 
					resizableColumns= {true} columnResizeMode="expand" showGridlines= {true} onColumnResizeEnd={handleColumnResizeEnd}
					paginator= {true} totalRecords={totalRecords} onPage={onLazyLoad} lazy= {true} first={tableState.first}
					paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
					currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
					rows={tableState.rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]} 
					rowClassName = {(props) => getRowClass(props)}>
					{props.isEditable &&
						<Column field='rowEditor' rowEditor style={{maxWidth: '7rem', minWidth: '7rem'}} filter filterElement={rowEditorFilterNameHeader} showFilterMenu={false}
							headerStyle={{ width: '7rem', position: 'sticky' }} bodyStyle={{ textAlign: 'center' }} frozen headerClassName='surface-0'
						/>
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

				<Dialog visible={deprecateDialog} style={{ width: '450px' }} header="Confirm Deletion" modal footer={deprecateDialogFooter} onHide={hideDeprecateDialog}>
					<div className="confirmation-content">
						<p><i className="pi pi-exclamation-triangle mr-3" style={{ fontSize: '2rem'}} />Warning: You are about to delete this data object from the database.  This cannot be undone.  Please confirm the following information or deprecate instead:</p><br/>
					</div>
					<div>
						<Checkbox onChange={e => setAllowDelete(!allowDelete)} checked={allowDelete}></Checkbox>
						<label>  This data object has not been made public OR this data object has been made public but fits criteria for deletion from the database</label>
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

				<Dialog visible={exceptionDialog} style={{ width: '550px' }} header="Exception" modal footer={exceptionDialogFooter} onHide={hideExceptionDialog}>
					<div className="error-message-dialog">
						<i className="pi pi-ban mr-3" style={{ fontSize: '2rem'}} />
						{<span>Exception Occurred!!</span>}
					</div>
					<hr/>
					<div className="error-message-detail">
						{<span style={{fontSize: '0.85rem'}}>{exceptionMessage}</span>}
					</div>
				</Dialog>
			</div>
	)
}


