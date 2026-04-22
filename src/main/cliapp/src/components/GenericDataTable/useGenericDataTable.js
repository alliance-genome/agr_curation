import { useRef, useState } from 'react';

import { trimWhitespace, returnSorted, validateBioEntityFields, restoreTableState } from '../../utils/utils';
import { getDefaultTableState, getModTableState } from '../../service/TableStateService';

export const useGenericDataTable = ({
	entities,
	setEntities,
	endpoint,
	tableState,
	setTableState,
	tableName,
	curieFields,
	idFields,
	mutation,
	setIsInEditMode,
	toasts,
	errorObject,
	deletionMethod,
	setTotalRecords,
	totalRecords,
	columns,
	defaultColumnWidth,
	isEditable,
	deletionEnabled,
	duplicationEnabled,
	hasDetails,
	defaultFilters,
}) => {
	const [originalRows, setOriginalRows] = useState([]);
	const [editingRows, setEditingRows] = useState({});

	const { errorMessages, setErrorMessages, uiErrorMessages, setUiErrorMessages } = errorObject;
	const closeRowRef = useRef([]);
	const areUiErrors = useRef(false);

	const dataTable = useRef(null);

	const { toast_topleft, toast_topright } = toasts;
	const [exceptionDialog, setExceptionDialog] = useState(false);
	const [exceptionMessage, setExceptionMessage] = useState('');

	const onLazyLoad = (event) => {
		let _tableState = {
			...tableState,

			rows: event.rows,
			page: event.page,
			first: event.first,
		};

		setTableState(_tableState);
	};

	const onFilter = (filtersCopy) => {
		let _tableState = {
			...tableState,
			filters: { ...filtersCopy },
		};
		setTableState(_tableState);
	};

	const onSort = (event) => {
		let _tableState = {
			...tableState,
			multiSortMeta: returnSorted(event, tableState.multiSortMeta || []),
		};
		setTableState(_tableState);
	};

	const setSelectedColumnNames = (newValue) => {
		let _tableState = {
			...tableState,
			selectedColumnNames: newValue,
		};
		setTableState(_tableState);
	};

	const setOrderedColumnNames = (newValue) => {
		let _tableState = {
			...tableState,
			orderedColumnNames: newValue,
		};
		setTableState(_tableState);
	};

	const setColumnWidths = (newValue) => {
		let _tableState = {
			...tableState,
			columnWidths: newValue,
		};
		setTableState(_tableState);
	};

	const onRowEditInit = (event) => {
		setIsInEditMode(true);
		const index = event.index % tableState.rows;
		let _originalRows = structuredClone(originalRows);
		_originalRows[index] = structuredClone(entities[index]);
		setOriginalRows(_originalRows);
	};

	const onRowEditCancel = (event) => {
		const index = event.index % tableState.rows;
		const rowsInEdit = Object.keys(editingRows).length - 1;
		if (rowsInEdit === 0) {
			setIsInEditMode(false);
		}

		closeRowRef.current[index] = true;

		let _entities = [...entities];
		_entities[index] = originalRows[index];
		delete originalRows[index];
		setOriginalRows(originalRows);
		setEntities(_entities);

		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[index] = {};
		setErrorMessages({ ...errorMessagesCopy });

		if (uiErrorMessages) {
			const uiErrorMessagesCopy = uiErrorMessages;
			uiErrorMessagesCopy[index] = {};
			setUiErrorMessages({ ...uiErrorMessagesCopy });
		}
	};

	//TODO: at some point it may make sense to refactor this function into a set of smaller utility functions and pass them down from the calling components
	const onRowEditSave = (event) => {
		const index = event.index % tableState.rows;
		areUiErrors.current = false;
		closeRowRef.current[index] = true;
		const rowsInEdit = Object.keys(editingRows).length - 1;
		if (rowsInEdit === 0) {
			setIsInEditMode(false);
		}

		// optimisticRow keeps the full nested data (needed for rendering display fields
		// like .name); updatedRow is a shallow copy stripped to {id}/{curie} for the server.
		const optimisticRow = structuredClone(event.newData ?? event.data);
		let updatedRow = { ...optimisticRow };

		if (tableName === 'Disease Annotations') {
			validateBioEntityFields(updatedRow, setUiErrorMessages, event, setIsInEditMode, closeRowRef, areUiErrors);
		}

		if (areUiErrors.current) {
			closeRowRef.current[index] = false;
			return;
		}

		if (curieFields) {
			curieFields.forEach((field) => {
				if (optimisticRow[field] && Object.keys(optimisticRow[field]).length >= 1) {
					updatedRow[field] = { curie: trimWhitespace(optimisticRow[field].curie) };
				}
			});
		}

		if (idFields) {
			idFields.forEach((field) => {
				if (optimisticRow[field] && Object.keys(optimisticRow[field]).length >= 1) {
					updatedRow[field] = { id: optimisticRow[field].id };
				}
			});
		}

		const rowKey = optimisticRow.id ?? optimisticRow.curie;
		setEntities((previousEntities) => {
			const nextEntities = [...previousEntities];
			nextEntities[index] = optimisticRow;
			return nextEntities;
		});

		// Replace the row at its current position by id/curie — guards against pagination,
		// sorting, or parallel edits changing which row lives at `index` between save and response.
		const replaceRowByKey = (newRow) => {
			setEntities((previousEntities) => {
				const liveIndex = previousEntities.findIndex(
					(candidateRow) => (candidateRow?.id ?? candidateRow?.curie) === rowKey
				);
				if (liveIndex === -1) return previousEntities;
				const nextEntities = [...previousEntities];
				nextEntities[liveIndex] = newRow;
				return nextEntities;
			});
		};

		mutation.mutate(updatedRow, {
			onSuccess: (response, variables, context) => {
				toast_topright.current.show({ severity: 'success', summary: 'Successful', detail: 'Row Updated' });

				// Reconcile with server entity (fills in computed fields like dbDateUpdated).
				// If the server didn't return an entity, keep the optimistic row as-is.
				if (response?.data?.entity) {
					replaceRowByKey(response.data.entity);
				}
				setErrorMessages((previousErrorMessages) => {
					const nextErrorMessages = structuredClone(previousErrorMessages);
					nextErrorMessages[index] = {};
					return nextErrorMessages;
				});
			},
			onError: (error, variables, context) => {
				setIsInEditMode(true);
				let errorMessage = '';
				if (error.response.data.errorMessage !== undefined) {
					errorMessage = error.response.data.errorMessage;
					toast_topright.current.show([
						{ life: 7000, severity: 'error', summary: 'Update error: ', detail: errorMessage, sticky: false },
					]);
					if (error.response.data.errorMessages && Object.keys(error.response.data.errorMessages).length > 0) {
						let messages = [];
						for (let errorField in error.response.data.errorMessages) {
							messages.push(errorField + ': ' + error.response.data.errorMessages[errorField]);
						}
						let messageSummary = messages.join(' / ');
						toast_topleft.current.show([
							{ life: 7000, severity: 'error', summary: 'Errors found: ', detail: messageSummary, sticky: false },
						]);
					}
				} else if (error.response.data !== undefined) {
					setExceptionMessage(error.response.data);
					setExceptionDialog(true);
				}

				// entities already holds the full optimistic row from the update above; leave as-is.
				// Always clear this row's errorMessages bucket first so a previous attempt's
				// errors don't linger, then populate with any new ones from the server.
				setErrorMessages((previousErrorMessages) => {
					const nextErrorMessages = structuredClone(previousErrorMessages);
					nextErrorMessages[index] = {};
					if (error.response.data.errorMessages !== undefined) {
						Object.keys(error.response.data.errorMessages).forEach((field) => {
							nextErrorMessages[index][field] = {
								severity: 'error',
								message: error.response.data.errorMessages[field],
							};
						});
					}
					return nextErrorMessages;
				});

				setEditingRows({ ...editingRows, [`${rowKey}`]: true });
			},
		});
	};

	const handleDeletion = async (idToDelete, entityToDelete) => {
		let result = await deletionMethod(entityToDelete);
		if (result.isError) {
			toast_topright.current.show([
				{
					life: 7000,
					severity: 'error',
					summary: 'Could not delete ' + endpoint + ' [' + idToDelete + ']',
					sticky: false,
				},
			]);
			let deletionErrorMessage = result?.message ? result.message : null;
			return deletionErrorMessage;
		} else {
			toast_topright.current.show([
				{
					life: 7000,
					severity: 'success',
					summary: 'Deletion successful: ',
					detail: 'Deletion of ' + endpoint + ' [' + idToDelete + '] was successful',
					sticky: false,
				},
			]);
			let _entities = structuredClone(entities);
			if (editingRows[idToDelete]) {
				let _editingRows = { ...editingRows };
				delete _editingRows[idToDelete];
				setEditingRows(_editingRows);

				const rowsInEdit = Object.keys(editingRows).length;
				if (rowsInEdit === 0) {
					setIsInEditMode(false);
				}
			}

			setEntities(_entities);
			let _tableState = {
				...tableState,
				rows: tableState.rows - 1,
			};

			setTableState(_tableState);
			setTotalRecords(totalRecords - 1);
			return null;
		}
	};
	const handleDeprecation = (entityToDeprecate) => {
		areUiErrors.current = false;
		let updatedRow = structuredClone(entityToDeprecate); //deep copy
		updatedRow.obsolete = true;

		let deprecatedIndex = entities
			.map(function (e) {
				return e.id;
			})
			.indexOf(updatedRow.id);

		mutation.mutate(updatedRow, {
			onSuccess: (response, variables, context) => {
				toast_topright.current.show({ severity: 'success', summary: 'Successful', detail: 'Row Deprecated' });

				let _entities = structuredClone(entities);
				_entities[deprecatedIndex] = response.data.entity;
				setEntities(_entities);
				const errorMessagesCopy = structuredClone(errorMessages);
				errorMessagesCopy[deprecatedIndex] = {};
				setErrorMessages({ ...errorMessagesCopy });
			},
			onError: (error, variables, context) => {
				setIsInEditMode(true);
				let errorMessage = '';
				if (error.response.data.errorMessage !== undefined) {
					errorMessage = error.response.data.errorMessage;
					toast_topright.current.show([
						{ life: 7000, severity: 'error', summary: 'Update error: ', detail: errorMessage, sticky: false },
					]);
				} else if (error.response.data !== undefined) {
					setExceptionMessage(error.response.data);
					setExceptionDialog(true);
				}

				let _entities = structuredClone(entities);

				const errorMessagesCopy = structuredClone(errorMessages);
				errorMessagesCopy[deprecatedIndex] = {};
				if (error.response.data.errorMessages !== undefined) {
					Object.keys(error.response.data.errorMessages).forEach((field) => {
						let messageObject = {
							severity: 'error',
							message: error.response.data.errorMessages[field],
						};
						errorMessagesCopy[deprecatedIndex][field] = messageObject;
					});
					setErrorMessages({ ...errorMessagesCopy });
				}
				setEntities(_entities);
			},
		});
	};

	const onRowEditChange = (event) => {
		const index = event.index % tableState.rows;

		//keep the row in edit mode if there are UI validation errors
		if (closeRowRef.current[index] === false) {
			return;
		}
		setEditingRows(event.data);
	};

	const setToModDefault = () => {
		const modTableState = getModTableState(
			tableState.tableKeyName,
			tableState.defaultColumnWidths,
			tableState.defaultColumnNames
		);
		restoreTableState(
			columns,
			dataTable,
			modTableState.orderedColumnNames,
			isEditable,
			deletionEnabled,
			duplicationEnabled,
			hasDetails,
			modTableState
		);
		dataTable.current.resetScroll();
		setTableState(modTableState);
	};

	const resetTableState = () => {
		let defaultTableState = getDefaultTableState(tableState.tableKeyName, columns, defaultColumnWidth, defaultFilters);
		restoreTableState(
			columns,
			dataTable,
			defaultTableState.orderedColumnNames,
			isEditable,
			deletionEnabled,
			duplicationEnabled,
			hasDetails,
			defaultTableState
		);
		dataTable.current.resetScroll();
		setTableState(defaultTableState);
	};

	const colReorderHandler = (event) => {
		const columnNames = event.columns
			.filter((column) => {
				return (
					column.props.field !== 'rowEditor' &&
					column.props.field !== 'delete' &&
					column.props.field !== 'duplicate' &&
					column.props.field !== 'details'
				);
			})
			.map((column) => column.props.header);

		for (let i = 0; i < tableState.orderedColumnNames.length; i++) {
			if (!columnNames.includes(tableState.orderedColumnNames[i])) {
				columnNames.splice(i, 0, tableState.orderedColumnNames[i]);
			}
		}
		setOrderedColumnNames(columnNames);
	};

	const handleColumnResizeEnd = (event) => {
		const currentWidth = event.element.clientWidth;
		const delta = event.delta;
		const newWidth = Math.floor(((currentWidth + delta) / window.innerWidth) * 100);
		const key = event.column.props.columnKey || event.column.props.field;

		const _columnWidths = { ...tableState.columnWidths };

		_columnWidths[key] = newWidth;
		setColumnWidths(_columnWidths);
	};

	return {
		setSelectedColumnNames,
		setOrderedColumnNames,
		tableState,
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
		exceptionDialog,
		setToModDefault,
		resetTableState,
		setExceptionDialog,
		exceptionMessage,
	};
};
