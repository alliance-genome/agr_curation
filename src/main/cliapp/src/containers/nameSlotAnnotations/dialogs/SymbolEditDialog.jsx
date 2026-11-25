import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { ValidationService } from '../../../service/ValidationService';
import { useVocabularyTermSetService } from '../../../service/useVocabularyTermSetService';
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';
import { KEEP_ROWS_IN_EDIT_MODE } from '../../../utils/rows';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useLocalRowsStrategy } from '../../../components/Editors/fields/strategies/useLocalRowsStrategy';
import { InputTextTableEditor } from '../../../components/Editors/text/InputTextTableEditor';
import { ControlledVocabularyTableEditor } from '../../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { markPendingEdits } from '../../../utils/pendingEdits';

/**
 * Dialog for editing a main-table row's symbol. Edits stay local until Keep Edits validates the symbol and hands
 * it back through the row's editor callback. An entity always has a symbol, so none can be added or deleted here.
 *
 * @param {string} field - the row's field holding the symbol, under which Pending Edits is marked
 * @param {string} endpoint - the slot annotation endpoint the symbol is validated against
 * @param {object} originalSymbolData - the row's symbol as a one-item list, dialog visibility, row index and
 *   PrimeReact editor options
 * @param {Function} setOriginalSymbolData - its setter
 * @param {object} errorMessagesMainRow - the main table's errors, keyed by row index
 * @param {Function} setErrorMessagesMainRow - its setter
 * @returns {JSX.Element}
 */
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
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const symbolNameTypeTerms = useVocabularyTermSetService('symbol_name_type');
	const synonymScopeTerms = useControlledVocabularyService('synonym_scope');

	const strategy = useLocalRowsStrategy({
		rows: localSymbols,
		setRows: setLocalSymbols,
		errors: errorMessages,
	});

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
			_clonableSymbols = structuredClone(clonableSymbols);
			if (_clonableSymbols) {
				_clonableSymbols.forEach((sym) => {
					sym.dataKey = dataKeyCounter.current++;
				});
			}
		}
		return _clonableSymbols;
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localSymbols = structuredClone(localSymbols);
		for (const sym of _localSymbols) {
			delete sym.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localSymbols[0] || null);
		}

		setErrorMessagesMainRow(markPendingEdits(errorMessagesMainRow, rowIndex, field));

		setOriginalSymbolData((originalSymbolData) => {
			return {
				...originalSymbolData,
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
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={localSymbols}
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
									options={symbolNameTypeTerms}
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
