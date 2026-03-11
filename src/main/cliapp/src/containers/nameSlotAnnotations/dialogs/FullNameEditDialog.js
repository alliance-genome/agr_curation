import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DialogErrorMessageComponent } from '../../../components/Error/DialogErrorMessageComponent';
import { ValidationService } from '../../../service/ValidationService';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { TableInputTextEditor } from '../../../components/Editors/TableInputTextEditor';
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { ControlledVocabularyEditor } from '../../../components/Editors/ControlledVocabularyEditor';
import { useVocabularyTermSetService } from '../../../service/useVocabularyTermSetService';
import { ControlledVocabularyDropdown } from '../../../components/ControlledVocabularySelector';

export const FullNameEditDialog = ({
	field,
	endpoint,
	originalFullNameData,
	setOriginalFullNameData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalFullNames, isInEdit, dialog, rowIndex, mainRowProps } = originalFullNameData;
	const [localFullNames, setLocalFullNames] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const fullNameTypeTerms = useVocabularyTermSetService('full_name_type');

	const showDialogHandler = () => {
		let _localFullNames = cloneFullNames(originalFullNames);
		setLocalFullNames(_localFullNames);

		let rowsObject = {};
		if (_localFullNames) {
			_localFullNames.forEach((fn) => {
				rowsObject[`${fn.dataKey}`] = true;
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
		setOriginalFullNameData((originalFullNameData) => {
			return {
				...originalFullNameData,
				dialog: false,
			};
		});
		setLocalFullNames([]);
	};

	const cloneFullNames = (clonableFullNames) => {
		let _clonableFullNames = [];
		if (clonableFullNames?.length > 0 && clonableFullNames[0]) {
			_clonableFullNames = global.structuredClone(clonableFullNames);
			if (_clonableFullNames) {
				let counter = 0;
				_clonableFullNames.forEach((fn) => {
					fn.dataKey = counter++;
				});
			}
		}
		return _clonableFullNames;
	};

	const textOnChangeHandler = (rowIndex, event, field) => {
		setLocalFullNames((prev) => {
			const updated = [...prev];
			updated[rowIndex] = { ...updated[rowIndex], [field]: event.target.value };
			return updated;
		});
	};

	const nameTypeOnChangeHandler = (props, event) => {
		props.editorCallback(event.value);
		setLocalFullNames((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], nameType: event.value };
			return updated;
		});
	};

	const synonymScopeOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		setLocalFullNames((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], synonymScope: event.target.value };
			return updated;
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		setLocalFullNames((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], internal: event.target?.value?.name };
			return updated;
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		setFieldValue(event.target.value);
		setLocalFullNames((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], evidence: event.target.value };
			return updated;
		});
	};

	const createNewFullNameHandler = () => {
		let cnt = localFullNames ? localFullNames.length : 0;
		const _localFullNames = global.structuredClone(localFullNames);
		_localFullNames.push({
			dataKey: cnt,
			internal: false,
			nameType: fullNameTypeTerms?.[0] || null,
		});
		let _editingRows = { ...editingRows, ...{ [`${cnt}`]: true } };
		setEditingRows(_editingRows);
		setLocalFullNames(_localFullNames);
	};

	const handleDeleteFullName = (e, dataKey) => {
		let _localFullNames = global.structuredClone(localFullNames);
		_localFullNames = _localFullNames.filter((fn) => fn.dataKey !== dataKey);
		setLocalFullNames(_localFullNames);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localFullNames = global.structuredClone(localFullNames);
		for (const fn of _localFullNames) {
			delete fn.dataKey;
		}
		mainRowProps.rowData[field] = _localFullNames[0] ? _localFullNames[0] : null;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex][field] = _localFullNames[0] ? _localFullNames[0] : null;
		// Signal PrimeReact that editing data changed so the main table re-renders the cell
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback();
		}

		const errorMessagesCopy = global.structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex][field] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalFullNameData((originalFullNameData) => {
			return {
				...originalFullNameData,
				dialog: false,
			};
		});
	};

	const cleanForValidation = (obj) => {
		const _obj = global.structuredClone(obj);
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

			for (let i = 0; i < localFullNames.length; i++) {
				const _fn = cleanForValidation(localFullNames[i]);
				const result = await validationService.validate(endpoint, _fn);
				const dataKey = localFullNames[i].dataKey;
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

	const nameTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="nameType"
					options={fullNameTypeTerms}
					editorChange={nameTypeOnChangeHandler}
					props={props}
					showClear={false}
					dataKey="id"
				/>
				<DialogErrorMessageComponent errorMessages={errorMessages[props?.rowData?.dataKey]} errorField={'nameType'} />
			</>
		);
	};

	const footerTemplate = () => {
		return (
			<div>
				<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
				<Button
					label="New Name"
					icon="pi pi-plus"
					onClick={createNewFullNameHandler}
					disabled={localFullNames?.length > 0}
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
				<h3>Full Name</h3>
				<DataTable
					value={localFullNames}
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
						editor={(props) => <DeleteAction deletionHandler={handleDeleteFullName} id={props?.rowData?.dataKey} />}
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
									field="displayText"
								/>
							);
						}}
						field="displayText"
						header="Display Text"
						headerClassName="surface-0"
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
									field="formatText"
								/>
							);
						}}
						field="formatText"
						header="Format Text"
						headerClassName="surface-0"
					/>
					<Column
						editor={(props) => {
							return (
								<ControlledVocabularyEditor
									props={props}
									onChangeHandler={synonymScopeOnChangeHandler}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
									rowIndex={props.rowIndex}
									vocabType="synonym_scope"
									field="synonymScope"
									showClear={true}
								/>
							);
						}}
						field="synonymScope"
						header="Synonym Scope"
						headerClassName="surface-0"
					/>
					<Column editor={nameTypeEditor} field="nameType" header="Name Type" headerClassName="surface-0" />
					<Column
						editor={(props) => {
							return (
								<TableInputTextEditor
									value={props.value}
									rowIndex={props.rowIndex}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
									textOnChangeHandler={textOnChangeHandler}
									field="synonymUrl"
								/>
							);
						}}
						field="synonymUrl"
						header="Synonym URL"
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
					<Column field="updatedBy.uniqueId" header="Updated By" />
					<Column field="dateUpdated" header="Date Updated" />
				</DataTable>
			</Dialog>
		</div>
	);
};
