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
import { useVocabularyTermSetService } from '../../service/useVocabularyTermSetService';
import { ValidationService } from '../../service/ValidationService';
import { evidenceTemplate, evidenceEditorTemplate } from '../../components/EvidenceComponent';
import { synonymScopeTemplate, nameTypeTemplate, synonymUrlTemplate, synonymUrlEditorTemplate, displayTextTemplate, displayTextEditorTemplate, formatTextTemplate, formatTextEditorTemplate } from '../../components/NameSlotAnnotationComponent';

export const SymbolDialog = ({
													originalSymbolData,
													setOriginalSymbolData,
													errorMessagesMainRow,
													setErrorMessagesMainRow
												}) => {
	const { originalSymbols, isInEdit, dialog, rowIndex, mainRowProps } = originalSymbolData;
	const [localSymbols, setLocalSymbols] = useState(null) ;
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState([]);
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const rowsEdited = useRef(0);
	const toast_topright = useRef(null);

	const synonymScopeTerms = useControlledVocabularyService('Synonym scope');
	const symbolNameTypeTerms = useVocabularyTermSetService('Symbol name types');

	const showDialogHandler = () => {
		let _localSymbols = cloneSymbols(originalSymbols);
		setLocalSymbols(_localSymbols);

		if(isInEdit){
			let rowsObject = {};
			if(_localSymbols) {
				_localSymbols.forEach((sym) => {
					rowsObject[`${sym.dataKey}`] = true;
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
		let _localSymbols = [...localSymbols];//add new note support
		if(originalSymbols && originalSymbols[event.index]){
			let dataKey = _localSymbols[event.index].dataKey;
			_localSymbols[event.index] = global.structuredClone(originalSymbols[event.index]);
			_localSymbols[event.index].dataKey = dataKey;
			setLocalSymbols(_localSymbols);
		}
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages(errorMessagesCopy);
		compareChangesInSymbols(event.data,event.index);
	};

	const compareChangesInSymbols = (data, index) => {
		if(originalSymbols && originalSymbols[index]) {
			if (data.internal !== originalSymbols[index].internal) {
				rowsEdited.current++;
			}
			if (data.displayText !== originalSymbols[index].displayText) {
				rowsEdited.current++;
			}
			if (data.formatText !== originalSymbols[index].formatText) {
				rowsEdited.current++;
			}
			if (data.synonymUrl !== originalSymbols[index].synonymUrl) {
				rowsEdited.current++;
			}
			if ((originalSymbols[index].evidence && !data.evidence) ||
					(!originalSymbols[index].evidence && data.evidence) ||
					(data.evidence && (data.evidence.length !== originalSymbols[index].evidence.length))
				) {
				rowsEdited.current++;
			} else {
				if (data.evidence) {
					for (var i = 0; i < data.evidence.length; i++) {
						if (data.evidence[i].curie !== originalSymbols[index].evidence[i].curie) {
							rowsEdited.current++;
						}
					}
				}
			}
			if ((originalSymbols[index].synonymScope && !data.synonymScope) ||
					(!originalSymbols[index].synonymScope && data.synonymScope) ||
					(originalSymbols[index].synonymScope && (originalSymbols[index].synonymScope.name !== data.synonymScope.name))
				) {
				rowsEdited.current++;
			}
			if ((originalSymbols[index].nameType && !data.nameType) ||
					(!originalSymbols[index].nameType && data.nameType) ||
					(originalSymbols[index].nameType && (originalSymbols[index].nameType.name !== data.nameType.name))
				) {
				rowsEdited.current++;
			}
		}
		
		if (localSymbols.length > originalSymbols?.length || !originalSymbols[0]) {
			rowsEdited.current++;
		}
	};

	const onRowEditSave = async(event) => {
		const result = await validateSymbol(localSymbols[event.index]);
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
						detail: 'Could not update AlleleSymbol [' + localSymbols[event.index].id + ']', sticky: false }
					]);
					reported = true;
				}
			});
		} else {
			delete _editingRows[event.index];
			compareChangesInSymbols(event.data, event.index);
		}
		setErrorMessages(errorMessagesCopy);
		let _localSymbols = [...localSymbols];
		_localSymbols[event.index] = event.data;
		setEditingRows(_editingRows);
		setLocalSymbols(_localSymbols);
	};

	const hideDialog = () => {
		setErrorMessages([]);
		setOriginalSymbolData((originalSymbolData) => {
			return {
				...originalSymbolData,
				dialog: false,
			};
		});
		let _localSymbols = [];
		setLocalSymbols(_localSymbols);
	};

	const validateSymbol = async (fid) => {
		let _fid = global.structuredClone(fid);
		delete _fid.dataKey;
		const result = await validationService.validate('allelesymbolslotannotation', _fid);
		return result;
	};

	const cloneSymbols = (clonableSymbols) => {
		let _clonableSymbols = [];
		if (clonableSymbols?.length > 0 && clonableSymbols[0]) {
			_clonableSymbols = global.structuredClone(clonableSymbols);
			if(_clonableSymbols) {
				let counter = 0 ;
				_clonableSymbols.forEach((name) => {
					name.dataKey = counter++;
				});
			}
		} 
		return _clonableSymbols;
	};

	const saveDataHandler = () => {
		setErrorMessages([]);
		for (const name of localSymbols) {
			delete name.dataKey;
		}
		mainRowProps.rowData.alleleSymbol = localSymbols[0] ? localSymbols[0] : null;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].alleleSymbol = localSymbols[0] ? localSymbols[0] : null;

		const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: "warn",
			message: "Pending Edits!"
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]["alleleSymbol"] = messageObject;
		setErrorMessagesMainRow({...errorMessagesCopy});

		setOriginalSymbolData((originalSymbolData) => {
				return {
					...originalSymbolData,
					dialog: false,
				}
			}
		);
	};

	const internalTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	};

	const onInternalEditorValueChange = (props, event) => {
		let _localSymbols = [...localSymbols];
		_localSymbols[props.rowIndex].internal = event.value.name;
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
		let _localSymbols = [...localSymbols];
		_localSymbols[props.rowIndex].synonymScope = event.value;
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

	const onNameTypeEditorValueChange = (props, event) => {
		let _localSymbols = [...localSymbols];
		_localSymbols[props.rowIndex].nameType = event.value;
	};

	const nameTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="nameType"
					options={symbolNameTypeTerms}
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
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={rowsEdited.current === 0}/>
			</div>
		);
	}

	let headerGroup = 
			<ColumnGroup>
				<Row>
					<Column header="Actions" style={{display: isInEdit ? 'visible' : 'none'}}/>
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
			<Dialog visible={dialog} className='w-6' modal onHide={hideDialog} closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
				<h3>Symbol</h3>
				<DataTable value={localSymbols} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
								editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
								bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0' />
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
