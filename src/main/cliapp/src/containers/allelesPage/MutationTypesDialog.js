import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { ListTableCell } from '../../components/ListTableCell';
import { DialogErrorMessageComponent } from '../../components/DialogErrorMessageComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { SearchService } from '../../service/SearchService';
import { ValidationService } from '../../service/ValidationService';
import { AutocompleteMultiEditor } from "../../components/Autocomplete/AutocompleteMultiEditor";
import { autocompleteSearch, buildAutocompleteFilter, multipleAutocompleteOnChange } from "../../utils/utils";
import { SubjectAutocompleteTemplate } from '../../components/Autocomplete/SubjectAutocompleteTemplate';
import { evidenceTemplate, evidenceEditorTemplate } from '../../components/EvidenceComponent';

export const MutationTypesDialog = ({
													originalMutationTypesData,
													setOriginalMutationTypesData,
													errorMessagesMainRow,
													setErrorMessagesMainRow
												}) => {
	const { originalMutationTypes, isInEdit, dialog, rowIndex, mainRowProps } = originalMutationTypesData;
	const [localMutationTypes, setLocalMutationTypes] = useState(null) ;
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState([]);
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const validationService = new ValidationService();
	const searchService = new SearchService();
	const tableRef = useRef(null);
	const rowsEdited = useRef(0);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		let _localMutationTypes = cloneMutationTypes(originalMutationTypes);
		setLocalMutationTypes(_localMutationTypes);

		if(isInEdit){
			let rowsObject = {};
			if(_localMutationTypes) {
				_localMutationTypes.forEach((mt) => {
					rowsObject[`${mt.dataKey}`] = true;
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
		let _localMutationTypes = [...localMutationTypes];//add new note support
		if(originalMutationTypes && originalMutationTypes[event.index]){
			let dataKey = _localMutationTypes[event.index].dataKey;
			_localMutationTypes[event.index] = global.structuredClone(originalMutationTypes[event.index]);
			_localMutationTypes[event.index].dataKey = dataKey;
			setLocalMutationTypes(_localMutationTypes);
		}
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages(errorMessagesCopy);
		compareChangesInMutationTypes(event.data,event.index);
	};

	const compareChangesInMutationTypes = (data,index) => {
		if(originalMutationTypes && originalMutationTypes[index]) {
			if (data.internal !== originalMutationTypes[index].internal) {
				rowsEdited.current++;
			}
			if (!data.mutationTypes.length !== originalMutationTypes[index].mutationTypes.length) {
				rowsEdited.current++;
			} else {
				for (var i = 0; i < data.mutationTypes.length; i++) {
					if (data.mutationTypes[i].curie !== originalMutationTypes[index].mutationTypes[i].curie) {
						rowsEdited.current++;
					}
				}
			}
			if ((originalMutationTypes[index].evidence && !data.evidence) ||
				(!originalMutationTypes[index].evidence && data.evidence) ||
				(data.evidence && (data.evidence.length !== originalMutationTypes[index].evidence.length))) {
				rowsEdited.current++;
			} else {
				if (data.evidence) {
					for (var j = 0; j < data.evidence.length; j++) {
						if (data.evidence[j].curie !== originalMutationTypes[index].evidence[j].curie) {
							rowsEdited.current++;
						}
					}
				}
			}
		}

		if((localMutationTypes.length > originalMutationTypes?.length) || !originalMutationTypes){
			rowsEdited.current++;
		}
	};

	const onRowEditSave = async(event) => {
		const result = await validateMutationType(localMutationTypes[event.index]);
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
				if(!reported) {
					toast_topright.current.show([
						{ life: 7000, severity: 'error', summary: 'Update error: ',
						detail: 'Could not update AlleleMutationType [' + localMutationTypes[event.index].id + ']', sticky: false }
					]);
					reported = true;
				}
			});
		} else {
			delete _editingRows[event.index];
			compareChangesInMutationTypes(event.data,event.index);
		}
		setErrorMessages(errorMessagesCopy);
		let _localMutationTypes = [...localMutationTypes];
		_localMutationTypes[event.index] = event.data;
		setEditingRows(_editingRows);
		setLocalMutationTypes(_localMutationTypes);
	};

	const hideDialog = () => {
		setErrorMessages([]);
		setOriginalMutationTypesData((originalMutationTypesData) => {
			return {
				...originalMutationTypesData,
				dialog: false,
			};
		});
		let _localMutationTypes = [];
		setLocalMutationTypes(_localMutationTypes);
	};

	const validateMutationType = async (mt) => {
		let _mt = global.structuredClone(mt);
		delete _mt.dataKey;
		const result = await validationService.validate('allelemutationtypeslotannotation', _mt);
		return result;
	};

	const cloneMutationTypes = (clonableMutationTypes) => {
		let _clonableMutationTypes = global.structuredClone(clonableMutationTypes);
		if(_clonableMutationTypes) {
			let counter = 0 ;
			_clonableMutationTypes.forEach((note) => {
				note.dataKey = counter++;
			});
		} else {
			_clonableMutationTypes = [];
		};
		return _clonableMutationTypes;
	};

	const saveDataHandler = () => {
		setErrorMessages([]);
		for (const note of localMutationTypes) {
			delete note.dataKey;
		}
		mainRowProps.rowData.alleleMutationTypes = localMutationTypes;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].alleleMutationTypes = localMutationTypes;

		const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: "warn",
			message: "Pending Edits!"
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]["alleleMutationTypes"] = messageObject;
		setErrorMessagesMainRow({...errorMessagesCopy});

		setOriginalMutationTypesData((originalMutationTypesData) => {
				return {
					...originalMutationTypesData,
					dialog: false,
				}
			}
		);
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

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
	
	const onInternalEditorValueChange = (props, event) => {
		let _localMutationTypes = [...localMutationTypes];
		_localMutationTypes[props.rowIndex].internal = event.value.name;
	}

	const mutationTypeTemplate = (rowData) => {
		if (rowData && rowData.mutationTypes) {
			const sortedMutationTypes = rowData.mutationTypes.sort((a,b) => (a.name > b.name) ? 1 : -1);
			const listTemplate = (item) => {
				return (
					<EllipsisTableCell>
						{item.name + ' (' + item.curie + ')'}
					</EllipsisTableCell>
				);
			};
			return <ListTableCell template={listTemplate} listData={sortedMutationTypes} />
		}
	};

	const mutationTypeEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={mutationTypeSearch}
					initialValue={props.rowData.mutationTypes}
					rowProps={props}
					fieldName='mutationTypes'
					subField='curie'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
							<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onMutationTypeValueChange}
				/>
				<DialogErrorMessageComponent
					errorMessages={errorMessages[props.rowIndex]}
					errorField={"mutationTypes"}
				/>
			</>
		);
	};

	const onMutationTypeValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, "mutationTypes", setFieldValue);
	};

	const mutationTypeSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["name", "curie"];
		const endpoint = "soterm";
		const filterName = "mutationTypeFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const footerTemplate = () => {
		if (!isInEdit) {
			return null;
		};
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button label="New Mutation Type" icon="pi pi-plus" onClick={createNewMutationTypeHandler}/>
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={rowsEdited.current === 0}/>
			</div>
		);
	}

	const createNewMutationTypeHandler = (event) => {
		let cnt = localMutationTypes ? localMutationTypes.length : 0;
		const _localMutationTypes = global.structuredClone(localMutationTypes);
		_localMutationTypes.push({
			dataKey : cnt,
			internal : false,
		});
		let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
		setEditingRows(_editingRows);
		setLocalMutationTypes(_localMutationTypes);
	};

	const handleDeleteMutationType = (event, props) => {
		let _localMutationTypes = global.structuredClone(localMutationTypes);
		if(props.dataKey){
			_localMutationTypes.splice(props.dataKey, 1);
		}else {
			_localMutationTypes.splice(props.rowIndex, 1);
		}
		setLocalMutationTypes(_localMutationTypes);
		rowsEdited.current++;
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={(event) => { handleDeleteMutationType(event, props) }}/>
		);
	}

	let headerGroup = 	<ColumnGroup>
							<Row>
								<Column header="Actions" colSpan={2} style={{display: isInEdit ? 'visible' : 'none'}}/>
								<Column header="Mutation Types" />
								<Column header="Internal" />
								<Column header="Evidence" />
							</Row>
						</ColumnGroup>;

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog visible={dialog} className='w-6' modal onHide={hideDialog} closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
				<h3>Mutation Types</h3>
				<DataTable value={localMutationTypes} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
						editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
							bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0' />
					<Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem' , display: isInEdit ? 'visible' : 'none'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={mutationTypeEditorTemplate} field="mutationType.curie" header="Mutation Type" headerClassName='surface-0' body={mutationTypeTemplate}/>
					<Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
					<Column editor={(props) =>evidenceEditorTemplate(props, errorMessages)} field="evidence.curie" header="Evidence" headerClassName='surface-0' body={(rowData) => evidenceTemplate(rowData)}/>
				</DataTable>
			</Dialog>
		</div>
	);
};
