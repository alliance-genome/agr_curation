import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { ValidationService } from '../../../service/ValidationService';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { InternalEditor } from '../../../components/Editors/legacyForm/InternalEditor';
import { ObsoleteEditor } from '../../../components/Editors/legacyForm/ObsoleteEditor';
import { EvidenceEditor } from '../../../components/Editors/legacyForm/EvidenceEditor';
import { ControlledVocabularyEditor } from '../../../components/Editors/legacyForm/ControlledVocabularyEditor';
import { Endpoints } from '../../../constants/Endpoints';

export const NomenclatureEventsEditDialog = ({
	originalNomenclatureEventsData,
	setOriginalNomenclatureEventsData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalNomenclatureEvents, isInEdit, dialog, rowIndex, mainRowProps } = originalNomenclatureEventsData;
	const [localNomenclatureEvents, setLocalNomenclatureEvents] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		let _localNomenclatureEvents = cloneNomenclatureEvents(originalNomenclatureEvents);
		setLocalNomenclatureEvents(_localNomenclatureEvents);

		let rowsObject = {};
		if (_localNomenclatureEvents) {
			_localNomenclatureEvents.forEach((ne) => {
				rowsObject[`${ne.dataKey}`] = true;
			});
		}
		setEditingRows(rowsObject);
		setErrorMessages({});
	};

	const onRowEditChange = () => {
		return null;
	};

	const hideDialog = () => {
		setErrorMessages({});
		setOriginalNomenclatureEventsData((originalNomenclatureEventsData) => {
			return {
				...originalNomenclatureEventsData,
				dialog: false,
			};
		});
		setLocalNomenclatureEvents([]);
	};

	const cloneNomenclatureEvents = (clonableNomenclatureEvents) => {
		let _clonableNomenclatureEvents = structuredClone(clonableNomenclatureEvents);
		if (_clonableNomenclatureEvents) {
			_clonableNomenclatureEvents.forEach((ne) => {
				ne.dataKey = dataKeyCounter.current++;
			});
		} else {
			_clonableNomenclatureEvents = [];
		}
		return _clonableNomenclatureEvents;
	};

	const nomenclatureEventOnChangeHandler = (props, event) => {
		props.editorCallback(event.value);
		setLocalNomenclatureEvents((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], nomenclatureEvent: event.value };
			return updated;
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		setLocalNomenclatureEvents((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], internal: event.target?.value?.name };
			return updated;
		});
	};

	const obsoleteOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		setLocalNomenclatureEvents((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], obsolete: event.target?.value?.name };
			return updated;
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		setFieldValue(event.target.value);
		setLocalNomenclatureEvents((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], evidence: event.target.value };
			return updated;
		});
	};

	const createNewNomenclatureEventHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localNomenclatureEvents = structuredClone(localNomenclatureEvents);
		_localNomenclatureEvents.push({
			dataKey: newKey,
			internal: false,
			obsolete: false,
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalNomenclatureEvents(_localNomenclatureEvents);
	};

	const handleDeleteNomenclatureEvent = (e, dataKey) => {
		let _localNomenclatureEvents = structuredClone(localNomenclatureEvents);
		_localNomenclatureEvents = _localNomenclatureEvents.filter((ne) => ne.dataKey !== dataKey);
		setLocalNomenclatureEvents(_localNomenclatureEvents);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localNomenclatureEvents = structuredClone(localNomenclatureEvents);
		for (const ne of _localNomenclatureEvents) {
			delete ne.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localNomenclatureEvents);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]['alleleNomenclatureEvents'] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalNomenclatureEventsData((originalNomenclatureEventsData) => {
			return {
				...originalNomenclatureEventsData,
				dialog: false,
			};
		});
	};

	const cleanForValidation = (obj) => {
		const _obj = structuredClone(obj);
		delete _obj.dataKey;
		delete _obj.updatedBy;
		delete _obj.createdBy;
		delete _obj.dateUpdated;
		delete _obj.dateCreated;
		if (_obj.evidence) {
			_obj.evidence.forEach((ref) => {
				delete ref.updatedBy;
				delete ref.createdBy;
				delete ref.dateUpdated;
				delete ref.dateCreated;
			});
		}
		return _obj;
	};

	const validateAndSave = async () => {
		setIsValidating(true);
		try {
			let hasErrors = false;
			const newErrorMessages = {};

			for (let i = 0; i < localNomenclatureEvents.length; i++) {
				const _ne = cleanForValidation(localNomenclatureEvents[i]);
				const result = await validationService.validate(Endpoints.SlotAnnotation.ALLELE_NOMENCLATURE_EVENT, _ne);
				const dataKey = localNomenclatureEvents[i].dataKey;
				if (result.isError) {
					hasErrors = true;
					newErrorMessages[dataKey] = {};
					if (result.data) {
						Object.keys(result.data).forEach((field) => {
							newErrorMessages[dataKey][field] = {
								severity: 'error',
								message: result.data[field],
							};
						});
					}
				}
			}

			if (hasErrors) {
				setErrorMessages(newErrorMessages);
				toast_topright.current.show([
					{
						life: 7000,
						severity: 'error',
						summary: 'Validation error',
						detail: 'Please fix validation errors before saving',
						sticky: false,
					},
				]);
			} else {
				saveDataHandler();
			}
		} finally {
			setIsValidating(false);
		}
	};

	const footerTemplate = () => {
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button label="New Nomenclature Event" icon="pi pi-plus" onClick={createNewNomenclatureEventHandler} />
				<Button
					label="Keep Edits"
					icon={isValidating ? 'pi pi-spin pi-spinner' : 'pi pi-check'}
					onClick={validateAndSave}
					disabled={isValidating}
				/>
			</div>
		);
	};

	let headerGroup = (
		<ColumnGroup>
			<Row>
				<Column header="Actions" />
				<Column header="Nomenclature Event" />
				<Column header="Evidence" />
				<Column header="Internal" />
				<Column header="Obsolete" />
				<Column header="Updated By" />
				<Column header="Date Updated" />
				<Column header="Created By" />
				<Column header="Date Created" />
			</Row>
		</ColumnGroup>
	);

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog
				visible={dialog && isInEdit}
				className="w-10"
				modal
				onHide={hideDialog}
				closable={false}
				onShow={showDialogHandler}
				footer={footerTemplate}
			>
				<h3>Nomenclature Events</h3>
				<DataTable
					value={localNomenclatureEvents}
					dataKey="dataKey"
					showGridlines
					editMode="row"
					headerColumnGroup={headerGroup}
					editingRows={editingRows}
					onRowEditChange={onRowEditChange}
					ref={tableRef}
					cellMemo={false}
				>
					<Column
						editor={(props) => (
							<DeleteAction deletionHandler={handleDeleteNomenclatureEvent} id={props?.rowData?.dataKey} />
						)}
						className="max-w-4rem"
						bodyClassName="text-center"
						headerClassName="surface-0"
						frozen
					/>
					<Column
						editor={(props) => {
							return (
								<ControlledVocabularyEditor
									editorOptions={props}
									onChangeHandler={nomenclatureEventOnChangeHandler}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
									rowIndex={props.rowIndex}
									vocabType="allele_nomenclature_event"
									field="nomenclatureEvent"
									showClear={false}
								/>
							);
						}}
						field="nomenclatureEvent"
						header="Nomenclature Event"
						headerClassName="surface-0"
					/>
					<Column
						editor={(props) => {
							return (
								<EvidenceEditor
									props={props}
									errorMessages={errorMessages}
									onChange={evidenceOnChangeHandler}
									dataKey={props?.rowData?.dataKey}
								/>
							);
						}}
						field="evidence"
						header="Evidence"
						headerClassName="surface-0"
					/>
					<Column
						editor={(props) => {
							return (
								<InternalEditor
									editorOptions={props}
									rowIndex={props.rowIndex}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
									internalOnChangeHandler={internalOnChangeHandler}
								/>
							);
						}}
						field="internal"
						header="Internal"
						headerClassName="surface-0"
					/>
					<Column
						editor={(props) => {
							return (
								<ObsoleteEditor
									editorOptions={props}
									obsoleteOnChangeHandler={obsoleteOnChangeHandler}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
								/>
							);
						}}
						field="obsolete"
						header="Obsolete"
						headerClassName="surface-0"
					/>
					<Column field="updatedBy.uniqueId" header="Updated By" headerClassName="surface-0" />
					<Column field="dateUpdated" header="Date Updated" headerClassName="surface-0" />
					<Column field="createdBy.uniqueId" header="Created By" headerClassName="surface-0" />
					<Column field="dateCreated" header="Date Created" headerClassName="surface-0" />
				</DataTable>
			</Dialog>
		</div>
	);
};
