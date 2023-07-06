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

export const DatabaseStatusDialog = ({
													originalDatabaseStatusData,
													setOriginalDatabaseStatusData,
													errorMessagesMainRow,
													setErrorMessagesMainRow
												}) => {
	const { originalDatabaseStatuses, isInEdit, dialog, rowIndex, mainRowProps } = originalDatabaseStatusData;
	const [localDatabaseStatuses, setLocalDatabaseStatuses] = useState(null) ;
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState([]);
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const rowsEdited = useRef(0);
	const toast_topright = useRef(null);

	const databaseStatusTerms = useControlledVocabularyService('Allele database status vocabulary');

	const showDialogHandler = () => {
		let _localDatabaseStatuses = cloneDatabaseStatuses(originalDatabaseStatuses);
		setLocalDatabaseStatuses(_localDatabaseStatuses);

		if(isInEdit){
			let rowsObject = {};
			if(_localDatabaseStatuses) {
				_localDatabaseStatuses.forEach((ds) => {
					rowsObject[`${ds.dataKey}`] = true;
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
		let _localDatabaseStatuses = [...localDatabaseStatuses];//add new note support
		if(originalDatabaseStatuses && originalDatabaseStatuses[event.index]){
			let dataKey = _localDatabaseStatuses[event.index].dataKey;
			_localDatabaseStatuses[event.index] = global.structuredClone(originalDatabaseStatuses[event.index]);
			_localDatabaseStatuses[event.index].dataKey = dataKey;
			setLocalDatabaseStatuses(_localDatabaseStatuses);
		}
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages(errorMessagesCopy);
		compareChangesInDatabaseStatuses(event.data,event.index);
	};

	const compareChangesInDatabaseStatuses = (data, index) => {
		if(originalDatabaseStatuses && originalDatabaseStatuses[index]) {
			if (data.internal !== originalDatabaseStatuses[index].internal) {
				rowsEdited.current++;
			}
			if ((originalDatabaseStatuses[index].evidence && !data.evidence) ||
					(!originalDatabaseStatuses[index].evidence && data.evidence) ||
					(data.evidence && (data.evidence.length !== originalDatabaseStatuses[index].evidence.length))
				) {
				rowsEdited.current++;
			} else {
				if (data.evidence) {
					for (var i = 0; i < data.evidence.length; i++) {
						if (data.evidence[i].curie !== originalDatabaseStatuses[index].evidence[i].curie) {
							rowsEdited.current++;
						}
					}
				}
			}
			if ((originalDatabaseStatuses[index].databaseStatus && !data.databaseStatus) ||
					(!originalDatabaseStatuses[index].databaseStatus && data.databaseStatus) ||
					(originalDatabaseStatuses[index].databaseStatus && (originalDatabaseStatuses[index].databaseStatus.name !== data.databaseStatus.name))
				) {
				rowsEdited.current++;
			}
		}
		
		if (localDatabaseStatuses.length > originalDatabaseStatuses?.length || !originalDatabaseStatuses[0]) {
			rowsEdited.current++;
		}
	};

	const onRowEditSave = async(event) => {
		const result = await validateDatabaseStatus(localDatabaseStatuses[event.index]);
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
						detail: 'Could not update AlleleDatabaseStatus [' + localDatabaseStatuses[event.index].id + ']', sticky: false }
					]);
					reported = true;
				}
			});
		} else {
			delete _editingRows[event.index];
			compareChangesInDatabaseStatuses(event.data, event.index);
		}
		setErrorMessages(errorMessagesCopy);
		let _localDatabaseStatuses = [...localDatabaseStatuses];
		_localDatabaseStatuses[event.index] = event.data;
		setEditingRows(_editingRows);
		setLocalDatabaseStatuses(_localDatabaseStatuses);
	};

	const hideDialog = () => {
		setErrorMessages([]);
		setOriginalDatabaseStatusData((originalDatabaseStatusData) => {
			return {
				...originalDatabaseStatusData,
				dialog: false,
			};
		});
		let _localDatabaseStatuses = [];
		setLocalDatabaseStatuses(_localDatabaseStatuses);
	};

	const validateDatabaseStatus = async (gts) => {
		let _gts = global.structuredClone(gts);
		delete _gts.dataKey;
		const result = await validationService.validate('alleledatabasestatusslotannotation', _gts);
		return result;
	};

	const cloneDatabaseStatuses = (clonableDatabaseStatuses) => {
		let _clonableDatabaseStatuses = [];
		if (clonableDatabaseStatuses?.length > 0 && clonableDatabaseStatuses[0]) {
			_clonableDatabaseStatuses = global.structuredClone(clonableDatabaseStatuses);
			if(_clonableDatabaseStatuses) {
				let counter = 0 ;
				_clonableDatabaseStatuses.forEach((gts) => {
					gts.dataKey = counter++;
				});
			}
		} 
		return _clonableDatabaseStatuses;
	};

	const saveDataHandler = () => {
		setErrorMessages([]);
		for (const name of localDatabaseStatuses) {
			delete name.dataKey;
		}
		mainRowProps.rowData.alleleDatabaseStatus = localDatabaseStatuses[0] ? localDatabaseStatuses[0] : null;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].alleleDatabaseStatus = localDatabaseStatuses[0] ? localDatabaseStatuses[0] : null;

		const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: "warn",
			message: "Pending Edits!"
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]["alleleDatabaseStatus"] = messageObject;
		setErrorMessagesMainRow({...errorMessagesCopy});

		setOriginalDatabaseStatusData((originalDatabaseStatusData) => {
				return {
					...originalDatabaseStatusData,
					dialog: false,
				}
			}
		);
	};

	const onDatabaseStatusEditorValueChange = (props, event) => {
		let _localDatabaseStatuses = [...localDatabaseStatuses];
		_localDatabaseStatuses[props.rowIndex].databaseStatus = event.value;
	};

	const databaseStatusEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="databaseStatus"
					options={databaseStatusTerms}
					editorChange={onDatabaseStatusEditorValueChange}
					props={props}
					showClear={true}
					dataKey='id'
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"databaseStatus"} />
			</>
		);
	};

	const databaseStatusTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.databaseStatus?.name}</EllipsisTableCell>;
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
		let _localDatabaseStatuses = [...localDatabaseStatuses];
		_localDatabaseStatuses[props.rowIndex].internal = event.value.name;
	}

	const footerTemplate = () => {
		if (!isInEdit) {
			return null;
		};
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button label="New Database Status" icon="pi pi-plus" onClick={createNewDatabaseStatusHandler} disabled={localDatabaseStatuses?.length > 0}/>
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={rowsEdited.current === 0}/>
			</div>
		);
	}

	const createNewDatabaseStatusHandler = (event) => {
		let cnt = localDatabaseStatuses ? localDatabaseStatuses.length : 0;
		const _localDatabaseStatuses = global.structuredClone(localDatabaseStatuses);
		_localDatabaseStatuses.push({
			dataKey : cnt,
			internal : false
		});
		let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
		setEditingRows(_editingRows);
		setLocalDatabaseStatuses(_localDatabaseStatuses);
	};

	const handleDeleteDatabaseStatus = (event, props) => {
		let _localDatabaseStatuses = global.structuredClone(localDatabaseStatuses);
		if(props.dataKey){
			_localDatabaseStatuses.splice(props.dataKey, 1);
		}else {
			_localDatabaseStatuses.splice(props.rowIndex, 1);
		}
		setLocalDatabaseStatuses(_localDatabaseStatuses);
		rowsEdited.current++;
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={(event) => { handleDeleteDatabaseStatus(event, props) }}/>
		);
	}

	let headerGroup = 
			<ColumnGroup>
				<Row>
					<Column header="Actions" colSpan={2} style={{display: isInEdit ? 'visible' : 'none'}}/>
					<Column header="Database Status" />
					<Column header="Internal" />
					<Column header="Evidence" />
				</Row>
			</ColumnGroup>;

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog visible={dialog} className='w-5' modal onHide={hideDialog} closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
				<h3>Database Status</h3>
				<DataTable value={localDatabaseStatuses} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
								editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
								bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0' />
					<Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem' , display: isInEdit ? 'visible' : 'none'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={databaseStatusEditor} field="databaseStatus" header="Database Status" headerClassName='surface-0' body={databaseStatusTemplate}/>
					<Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
					<Column editor={(props) => evidenceEditorTemplate(props, errorMessages)} field="evidence.curie" header="Evidence" headerClassName='surface-0' body={(rowData) => evidenceTemplate(rowData)}/>
				</DataTable>
			</Dialog>
		</div>
	);
};
