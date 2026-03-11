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
import { TableInputTextEditor } from '../../../components/Editors/TableInputTextEditor';
import { InternalEditor } from '../../../components/Editors/InternalEditor';
import { EvidenceEditor } from '../../../components/Editors/EvidenceEditor';
import { ControlledVocabularyEditor } from '../../../components/Editors/ControlledVocabularyEditor';
import { useVocabularyTermSetService } from '../../../service/useVocabularyTermSetService';
import { ControlledVocabularyDropdown } from '../../../components/ControlledVocabularySelector';

export const SymbolEditDialog = ({
	field,
	endpoint,
	originalSymbolData,
	setOriginalSymbolData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalSymbols, isInEdit, dialog, rowIndex, mainRowProps } = originalSymbolData;
	const [localSymbols, setLocalSymbols] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const symbolNameTypeTerms = useVocabularyTermSetService('symbol_name_type');

	const showDialogHandler = () => {
		let _localSymbols = cloneSymbols(originalSymbols);
		setLocalSymbols(_localSymbols);

		let rowsObject = {};
		if (_localSymbols) {
			_localSymbols.forEach((sym) => {
				rowsObject[`${sym.dataKey}`] = true;
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
		setOriginalSymbolData((originalSymbolData) => {
			return {
				...originalSymbolData,
				dialog: false,
			};
		});
		setLocalSymbols([]);
	};

	const cloneSymbols = (clonableSymbols) => {
		let _clonableSymbols = [];
		if (clonableSymbols?.length > 0 && clonableSymbols[0]) {
			_clonableSymbols = global.structuredClone(clonableSymbols);
			if (_clonableSymbols) {
				let counter = 0;
				_clonableSymbols.forEach((sym) => {
					sym.dataKey = counter++;
				});
			}
		}
		return _clonableSymbols;
	};

	const textOnChangeHandler = (rowIndex, event, field) => {
		setLocalSymbols((prev) => {
			const updated = [...prev];
			updated[rowIndex] = { ...updated[rowIndex], [field]: event.target.value };
			return updated;
		});
	};

	const nameTypeOnChangeHandler = (props, event) => {
		props.editorCallback(event.value);
		setLocalSymbols((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], nameType: event.value };
			return updated;
		});
	};

	const synonymScopeOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		setLocalSymbols((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], synonymScope: event.target.value };
			return updated;
		});
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		setLocalSymbols((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], internal: event.target?.value?.name };
			return updated;
		});
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		setFieldValue(event.target.value);
		setLocalSymbols((prev) => {
			const updated = [...prev];
			updated[props.rowIndex] = { ...updated[props.rowIndex], evidence: event.target.value };
			return updated;
		});
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localSymbols = global.structuredClone(localSymbols);
		for (const sym of _localSymbols) {
			delete sym.dataKey;
		}
		mainRowProps.rowData[field] = _localSymbols[0] ? _localSymbols[0] : null;
		let updatedAnnotations = [...mainRowProps.props.value];
		updatedAnnotations[rowIndex][field] = _localSymbols[0] ? _localSymbols[0] : null;
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

		setOriginalSymbolData((originalSymbolData) => {
			return {
				...originalSymbolData,
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

			for (let i = 0; i < localSymbols.length; i++) {
				const _sym = cleanForValidation(localSymbols[i]);
				const result = await validationService.validate(endpoint, _sym);
				const dataKey = localSymbols[i].dataKey;
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
					options={symbolNameTypeTerms}
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
				<h3>Symbol</h3>
				<DataTable
					value={localSymbols}
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
