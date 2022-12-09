import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ListTableCell } from '../../components/ListTableCell';
import { Toast } from 'primereact/toast';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ValidationService } from '../../service/ValidationService';
import { DialogErrorMessageComponent } from '../../components/DialogErrorMessageComponent';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { ExConAutocompleteTemplate } from '../../components/Autocomplete/ExConAutocompleteTemplate';
import { SearchService } from '../../service/SearchService';
import {autocompleteSearch, buildAutocompleteFilter, multipleAutocompleteOnChange} from "../../utils/utils";
import {AutocompleteMultiEditor} from "../../components/Autocomplete/AutocompleteMultiEditor";


export const ConditionRelationsDialog = ({
		originalConditionRelationsData, setOriginalConditionRelationsData,
		errorMessagesMainRow, setErrorMessagesMainRow
		}) => {

	const { originalConditionRelations, isInEdit, dialog, rowIndex, mainRowProps } = originalConditionRelationsData;
	const [localConditionRelations, setLocalConditionRelations] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState([]);
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const conditionRelationTypeTerms = useControlledVocabularyService('Condition relation types');
	const validationService = new ValidationService();
	const searchService = new SearchService();
	const tableRef = useRef(null);
	const rowsEdited = useRef(0);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		let _localConditionRelations = cloneRelations(originalConditionRelations);
		setLocalConditionRelations(_localConditionRelations);
		if(isInEdit){
			let rowsObject = {};
			if(_localConditionRelations) {
				_localConditionRelations.forEach((relation) => {
					rowsObject[`${relation.dataKey}`] = true;
				});
			}
			setEditingRows(rowsObject);
		}else{
			setEditingRows({});
		}
		rowsEdited.current = 0;
	};

	const onRowEditChange = (e) => {
		setEditingRows(e.data);
	}

	const onRowEditCancel = (event) => {
		let _editingRows = { ...editingRows };
		delete _editingRows[event.index];
		setEditingRows(_editingRows);
		let _localConditionRelations = [...localConditionRelations];
		if(originalConditionRelations && originalConditionRelations[event.index]){
			let dataKey = _localConditionRelations[event.index].dataKey;
			_localConditionRelations[event.index] = global.structuredClone(originalConditionRelations[event.index]);
			_localConditionRelations[event.index].dataKey = dataKey;
			setLocalConditionRelations(_localConditionRelations);
		}

		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages(errorMessagesCopy);
	};

	const onRowEditSave = async(event) => {
		const result = await validateRelation(localConditionRelations[event.index]);
		const errorMessagesCopy = [...errorMessages];
		errorMessagesCopy[event.index] = {};
		let _editingRows = { ...editingRows };
		if (result.isError) {
			let reported = false;
			Object.keys(result.data).forEach((field) => {
				let messageObject = {
					severity: "error",
					message: result.data[field]
				};
				errorMessagesCopy[event.index][field] = messageObject;
				if (!reported) {
					toast_topright.current.show([
						{ life: 7000, severity: 'error', summary: 'Update error: ',
						detail: 'Could not update condition relation [' + localConditionRelations[event.index].id + ']', sticky: false }
					]);
					reported = true;
				}
			});
		} else {
			delete _editingRows[event.index];
			compareChangesInRelations(event.data,event.index);
		}
		setErrorMessages(errorMessagesCopy);
		let _localConditionRelations = [...localConditionRelations];
		_localConditionRelations[event.index] = event.data;
		setEditingRows(_editingRows);
		setLocalConditionRelations(_localConditionRelations);

	};

	const compareChangesInRelations = (data,index) => {
		if(originalConditionRelations && originalConditionRelations[index]) {
			if (data.conditionRelationType.name !== originalConditionRelations[index].conditionRelationType.name) {
				rowsEdited.current++;
			}
			if (data.internal !== originalConditionRelations[index].internal) {
				rowsEdited.current++;
			}
			if (data.conditions.length !== originalConditionRelations[index].conditions.length) {
				rowsEdited.current++;
			} else {
				for (var i = 0; i < data.conditions.length; i++) {
					if (data.conditions[i].conditionSummary !== originalConditionRelations[index].conditions[i].conditionSummary) {
						rowsEdited.current++;
					}
				}
			}
		}
		//if a new relation has been added or there were no condition relations to begin with
		if((localConditionRelations.length > originalConditionRelations?.length) || !originalConditionRelations){
			rowsEdited.current++;
		}
	};

	const hideDialog = () => {
		setErrorMessages([]);
		setOriginalConditionRelationsData((originalConditionRelationsData) => {
			return {
				...originalConditionRelationsData,
				dialog: false,
			};
		});
		let _localConditionRelations = [];
		setLocalConditionRelations(_localConditionRelations);
	};

	const validateRelation = async (relation) => {
		let _relation = global.structuredClone(relation);
		delete _relation.dataKey;
		const result = await validationService.validate('condition-relation', _relation);
		return result;
	};

	const cloneRelations = (clonableRelations) => {
		let _clonableRelations = global.structuredClone(clonableRelations);
		if(_clonableRelations) {
			let counter = 0 ;
			_clonableRelations.forEach((relation) => {
				relation.dataKey = counter++;
			});
		} else {
			_clonableRelations = [];
		};
		return _clonableRelations;
	};

	const createNewRelationHandler = (event) => {
		let cnt = localConditionRelations ? localConditionRelations.length : 0;
		const _localConditionRelations = global.structuredClone(localConditionRelations);
		_localConditionRelations.push({
			dataKey : cnt,
		});
		let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
		setEditingRows(_editingRows);
		setLocalConditionRelations(_localConditionRelations);
	};

	const saveDataHandler = () => {

		setErrorMessages([]);
		for (const relation of localConditionRelations) {
			delete relation.dataKey;
		}
		mainRowProps.rowData.conditionRelations = localConditionRelations;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].conditionRelations = localConditionRelations;

		const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: "warn",
			message: "Pending Edits!"
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]["conditionRelations"] = messageObject;
		setErrorMessagesMainRow({...errorMessagesCopy});

		setOriginalConditionRelationsData((originalConditionRelationsData) => {
				return {
					...originalConditionRelationsData,
					dialog: false,
				}
			}
		);
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	const onInternalEditorValueChange = (props, event) => {
		let _localConditionRelations = [...localConditionRelations];
		_localConditionRelations[props.rowIndex].internal = event.value.name;
	}

	const internalEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onInternalEditorValueChange}
					props={props}
					field={"internal"}
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"internal"} />
			</>
		);
	};

	const conditionRelationTypeTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.conditionRelationType?.name}</EllipsisTableCell>;
	};

	const onConditionRelationTypeEditorValueChange = (props, event) => {
		let _localConditionRelations = [...localConditionRelations];
		_localConditionRelations[props.rowIndex].conditionRelationType = event.value;
	};

	const conditionRelationTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="conditionRelationType"
					options={conditionRelationTypeTerms}
					editorChange={onConditionRelationTypeEditorValueChange}
					props={props}
					showClear={false}
					dataKey='id'
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"conditionRelationType"} />
			</>
		);
	};

	const conditionsTemplate = (rowData) => {
		if (rowData && rowData.conditions) {
			const sortedConditionSummaries = rowData.conditions.sort((a,b) => (a.conditionSummary > b.conditionSummary) ? 1 : -1);
			const listTemplate = (item) => {
				return (
					<EllipsisTableCell>
						{item.conditionSummary}
					</EllipsisTableCell>
				);
			};
			return <ListTableCell template={listTemplate} listData={sortedConditionSummaries} />
		}
	};

	const onConditionRelationValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, "conditions", setFieldValue);
	};

	const conditionSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["conditionSummary","conditionId.curie","conditionClass.curie","conditionTaxon.curie","conditionGeneOntology.curie","conditionChemical.curie","conditionAnatomy.curie"];
		const endpoint = "experimental-condition";
		const filterName = "conditionSummaryFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const conditionsEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={conditionSearch}
					initialValue={props.rowData.conditions}
					rowProps={props}
					fieldName='conditions'
					subField='conditionSummary'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<ExConAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onConditionRelationValueChange}
				/>
				<DialogErrorMessageComponent
					errorMessages={errorMessages[props.rowIndex]}
					errorField={"conditions"}
				/>
			</>
		);
	};

	const footerTemplate = () => {
		if (!isInEdit) {
			return null;
		};
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button label="New Condition" icon="pi pi-plus" onClick={createNewRelationHandler}/>
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={rowsEdited.current === 0}/>
			</div>
		);
	}

	const handleDeleteConditionRelation = (event, props) => {
		let _localConditionRelations = global.structuredClone(localConditionRelations);
		if(props.dataKey){
			_localConditionRelations.splice(props.dataKey, 1);
		}else {
			_localConditionRelations.splice(props.rowIndex, 1);
		}
		setLocalConditionRelations(_localConditionRelations);
		rowsEdited.current++;
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={(event) => { handleDeleteConditionRelation(event, props) }}/>
		);
	}

	let headerGroup = <ColumnGroup>
						<Row>
							<Column header="Actions" colSpan={2} style={{display: isInEdit ? 'visible' : 'none'}}/>
							<Column header="Relation" />
							<Column header="Conditions" />
							<Column header="Internal" />
						</Row>
						</ColumnGroup>;

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog visible={dialog} className='w-6' modal onHide={hideDialog} closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
				<h3>Experimental Conditions</h3>
				<DataTable value={localConditionRelations} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
								editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
								bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0' />
					<Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem' , display: isInEdit ? 'visible' : 'none'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={conditionRelationTypeEditor} field="conditionRelationType.name" header="Relation" headerClassName='surface-0' body={conditionRelationTypeTemplate}/>
					<Column editor={conditionsEditorTemplate} field="conditions.conditionSummary" header="Conditions" headerClassName='surface-0' body={conditionsTemplate}/>
					<Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
				</DataTable>
			</Dialog>
		</div>
	);
};

