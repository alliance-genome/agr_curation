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
import { TableInputTextEditor } from '../../../components/Editors/TableInputTextEditor';
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { Endpoints } from '../../../constants/Endpoints';

export const SecondaryIdsEditDialog = ({
	originalSecondaryIdsData,
	setOriginalSecondaryIdsData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalSecondaryIds, isInEdit, dialog, rowIndex, mainRowProps } = originalSecondaryIdsData;
	const [localSecondaryIds, setLocalSecondaryIds] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		let _localSecondaryIds = cloneSecondaryIds(originalSecondaryIds);
		setLocalSecondaryIds(_localSecondaryIds);

		let rowsObject = {};
		if (_localSecondaryIds) {
			_localSecondaryIds.forEach((sid) => {
				rowsObject[`${sid.dataKey}`] = true;
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
		setOriginalSecondaryIdsData((originalSecondaryIdsData) => {
			return {
				...originalSecondaryIdsData,
				dialog: false,
			};
		});
		setLocalSecondaryIds([]);
	};

	const cloneSecondaryIds = (clonableSecondaryIds) => {
		let _clonableSecondaryIds = structuredClone(clonableSecondaryIds);
		if (_clonableSecondaryIds) {
			_clonableSecondaryIds.forEach((sid) => {
				sid.dataKey = dataKeyCounter.current++;
			});
		} else {
			_clonableSecondaryIds = [];
		}
		return _clonableSecondaryIds;
	};

	const textOnChangeHandler = (rowIndex, event, field) => {
		setLocalSecondaryIds((prev) => {
			const updated = [...prev];
			updated[rowIndex] = { ...updated[rowIndex], [field]: event.target.value };
			return updated;
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		setLocalSecondaryIds((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], internal: event.target?.value?.name };
			return updated;
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		setFieldValue(event.target.value);
		setLocalSecondaryIds((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], evidence: event.target.value };
			return updated;
		});
	};

	const createNewSecondaryIdHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localSecondaryIds = structuredClone(localSecondaryIds);
		_localSecondaryIds.push({
			dataKey: newKey,
			internal: false,
			secondaryId: '',
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalSecondaryIds(_localSecondaryIds);
	};

	const handleDeleteSecondaryId = (e, dataKey) => {
		let _localSecondaryIds = structuredClone(localSecondaryIds);
		_localSecondaryIds = _localSecondaryIds.filter((sid) => sid.dataKey !== dataKey);
		setLocalSecondaryIds(_localSecondaryIds);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localSecondaryIds = structuredClone(localSecondaryIds);
		for (const sid of _localSecondaryIds) {
			delete sid.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localSecondaryIds);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]['alleleSecondaryIds'] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalSecondaryIdsData((originalSecondaryIdsData) => {
			return {
				...originalSecondaryIdsData,
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

			for (let i = 0; i < localSecondaryIds.length; i++) {
				const _sid = cleanForValidation(localSecondaryIds[i]);
				const result = await validationService.validate(Endpoints.SlotAnnotation.ALLELE_SECONDARY_ID, _sid);
				const dataKey = localSecondaryIds[i].dataKey;
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
				<Button label="New Secondary ID" icon="pi pi-plus" onClick={createNewSecondaryIdHandler} />
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
				<Column header="Secondary ID" />
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
				className="w-6"
				modal
				onHide={hideDialog}
				closable={false}
				onShow={showDialogHandler}
				footer={footerTemplate}
			>
				<h3>Secondary IDs</h3>
				<DataTable
					value={localSecondaryIds}
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
						editor={(props) => <DeleteAction deletionHandler={handleDeleteSecondaryId} id={props?.rowData?.dataKey} />}
						className="max-w-4rem"
						bodyClassName="text-center"
						headerClassName="surface-0"
						frozen
					/>
					<Column
						editor={(props) => {
							return (
								<TableInputTextEditor
									value={props.value}
									rowIndex={props.rowIndex}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
									textOnChangeHandler={textOnChangeHandler}
									editorCallback={props.editorCallback}
									field="secondaryId"
								/>
							);
						}}
						field="secondaryId"
						header="Secondary ID"
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
				</DataTable>
			</Dialog>
		</div>
	);
};
