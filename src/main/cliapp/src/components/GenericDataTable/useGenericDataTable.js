import { useRef, useState, useEffect } from 'react';
import { useQuery } from 'react-query';
import { SearchService } from '../../service/SearchService';

import { trimWhitespace, returnSorted, reorderArray, validateBioEntityFields, setDefaultColumnOrder } from '../../utils/utils';
import { useGetUserSettings } from "../../service/useGetUserSettings";
import { getDefaultTableState, getModTableState } from '../../service/TableStateService';


export const useGenericDataTable = ({
	endpoint,
	tableName,
	columns,
	defaultColumnNames,
	initialTableState,
 	curieFields,
	idFields,
	sortMapping,
	nonNullFieldsTable,
	mutation,
	setIsEnabled,
	toasts,
	initialColumnWidth,
	errorObject,
	newEntity,
	deletionEnabled,
	deletionMethod,
}) => {


	const { settings: tableState, mutate: setTableState } = useGetUserSettings(initialTableState.tableSettingsKeyName, initialTableState);

	const [entities, setEntities] = useState(null);
	const [totalRecords, setTotalRecords] = useState(0);
	const [originalRows, setOriginalRows] = useState([]);
	const [columnList, setColumnList] = useState([]);
	const [editingRows, setEditingRows] = useState({});
	const [columnWidths, setColumnWidths] = useState(() => {
		const width = initialColumnWidth;

		const widthsObject = {};

		columns.forEach((col) => {
			widthsObject[col.field] = width;
		});

		return widthsObject;
	});

	const searchService = new SearchService();

	const { errorMessages, setErrorMessages, uiErrorMessages, setUiErrorMessages } = errorObject;
	const closeRowRef = useRef([]);
	const areUiErrors = useRef(false);

	const dataTable = useRef(null);

	const { toast_topleft, toast_topright } = toasts;
	const [exceptionDialog, setExceptionDialog] = useState(false);
	const [exceptionMessage,setExceptionMessage] = useState("");
	
	useQuery([tableState.tableKeyName, tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters],
		() => searchService.search(endpoint, tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters, sortMapping, [], nonNullFieldsTable), 
		{
			onSuccess: (data) => {
				setIsEnabled(true);
				setEntities(data.results);
				setTotalRecords(data.totalResults);
			},
			onError: (error) => {
				toast_topleft.current.show([
					{ severity: 'error', summary: 'Error', detail: error.message, sticky: true }
				])
			},
			keepPreviousData: true,
			refetchOnWindowFocus: false,
			enabled: !!(tableState.rows)
		}
	);

	useEffect(() => {
		if (
			(tableState.filters && Object.keys(tableState.filters).length > 0)
			|| (tableState.multiSortMeta && tableState.multiSortMeta.length > 0)
			|| tableState.page > 0
			|| !newEntity
		) return;

		//adds new entity to the top of the table when there are no filters or sorting
		setEntities((previousEntities) => {
			const newEntities = global.structuredClone(previousEntities);
			newEntities.unshift(newEntity);
			return newEntities;
		})
	// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [newEntity])

	const onLazyLoad = (event) => {
		let _tableState = {
			...tableState,

			rows: event.rows,
			page: event.page,
			first: event.first
		};

		setTableState(_tableState);
	}

	const onFilter = (filtersCopy) => {
		let _tableState = {
			...tableState,
			filters: { ...filtersCopy }
		}
		setTableState(_tableState);
	};

	const onSort = (event) => {
		let _tableState = {
			...tableState,
			multiSortMeta: returnSorted(event, tableState.multiSortMeta || [])
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

	useEffect(() => {
		setDefaultColumnOrder(columns, dataTable, defaultColumnNames, deletionEnabled, tableState);
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	const onRowEditInit = (event) => {
		setIsEnabled(false);
		let _originalRows = global.structuredClone(originalRows);
		_originalRows[event.index] = global.structuredClone(entities[event.index]);
		setOriginalRows(_originalRows);
	};

	const onRowEditCancel = (event) => {
		const rowsInEdit = Object.keys(editingRows).length - 1;
		if (rowsInEdit === 0) {
			setIsEnabled(true);
		}

		closeRowRef.current[event.index] = true;

		let _entities = [...entities];
		_entities[event.index] = originalRows[event.index];
		delete originalRows[event.index];
		setOriginalRows(originalRows);
		setEntities(_entities);

		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages({ ...errorMessagesCopy });

		if (uiErrorMessages){
			const uiErrorMessagesCopy = uiErrorMessages;
			uiErrorMessagesCopy[event.index] = {};
			setUiErrorMessages({ ...uiErrorMessagesCopy });
		}

	};

	//Todo: at some point it may make sense to refactor this function into a set of smaller utility functions and pass them down from the calling components
	const onRowEditSave = (event) => {
		areUiErrors.current = false;
		closeRowRef.current[event.index] = true;
		const rowsInEdit = Object.keys(editingRows).length - 1;
		if (rowsInEdit === 0) {
			setIsEnabled(true);
		}

		let updatedRow = global.structuredClone(event.data);//deep copy

		if(tableName === "Disease Annotations"){
			validateBioEntityFields(updatedRow, setUiErrorMessages, event, setIsEnabled, closeRowRef, areUiErrors);
		}

		if (areUiErrors.current) {
			closeRowRef.current[event.index] = false;
			return;
		}

		if(curieFields){
			curieFields.forEach((field) => {
				if (event.data[field] && Object.keys(event.data[field]).length >= 1) {
					const curie = trimWhitespace(event.data[field].curie);
					updatedRow[field] = {};
					updatedRow[field].curie = curie;
				}
			});
		}

		if(idFields){
			idFields.forEach((field) => {
				if (event.data[field] && Object.keys(event.data[field]).length >= 1) {
					const id = event.data[field].id;
					updatedRow[field] = {};
					updatedRow[field].id = id;
				}
			})
		}

		mutation.mutate(updatedRow, {
			onSuccess: (response, variables, context) => {
				toast_topright.current.show({ severity: 'success', summary: 'Successful', detail: 'Row Updated' });

				let _entities = global.structuredClone(entities);
				_entities[event.index] = response.data.entity;
				setEntities(_entities);
				const errorMessagesCopy = global.structuredClone(errorMessages);
				errorMessagesCopy[event.index] = {};
				setErrorMessages({ ...errorMessagesCopy });
			},
			onError: (error, variables, context) => {
				setIsEnabled(false);
				let errorMessage = "";
				if(error.response.data.errorMessage !== undefined) {
					errorMessage = error.response.data.errorMessage;
					toast_topright.current.show([
						{ life: 7000, severity: 'error', summary: 'Update error: ', detail: errorMessage, sticky: false }
					]);
				}
				else if(error.response.data !== undefined) {
					setExceptionMessage(error.response.data);
					setExceptionDialog(true);
				}

				let _entities = global.structuredClone(entities);

				const errorMessagesCopy = global.structuredClone(errorMessages);
				errorMessagesCopy[event.index] = {};
				if(error.response.data.errorMessages !== undefined) {
					Object.keys(error.response.data.errorMessages).forEach((field) => {
						let messageObject = {
							severity: "error",
							message: error.response.data.errorMessages[field]
						};
						errorMessagesCopy[event.index][field] = messageObject;
					});
					setErrorMessages({...errorMessagesCopy});
				}

				setEntities(_entities);
				let key = _entities[event.index].id ? _entities[event.index].id : _entities[event.index].curie;
				let _editingRows = { ...editingRows, ...{ [`${key}`]: true } };
				setEditingRows(_editingRows);
			},
		});
	};

	const handleDeletion = async (idToDelete, entityToDelete, deprecation) => {
		const result = await deletionMethod(entityToDelete)
		if (result.isError) {
			let action = deprecation ? 'deprecate' : 'delete';
			toast_topright.current.show([
				{ life: 7000, severity: 'error', summary: 'Could not ' + action + ' ' + endpoint +
					' [' + idToDelete + ']', sticky: false }
			]);
			let deletionErrorMessage = result?.message ? result.message : null;
			return deletionErrorMessage;
		} else {
			let action = deprecation ? 'Deprecation' : 'Deletion';
			toast_topright.current.show([
				{ life: 7000, severity: 'success', summary: action +' successful: ',
					detail: action + ' of ' + endpoint + ' [' + idToDelete + '] was successful', sticky: false }
			]);
			let _entities = global.structuredClone(entities);

			if (editingRows[idToDelete]) {
				let _editingRows = { ...editingRows};
				delete _editingRows[idToDelete];
				setEditingRows(_editingRows);

				const rowsInEdit = Object.keys(editingRows).length;
				if (rowsInEdit === 0) {
					setIsEnabled(true);
				};
			}

			setEntities(_entities);
			let _tableState = {
				...tableState,
				rows: tableState.rows - 1
			}

			setTableState(_tableState);
			setTotalRecords(totalRecords - 1);
			return null;
		}
	}

	const onRowEditChange = (event) => {
		//keep the row in edit mode if there are UI validation errors
		if(closeRowRef.current[event.index] === false){
			return;
		}
		setEditingRows(event.data);
	};

	const resetToModDefault = () => {
		const initialTableState = getModTableState(tableState.tableKeyName);

		setDefaultColumnOrder(columns, dataTable, initialTableState.selectedColumnNames, deletionEnabled, initialTableState);
		setTableState(initialTableState);
		const _columnWidths = {...columnWidths};

		Object.keys(_columnWidths).map((key) => {
			return _columnWidths[key] = initialColumnWidth;
		});

		setColumnWidths(_columnWidths);
		dataTable.current.resetScroll();
	}

	const resetTableState = () => {
		let defaultTableState = getDefaultTableState(tableState.tableKeyName, defaultColumnNames);
		
		setDefaultColumnOrder(columns, dataTable, defaultColumnNames, deletionEnabled, defaultTableState);
		setTableState(defaultTableState);
		const _columnWidths = {...columnWidths};

		Object.keys(_columnWidths).map((key) => {
			return _columnWidths[key] = initialColumnWidth;
		});

		setColumnWidths(_columnWidths);
		dataTable.current.resetScroll();
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

	return {
		setSelectedColumnNames,
		defaultColumnNames,
		tableState,
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
		exceptionDialog,
		resetToModDefault,
		resetTableState,
		setExceptionDialog,
		exceptionMessage,
	};
};
