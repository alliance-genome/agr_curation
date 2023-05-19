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
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ValidationService } from '../../service/ValidationService';
import { evidenceTemplate, evidenceEditorTemplate } from '../../components/EvidenceComponent';

export const GermlineTransmissionStatusDialog = ({
													originalGermlineTransmissionStatusData,
													setOriginalGermlineTransmissionStatusData,
													errorMessagesMainRow,
													setErrorMessagesMainRow
												}) => {
	const { originalGermlineTransmissionStatuses, isInEdit, dialog, rowIndex, mainRowProps } = originalGermlineTransmissionStatusData;
	const [localGermlineTransmissionStatuses, setLocalGermlineTransmissionStatuses] = useState(null) ;
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState([]);
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const rowsEdited = useRef(0);
	const toast_topright = useRef(null);

	const germlineTransmissionStatusTerms = useControlledVocabularyService('Allele Germline Transmission Status');

	const showDialogHandler = () => {
		let _localGermlineTransmissionStatuses = cloneGermlineTransmissionStatuses(originalGermlineTransmissionStatuses);
		setLocalGermlineTransmissionStatuses(_localGermlineTransmissionStatuses);

		if(isInEdit){
			let rowsObject = {};
			if(_localGermlineTransmissionStatuses) {
				_localGermlineTransmissionStatuses.forEach((gst) => {
					rowsObject[`${gst.dataKey}`] = true;
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
		let _localGermlineTransmissionStatuses = [...localGermlineTransmissionStatuses];//add new note support
		if(originalGermlineTransmissionStatuses && originalGermlineTransmissionStatuses[event.index]){
			let dataKey = _localGermlineTransmissionStatuses[event.index].dataKey;
			_localGermlineTransmissionStatuses[event.index] = global.structuredClone(originalGermlineTransmissionStatuses[event.index]);
			_localGermlineTransmissionStatuses[event.index].dataKey = dataKey;
			setLocalGermlineTransmissionStatuses(_localGermlineTransmissionStatuses);
		}
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages(errorMessagesCopy);
		compareChangesInGermlineTransmissionStatuses(event.data,event.index);
	};

	const compareChangesInGermlineTransmissionStatuses = (data, index) => {
		if(originalGermlineTransmissionStatuses && originalGermlineTransmissionStatuses[index]) {
			if ((originalGermlineTransmissionStatuses[index].evidence && !data.evidence) ||
					(!originalGermlineTransmissionStatuses[index].evidence && data.evidence) ||
					(data.evidence && (data.evidence.length !== originalGermlineTransmissionStatuses[index].evidence.length))
				) {
				rowsEdited.current++;
			} else {
				if (data.evidence) {
					for (var i = 0; i < data.evidence.length; i++) {
						if (data.evidence[i].curie !== originalGermlineTransmissionStatuses[index].evidence[i].curie) {
							rowsEdited.current++;
						}
					}
				}
			}
			if ((originalGermlineTransmissionStatuses[index].germlineTransmissionStatus && !data.germlineTransmissionStatus) ||
					(!originalGermlineTransmissionStatuses[index].germlineTransmissionStatus && data.germlineTransmissionStatus) ||
					(originalGermlineTransmissionStatuses[index].germlineTransmissionStatus && (originalGermlineTransmissionStatuses[index].germlineTransmissionStatus.name !== data.germlineTransmissionStatus.name))
				) {
				rowsEdited.current++;
			}
		}
		
		if (localGermlineTransmissionStatuses.length > originalGermlineTransmissionStatuses?.length || !originalGermlineTransmissionStatuses[0]) {
			rowsEdited.current++;
		}
	};

	const onRowEditSave = async(event) => {
		const result = await validateGermlineTransmissionStatus(localGermlineTransmissionStatuses[event.index]);
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
						detail: 'Could not update AlleleGermlineTransmissionStatus [' + localGermlineTransmissionStatuses[event.index].id + ']', sticky: false }
					]);
					reported = true;
				}
			});
		} else {
			delete _editingRows[event.index];
			compareChangesInGermlineTransmissionStatuses(event.data, event.index);
		}
		setErrorMessages(errorMessagesCopy);
		let _localGermlineTransmissionStatuses = [...localGermlineTransmissionStatuses];
		_localGermlineTransmissionStatuses[event.index] = event.data;
		setEditingRows(_editingRows);
		setLocalGermlineTransmissionStatuses(_localGermlineTransmissionStatuses);
	};

	const hideDialog = () => {
		setErrorMessages([]);
		setOriginalGermlineTransmissionStatusData((originalGermlineTransmissionStatusData) => {
			return {
				...originalGermlineTransmissionStatusData,
				dialog: false,
			};
		});
		let _localGermlineTransmissionStatuses = [];
		setLocalGermlineTransmissionStatuses(_localGermlineTransmissionStatuses);
	};

	const validateGermlineTransmissionStatus = async (gts) => {
		let _gts = global.structuredClone(gts);
		delete _gts.dataKey;
		const result = await validationService.validate('allelegermlinetransmissionstatusslotannotation', _gts);
		return result;
	};

	const cloneGermlineTransmissionStatuses = (clonableGermlineTransmissionStatuses) => {
		let _clonableGermlineTransmissionStatuses = [];
		if (clonableGermlineTransmissionStatuses?.length > 0 && clonableGermlineTransmissionStatuses[0]) {
			_clonableGermlineTransmissionStatuses = global.structuredClone(clonableGermlineTransmissionStatuses);
			if(_clonableGermlineTransmissionStatuses) {
				let counter = 0 ;
				_clonableGermlineTransmissionStatuses.forEach((gts) => {
					gts.dataKey = counter++;
				});
			}
		} 
		return _clonableGermlineTransmissionStatuses;
	};

	const saveDataHandler = () => {
		setErrorMessages([]);
		for (const name of localGermlineTransmissionStatuses) {
			delete name.dataKey;
		}
		mainRowProps.rowData.alleleGermlineTransmissionStatus = localGermlineTransmissionStatuses[0] ? localGermlineTransmissionStatuses[0] : null;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].alleleGermlineTransmissionStatus = localGermlineTransmissionStatuses[0] ? localGermlineTransmissionStatuses[0] : null;

		const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: "warn",
			message: "Pending Edits!"
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]["alleleGermlineTransmissionStatus"] = messageObject;
		setErrorMessagesMainRow({...errorMessagesCopy});

		setOriginalGermlineTransmissionStatusData((originalGermlineTransmissionStatusData) => {
				return {
					...originalGermlineTransmissionStatusData,
					dialog: false,
				}
			}
		);
	};

	const onGermlineTransmissionStatusEditorValueChange = (props, event) => {
		let _localGermlineTransmissionStatuses = [...localGermlineTransmissionStatuses];
		_localGermlineTransmissionStatuses[props.rowIndex].germlineTransmissionStatus = event.value;
	};

	const germlineTransmissionStatusEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="germlineTransmissionStatus"
					options={germlineTransmissionStatusTerms}
					editorChange={onGermlineTransmissionStatusEditorValueChange}
					props={props}
					showClear={true}
					dataKey='id'
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"germlineTransmissionStatus"} />
			</>
		);
	};

	const germlineTransmissionStatusTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.germlineTransmissionStatus?.name}</EllipsisTableCell>;
	};

	const footerTemplate = () => {
		if (!isInEdit) {
			return null;
		};
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button label="New Germline Transmission Status" icon="pi pi-plus" onClick={createNewGermlineTransmissionStatusHandler} disabled={localGermlineTransmissionStatuses?.length > 0}/>
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={rowsEdited.current === 0}/>
			</div>
		);
	}

	const createNewGermlineTransmissionStatusHandler = (event) => {
		let cnt = localGermlineTransmissionStatuses ? localGermlineTransmissionStatuses.length : 0;
		const _localGermlineTransmissionStatuses = global.structuredClone(localGermlineTransmissionStatuses);
		_localGermlineTransmissionStatuses.push({
			dataKey : cnt,
			internal : false
		});
		let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
		setEditingRows(_editingRows);
		setLocalGermlineTransmissionStatuses(_localGermlineTransmissionStatuses);
	};

	const handleDeleteGermlineTransmissionStatus = (event, props) => {
		let _localGermlineTransmissionStatuses = global.structuredClone(localGermlineTransmissionStatuses);
		if(props.dataKey){
			_localGermlineTransmissionStatuses.splice(props.dataKey, 1);
		}else {
			_localGermlineTransmissionStatuses.splice(props.rowIndex, 1);
		}
		setLocalGermlineTransmissionStatuses(_localGermlineTransmissionStatuses);
		rowsEdited.current++;
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={(event) => { handleDeleteGermlineTransmissionStatus(event, props) }}/>
		);
	}

	let headerGroup = 
			<ColumnGroup>
				<Row>
					<Column header="Actions" colSpan={2} style={{display: isInEdit ? 'visible' : 'none'}}/>
					<Column header="Germline Transmission Status" />
					<Column header="Evidence" />
				</Row>
			</ColumnGroup>;

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog visible={dialog} className='w-5' modal onHide={hideDialog} closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate} resizable>
				<h3>Germline Transmission Status</h3>
				<DataTable value={localGermlineTransmissionStatuses} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup}
								editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
								bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0' />
					<Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem' , display: isInEdit ? 'visible' : 'none'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={germlineTransmissionStatusEditor} field="germlineTransmissionStatus" header="Germline Transmission Status" headerClassName='surface-0' body={germlineTransmissionStatusTemplate}/>
					<Column editor={(props) => evidenceEditorTemplate(props, errorMessages)} field="evidence.curie" header="Evidence" headerClassName='surface-0' body={(rowData) => evidenceTemplate(rowData)}/>
				</DataTable>
			</Dialog>
		</div>
	);
};
