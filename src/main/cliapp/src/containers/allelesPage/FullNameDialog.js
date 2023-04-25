import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DialogErrorMessageComponent } from '../../components/DialogErrorMessageComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { InputTextEditor } from '../../components/InputTextEditor';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { useVocabularyTermSetService } from '../../service/useVocabularyTermSetService';
import { ValidationService } from '../../service/ValidationService';
import { evidenceTemplate, evidenceEditorTemplate } from '../../components/EvidenceComponent';

export const FullNameDialog = ({
													originalFullNameData,
													setOriginalFullNameData,
													errorMessagesMainRow,
													setErrorMessagesMainRow
												}) => {
	const { originalFullNames, isInEdit, dialog, rowIndex, mainRowProps } = originalFullNameData;
	const [localFullNames, setLocalFullNames] = useState(null) ;
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState([]);
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const rowsEdited = useRef(0);
	const toast_topright = useRef(null);

	const synonymScopeTerms = useControlledVocabularyService('Synonym scope');
	const fullNameTypeTerms = useVocabularyTermSetService('Full name types');

	const showDialogHandler = () => {
		let _localFullNames = cloneFullNames(originalFullNames);
		setLocalFullNames(_localFullNames);

		if(isInEdit){
			let rowsObject = {};
			if(_localFullNames) {
				_localFullNames.forEach((fn) => {
					rowsObject[`${fn.dataKey}`] = true;
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
		let _localFullNames = [...localFullNames];//add new note support
		if(originalFullNames && originalFullNames[event.index]){
			let dataKey = _localFullNames[event.index].dataKey;
			_localFullNames[event.index] = global.structuredClone(originalFullNames[event.index]);
			_localFullNames[event.index].dataKey = dataKey;
			setLocalFullNames(_localFullNames);
		}
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages(errorMessagesCopy);
		compareChangesInFullNames(event.data,event.index);
	};

	const compareChangesInFullNames = (data, index) => {
		if(originalFullNames && originalFullNames[index]) {
			if (data.internal !== originalFullNames[index].internal) {
				rowsEdited.current++;
			}
			if (data.displayText !== originalFullNames[index].displayText) {
				rowsEdited.current++;
			}
			if (data.formatText !== originalFullNames[index].formatText) {
				rowsEdited.current++;
			}
			if (data.synonymUrl !== originalFullNames[index].synonymUrl) {
				rowsEdited.current++;
			}
			if ((originalFullNames[index].evidence && !data.evidence) ||
					(!originalFullNames[index].evidence && data.evidence) ||
					(data.evidence && (data.evidence.length !== originalFullNames[index].evidence.length))
				) {
				rowsEdited.current++;
			} else {
				if (data.evidence) {
					for (var i = 0; i < data.evidence.length; i++) {
						if (data.evidence[i].curie !== originalFullNames[index].evidence[i].curie) {
							rowsEdited.current++;
						}
					}
				}
			}
			if ((originalFullNames[index].synonymScope && !data.synonymScope) ||
					(!originalFullNames[index].synonymScope && data.synonymScope) ||
					(originalFullNames[index].synonymScope && (originalFullNames[index].synonymScope.name !== data.synonymScope.name))
				) {
				rowsEdited.current++;
			}
			if ((originalFullNames[index].nameType && !data.nameType) ||
					(!originalFullNames[index].nameType && data.nameType) ||
					(originalFullNames[index].nameType && (originalFullNames[index].nameType.name !== data.nameType.name))
				) {
				rowsEdited.current++;
			}
		}
		
		if (localFullNames.length > originalFullNames?.length || !originalFullNames[0]) {
			rowsEdited.current++;
		}
	};

	const onRowEditSave = async(event) => {
		const result = await validateFullName(localFullNames[event.index]);
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
						detail: 'Could not update AlleleFullName [' + localFullNames[event.index].id + ']', sticky: false }
					]);
					reported = true;
				}
			});
		} else {
			delete _editingRows[event.index];
			compareChangesInFullNames(event.data, event.index);
		}
		setErrorMessages(errorMessagesCopy);
		let _localFullNames = [...localFullNames];
		_localFullNames[event.index] = event.data;
		setEditingRows(_editingRows);
		setLocalFullNames(_localFullNames);
	};

	const hideDialog = () => {
		setErrorMessages([]);
		setOriginalFullNameData((originalFullNameData) => {
			return {
				...originalFullNameData,
				dialog: false,
			};
		});
		let _localFullNames = [];
		setLocalFullNames(_localFullNames);
	};

	const validateFullName = async (fid) => {
		let _fid = global.structuredClone(fid);
		delete _fid.dataKey;
		const result = await validationService.validate('allelefullnameslotannotation', _fid);
		return result;
	};

	const cloneFullNames = (clonableFullNames) => {
		let _clonableFullNames = [];
		if (clonableFullNames?.length > 0 && clonableFullNames[0]) {
			_clonableFullNames = global.structuredClone(clonableFullNames);
			if(_clonableFullNames) {
				let counter = 0 ;
				_clonableFullNames.forEach((name) => {
					name.dataKey = counter++;
				});
			}
		} 
		return _clonableFullNames;
	};

	const saveDataHandler = () => {
		setErrorMessages([]);
		for (const name of localFullNames) {
			delete name.dataKey;
		}
		mainRowProps.rowData.alleleFullName = localFullNames[0] ? localFullNames[0] : null;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].alleleFullName = localFullNames[0] ? localFullNames[0] : null;

		const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: "warn",
			message: "Pending Edits!"
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]["alleleFullName"] = messageObject;
		setErrorMessagesMainRow({...errorMessagesCopy});

		setOriginalFullNameData((originalFullNameData) => {
				return {
					...originalFullNameData,
					dialog: false,
				}
			}
		);
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	const onInternalEditorValueChange = (props, event) => {
		let _localFullNames = [...localFullNames];
		_localFullNames[props.rowIndex].internal = event.value.name;
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

	const displayTextTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.displayText}</EllipsisTableCell>;
	};

	const displayTextEditorTemplate = (props) => {
		return (
			<>
				<InputTextEditor
					rowProps={props}
					fieldName={"displayText"}
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"displayText"} />
			</>
		);
	};

	const formatTextTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.formatText}</EllipsisTableCell>;
	};

	const formatTextEditorTemplate = (props) => {
		return (
			<>
				<InputTextEditor
					rowProps={props}
					fieldName={"formatText"}
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"formatText"} />
			</>
		);
	};

	const synonymUrlTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.synonymUrl}</EllipsisTableCell>;
	};

	const synonymUrlEditorTemplate = (props) => {
		return (
			<>
				<InputTextEditor
					rowProps={props}
					fieldName={"synonymUrl"}
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"synonymUrl"} />
			</>
		);
	};

	const onSynonymScopeEditorValueChange = (props, event) => {
		let _localFullNames = [...localFullNames];
		_localFullNames[props.rowIndex].synonymScope = event.value;
	};

	const synonymScopeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="synonymScope"
					options={synonymScopeTerms}
					editorChange={onSynonymScopeEditorValueChange}
					props={props}
					showClear={false}
					dataKey='id'
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"synonymScope"} />
			</>
		);
	};

	const synonymScopeTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.synonymScope?.name}</EllipsisTableCell>;
	};

	const onNameTypeEditorValueChange = (props, event) => {
		let _localFullNames = [...localFullNames];
		_localFullNames[props.rowIndex].nameType = event.value;
	};

	const nameTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="nameType"
					options={fullNameTypeTerms}
					editorChange={onNameTypeEditorValueChange}
					props={props}
					showClear={false}
					dataKey='id'
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"nameType"} />
			</>
		);
	};

	const nameTypeTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.nameType?.name}</EllipsisTableCell>;
	};

	const footerTemplate = () => {
		if (!isInEdit) {
			return null;
		};
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button label="New Name" icon="pi pi-plus" onClick={createNewFullNameHandler} disabled={localFullNames?.length > 0}/>
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={rowsEdited.current === 0}/>
			</div>
		);
	}

	const createNewFullNameHandler = (event) => {
		let cnt = localFullNames ? localFullNames.length : 0;
		const _localFullNames = global.structuredClone(localFullNames);
		_localFullNames.push({
			dataKey : cnt,
		});
		let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
		setEditingRows(_editingRows);
		setLocalFullNames(_localFullNames);
	};

	const handleDeleteFullName = (event, props) => {
		let _localFullNames = global.structuredClone(localFullNames);
		if(props.dataKey){
			_localFullNames.splice(props.dataKey, 1);
		}else {
			_localFullNames.splice(props.rowIndex, 1);
		}
		setLocalFullNames(_localFullNames);
		rowsEdited.current++;
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={(event) => { handleDeleteFullName(event, props) }}/>
		);
	}

	let headerGroup = 
			<ColumnGroup>
				<Row>
					<Column header="Actions" colSpan={2} style={{display: isInEdit ? 'visible' : 'none'}}/>
					<Column header="Display Text" />
					<Column header="Format Text" />
					<Column header="Synonym Scope" />
					<Column header="Name Type" />
					<Column header="Synonym URL" />
					<Column header="Internal" />
					<Column header="Evidence" />
				</Row>
			</ColumnGroup>;

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog visible={dialog} className='w-6' modal onHide={hideDialog} closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
				<h3>Full Name</h3>
				<DataTable value={localFullNames} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
								editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
								bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0' />
					<Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem' , display: isInEdit ? 'visible' : 'none'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={displayTextEditorTemplate} field="displayText" header="Display Text" headerClassName='surface-0' body={displayTextTemplate}/>
					<Column editor={formatTextEditorTemplate} field="formatText" header="Format Text" headerClassName='surface-0' body={formatTextTemplate}/>
					<Column editor={synonymScopeEditor} field="synonymScope" header="Synonym Scope" headerClassName='surface-0' body={synonymScopeTemplate}/>
					<Column editor={nameTypeEditor} field="nameType" header="Name Type" headerClassName='surface-0' body={nameTypeTemplate}/>
					<Column editor={synonymUrlEditorTemplate} field="synonymUrl" header="Synonym URL" headerClassName='surface-0' body={synonymUrlTemplate}/>
					<Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
					<Column editor={(props) => evidenceEditorTemplate(props, errorMessages)} field="evidence.curie" header="Evidence" headerClassName='surface-0' body={(rowData) => evidenceTemplate(rowData)}/>
				</DataTable>
			</Dialog>
		</div>
	);
};
