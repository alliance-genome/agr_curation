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
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { ControlledVocabularyEditor } from '../../../components/Editors/ControlledVocabularyEditor';
import { Endpoints } from '../../../constants/Endpoints';

export const GermlineTransmissionStatusEditDialog = ({
	originalGermlineTransmissionStatusData,
	setOriginalGermlineTransmissionStatusData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalGermlineTransmissionStatuses, isInEdit, dialog, rowIndex, mainRowProps } =
		originalGermlineTransmissionStatusData;
	const [localGermlineTransmissionStatuses, setLocalGermlineTransmissionStatuses] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		dataKeyCounter.current = 0;
		let _localGermlineTransmissionStatuses = cloneGermlineTransmissionStatuses(originalGermlineTransmissionStatuses);
		setLocalGermlineTransmissionStatuses(_localGermlineTransmissionStatuses);

		let rowsObject = {};
		if (_localGermlineTransmissionStatuses) {
			_localGermlineTransmissionStatuses.forEach((gts) => {
				rowsObject[`${gts.dataKey}`] = true;
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
		setOriginalGermlineTransmissionStatusData((originalGermlineTransmissionStatusData) => {
			return {
				...originalGermlineTransmissionStatusData,
				dialog: false,
			};
		});
		setLocalGermlineTransmissionStatuses([]);
	};

	const cloneGermlineTransmissionStatuses = (clonableGermlineTransmissionStatuses) => {
		let _clonableGermlineTransmissionStatuses = [];
		if (clonableGermlineTransmissionStatuses?.length > 0 && clonableGermlineTransmissionStatuses[0]) {
			_clonableGermlineTransmissionStatuses = structuredClone(clonableGermlineTransmissionStatuses);
			if (_clonableGermlineTransmissionStatuses) {
				_clonableGermlineTransmissionStatuses.forEach((gts) => {
					gts.dataKey = dataKeyCounter.current++;
				});
			}
		}
		return _clonableGermlineTransmissionStatuses;
	};

	const germlineTransmissionStatusOnChangeHandler = (props, event) => {
		props.editorCallback(event.value);
		setLocalGermlineTransmissionStatuses((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], germlineTransmissionStatus: event.value };
			return updated;
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		setLocalGermlineTransmissionStatuses((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], internal: event.target?.value?.name };
			return updated;
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		setFieldValue(event.target.value);
		setLocalGermlineTransmissionStatuses((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], evidence: event.target.value };
			return updated;
		});
	};

	const createNewGermlineTransmissionStatusHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localGermlineTransmissionStatuses = structuredClone(localGermlineTransmissionStatuses);
		_localGermlineTransmissionStatuses.push({
			dataKey: newKey,
			internal: false,
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalGermlineTransmissionStatuses(_localGermlineTransmissionStatuses);
	};

	const handleDeleteGermlineTransmissionStatus = (e, dataKey) => {
		let _localGermlineTransmissionStatuses = structuredClone(localGermlineTransmissionStatuses);
		_localGermlineTransmissionStatuses = _localGermlineTransmissionStatuses.filter((gts) => gts.dataKey !== dataKey);
		setLocalGermlineTransmissionStatuses(_localGermlineTransmissionStatuses);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localGermlineTransmissionStatuses = structuredClone(localGermlineTransmissionStatuses);
		for (const gts of _localGermlineTransmissionStatuses) {
			delete gts.dataKey;
		}
		mainRowProps.rowData.alleleGermlineTransmissionStatus = _localGermlineTransmissionStatuses[0]
			? _localGermlineTransmissionStatuses[0]
			: null;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex].alleleGermlineTransmissionStatus = _localGermlineTransmissionStatuses[0]
			? _localGermlineTransmissionStatuses[0]
			: null;
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localGermlineTransmissionStatuses[0]?.germlineTransmissionStatus?.name ?? null);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]['alleleGermlineTransmissionStatus'] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalGermlineTransmissionStatusData((originalGermlineTransmissionStatusData) => {
			return {
				...originalGermlineTransmissionStatusData,
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

			for (let i = 0; i < localGermlineTransmissionStatuses.length; i++) {
				const _gts = cleanForValidation(localGermlineTransmissionStatuses[i]);
				const result = await validationService.validate(
					Endpoints.SlotAnnotation.ALLELE_GERMLINE_TRANSMISSION_STATUS,
					_gts
				);
				const dataKey = localGermlineTransmissionStatuses[i].dataKey;
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
				<Button
					label="New Germline Transmission Status"
					icon="pi pi-plus"
					onClick={createNewGermlineTransmissionStatusHandler}
					disabled={localGermlineTransmissionStatuses?.length > 0}
				/>
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
				<Column header="Germline Transmission Status" />
				<Column header="Internal" />
				<Column header="Evidence" />
			</Row>
		</ColumnGroup>
	);

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog
				visible={dialog && isInEdit}
				className="w-5"
				modal
				onHide={hideDialog}
				closable={false}
				onShow={showDialogHandler}
				footer={footerTemplate}
			>
				<h3>Germline Transmission Status</h3>
				<DataTable
					value={localGermlineTransmissionStatuses}
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
							<DeleteAction deletionHandler={handleDeleteGermlineTransmissionStatus} id={props?.rowData?.dataKey} />
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
									props={props}
									onChangeHandler={germlineTransmissionStatusOnChangeHandler}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
									rowIndex={props.rowIndex}
									vocabType="allele_germline_transmission_status"
									field="germlineTransmissionStatus"
									showClear={true}
								/>
							);
						}}
						field="germlineTransmissionStatus"
						header="Germline Transmission Status"
						headerClassName="surface-0"
					/>
					<Column
						editor={(props) => {
							return (
								<InternalEditor
									props={props}
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
								<EvidenceEditor
									props={props}
									errorMessages={errorMessages}
									onChange={evidenceOnChangeHandler}
									dataKey={props?.rowData?.dataKey}
								/>
							);
						}}
						field="evidence.curie"
						header="Evidence"
						headerClassName="surface-0"
					/>
				</DataTable>
			</Dialog>
		</div>
	);
};
