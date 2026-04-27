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
import { MutationTypesEditor } from '../../../components/Editors/MutationTypesEditor';
import { Endpoints } from '../../../constants/Endpoints';
import { multipleAutocompleteOnChange } from '../../../utils/utils';

export const MutationTypesEditDialog = ({
	originalMutationTypesData,
	setOriginalMutationTypesData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalMutationTypes, isInEdit, dialog, rowIndex, mainRowProps } = originalMutationTypesData;
	const [localMutationTypes, setLocalMutationTypes] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const showDialogHandler = () => {
		let _localMutationTypes = cloneMutationTypes(originalMutationTypes);
		setLocalMutationTypes(_localMutationTypes);

		let rowsObject = {};
		if (_localMutationTypes) {
			_localMutationTypes.forEach((mt) => {
				rowsObject[`${mt.dataKey}`] = true;
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
		setOriginalMutationTypesData((originalMutationTypesData) => {
			return {
				...originalMutationTypesData,
				dialog: false,
			};
		});
		setLocalMutationTypes([]);
	};

	const cloneMutationTypes = (clonableMutationTypes) => {
		let _clonableMutationTypes = structuredClone(clonableMutationTypes);
		if (_clonableMutationTypes) {
			_clonableMutationTypes.forEach((mt) => {
				mt.dataKey = dataKeyCounter.current++;
			});
		} else {
			_clonableMutationTypes = [];
		}
		return _clonableMutationTypes;
	};

	const onMutationTypeValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, 'mutationTypes', setFieldValue);
		setLocalMutationTypes((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], mutationTypes: event.target.value };
			return updated;
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		setLocalMutationTypes((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], internal: event.target?.value?.name };
			return updated;
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		setFieldValue(event.target.value);
		setLocalMutationTypes((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], evidence: event.target.value };
			return updated;
		});
	};

	const createNewMutationTypeHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localMutationTypes = structuredClone(localMutationTypes);
		_localMutationTypes.push({
			dataKey: newKey,
			internal: false,
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalMutationTypes(_localMutationTypes);
	};

	const handleDeleteMutationType = (e, dataKey) => {
		let _localMutationTypes = structuredClone(localMutationTypes);
		_localMutationTypes = _localMutationTypes.filter((mt) => mt.dataKey !== dataKey);
		setLocalMutationTypes(_localMutationTypes);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localMutationTypes = structuredClone(localMutationTypes);
		for (const mt of _localMutationTypes) {
			delete mt.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localMutationTypes);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]['alleleMutationTypes'] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalMutationTypesData((originalMutationTypesData) => {
			return {
				...originalMutationTypesData,
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

			for (let i = 0; i < localMutationTypes.length; i++) {
				const _mt = cleanForValidation(localMutationTypes[i]);
				const result = await validationService.validate(Endpoints.SlotAnnotation.ALLELE_MUTATION_TYPE, _mt);
				const dataKey = localMutationTypes[i].dataKey;
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
				<Button label="New Mutation Type" icon="pi pi-plus" onClick={createNewMutationTypeHandler} />
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
				<Column header="Mutation Types" />
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
				<h3>Mutation Types</h3>
				<DataTable
					value={localMutationTypes}
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
						editor={(props) => <DeleteAction deletionHandler={handleDeleteMutationType} id={props?.rowData?.dataKey} />}
						className="max-w-4rem"
						bodyClassName="text-center"
						headerClassName="surface-0"
						frozen
					/>
					<Column
						editor={(props) => {
							return (
								<MutationTypesEditor
									props={props}
									errorMessages={errorMessages}
									onChange={onMutationTypeValueChange}
									dataKey={props?.rowData?.dataKey}
								/>
							);
						}}
						field="mutationTypes"
						header="Mutation Types"
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
