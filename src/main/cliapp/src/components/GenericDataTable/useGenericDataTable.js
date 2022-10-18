import { useRef, useState, useEffect } from 'react';
import { useQuery } from 'react-query';

import { SearchService } from '../../service/SearchService';

import { trimWhitespace, returnSorted, reorderArray, setDefaultColumnOrder, genericConfirmDialog } from '../../utils/utils';
import { useSetDefaultColumnOrder } from '../../utils/useSetDefaultColumnOrder';
import {useGetUserSettings} from "../../service/useGetUserSettings";

export const useGenericDataTable = ({
	endpoint,
	tableName,
	columns,
	aggregationFields,
	curieFields,
	idFields,
	sortMapping,
	nonNullFieldsTable,
	mutation,
	setIsEnabled,
	toasts,
	initialColumnWidth,
	errorObject,
	defaultVisibleColumns,
	newEntity,
	deletionEnabled,
	deletionMethod,
}) => {

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});

	let initialTableState = {
		page: 0,
		first: 0,
		rows: 50,
		multiSortMeta: [],
		selectedColumnNames: defaultVisibleColumns ? defaultVisibleColumns : defaultColumnNames,
		filters: {},
		isFirst: false,
		tableKeyName: tableName.replace(/\s+/g, ''), //remove whitespace from tableName
		tableSettingsKeyName: tableName.replace(/\s+/g,'') + "TableSettings"
	}

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

	const { errorMessages, setErrorMessages } = errorObject;

	const dataTable = useRef(null);

	const { toast_topleft, toast_topright } = toasts;

	useQuery([tableState.tableKeyName, tableState],
		() => searchService.search(endpoint, tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters, sortMapping, [], nonNullFieldsTable), {
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
		enabled: !!(tableState)
	});

	useEffect(() => {
		if (
			!tableState.filters
			|| Object.keys(tableState.filters).length > 0
			|| tableState.multiSortMeta.length > 0
			|| tableState.page > 0
			|| !newEntity
		) return;

		setEntities((previousEntities) => {
			const newEntities = previousEntities;
			newEntities.unshift(newEntity);
			return newEntities;
		})
	// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [newEntity])

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

	useSetDefaultColumnOrder(columns, dataTable, defaultColumnNames, setIsFirst, tableState.isFirst, deletionEnabled);

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
		};

		let _entities = [...entities];
		_entities[event.index] = originalRows[event.index];
		delete originalRows[event.index];
		setOriginalRows(originalRows);
		setEntities(_entities);
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages({ ...errorMessagesCopy });

	};

	const onRowEditSave = (event) => {
		const rowsInEdit = Object.keys(editingRows).length - 1;
		if (rowsInEdit === 0) {
			setIsEnabled(true);
		}

		let updatedRow = global.structuredClone(event.data);//deep copy

		if(curieFields){
			curieFields.forEach((field) => {
				if (event.data[field] && Object.keys(event.data[field]).length >= 1) {
					const curie = trimWhitespace(event.data[field].curie);
					updatedRow[field] = {};
					updatedRow[field].curie = curie;
				}
			});
		};

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
				toast_topright.current.show([
					{ life: 7000, severity: 'error', summary: 'Update error: ', detail: error.response.data.errorMessage, sticky: false }
				]);

				let _entities = global.structuredClone(entities);

				const errorMessagesCopy = global.structuredClone(errorMessages);

				errorMessagesCopy[event.index] = {};
				Object.keys(error.response.data.errorMessages).forEach((field) => {
					let messageObject = {
						severity: "error",
						message: error.response.data.errorMessages[field]
					};
					errorMessagesCopy[event.index][field] = messageObject;
				});

				setErrorMessages({ ...errorMessagesCopy });

				setEntities(_entities);
				let _editingRows = { ...editingRows, ...{ [`${_entities[event.index].id}`]: true } };
				setEditingRows(_editingRows);
			},
		});
	};

	const handleDeletion = async (idToDelete, entityToDelete) => {
		const result = await deletionMethod(entityToDelete)
		if (result.isError) {
			toast_topright.current.show([
				{ life: 7000, severity: 'error', summary: 'Could not delete ' + endpoint +
					' [' + idToDelete + ']', sticky: false }
			]);
			let deletionErrorMessage = result?.message ? result.message : null;
			return deletionErrorMessage;
		} else {
			toast_topright.current.show([
				{ life: 7000, severity: 'success', summary: 'Deletion successful: ',
					detail: 'Deletion of ' + endpoint + ' [' + idToDelete + '] was successful', sticky: false }
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
		setEditingRows(event.data);
	};

	const resetTableState = () => {
		let _tableState = {
			...initialTableState,
			isFirst: false,
		};

		setTableState(_tableState);
		setDefaultColumnOrder(columns, dataTable, defaultColumnNames, deletionEnabled);
		const _columnWidths = {...columnWidths};

		Object.keys(_columnWidths).map((key) => {
			return _columnWidths[key] = initialColumnWidth;
		});

		setColumnWidths(_columnWidths);
		dataTable.current.el.children[1].scrollLeft = 0;
	}

	const tableStateConfirm = () => {
		genericConfirmDialog({
			header: `${tableName} Table State Reset`,
			message: `Are you sure? This will reset the local state of the ${tableName} table.`,
			accept: resetTableState
		});
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
	};
};
