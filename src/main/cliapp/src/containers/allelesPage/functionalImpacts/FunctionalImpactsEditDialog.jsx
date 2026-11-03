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
import { Endpoints } from '../../../constants/Endpoints';
import { KEEP_ROWS_IN_EDIT_MODE, addDataKey } from '../utils';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useLocalRowsStrategy } from '../../../components/Editors/fields/strategies/useLocalRowsStrategy';
import { FunctionalImpactsTableEditor } from '../../../components/Editors/autocomplete/vocabularyTerm/FunctionalImpactsTableEditor';
import { PhenotypeTermTableEditor } from '../../../components/Editors/autocomplete/ontology/PhenotypeTermTableEditor';
import { InputTextAreaTableEditor } from '../../../components/Editors/text/InputTextAreaTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';

/**
 * Dialog for editing a main-table row's functional impacts. Edits stay local until Keep Edits
 * validates every row and hands them back through the row's editor callback.
 *
 * @param {object} originalFunctionalImpactsData - the row's functional impacts, dialog
 *   visibility, row index and PrimeReact editor options
 * @param {Function} setOriginalFunctionalImpactsData - its setter
 * @param {object} errorMessagesMainRow - the main table's errors, keyed by row index
 * @param {Function} setErrorMessagesMainRow - its setter
 * @returns {JSX.Element}
 */
export const FunctionalImpactsEditDialog = ({
	originalFunctionalImpactsData,
	setOriginalFunctionalImpactsData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalFunctionalImpacts, isInEdit, dialog, rowIndex, mainRowProps } = originalFunctionalImpactsData;
	const [localFunctionalImpacts, setLocalFunctionalImpacts] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const strategy = useLocalRowsStrategy({
		rows: localFunctionalImpacts,
		setRows: setLocalFunctionalImpacts,
		errors: errorMessages,
	});

	const showDialogHandler = () => {
		let _localFunctionalImpacts = cloneFunctionalImpacts(originalFunctionalImpacts);
		setLocalFunctionalImpacts(_localFunctionalImpacts);

		let rowsObject = {};
		if (_localFunctionalImpacts) {
			_localFunctionalImpacts.forEach((fi) => {
				rowsObject[`${fi.dataKey}`] = true;
			});
		}
		setEditingRows(rowsObject);
		setErrorMessages({});
	};

	const hideDialog = () => {
		setErrorMessages({});
		setOriginalFunctionalImpactsData((originalFunctionalImpactsData) => {
			return {
				...originalFunctionalImpactsData,
				dialog: false,
			};
		});
		setLocalFunctionalImpacts([]);
	};

	const cloneFunctionalImpacts = (clonableFunctionalImpacts) => {
		let _clonableFunctionalImpacts = structuredClone(clonableFunctionalImpacts);
		if (_clonableFunctionalImpacts) {
			_clonableFunctionalImpacts.forEach((fi) => {
				addDataKey(fi);
			});
		} else {
			_clonableFunctionalImpacts = [];
		}
		return _clonableFunctionalImpacts;
	};

	const createNewFunctionalImpactHandler = () => {
		const _localFunctionalImpacts = structuredClone(localFunctionalImpacts);
		const newRow = {
			internal: false,
		};
		addDataKey(newRow);
		_localFunctionalImpacts.push(newRow);
		let _editingRows = { ...editingRows, ...{ [`${newRow.dataKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalFunctionalImpacts(_localFunctionalImpacts);
	};

	const handleDeleteFunctionalImpact = (e, dataKey) => {
		let _localFunctionalImpacts = structuredClone(localFunctionalImpacts);
		_localFunctionalImpacts = _localFunctionalImpacts.filter((fi) => fi.dataKey !== dataKey);
		setLocalFunctionalImpacts(_localFunctionalImpacts);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localFunctionalImpacts = structuredClone(localFunctionalImpacts);
		for (const fi of _localFunctionalImpacts) {
			delete fi.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localFunctionalImpacts);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]['alleleFunctionalImpacts'] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalFunctionalImpactsData((originalFunctionalImpactsData) => {
			return {
				...originalFunctionalImpactsData,
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

			for (let i = 0; i < localFunctionalImpacts.length; i++) {
				const _fi = cleanForValidation(localFunctionalImpacts[i]);
				const result = await validationService.validate(Endpoints.SlotAnnotation.ALLELE_FUNCTIONAL_IMPACT, _fi);
				const dataKey = localFunctionalImpacts[i].dataKey;
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
				<Button label="New Functional Impact" icon="pi pi-plus" onClick={createNewFunctionalImpactHandler} />
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
				<Column header="Functional Impacts" />
				<Column header="Phenotype Term" />
				<Column header="Phenotype Statement" />
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
				<h3>Functional Impacts</h3>
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={localFunctionalImpacts}
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
								<DeleteAction deletionHandler={handleDeleteFunctionalImpact} id={editorOptions?.rowData?.dataKey} />
							)}
							className="max-w-4rem"
							bodyClassName="text-center"
							headerClassName="surface-0"
							frozen
						/>
						<Column
							editor={(editorOptions) => <FunctionalImpactsTableEditor editorOptions={editorOptions} />}
							field="functionalImpacts"
							header="Functional Impacts"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => <PhenotypeTermTableEditor editorOptions={editorOptions} />}
							field="phenotypeTerm"
							header="Phenotype Term"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => (
								<InputTextAreaTableEditor editorOptions={editorOptions} field="phenotypeStatement" rows={1} cols={30} />
							)}
							field="phenotypeStatement"
							header="Phenotype Statement"
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
					</DataTable>
				</TableEditorProvider>
			</Dialog>
		</div>
	);
};
