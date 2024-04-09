import { useRef, useState, useEffect } from 'react';

import { trimWhitespace, returnSorted, validateBioEntityFields } from '../../utils/utils';
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
	newEntity,
	deletionMethod,
	setTotalRecords,
	totalRecords,
	columns,
	defaultColumnWidth
}) => {

	const [originalRows, setOriginalRows] = useState([]);
	const [columnList, setColumnList] = useState([]);
	const [editingRows, setEditingRows] = useState({});

	const { errorMessages, setErrorMessages, uiErrorMessages, setUiErrorMessages } = errorObject;
	const closeRowRef = useRef([]);
	const areUiErrors = useRef(false);

	const dataTable = useRef(null);

	const { toast_topleft, toast_topright } = toasts;
	const [exceptionDialog, setExceptionDialog] = useState(false);
	const [exceptionMessage,setExceptionMessage] = useState("");

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

	const setOrderedColumnNames = (newValue) => {
		let _tableState = {
			...tableState,
			orderedColumnNames: newValue
		};
		setTableState(_tableState);
	};

	const setColumnWidths= (newValue) => {
		let _tableState = {
			...tableState,
			columnWidths: newValue
		};
		setTableState(_tableState);
	};



	const onRowEditInit = (event) => {
		setIsInEditMode(true);
		const index = event.index % tableState.rows;
		let _originalRows = global.structuredClone(originalRows);
		_originalRows[index] = global.structuredClone(entities[index]);
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

		if (uiErrorMessages){
			const uiErrorMessagesCopy = uiErrorMessages;
			uiErrorMessagesCopy[index] = {};
			setUiErrorMessages({ ...uiErrorMessagesCopy });
		}
	};
	
	//Todo: at some point it may make sense to refactor this function into a set of smaller utility functions and pass them down from the calling components
	const onRowEditSave = (event) => {
		const index = event.index % tableState.rows;
		areUiErrors.current = false;
		closeRowRef.current[index] = true;
		const rowsInEdit = Object.keys(editingRows).length - 1;
		if (rowsInEdit === 0) {
			setIsInEditMode(false);
		}

		let updatedRow = global.structuredClone(event.data);//deep copy
		
		if(tableName === "Disease Annotations"){
			validateBioEntityFields(updatedRow, setUiErrorMessages, event, setIsInEditMode, closeRowRef, areUiErrors);
		}

		if (areUiErrors.current) {
			closeRowRef.current[index] = false;
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
				_entities[index] = response.data.entity;
				setEntities(_entities);
				const errorMessagesCopy = global.structuredClone(errorMessages);
				errorMessagesCopy[index] = {};
				setErrorMessages({ ...errorMessagesCopy });
			},
			onError: (error, variables, context) => {
				setIsInEditMode(true);
				let errorMessage = "";
				if(error.response.data.errorMessage !== undefined) {
					errorMessage = error.response.data.errorMessage;
					toast_topright.current.show([
						{ life: 7000, severity: 'error', summary: 'Update error: ', detail: errorMessage, sticky: false }
					]);
					if (error.response.data.errorMessages && Object.keys(error.response.data.errorMessages).length > 0) {
						let messages = [];
						for (let errorField in error.response.data.errorMessages) {
							messages.push(errorField + ": " + error.response.data.errorMessages[errorField]);
						}
						let messageSummary = messages.join(" / ");
						toast_topleft.current.show([
							{ life: 7000, severity: 'error', summary: 'Errors found: ', detail: messageSummary, sticky: false }
						]);
					}
				}
				else if(error.response.data !== undefined) {
					setExceptionMessage(error.response.data);
					setExceptionDialog(true);
				}

				let _entities = global.structuredClone(entities);

				const errorMessagesCopy = global.structuredClone(errorMessages);
				errorMessagesCopy[index] = {};
				if(error.response.data.errorMessages !== undefined) {
					Object.keys(error.response.data.errorMessages).forEach((field) => {
						let messageObject = {
							severity: "error",
							message: error.response.data.errorMessages[field]
						};
						errorMessagesCopy[index][field] = messageObject;
					});
					setErrorMessages({...errorMessagesCopy});
				}

				setEntities(_entities);
				let key = _entities[index].id ? _entities[index].id : _entities[index].curie;
				let _editingRows = { ...editingRows, ...{ [`${key}`]: true } };
				setEditingRows(_editingRows);
			},
		});
	};

	const handleDeletion = async (idToDelete, entityToDelete) => {
		let result = await deletionMethod(entityToDelete);
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
					setIsInEditMode(false);
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
	const handleDeprecation = (entityToDeprecate) => {
		areUiErrors.current = false;
		let updatedRow = global.structuredClone(entityToDeprecate);//deep copy
		updatedRow.obsolete = true;
		
		let deprecatedIndex = entities.map(function(e) {return e.id;}).indexOf(updatedRow.id);

		mutation.mutate(updatedRow, {
			onSuccess: (response, variables, context) => {
				toast_topright.current.show({ severity: 'success', summary: 'Successful', detail: 'Row Deprecated' });

				let _entities = global.structuredClone(entities);
				_entities[deprecatedIndex] = response.data.entity;
				setEntities(_entities);
				const errorMessagesCopy = global.structuredClone(errorMessages);
				errorMessagesCopy[deprecatedIndex] = {};
				setErrorMessages({ ...errorMessagesCopy });
			},
			onError: (error, variables, context) => {
				setIsInEditMode(true);
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
				errorMessagesCopy[deprecatedIndex] = {};
				if(error.response.data.errorMessages !== undefined) {
					Object.keys(error.response.data.errorMessages).forEach((field) => {
						let messageObject = {
							severity: "error",
							message: error.response.data.errorMessages[field]
						};
						errorMessagesCopy[deprecatedIndex][field] = messageObject;
					});
					setErrorMessages({...errorMessagesCopy});
				}
				setEntities(_entities);
			},
		});
	};


	const onRowEditChange = (event) => {
		const index = event.index % tableState.rows;

		//keep the row in edit mode if there are UI validation errors
		if(closeRowRef.current[index] === false){
			return;
		}
		setEditingRows(event.data);
	};

	const setToModDefault = () => {
		const modTableState = getModTableState(tableState.tableKeyName, tableState.defaultColumnWidths, tableState.defaultColumnNames);
		setTableState(modTableState);
		dataTable.current.resetScroll();
	}
	
	const resetTableState = () => {
		let defaultTableState = getDefaultTableState(tableState.tableKeyName, columns, defaultColumnWidth);
		
		setTableState(defaultTableState);
		dataTable.current.resetScroll();
	}

	const colReorderHandler = (event) => {
		const columnNames = event.columns.filter(column => column.props.field !== 'rowEditor' && column.props.field !== 'delete').map(column => column.props.header);

		for(let i = 0; i < tableState.orderedColumnNames.length; i++) {
			if(!columnNames.includes(tableState.orderedColumnNames[i])) {
				columnNames.splice(i, 0, tableState.orderedColumnNames[i]);
			}
		}
		setOrderedColumnNames(columnNames);
	};

	const handleColumnResizeEnd = (event) => {
		const currentWidth = event.element.clientWidth;
		const delta = event.delta;
		const newWidth = Math.floor(((currentWidth + delta) / window.innerWidth) * 100);
		const field = event.column.props.field;

		const _columnWidths = {...tableState.columnWidths};

		_columnWidths[field] = newWidth;
		setColumnWidths(_columnWidths);
	};

	return {
		setSelectedColumnNames,
		setOrderedColumnNames,
		tableState,
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
		exceptionDialog,
		setToModDefault,
		resetTableState,
		setExceptionDialog,
		exceptionMessage,
	};
};
