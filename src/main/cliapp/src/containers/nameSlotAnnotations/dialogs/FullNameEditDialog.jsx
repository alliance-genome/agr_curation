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
import { useVocabularyTermSetService } from '../../../service/useVocabularyTermSetService';
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';
import { KEEP_ROWS_IN_EDIT_MODE } from '../../../utils/rows';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useLocalRowsStrategy } from '../../../components/Editors/fields/strategies/useLocalRowsStrategy';
import { InputTextTableEditor } from '../../../components/Editors/text/InputTextTableEditor';
import { ControlledVocabularyTableEditor } from '../../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';

/**
 * Dialog for editing a main-table row's full name, or, given a title and term set, another name held the same
 * way, such as a gene's systematic name. Edits stay local until Keep Edits validates the name and hands it back,
 * or null once deleted, through the row's editor callback.
 *
 * @param {string} field - the row's field holding the name, under which Pending Edits is marked
 * @param {string} endpoint - the slot annotation endpoint the name is validated against
 * @param {string} [title='Full Name'] - the dialog's heading
 * @param {string} [nameTypeVocabularyTermSet='full_name_type'] - the term set the name types come from
 * @param {object} originalFullNameData - the row's name as a one-item list, dialog visibility, row index and
 *   PrimeReact editor options
 * @param {Function} setOriginalFullNameData - its setter
 * @param {object} errorMessagesMainRow - the main table's errors, keyed by row index
 * @param {Function} setErrorMessagesMainRow - its setter
 * @returns {JSX.Element}
 */
export const FullNameEditDialog = ({
	field,
	endpoint,
	title = 'Full Name',
	nameTypeVocabularyTermSet = 'full_name_type',
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
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const fullNameTypeTerms = useVocabularyTermSetService(nameTypeVocabularyTermSet);
	const synonymScopeTerms = useControlledVocabularyService('synonym_scope');

	const strategy = useLocalRowsStrategy({
		rows: localFullNames,
		setRows: setLocalFullNames,
		errors: errorMessages,
	});

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
			_clonableFullNames = structuredClone(clonableFullNames);
			if (_clonableFullNames) {
				_clonableFullNames.forEach((fn) => {
					fn.dataKey = dataKeyCounter.current++;
				});
			}
		}
		return _clonableFullNames;
	};

	const createNewFullNameHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localFullNames = structuredClone(localFullNames);
		_localFullNames.push({
			dataKey: newKey,
			internal: false,
			nameType: fullNameTypeTerms?.[0] || null,
			displayText: '',
			formatText: '',
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalFullNames(_localFullNames);
	};

	const handleDeleteFullName = (e, dataKey) => {
		let _localFullNames = structuredClone(localFullNames);
		_localFullNames = _localFullNames.filter((fn) => fn.dataKey !== dataKey);
		setLocalFullNames(_localFullNames);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localFullNames = structuredClone(localFullNames);
		for (const fn of _localFullNames) {
			delete fn.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localFullNames[0] || null);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
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
				<h3>{title}</h3>
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={localFullNames}
						dataKey="dataKey"
						showGridlines
						editMode="row"
						headerColumnGroup={headerGroup}
						editingRows={editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
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
							editor={(editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="displayText" />}
							field="displayText"
							header="Display Text"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="formatText" />}
							field="formatText"
							header="Format Text"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => (
								<ControlledVocabularyTableEditor
									editorOptions={editorOptions}
									field="synonymScope"
									options={synonymScopeTerms}
									showClear={true}
								/>
							)}
							field="synonymScope"
							header="Synonym Scope"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => (
								<ControlledVocabularyTableEditor
									editorOptions={editorOptions}
									field="nameType"
									options={fullNameTypeTerms}
									showClear={false}
								/>
							)}
							field="nameType"
							header="Name Type"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="synonymUrl" />}
							field="synonymUrl"
							header="Synonym URL"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="internal" />}
							field="internal"
							header="Internal"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => <EvidenceTableEditor editorOptions={editorOptions} />}
							field="evidence"
							header="Evidence"
							headerClassName="surface-0"
						/>
						<Column field="updatedBy.uniqueId" header="Updated By" />
						<Column field="dateUpdated" header="Date Updated" />
					</DataTable>
				</TableEditorProvider>
			</Dialog>
		</div>
	);
};
