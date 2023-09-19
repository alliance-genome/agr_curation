import React, { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DialogErrorMessageComponent } from '../../components/Error/DialogErrorMessageComponent';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ValidationService } from '../../service/ValidationService';
import { evidenceTemplate, evidenceEditorTemplate } from '../../components/EvidenceComponent';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';

export const NomenclatureEventsDialog = ({
													originalNomenclatureEventsData,
													setOriginalNomenclatureEventsData,
													errorMessagesMainRow,
													setErrorMessagesMainRow
												}) => {
	const { originalNomenclatureEvents, isInEdit, dialog, rowIndex, mainRowProps } = originalNomenclatureEventsData;
	const [localNomenclatureEvents, setLocalNomenclatureEvents] = useState(null) ;
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState([]);
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const rowsEdited = useRef(0);
	const toast_topright = useRef(null);

	const nomenclatureEventTerms = useControlledVocabularyService('allele_nomenclature_event');

	const showDialogHandler = () => {
		let _localNomenclatureEvents = cloneNomenclatureEvents(originalNomenclatureEvents);
		setLocalNomenclatureEvents(_localNomenclatureEvents);

		if(isInEdit){
			let rowsObject = {};
			if(_localNomenclatureEvents) {
				_localNomenclatureEvents.forEach((ne) => {
					rowsObject[`${ne.dataKey}`] = true;
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
		let _localNomenclatureEvents = [...localNomenclatureEvents];//add new note support
		if(originalNomenclatureEvents && originalNomenclatureEvents[event.index]){
			let dataKey = _localNomenclatureEvents[event.index].dataKey;
			_localNomenclatureEvents[event.index] = global.structuredClone(originalNomenclatureEvents[event.index]);
			_localNomenclatureEvents[event.index].dataKey = dataKey;
			setLocalNomenclatureEvents(_localNomenclatureEvents);
		}
		const errorMessagesCopy = errorMessages;
		errorMessagesCopy[event.index] = {};
		setErrorMessages(errorMessagesCopy);
		compareChangesInNomenclatureEvents(event.data,event.index);
	};

	const compareChangesInNomenclatureEvents = (data,index) => {
		if(originalNomenclatureEvents && originalNomenclatureEvents[index]) {
			if (data.internal !== originalNomenclatureEvents[index].internal) {
				rowsEdited.current++;
			}
			if (data.nomenclatureEvent.name !== originalNomenclatureEvents[index].nomenclatureEvent.name) {
				rowsEdited.current++;
			}
			if ((originalNomenclatureEvents[index].evidence && !data.evidence) ||
				(!originalNomenclatureEvents[index].evidence && data.evidence) ||
				(data.evidence && (data.evidence.length !== originalNomenclatureEvents[index].evidence.length))) {
				rowsEdited.current++;
			} else {
				if (data.evidence) {
					for (var j = 0; j < data.evidence.length; j++) {
						if (data.evidence[j].curie !== originalNomenclatureEvents[index].evidence[j].curie) {
							rowsEdited.current++;
						}
					}
				}
			}
		}

		if((localNomenclatureEvents.length > originalNomenclatureEvents?.length) || !originalNomenclatureEvents){
			rowsEdited.current++;
		}
	};

	const onRowEditSave = async(event) => {
		const result = await validateNomenclatureEvent(localNomenclatureEvents[event.index]);
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
						detail: 'Could not update AlleleNomenclatureEvent [' + localNomenclatureEvents[event.index].id + ']', sticky: false }
					]);
					reported = true;
				}
			});
		} else {
			delete _editingRows[event.index];
			compareChangesInNomenclatureEvents(event.data,event.index);
		}
		setErrorMessages(errorMessagesCopy);
		let _localNomenclatureEvents = [...localNomenclatureEvents];
		_localNomenclatureEvents[event.index] = event.data;
		setEditingRows(_editingRows);
		setLocalNomenclatureEvents(_localNomenclatureEvents);
	};

	const hideDialog = () => {
		setErrorMessages([]);
		setOriginalNomenclatureEventsData((originalNomenclatureEventsData) => {
			return {
				...originalNomenclatureEventsData,
				dialog: false,
			};
		});
		let _localNomenclatureEvents = [];
		setLocalNomenclatureEvents(_localNomenclatureEvents);
	};

	const validateNomenclatureEvent = async (ne) => {
		let _ne = global.structuredClone(ne);
		delete _ne.dataKey;
		const result = await validationService.validate('allelenomenclatureeventslotannotation', _ne);
		return result;
	};

	const cloneNomenclatureEvents = (clonableNomenclatureEvents) => {
		let _clonableNomenclatureEvents = global.structuredClone(clonableNomenclatureEvents);
		if(_clonableNomenclatureEvents) {
			let counter = 0 ;
			_clonableNomenclatureEvents.forEach((ne) => {
				ne.dataKey = counter++;
			});
		} else {
			_clonableNomenclatureEvents = [];
		};
		return _clonableNomenclatureEvents;
	};

	const saveDataHandler = () => {
		setErrorMessages([]);
		for (const ne of localNomenclatureEvents) {
			delete ne.dataKey;
		}
		mainRowProps.rowData.alleleNomenclatureEvents = localNomenclatureEvents;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].alleleNomenclatureEvents = localNomenclatureEvents;

		const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: "warn",
			message: "Pending Edits!"
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]["alleleNomenclatureEvents"] = messageObject;
		setErrorMessagesMainRow({...errorMessagesCopy});

		setOriginalNomenclatureEventsData((originalNomenclatureEventsData) => {
				return {
					...originalNomenclatureEventsData,
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
		let _localNomenclatureEvents = [...localNomenclatureEvents];
		_localNomenclatureEvents[props.rowIndex].internal = event.value.name;
	}

	const obsoleteTemplate = (rowData) => {
		return <EllipsisTableCell>{JSON.stringify(rowData.obsolete)}</EllipsisTableCell>;
	};

	const obsoleteEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onObsoleteEditorValueChange}
					props={props}
					field={"obsolete"}
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"obsolete"} />
			</>
		);
	};
	
	const onObsoleteEditorValueChange = (props, event) => {
		let _localNomenclatureEvents = [...localNomenclatureEvents];
		_localNomenclatureEvents[props.rowIndex].obsolete = event.value.name;
	}

	const onNomenclatureEventEditorValueChange = (props, event) => {
		let _localNomenclatureEvents = [...localNomenclatureEvents];
		_localNomenclatureEvents[props.rowIndex].nomenclatureEvent = event.value;
	};

	const nomenclatureEventEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="nomenclatureEvent"
					options={nomenclatureEventTerms}
					editorChange={onNomenclatureEventEditorValueChange}
					props={props}
					showClear={false}
					dataKey='id'
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"nomenclatureEvent"} />
			</>
		);
	};

	const nomenclatureEventTemplate = (rowData) => {
		return <EllipsisTableCell>{rowData.nomenclatureEvent?.name}</EllipsisTableCell>;
	};

	const footerTemplate = () => {
		if (!isInEdit) {
			return null;
		};
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button label="New Nomenclature Event" icon="pi pi-plus" onClick={createNewNomenclatureEventHandler}/>
				<Button label="Keep Edits" icon="pi pi-check" onClick={saveDataHandler} disabled={rowsEdited.current === 0}/>
			</div>
		);
	}

	const createNewNomenclatureEventHandler = (event) => {
		let cnt = localNomenclatureEvents ? localNomenclatureEvents.length : 0;
		const _localNomenclatureEvents = global.structuredClone(localNomenclatureEvents);
		_localNomenclatureEvents.push({
			dataKey : cnt,
			internal : false,
		});
		let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
		setEditingRows(_editingRows);
		setLocalNomenclatureEvents(_localNomenclatureEvents);
	};

	const handleDeleteNomenclatureEvent = (event, props) => {
		let _localNomenclatureEvents = global.structuredClone(localNomenclatureEvents);
		if(props.dataKey){
			_localNomenclatureEvents.splice(props.dataKey, 1);
		}else {
			_localNomenclatureEvents.splice(props.rowIndex, 1);
		}
		setLocalNomenclatureEvents(_localNomenclatureEvents);
		rowsEdited.current++;
	}

	const deleteAction = (props) => {
		return (
			<Button icon="pi pi-trash" className="p-button-text"
					onClick={(event) => { handleDeleteNomenclatureEvent(event, props) }}/>
		);
	}

	let headerGroup = 	<ColumnGroup>
							<Row>
								<Column header="Actions" colSpan={2} style={{display: isInEdit ? 'visible' : 'none'}}/>
								<Column header="Nomenclature Event" />
								<Column header="Evidence" />
								<Column header="Internal" />
								<Column header="Obsolete" />
								<Column header="Updated By" />
								<Column header="Date Updated" />
								<Column header="Created By" />
								<Column header="Date Created" />
							</Row>
						</ColumnGroup>;

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog visible={dialog} className='w-10' modal onHide={hideDialog} closable={!isInEdit} onShow={showDialogHandler} footer={footerTemplate}>
				<h3>Nomenclature Events</h3>
				<DataTable value={localNomenclatureEvents} dataKey="dataKey" showGridlines editMode='row' headerColumnGroup={headerGroup} 
						editingRows={editingRows} onRowEditChange={onRowEditChange} ref={tableRef} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}>
					<Column rowEditor={isInEdit} style={{maxWidth: '7rem', display: isInEdit ? 'visible' : 'none'}} headerStyle={{width: '7rem', position: 'sticky'}}
							bodyStyle={{textAlign: 'center'}} frozen headerClassName='surface-0' />
					<Column editor={(props) => deleteAction(props)} body={(props) => deleteAction(props)} style={{ maxWidth: '4rem' , display: isInEdit ? 'visible' : 'none'}} frozen headerClassName='surface-0' bodyStyle={{textAlign: 'center'}}/>
					<Column editor={(props) => nomenclatureEventEditor(props)} field="nomenclatureEvent.name" header="Nomenclature Event" headerClassName='surface-0' body={nomenclatureEventTemplate}/>
					<Column editor={(props) => evidenceEditorTemplate(props, errorMessages)} field="evidence.curie" header="Evidence" headerClassName='surface-0' body={(rowData) => evidenceTemplate(rowData)}/>
					<Column editor={internalEditor} field="internal" header="Internal" body={internalTemplate} headerClassName='surface-0'/>
					<Column editor={obsoleteEditor} field="obsolete" header="Obsolete" body={obsoleteTemplate} headerClassName='surface-0'/>
					<Column field="updatedBy.uniqueId" header="Updated By" headerClassName='surface-0'/>
					<Column field="dateUpdated" header="Date Updated" headerClassName='surface-0'/>
					<Column field="createdBy.uniqueId" header="Created By" headerClassName='surface-0'/>
					<Column field="dateCreated" header="Date Created" headerClassName='surface-0'/>
				</DataTable>
			</Dialog>
		</div>
	);
};
