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
import { ControlledVocabularyEditor } from '../../../components/Editors/ControlledVocabularyEditor';
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';

export const SynonymsEditDialog = ({
	field,
	endpoint,
	originalSynonymsData,
	setOriginalSynonymsData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalSynonyms, isInEdit, dialog, rowIndex, mainRowProps } = originalSynonymsData;
	const [localSynonyms, setLocalSynonyms] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const synonymTypeTerms = useControlledVocabularyService('name_type');

	const showDialogHandler = () => {
		dataKeyCounter.current = 0;
		let _localSynonyms = cloneSynonyms(originalSynonyms);
		setLocalSynonyms(_localSynonyms);

		let rowsObject = {};
		if (_localSynonyms) {
			_localSynonyms.forEach((syn) => {
				rowsObject[`${syn.dataKey}`] = true;
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
		setOriginalSynonymsData((originalSynonymsData) => {
			return {
				...originalSynonymsData,
				dialog: false,
			};
		});
		setLocalSynonyms([]);
	};

	const cloneSynonyms = (clonableSynonyms) => {
		let _clonableSynonyms = [];
		if (clonableSynonyms?.length > 0 && clonableSynonyms[0]) {
			_clonableSynonyms = structuredClone(clonableSynonyms);
			if (_clonableSynonyms) {
				_clonableSynonyms.forEach((syn) => {
					syn.dataKey = dataKeyCounter.current++;
				});
			}
		}
		return _clonableSynonyms;
	};

	const textOnChangeHandler = (rowIndex, event, field) => {
		setLocalSynonyms((prev) => {
			const updated = [...prev];
			updated[rowIndex] = { ...updated[rowIndex], [field]: event.target.value };
			return updated;
		});
	};

	const nameTypeOnChangeHandler = (props, event) => {
		props.editorCallback(event.value);
		setLocalSynonyms((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], nameType: event.value };
			return updated;
		});
	};

	const synonymScopeOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		setLocalSynonyms((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], synonymScope: event.target.value };
			return updated;
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		setLocalSynonyms((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], internal: event.target?.value?.name };
			return updated;
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		setFieldValue(event.target.value);
		setLocalSynonyms((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], evidence: event.target.value };
			return updated;
		});
	};

	const createNewSynonymHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localSynonyms = structuredClone(localSynonyms);
		_localSynonyms.push({
			dataKey: newKey,
			internal: false,
			nameType: synonymTypeTerms?.[0] || null,
			displayText: '',
			formatText: '',
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalSynonyms(_localSynonyms);
	};

	const handleDeleteSynonym = (e, dataKey) => {
		let _localSynonyms = structuredClone(localSynonyms);
		_localSynonyms = _localSynonyms.filter((syn) => syn.dataKey !== dataKey);
		setLocalSynonyms(_localSynonyms);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localSynonyms = structuredClone(localSynonyms);
		for (const syn of _localSynonyms) {
			delete syn.dataKey;
		}
		mainRowProps.rowData[field] = _localSynonyms;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex][field] = _localSynonyms;
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localSynonyms?.[0]?.displayText ?? null);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex][field] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalSynonymsData((originalSynonymsData) => {
			return {
				...originalSynonymsData,
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

			for (let i = 0; i < localSynonyms.length; i++) {
				const _syn = cleanForValidation(localSynonyms[i]);
				const result = await validationService.validate(endpoint, _syn);
				const dataKey = localSynonyms[i].dataKey;
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
				<Button label="New Synonym" icon="pi pi-plus" onClick={createNewSynonymHandler} />
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
				<h3>Synonyms</h3>
				<DataTable
					value={localSynonyms}
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
						editor={(props) => <DeleteAction deletionHandler={handleDeleteSynonym} id={props?.rowData?.dataKey} />}
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
									editorCallback={props.editorCallback}
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
					<Column
						editor={(props) => {
							return (
								<ControlledVocabularyEditor
									props={props}
									onChangeHandler={nameTypeOnChangeHandler}
									errorMessages={errorMessages}
									dataKey={props?.rowData?.dataKey}
									rowIndex={props.rowIndex}
									vocabType="name_type"
									field="nameType"
									showClear={false}
								/>
							);
						}}
						field="nameType"
						header="Name Type"
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
									editorCallback={props.editorCallback}
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
