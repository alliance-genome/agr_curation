import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { ValidationService } from '../../../service/ValidationService';
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';
import { KEEP_ROWS_IN_EDIT_MODE, addDataKey } from '../../allelesPage/utils';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useLocalRowsStrategy } from '../../../components/Editors/fields/strategies/useLocalRowsStrategy';
import { InputTextTableEditor } from '../../../components/Editors/text/InputTextTableEditor';
import { ControlledVocabularyTableEditor } from '../../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';

/**
 * Dialog for editing a main-table row's synonyms, shared by the Alleles, Genes and Affected
 * Genomic Models tables. Edits stay local until Keep Edits validates every row and hands them
 * back through the row's editor callback.
 *
 * @param {string} field - the main row property holding the synonyms, and the key the main
 *   row's pending-edit warning is filed under
 * @param {string} endpoint - the slot annotation endpoint each row is validated against
 * @param {object} originalSynonymsData - the row's synonyms, dialog visibility, row index and
 *   PrimeReact editor options
 * @param {Function} setOriginalSynonymsData - its setter
 * @param {object} errorMessagesMainRow - the main table's errors, keyed by row index
 * @param {Function} setErrorMessagesMainRow - its setter
 * @returns {JSX.Element}
 */
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
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	// Synonym name types come from the name_type vocabulary, where symbol and full name use a
	// vocabulary term set.
	const synonymTypeTerms = useControlledVocabularyService('name_type');
	const synonymScopeTerms = useControlledVocabularyService('synonym_scope');

	const strategy = useLocalRowsStrategy({
		rows: localSynonyms,
		setRows: setLocalSynonyms,
		errors: errorMessages,
	});

	const showDialogHandler = () => {
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
					addDataKey(syn);
				});
			}
		}
		return _clonableSynonyms;
	};

	const createNewSynonymHandler = () => {
		const _localSynonyms = structuredClone(localSynonyms);
		const newRow = {
			internal: false,
			nameType: synonymTypeTerms?.[0] || null,
			displayText: '',
			formatText: '',
		};
		addDataKey(newRow);
		_localSynonyms.push(newRow);
		let _editingRows = { ...editingRows, ...{ [`${newRow.dataKey}`]: true } };
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
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localSynonyms);
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
				const synonymForValidation = cleanForValidation(localSynonyms[i]);
				const result = await validationService.validate(endpoint, synonymForValidation);
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

	const headerGroup = (
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
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={localSynonyms}
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
							editor={(editorOptions) => (
								<DeleteAction deletionHandler={handleDeleteSynonym} id={editorOptions?.rowData?.dataKey} />
							)}
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
									dataKey="id"
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
									options={synonymTypeTerms}
									dataKey="id"
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
