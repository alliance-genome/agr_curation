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
import { AutocompleteEditor } from '../../components/Autocomplete/AutocompleteEditor';
import { ExConAutocompleteTemplate } from '../../components/Autocomplete/ExConAutocompleteTemplate';
import { SearchService } from '../../service/SearchService';


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
	const rowsInEdit = useRef(0);
	const hasEdited = useRef(false);
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
			rowsInEdit.current++;
		}else{
			setEditingRows({});
		}
		hasEdited.current = false;
	};
	
	const onRowEditChange = (e) => {
		setEditingRows(e.data);
	}
	
	const onRowEditCancel = (event) => {
		rowsInEdit.current--;
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
		compareChangesInRelations(event.data,event.index);
	};
	
	const onRowEditSave = async(event) => {
		const result = await validateRelation(localConditionRelations[event.index]);
		const errorMessagesCopy = [...errorMessages];
		errorMessagesCopy[event.index] = {};
		let _editingRows = { ...editingRows };
		if (result.isError) {
			Object.keys(result.data).forEach((field) => {
				let messageObject = {
					severity: "error",
					message: result.data[field]
				};
				errorMessagesCopy[event.index][field] = messageObject;
				toast_topright.current.show([
					{ life: 7000, severity: 'error', summary: 'Update error: ',
					detail: 'Could not update condition relation [' + localConditionRelations[event.index].id + ']', sticky: false }
				]);
			});
		} else {
			delete _editingRows[event.index];
		}
		setErrorMessages(errorMessagesCopy);
		let _localConditionRelations = [...localConditionRelations];
		_localConditionRelations[event.index] = event.data;
			setEditingRows(_editingRows);	
			compareChangesInRelations(event.data,event.index);
		setLocalConditionRelations(_localConditionRelations);
		
	};
	
	const compareChangesInRelations = (data,index) => {
		if(originalConditionRelations && originalConditionRelations[index]) {
			hasEdited.current = false;
			if (data.conditionRelationType.name !== originalConditionRelations[index].conditionRelationType.name) {
				hasEdited.current = true;
			}
			if (data.internal !== originalConditionRelations[index].internal) {
				hasEdited.current = true;
			}
			if (data.conditions.length !== originalConditionRelations[index].conditions.length) {
				hasEdited.current = true;
			} else {
				for (var i = 0; i < data.conditions.length; i++) {
					if (data.conditions[i].conditionStatement !== originalConditionRelations[index].conditions[i].conditionStatement) {
						hasEdited.current = true;
					}
				}
			}
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
	
	const saveDataHandler = async () => {
		
		setErrorMessages([]);
		for (const relation of localConditionRelations) {
			delete relation.dataKey;
		}
		mainRowProps.rowData.conditionRelations = localConditionRelations;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].conditionRelations = localConditionRelations;
		
		if(hasEdited.current){
			const errorMessagesCopy = errorMessagesMainRow;
			let messageObject = {
				severity: "warn",
				message: "Pending Edits!"
			};
			errorMessagesCopy[rowIndex] = {};
			errorMessagesCopy[rowIndex]["conditionRelations"] = messageObject;
			setErrorMessagesMainRow({...errorMessagesCopy});
		}
		
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
		return <EllipsisTableCell>{rowData.conditionRelationType.name}</EllipsisTableCell>;
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
			const sortedConditionStatements = rowData.conditions.sort((a,b) => (a.conditionStatement > b.conditionStatement) ? 1 : -1);
			const listTemplate = (item) => {
				return (
					<EllipsisTableCell>
						{item.conditionStatement}
					</EllipsisTableCell>
				);
			};
			return <ListTableCell template={listTemplate} listData={sortedConditionStatements} />
		}
	};
	
	const conditionsEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					autocompleteFields={["conditionStatement"]}
					rowProps={props}
					searchService={searchService}
					endpoint='experimental-condition'
					filterName='conditionStatementFilter'
					fieldName='conditions'
					subField='conditionStatement'
					isMultiple={true}
					valueDisplay={(item, setAutocompleteSelectedItem, op, query) => 
						<ExConAutocompleteTemplate item={item} setAutocompleteSelectedItem={setAutocompleteSelectedItem} op={op} query={query}/>}
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
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={!hasEdited.current}/>
			</div>
		);
	}
	
	let headerGroup = 	<ColumnGroup>
						<Row>
							<Column header="Actions" style={{display: isInEdit ? 'visible' : 'none'}}/>
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
				<DataTable value={localConditionRelations} dataKey="dataKey" showGridlines  editMode='row' headerColumnGroup={headerGroup}
								editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
								bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0' />
					<Column editor={conditionRelationTypeEditor} field="conditionRelationType.name" header="Relation" headerClassName='surface-0' body={conditionRelationTypeTemplate}/>
					<Column editor={conditionsEditorTemplate} field="conditions.conditionStatement" header="Conditions" headerClassName='surface-0' body={conditionsTemplate}/>
					<Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
				</DataTable>
			</Dialog>
		</div>
	);
};

