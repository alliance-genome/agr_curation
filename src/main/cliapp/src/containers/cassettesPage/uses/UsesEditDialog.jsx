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
import { EvidenceEditor } from '../../../components/Editors/legacyForm/EvidenceEditor';
import { UsesEditor } from './UsesEditor';
import { Endpoints } from '../../../constants/Endpoints';
import { multipleAutocompleteOnChange } from '../../../utils/utils';

// The uses of one cassette, edited in a pop-up from the Cassettes table: FBcv terms from the
// 'experimental_tool_descriptor' namespace with their evidence. Adapted from MutationTypesEditDialog.
export const UsesEditDialog = ({
	originalUsesData,
	setOriginalUsesData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalUses, isInEdit, dialog, rowIndex, mainRowProps } = originalUsesData;
	const [localUses, setLocalUses] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		let _localUses = cloneUses(originalUses);
		setLocalUses(_localUses);

		let rowsObject = {};
		if (_localUses) {
			_localUses.forEach((item) => {
				rowsObject[`${item.dataKey}`] = true;
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
		setOriginalUsesData((originalUsesData) => {
			return {
				...originalUsesData,
				dialog: false,
			};
		});
		setLocalUses([]);
	};

	const cloneUses = (clonableUses) => {
		let _clonableUses = structuredClone(clonableUses);
		if (_clonableUses) {
			_clonableUses.forEach((item) => {
				item.dataKey = dataKeyCounter.current++;
			});
		} else {
			_clonableUses = [];
		}
		return _clonableUses;
	};

	const onUseValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, 'uses', setFieldValue);
		setLocalUses((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], uses: event.target.value };
			return updated;
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		setLocalUses((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], internal: event.target?.value?.name };
			return updated;
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		setFieldValue(event.target.value);
		setLocalUses((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], evidence: event.target.value };
			return updated;
		});
	};

	const createNewUseHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localUses = structuredClone(localUses);
		_localUses.push({
			dataKey: newKey,
			internal: false,
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalUses(_localUses);
	};

	const handleDeleteUse = (e, dataKey) => {
		let _localUses = structuredClone(localUses);
		_localUses = _localUses.filter((item) => item.dataKey !== dataKey);
		setLocalUses(_localUses);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localUses = structuredClone(localUses);
		for (const item of _localUses) {
			delete item.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localUses);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]['cassetteUses'] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalUsesData((originalUsesData) => {
			return {
				...originalUsesData,
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

			for (let i = 0; i < localUses.length; i++) {
				const _item = cleanForValidation(localUses[i]);
				const result = await validationService.validate(Endpoints.SlotAnnotation.CASSETTE_USE, _item);
				const dataKey = localUses[i].dataKey;
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
				<Button label="New Use" icon="pi pi-plus" onClick={createNewUseHandler} />
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
				<Column header="Uses" />
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
				<h3>Uses</h3>
				<DataTable
					value={localUses}
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
						editor={(props) => <DeleteAction deletionHandler={handleDeleteUse} id={props?.rowData?.dataKey} />}
						className="max-w-4rem"
						bodyClassName="text-center"
						headerClassName="surface-0"
						frozen
					/>
					<Column
						editor={(props) => {
							return (
								<UsesEditor
									props={props}
									errorMessages={errorMessages}
									onChange={onUseValueChange}
									dataKey={props?.rowData?.dataKey}
								/>
							);
						}}
						field="uses"
						header="Uses"
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
