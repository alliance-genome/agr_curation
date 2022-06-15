import React, {useEffect, useRef, useState} from 'react';
import {DataTable} from 'primereact/datatable';
import {useSessionStorage} from '../../service/useSessionStorage';
import {useSetDefaultColumnOrder} from '../../utils/useSetDefaultColumnOrder';
import {Column} from 'primereact/column';
import {useMutation, useQuery} from 'react-query';
import {Toast} from 'primereact/toast';
import {SearchService} from '../../service/SearchService';
import {Messages} from 'primereact/messages';
import {MultiSelect} from 'primereact/multiselect';
import {filterColumns, orderColumns, reorderArray, returnSorted, trimWhitespace} from '../../utils/utils';
import {DataTableHeaderFooterTemplate} from "../../components/DataTableHeaderFooterTemplate";
import {FilterComponentInputText} from "../../components/FilterComponentInputText";
import {FilterMultiSelectComponent} from "../../components/FilterMultiSelectComponent";
import {ControlledVocabularyDropdown} from "../../components/ControlledVocabularySelector";
import {ErrorMessageComponent} from "../../components/ErrorMessageComponent";
import {useControlledVocabularyService} from "../../service/useControlledVocabularyService";
import {EllipsisTableCell} from "../../components/EllipsisTableCell";
import {ListTableCell} from "../../components/ListTableCell";
import {ConditionRelationService} from "../../service/ConditionRelationService";
import {AutocompleteEditor} from "../../components/AutocompleteEditor";
import {InputTextEditor} from "../../components/InputTextEditor";


export const ConditionRelationTable = () => {
	const defaultColumnNames = ["Handle", "Reference", "Relation", "Conditions"];

	let initialTableState = {
		page: 0,
		first: 0,
		rows: 50,
		multiSortMeta: [],
		selectedColumnNames: defaultColumnNames,
		filters: {},
		isFirst: true
	}

	const [tableState, setTableState] = useSessionStorage("ConRelTableSettings", initialTableState);

	let [conditionRelations, setConditionRelations] = useState(null);

	const [originalRows, setOriginalRows] = useState([]);
	const [totalRecords, setTotalRecords] = useState(0);
	const [editingRows, setEditingRows] = useState({});
	const [isEnabled, setIsEnabled] = useState(true);
	const [columnList, setColumnList] = useState([]);

	const searchService = new SearchService();
	const errorMessage = useRef(null);
	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const dataTable = useRef(null);
	const [errorMessages, setErrorMessages] = useState({});

	const rowsInEdit = useRef(0);

	let conditionRelationService = null;

	const conditionRelationTypeTerms = useControlledVocabularyService('Condition relation types');

	useQuery(['conditionRelations', tableState],
		() => searchService.search('condition-relation', tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters, null, null, ['handle', 'singleReference.curie']), {
			onSuccess: (data) => {
				setConditionRelations(data.results);
				setTotalRecords(data.totalResults);
			},
			onError: (error) => {
				toast_topleft.current.show([
					{life: 7000, severity: 'error', summary: 'Page error: ', detail: error.message, sticky: false}
				]);
			},
			onSettled: () => {
				setOriginalRows([]);
			},
			keepPreviousData: true,
			refetchOnWindowFocus: false
		});

	const mutation = useMutation(updatedAnnotation => {
		if (!conditionRelationService) {
			conditionRelationService = new ConditionRelationService();
		}
		return conditionRelationService.saveConditionRelation(updatedAnnotation);
	});

	const setIsFirst = (value) => {
		let _tableState = {
			...tableState,
			first: value,
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
			filters: {...filtersCopy}
		}
		setTableState(_tableState);
	};

	const onSort = (event) => {
		let _tableState = {
			...tableState,
			multiSortMeta: returnSorted(event, tableState.multiSortMeta)
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

	const aggregationFields = [
		'conditionRelationType.name'
	];

	const filterComponentTemplate = (filterName, fields) => {
		return (<FilterComponentInputText
			isEnabled={isEnabled}
			fields={fields}
			filterName={filterName}
			currentFilters={tableState.filters}
			onFilter={onFilter}
		/>);
	};

	const filterComponentInputTextTemplate = (filterName, fields) => {
		return (<FilterComponentInputText
			isEnabled={isEnabled}
			fields={fields}
			filterName={filterName}
			currentFilters={tableState.filters}
			onFilter={onFilter}
		/>);
	};

	const FilterMultiSelectComponentTemplate = (filterName, field, useKeywordFields = false) => {
		return (<FilterMultiSelectComponent
			isEnabled={isEnabled}
			field={field}
			useKeywordFields={useKeywordFields}
			filterName={filterName}
			currentFilters={tableState.filters}
			onFilter={onFilter}
			aggregationFields={aggregationFields}
			tableState={tableState}
			annotationsAggregations='conditionRelationAnnotationAggregation'
			endpoint='condition-relation'
		/>);
	}

	const onConditionRelationValueChange = (props, event) => {
		let updatedConditions = [...props.props.value];
		if (event.value || event.value === '') {
			updatedConditions[props.rowIndex].conditionRelationType = event.value;
		}
	};


	const conditionRelationTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="conditionRelationType.name"
					options={conditionRelationTypeTerms}
					editorChange={onConditionRelationValueChange}
					props={props}
					showClear={false}
					placeholderText={props.rowData.conditionRelationType.name}
				/>
				<ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"conditionRelationType.name"}/>
			</>
		);
	};

	const referenceEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					autocompleteFields={["curie", "cross_reference.curie"]}
					rowProps={props}
					searchService={searchService}
					endpoint='literature-reference'
					filterName='curieFilter'
					isWith={true}
					fieldName='singleReference'
				/>
				<ErrorMessageComponent
					errorMessages={errorMessages[props.rowIndex]}
					errorField={"reference"}
				/>
			</>
		);
	};


	const conditionTemplate = (rowData) => {
		if (rowData.conditions) {
			const listTemplate = (condition) => {
				return (
					<EllipsisTableCell>
						{condition.conditionSummary}
					</EllipsisTableCell>
				);
			};
			return (
				<>
					<ListTableCell template={listTemplate} listData={rowData.conditions} showBullets={true}/>
				</>
			);
		}
	};

	const conditionRelationTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					autocompleteFields={["conditionSummary"]}
					rowProps={props}
					searchService={searchService}
					endpoint='experimental-condition'
					filterName='experimentalConditionFilter'
					fieldName='conditions'
					subField='conditionSummary'
					isMultiple={true}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessages[props.rowIndex]}
					errorField="conditions"
				/>
			</>
		);
	};

	const handleEditor = (props) => {
		return (
			<>
				<InputTextEditor
					rowProps={props}
					fieldName={'handle'}
				/>
				<ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"handle"}/>
			</>
		);
	};

	const onRowEditInit = (event) => {
		rowsInEdit.current++;
		setIsEnabled(false);
		originalRows[event.index] = {...conditionRelations[event.index]};
		setOriginalRows(originalRows);
		//console.log(dataTable.current.state);
	};

	const onRowEditCancel = (event) => {
		rowsInEdit.current--;
		if (rowsInEdit.current === 0) {
			setIsEnabled(true);
		}

		let annotations = [...conditionRelations];
		annotations[event.index] = originalRows[event.index];
		delete originalRows[event.index];
		setOriginalRows(originalRows);
		setConditionRelations(annotations);
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages({...errorMessagesCopy});

	};


	const onRowEditSave = (event) => {//possible to shrink?
		rowsInEdit.current--;
		if (rowsInEdit.current === 0) {
			setIsEnabled(true);
		}
		let updatedRow = global.structuredClone(event.data);//deep copy
		if (Object.keys(event.data.singleReference).length >= 1) {
			event.data.singleReference.curie = trimWhitespace(event.data.singleReference.curie);
			updatedRow.singleReference = {};
			updatedRow.singleReference.curie = event.data.singleReference.curie;
		}
		if (event.data.conditionRelationType && Object.keys(event.data.conditionRelationType).length >= 1) {
			updatedRow.conditionRelationType = {};
			updatedRow.conditionRelationType.id = event.data.conditionRelationType.id;
		}


		mutation.mutate(updatedRow, {
			onSuccess: (data) => {
				toast_topright.current.show({severity: 'success', summary: 'Successful', detail: 'Row Updated'});
				let _conditionRelations = [...conditionRelations];
				_conditionRelations[event.index] = data.data.entity;
				setConditionRelations(_conditionRelations);
				const errorMessagesCopy = errorMessages;
				errorMessagesCopy[event.index] = {};
				setErrorMessages({...errorMessagesCopy});
			},
			onError: (error, variables, context) => {
				rowsInEdit.current++;
				setIsEnabled(false);
				toast_topright.current.show([
					{life: 7000, severity: 'error', summary: 'Update error: ', detail: error.response.data.errorMessage, sticky: false}
				]);

				let _conditionRelations = [...conditionRelations];

				const errorMessagesCopy = errorMessages;

				console.log(error);
				console.log(errorMessagesCopy);
				errorMessagesCopy[event.index] = {};
				Object.keys(error.response.data.errorMessages).forEach((field) => {
					let messageObject = {
						severity: "error",
						message: error.response.data.errorMessages[field]
					};
					errorMessagesCopy[event.index][field] = messageObject;
				});

				//console.log(errorMessagesCopy);
				setErrorMessages({...errorMessagesCopy});

				setConditionRelations(_conditionRelations);
				let _editingRows = {...editingRows, ...{[`${_conditionRelations[event.index].id}`]: true}};
				setEditingRows(_editingRows);
			},
			onSettled: (data, error, variables, context) => {

			},
		});
	};

	const onRowEditChange = (event) => {
		setEditingRows(event.data);
	};
	const columns = [
		{
			field: "handle",
			header: "Handle",
			sortable: isEnabled,
			filter: true,
			body: (rowData) => rowData.handle,
			filterElement: filterComponentTemplate("uniqueIdFilter", ["handle"]),
			editor: (props) => handleEditor(props)
		},
		{
			field: "singleReference.curie",
			header: "Reference",
			sortable: isEnabled,
			filter: true,
			filterElement: filterComponentInputTextTemplate("singleReferenceFilter", ["singleReference.curie"]),
			editor: (props) => referenceEditorTemplate(props)
		},
		{
			field: "conditionRelationType.name",
			header: "Relation",
			sortable: isEnabled,
			filter: true,
			filterElement: FilterMultiSelectComponentTemplate("conditionRelationFilter", ["conditionRelationType.name"]),
			editor: (props) => conditionRelationTypeEditor(props)
		},
		{
			field: "conditions.conditionSummary",
			header: "Conditions",
			sortable: isEnabled,
			filter: true,
			body: conditionTemplate,
			filterElement: filterComponentTemplate("experimentalConditionFilter", ["conditions.conditionSummary"]),
			editor: (props) => conditionRelationTemplate(props)
		},

	];

	useSetDefaultColumnOrder(columns, dataTable, defaultColumnNames, setIsFirst, tableState.isFirst);

	const [columnWidths, setColumnWidths] = useState(() => {
		const width = 10;

		const widthsObject = {};

		columns.forEach((col) => {
			widthsObject[col.field] = width;
		});

		return widthsObject;
	});


	const createMultiselectComponent = (tableState, defaultColumnNames, isEnabled) => {
		return (<MultiSelect
			value={tableState.selectedColumnNames}
			options={defaultColumnNames}
			onChange={e => setSelectedColumnNames(e.value)}
			style={{width: '20em', textAlign: 'center'}}
			disabled={!isEnabled}
		/>);
	};

	const header = (
		<DataTableHeaderFooterTemplate
			title={"Condition Relation Handles Table"}
			tableState={tableState}
			defaultColumnNames={defaultColumnNames}
			multiselectComponent={createMultiselectComponent(tableState, defaultColumnNames, isEnabled)}
			onclickEvent={(event) => resetTableState(event)}
			isEnabled={isEnabled}
		/>
	);

	const resetTableState = () => {
		setTableState(initialTableState);
		dataTable.current.state.columnOrder = initialTableState.selectedColumnNames;
		const _columnWidths = {...columnWidths};

		Object.keys(_columnWidths).map((key) => {
			return _columnWidths[key] = 10;
		});

		setColumnWidths(_columnWidths);
		dataTable.current.el.children[1].scrollLeft = 0;
	};


	useEffect(() => {
		const filteredColumns = filterColumns(columns, tableState.selectedColumnNames);
		const orderedColumns = orderColumns(filteredColumns, tableState.selectedColumnNames);
		setColumnList(
			orderedColumns.map((col) => {
				return <Column
					style={{'minWidth': `${columnWidths[col.field]}vw`, 'maxWidth': `${columnWidths[col.field]}vw`}}
					headerClassName='surface-0'
					columnKey={col.field}
					key={col.field}
					field={col.field}
					header={col.header}
					sortable={isEnabled}
					filter={col.filter}
					showFilterMenu={false}
					filterElement={col.filterElement}
					editor={col.editor}
					body={col.body}
				/>;
			})
		);
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [tableState, isEnabled, columnWidths]);

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
			<Toast ref={toast_topleft} position="top-left"/>
			<Toast ref={toast_topright} position="top-right"/>
			<Messages ref={errorMessage}/>
			<DataTable value={conditionRelations} header={header} reorderableColumns={isEnabled}
				tableClassName='p-datatable-md' scrollable scrollDirection="horizontal" tableStyle={{width: '200%'}} scrollHeight="62vh"
				editMode="row" onRowEditInit={onRowEditInit} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}
				onColReorder={colReorderHandler}
				editingRows={editingRows} onRowEditChange={onRowEditChange}
				ref={dataTable}
				filterDisplay="row"
				sortMode="multiple" removableSort onSort={onSort} multiSortMeta={tableState.multiSortMeta}
				first={tableState.first}
				dataKey="id" resizableColumns columnResizeMode="expand" showGridlines onColumnResizeEnd={handleColumnResizeEnd}
				paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
				paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
				currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={tableState.rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
			>
				<Column field='rowEditor' rowEditor style={{maxWidth: '7rem', minWidth: '7rem'}}
                        headerStyle={{width: '7rem', position: 'sticky'}} bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0'/>
				{columnList}
			</DataTable>
		</div>
	)
}
