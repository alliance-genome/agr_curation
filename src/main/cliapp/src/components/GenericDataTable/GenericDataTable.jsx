import React, { useRef, useMemo, useCallback } from 'react';

import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { MultiSelect } from 'primereact/multiselect';

import { FilterComponent } from '../Filters/FilterComponent';
import { DataTableHeaderTemplate } from '../DataTableHeaderFooterTemplate';
import { DuplicationAction } from '../Actions/DuplicationAction';
import { EntityDetailsAction } from '../Actions/EntityDetailsAction';

import { filterColumns, orderColumns, getIdentifier } from '../../utils/utils';
import { useGenericDataTable } from './useGenericDataTable';
import { useDeleteOrDeprecateDialogs } from '../DeleteOrDeprecateDialogs';

import './styles.scss';
import { DataTableFooter } from './DataTableFooter';
import { TableEditorProvider } from '../Editors/fields/TableEditorContext';
import { usePrimeRowEditStrategy } from '../Editors/fields/strategies/usePrimeRowEditStrategy';

export const GenericDataTable = (props) => {
	const {
		tableName,
		isInEditMode,
		aggregationFields,
		endpoint,
		columns,
		headerButtons,
		deletionEnabled,
		handleDuplication,
		duplicationEnabled,
		hasDetails = false,
		dataKey = 'id',
		deprecateOption = false,
		modReset = false,
		highlightObsolete = true,
		fetching,
		isEditable,
	} = props;

	const {
		setSelectedColumnNames,
		tableStateConfirm,
		onFilter,
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
		handleDeletion,
		handleDeprecation,
		tableState,
		exceptionDialog,
		setExceptionDialog,
		setToModDefault,
		resetTableState,
		exceptionMessage,
		errorMessages,
		uiErrorMessages,
	} = useGenericDataTable(props);

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const { openDeleteOrDeprecateDialog, deleteOrDeprecateDialogs } = useDeleteOrDeprecateDialogs({
		deprecateOption,
		onDelete: handleDeletion,
		onDeprecate: handleDeprecation,
	});
	const strategy = usePrimeRowEditStrategy({ errorMessages, uiErrorMessages });

	const createMultiselectComponent = () => {
		return (
			<MultiSelect
				aria-label="columnToggle"
				value={tableState.selectedColumnNames}
				options={tableState.defaultColumnNames}
				filter
				resetFilterOnHide
				onChange={(e) => {
					let orderedSelectedColumnNames = tableState.orderedColumnNames.filter((columnName) => {
						return e.value.some((selectedColumn) => selectedColumn === columnName);
					});

					setSelectedColumnNames(orderedSelectedColumnNames);
				}}
				className="w-20rem text-center"
				disabled={isInEditMode}
				maxSelectedLabels={4}
			/>
		);
	};

	const header = (
		<DataTableHeaderTemplate
			title={tableName + ' Table'}
			tableState={tableState}
			multiselectComponent={createMultiselectComponent()}
			buttons={headerButtons ? headerButtons(isInEditMode) : undefined}
			tableStateConfirm={tableStateConfirm}
			setToModDefault={setToModDefault}
			resetTableState={resetTableState}
			isInEditMode={isInEditMode}
			modReset={modReset}
		/>
	);

	const filterComponentTemplate = useCallback(
		(config) => {
			return (
				<FilterComponent
					filterConfig={config}
					isInEditMode={isInEditMode}
					onFilter={onFilter}
					aggregationFields={aggregationFields}
					tableState={tableState}
					endpoint={endpoint}
				/>
			);
		},
		[isInEditMode, onFilter, aggregationFields, tableState, endpoint]
	);

	const columnList = useMemo(() => {
		const orderedColumns = orderColumns(columns, tableState.orderedColumnNames);
		const filteredColumns = filterColumns(orderedColumns, tableState.selectedColumnNames);
		return filteredColumns.map((col) => {
			if (col) {
				const key = col.columnKey || col.field;
				return (
					<Column
						style={{
							minWidth: `${tableState.columnWidths[key]}vw`,
							maxWidth: `${tableState.columnWidths[key]}vw`,
							padding: '4px 10px 4px',
						}}
						headerClassName="surface-0"
						showClearButton={false}
						columnKey={key}
						key={key}
						field={col.field}
						sortField={col.sortField || col.columnKey}
						filterField={col.filterField || col.columnKey}
						header={col.header}
						body={col.body}
						sortable={col.sortable && !isInEditMode}
						filter
						editor={col.editor}
						showFilterMenu={false}
						filterElement={() => filterComponentTemplate(col.filterConfig)}
						headerStyle={{ padding: '1rem' }}
					/>
				);
			} else {
				return null;
			}
		});
	}, [
		columns,
		tableState.orderedColumnNames,
		tableState.selectedColumnNames,
		tableState.columnWidths,
		isInEditMode,
		filterComponentTemplate,
	]);

	const rowEditorFilterNameHeader = (options) => {
		return (
			<div>
				<span className="p-column-title text-center">Filters</span>
			</div>
		);
	};

	const showDeleteOrDeprecateDialog = (props) => {
		let _idToDelete = props.rowData ? props.rowData[dataKey] : props[dataKey];
		openDeleteOrDeprecateDialog(_idToDelete, props);
	};

	const deleteAction = (props, disabled) => {
		return (
			<Button
				icon="pi pi-trash"
				className="p-button-text p-0 text-base"
				disabled={disabled}
				onClick={() => showDeleteOrDeprecateDialog(props)}
			/>
		);
	};

	const hideExceptionDialog = () => {
		setExceptionDialog(false);
	};

	const exceptionDialogFooter = () => {
		return (
			<React.Fragment>
				<Button label="OK" icon="pi pi-times" className="p-button-text" onClick={hideExceptionDialog} />
			</React.Fragment>
		);
	};

	const getRowClass = (props) => {
		if (props?.obsolete && highlightObsolete) {
			return 'bg-gray-500 text-white';
		}
		return null;
	};

	return (
		<div className="card">
			<Toast ref={toast_topleft} position="top-left" />
			<Toast ref={toast_topright} position="top-right" />
			<TableEditorProvider strategy={strategy}>
				<DataTable
					dataKey={dataKey}
					value={entities}
					header={header}
					ref={dataTable}
					filterDisplay="row"
					scrollHeight="62vh"
					scrollable={true}
					tableClassName="p-datatable-md"
					editMode="row"
					onRowEditInit={onRowEditInit}
					onRowEditCancel={onRowEditCancel}
					onRowEditSave={onRowEditSave}
					editingRows={editingRows}
					onRowEditChange={onRowEditChange}
					sortMode="multiple"
					removableSort={true}
					onSort={onSort}
					multiSortMeta={tableState.multiSortMeta}
					onColReorder={colReorderHandler}
					reorderableColumns={true}
					resizableColumns={true}
					columnResizeMode="expand"
					showGridlines={true}
					onColumnResizeEnd={handleColumnResizeEnd}
					totalRecords={totalRecords}
					lazy={true}
					rowClassName={(props) => getRowClass(props)}
					loading={fetching}
					loadingIcon="pi pi-spin pi-spinner"
					footer={
						<DataTableFooter
							first={tableState.first}
							rows={tableState.rows}
							totalRecords={totalRecords}
							onLazyLoad={onLazyLoad}
							isInEditMode={isInEditMode}
						/>
					}
				>
					{isEditable && (
						<Column
							field="rowEditor"
							rowEditor
							className={`text-center row-editor-column p-0 text-base`}
							filter
							filterElement={rowEditorFilterNameHeader}
							showFilterMenu={false}
							bodyStyle={{ textAlign: 'center' }}
							frozen
							headerClassName={`surface-0 row-editor-column sticky`}
						/>
					)}
					{deletionEnabled && (
						<Column
							field="delete"
							editor={(props) => deleteAction(props, true)}
							body={(props) => deleteAction(props, isInEditMode)}
							filterElement={rowEditorFilterNameHeader}
							showFilterMenu={false}
							className={`text-center p-0 action-column ${isEditable ? 'visible' : 'hidden'}`}
							bodyStyle={{ textAlign: 'center' }}
							frozen
							headerClassName="surface-0 action-column sticky"
						/>
					)}
					{duplicationEnabled && (
						<Column
							field="duplicate"
							editor={(props) => (
								<DuplicationAction props={props} handleDuplication={handleDuplication} disabled={true} />
							)}
							body={(props) => (
								<DuplicationAction props={props} handleDuplication={handleDuplication} disabled={isInEditMode} />
							)}
							showFilterMenu={false}
							className={`text-center p-0 action-column ${isEditable ? 'visible' : 'hidden'}`}
							bodyStyle={{ textAlign: 'center' }}
							frozen
							headerClassName="surface-0 action-column sticky"
						/>
					)}
					{hasDetails && (
						<Column
							field="details"
							editor={(props) => (
								<EntityDetailsAction endpoint={endpoint} identifier={getIdentifier(props.rowData)} disabled={true} />
							)}
							body={(props) => (
								<EntityDetailsAction endpoint={endpoint} identifier={getIdentifier(props)} disabled={isInEditMode} />
							)}
							showFilterMenu={false}
							className="text-center p-0 action-column"
							bodyStyle={{ textAlign: 'center' }}
							frozen
							headerClassName="surface-0 action-column sticky"
						/>
					)}
					{columnList}
				</DataTable>
			</TableEditorProvider>

			{deleteOrDeprecateDialogs}

			<Dialog
				visible={exceptionDialog}
				className="w-34rem"
				header="Exception"
				modal
				footer={exceptionDialogFooter}
				onHide={hideExceptionDialog}
			>
				<div className="error-message-dialog">
					<i className="pi pi-ban mr-3 text-4xl" />
					{<span>Exception Occurred!!</span>}
				</div>
				<hr />
				<div className="error-message-detail">{<span className="text-sm">{exceptionMessage}</span>}</div>
			</Dialog>
		</div>
	);
};
