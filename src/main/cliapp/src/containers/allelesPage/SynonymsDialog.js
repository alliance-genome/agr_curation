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
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ValidationService } from '../../service/ValidationService';
import { evidenceTemplate, evidenceEditorTemplate } from '../../components/EvidenceComponent';
import { synonymScopeTemplate, nameTypeTemplate, synonymUrlTemplate, synonymUrlEditorTemplate, displayTextTemplate, displayTextEditorTemplate, formatTextTemplate, formatTextEditorTemplate } from '../../components/NameSlotAnnotationComponent';

export const SynonymsDialog = ({
													originalSynonymsData,
													setOriginalSynonymsData,
													errorMessagesMainRow,
													setErrorMessagesMainRow
												}) => {
	const { originalSynonyms, isInEdit, dialog, rowIndex, mainRowProps } = originalSynonymsData;
	const [localSynonyms, setLocalSynonyms] = useState(null) ;
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState([]);
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const rowsEdited = useRef(0);
	const toast_topright = useRef(null);

	const synonymScopeTerms = useControlledVocabularyService('Synonym scope');
	const synonymTypeTerms = useControlledVocabularyService('Name type');

	const showDialogHandler = () => {
		let _localSynonyms = cloneSynonyms(originalSynonyms);
		setLocalSynonyms(_localSynonyms);

		if(isInEdit){
			let rowsObject = {};
			if(_localSynonyms) {
				_localSynonyms.forEach((fn) => {
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
		let _localSynonyms = [...localSynonyms];//add new note support
		if(originalSynonyms && originalSynonyms[event.index]){
			let dataKey = _localSynonyms[event.index].dataKey;
			_localSynonyms[event.index] = global.structuredClone(originalSynonyms[event.index]);
			_localSynonyms[event.index].dataKey = dataKey;
			setLocalSynonyms(_localSynonyms);
		}
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages(errorMessagesCopy);
		compareChangesInSynonyms(event.data,event.index);
	};

	const compareChangesInSynonyms = (data, index) => {
		if(originalSynonyms && originalSynonyms[index]) {
			if (data.internal !== originalSynonyms[index].internal) {
				rowsEdited.current++;
			}
			if (data.displayText !== originalSynonyms[index].displayText) {
				rowsEdited.current++;
			}
			if (data.formatText !== originalSynonyms[index].formatText) {
				rowsEdited.current++;
			}
			if (data.synonymUrl !== originalSynonyms[index].synonymUrl) {
				rowsEdited.current++;
			}
			if ((originalSynonyms[index].evidence && !data.evidence) ||
					(!originalSynonyms[index].evidence && data.evidence) ||
					(data.evidence && (data.evidence.length !== originalSynonyms[index].evidence.length))
				) {
				rowsEdited.current++;
			} else {
				if (data.evidence) {
					for (var i = 0; i < data.evidence.length; i++) {
						if (data.evidence[i].curie !== originalSynonyms[index].evidence[i].curie) {
							rowsEdited.current++;
						}
					}
				}
			}
			if ((originalSynonyms[index].synonymScope && !data.synonymScope) ||
					(!originalSynonyms[index].synonymScope && data.synonymScope) ||
					(originalSynonyms[index].synonymScope && (originalSynonyms[index].synonymScope.name !== data.synonymScope.name))
				) {
				rowsEdited.current++;
			}
			if ((originalSynonyms[index].nameType && !data.nameType) ||
					(!originalSynonyms[index].nameType && data.nameType) ||
					(originalSynonyms[index].nameType && (originalSynonyms[index].nameType.name !== data.nameType.name))
				) {
				rowsEdited.current++;
			}
		}
		
		if (localSynonyms.length > originalSynonyms?.length || !originalSynonyms[0]) {
			rowsEdited.current++;
		}
	};

	const onRowEditSave = async(event) => {
		const result = await validateSynonym(localSynonyms[event.index]);
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
						detail: 'Could not update AlleleSynonym [' + localSynonyms[event.index].id + ']', sticky: false }
					]);
					reported = true;
				}
			});
		} else {
			delete _editingRows[event.index];
			compareChangesInSynonyms(event.data, event.index);
		}
		setErrorMessages(errorMessagesCopy);
		let _localSynonyms = [...localSynonyms];
		_localSynonyms[event.index] = event.data;
		setEditingRows(_editingRows);
		setLocalSynonyms(_localSynonyms);
	};

	const hideDialog = () => {
		setErrorMessages([]);
		setOriginalSynonymsData((originalSynonymsData) => {
			return {
				...originalSynonymsData,
				dialog: false,
			};
		});
		let _localSynonyms = [];
		setLocalSynonyms(_localSynonyms);
	};

	const validateSynonym = async (fid) => {
		let _fid = global.structuredClone(fid);
		delete _fid.dataKey;
		const result = await validationService.validate('allelesynonymslotannotation', _fid);
		return result;
	};

	const cloneSynonyms = (clonableSynonyms) => {
		let _clonableSynonyms = [];
		if (clonableSynonyms?.length > 0 && clonableSynonyms[0]) {
			_clonableSynonyms = global.structuredClone(clonableSynonyms);
			if(_clonableSynonyms) {
				let counter = 0 ;
				_clonableSynonyms.forEach((name) => {
					name.dataKey = counter++;
				});
			}
		} 
		return _clonableSynonyms;
	};

	const saveDataHandler = () => {
		setErrorMessages([]);
		for (const name of localSynonyms) {
			delete name.dataKey;
		}
		mainRowProps.rowData.alleleSynonyms = localSynonyms;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].alleleSynonyms = localSynonyms;

		const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: "warn",
			message: "Pending Edits!"
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]["alleleSynonyms"] = messageObject;
		setErrorMessagesMainRow({...errorMessagesCopy});

		setOriginalSynonymsData((originalSynonymsData) => {
				return {
					...originalSynonymsData,
					dialog: false,
				}
			}
		);
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	const onInternalEditorValueChange = (props, event) => {
		let _localSynonyms = [...localSynonyms];
		_localSynonyms[props.rowIndex].internal = event.value.name;
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

	

	const onSynonymScopeEditorValueChange = (props, event) => {
		let _localSynonyms = [...localSynonyms];
		_localSynonyms[props.rowIndex].synonymScope = event.value;
	};

	const synonymScopeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="synonymScope"
					options={synonymScopeTerms}
					editorChange={onSynonymScopeEditorValueChange}
					props={props}
					showClear={true}
					dataKey='id'
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"synonymScope"} />
			</>
		);
	};

	const onNameTypeEditorValueChange = (props, event) => {
		let _localSynonyms = [...localSynonyms];
		_localSynonyms[props.rowIndex].nameType = event.value;
	};

	const nameTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="nameType"
					options={synonymTypeTerms}
					editorChange={onNameTypeEditorValueChange}
					props={props}
					showClear={false}
					dataKey='id'
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"nameType"} />
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
				<Button label="New Synonym" icon="pi pi-plus" onClick={createNewSynonymHandler}/>
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={rowsEdited.current === 0}/>
			</div>
		);
	}

	const createNewSynonymHandler = (event) => {
		let cnt = localSynonyms ? localSynonyms.length : 0;
		const _localSynonyms = global.structuredClone(localSynonyms);
		_localSynonyms.push({
			dataKey : cnt,
			internal : false,
			nameType : synonymTypeTerms[0]
		});
		let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
		setEditingRows(_editingRows);
		setLocalSynonyms(_localSynonyms);
	};

	const handleDeleteSynonym = (event, props) => {
		let _localSynonyms = global.structuredClone(localSynonyms);
		if(props.dataKey){
			_localSynonyms.splice(props.dataKey, 1);
		}else {
			_localSynonyms.splice(props.rowIndex, 1);
		}
		setLocalSynonyms(_localSynonyms);
		rowsEdited.current++;
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={(event) => { handleDeleteSynonym(event, props) }}/>
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
					<Column header="Updated By" />
					<Column header="Date Updated" />
				</Row>
			</ColumnGroup>;

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog visible={dialog} className='w-10' modal onHide={hideDialog} closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
				<h3>Synonyms</h3>
				<DataTable value={localSynonyms} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
								editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
								bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0' />
					<Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem' , display: isInEdit ? 'visible' : 'none'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={(props) => displayTextEditorTemplate(props, errorMessages)} field="displayText" header="Display Text" headerClassName='surface-0' body={displayTextTemplate}/>
					<Column editor={(props) => formatTextEditorTemplate(props, errorMessages)} field="formatText" header="Format Text" headerClassName='surface-0' body={formatTextTemplate}/>
					<Column editor={synonymScopeEditor} field="synonymScope" header="Synonym Scope" headerClassName='surface-0' body={synonymScopeTemplate}/>
					<Column editor={nameTypeEditor} field="nameType" header="Name Type" headerClassName='surface-0' body={nameTypeTemplate}/>
					<Column editor={(props) => synonymUrlEditorTemplate(props, errorMessages)} field="synonymUrl" header="Synonym URL" headerClassName='surface-0' body={synonymUrlTemplate}/>
					<Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
					<Column editor={(props) => evidenceEditorTemplate(props, errorMessages)} field="evidence.curie" header="Evidence" headerClassName='surface-0' body={(rowData) => evidenceTemplate(rowData)}/>
					<Column field="updatedBy.uniqueId" header="Updated By" />
					<Column field="dateUpdated" header="Date Updated" />
				</DataTable>
			</Dialog>
		</div>
	);
};
