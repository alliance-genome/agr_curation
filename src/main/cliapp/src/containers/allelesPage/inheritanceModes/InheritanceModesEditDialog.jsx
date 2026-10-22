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
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useLocalRowsStrategy } from '../../../components/Editors/fields/strategies/useLocalRowsStrategy';
import { ControlledVocabularyTableEditor } from '../../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { PhenotypeTermTableEditor } from '../../../components/Editors/autocomplete/ontology/PhenotypeTermTableEditor';
import { InputTextAreaTableEditor } from '../../../components/Editors/text/InputTextAreaTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';

/**
 * Dialog for editing a main-table row's inheritance modes. Edits stay local until Keep Edits
 * validates every row and hands them back through the row's editor callback.
 *
 * @param {object} originalInheritanceModesData - the row's inheritance modes, dialog
 *   visibility, row index and PrimeReact editor options
 * @param {Function} setOriginalInheritanceModesData - its setter
 * @param {object} errorMessagesMainRow - the main table's errors, keyed by row index
 * @param {Function} setErrorMessagesMainRow - its setter
 * @returns {JSX.Element}
 */
export const InheritanceModesEditDialog = ({
	originalInheritanceModesData,
	setOriginalInheritanceModesData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalInheritanceModes, isInEdit, dialog, rowIndex, mainRowProps } = originalInheritanceModesData;
	const [localInheritanceModes, setLocalInheritanceModes] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const strategy = useLocalRowsStrategy({
		rows: localInheritanceModes,
		setRows: setLocalInheritanceModes,
		errors: errorMessages,
	});
	const inheritanceModeTerms = useControlledVocabularyService('allele_inheritance_mode');

	const showDialogHandler = () => {
		let _localInheritanceModes = cloneInheritanceModes(originalInheritanceModes);
		setLocalInheritanceModes(_localInheritanceModes);

		let rowsObject = {};
		if (_localInheritanceModes) {
			_localInheritanceModes.forEach((im) => {
				rowsObject[`${im.dataKey}`] = true;
			});
		}
		setEditingRows(rowsObject);
		setErrorMessages({});
	};

	const hideDialog = () => {
		setErrorMessages({});
		setOriginalInheritanceModesData((originalInheritanceModesData) => {
			return {
				...originalInheritanceModesData,
				dialog: false,
			};
		});
		setLocalInheritanceModes([]);
	};

	const cloneInheritanceModes = (clonableInheritanceModes) => {
		let _clonableInheritanceModes = structuredClone(clonableInheritanceModes);
		if (_clonableInheritanceModes) {
			_clonableInheritanceModes.forEach((im) => {
				addDataKey(im);
			});
		} else {
			_clonableInheritanceModes = [];
		}
		return _clonableInheritanceModes;
	};

	const createNewInheritanceModeHandler = () => {
		const _localInheritanceModes = structuredClone(localInheritanceModes);
		const newRow = {
			internal: false,
		};
		addDataKey(newRow);
		_localInheritanceModes.push(newRow);
		let _editingRows = { ...editingRows, ...{ [`${newRow.dataKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalInheritanceModes(_localInheritanceModes);
	};

	const handleDeleteInheritanceMode = (e, dataKey) => {
		let _localInheritanceModes = structuredClone(localInheritanceModes);
		_localInheritanceModes = _localInheritanceModes.filter((im) => im.dataKey !== dataKey);
		setLocalInheritanceModes(_localInheritanceModes);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localInheritanceModes = structuredClone(localInheritanceModes);
		for (const im of _localInheritanceModes) {
			delete im.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localInheritanceModes);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]['alleleInheritanceModes'] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalInheritanceModesData((originalInheritanceModesData) => {
			return {
				...originalInheritanceModesData,
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

			for (let i = 0; i < localInheritanceModes.length; i++) {
				const _im = cleanForValidation(localInheritanceModes[i]);
				const result = await validationService.validate(Endpoints.SlotAnnotation.ALLELE_INHERITANCE_MODE, _im);
				const dataKey = localInheritanceModes[i].dataKey;
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
				<Button label="New Inheritance Mode" icon="pi pi-plus" onClick={createNewInheritanceModeHandler} />
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
				<Column header="Inheritance Mode" />
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
				<h3>Inheritance Modes</h3>
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={localInheritanceModes}
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
								<DeleteAction deletionHandler={handleDeleteInheritanceMode} id={editorOptions?.rowData?.dataKey} />
							)}
							className="max-w-4rem"
							bodyClassName="text-center"
							headerClassName="surface-0"
							frozen
						/>
						<Column
							editor={(editorOptions) => (
								<ControlledVocabularyTableEditor
									editorOptions={editorOptions}
									field="inheritanceMode"
									options={inheritanceModeTerms}
									dataKey="id"
									showClear={false}
								/>
							)}
							field="inheritanceMode"
							header="Inheritance Mode"
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
