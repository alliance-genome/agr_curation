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
import { KEEP_ROWS_IN_EDIT_MODE } from '../../../utils/rows';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useLocalRowsStrategy } from '../../../components/Editors/fields/strategies/useLocalRowsStrategy';
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';
import { ControlledVocabularyTableEditor } from '../../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';

/**
 * Dialog for editing a main-table row's database status. Edits stay local until Keep Edits validates the status
 * and hands it back, or null once deleted, through the row's editor callback.
 *
 * @param {object} originalDatabaseStatusData - the row's database status as a one-item list, dialog visibility,
 *   row index and PrimeReact editor options
 * @param {Function} setOriginalDatabaseStatusData - its setter
 * @param {object} errorMessagesMainRow - the main table's errors, keyed by row index
 * @param {Function} setErrorMessagesMainRow - its setter
 * @returns {JSX.Element}
 */
export const DatabaseStatusEditDialog = ({
	originalDatabaseStatusData,
	setOriginalDatabaseStatusData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalDatabaseStatuses, isInEdit, dialog, rowIndex, mainRowProps } = originalDatabaseStatusData;
	const [localDatabaseStatuses, setLocalDatabaseStatuses] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const strategy = useLocalRowsStrategy({
		rows: localDatabaseStatuses,
		setRows: setLocalDatabaseStatuses,
		errors: errorMessages,
	});
	const databaseStatusTerms = useControlledVocabularyService('allele_db_status');

	const showDialogHandler = () => {
		let _localDatabaseStatuses = cloneDatabaseStatuses(originalDatabaseStatuses);
		setLocalDatabaseStatuses(_localDatabaseStatuses);

		let rowsObject = {};
		if (_localDatabaseStatuses) {
			_localDatabaseStatuses.forEach((ds) => {
				rowsObject[`${ds.dataKey}`] = true;
			});
		}
		setEditingRows(rowsObject);
		setErrorMessages({});
	};

	const hideDialog = () => {
		setErrorMessages({});
		setOriginalDatabaseStatusData((originalDatabaseStatusData) => {
			return {
				...originalDatabaseStatusData,
				dialog: false,
			};
		});
		setLocalDatabaseStatuses([]);
	};

	const cloneDatabaseStatuses = (clonableDatabaseStatuses) => {
		let _clonableDatabaseStatuses = [];
		if (clonableDatabaseStatuses?.length > 0 && clonableDatabaseStatuses[0]) {
			_clonableDatabaseStatuses = structuredClone(clonableDatabaseStatuses);
			if (_clonableDatabaseStatuses) {
				_clonableDatabaseStatuses.forEach((ds) => {
					ds.dataKey = dataKeyCounter.current++;
				});
			}
		}
		return _clonableDatabaseStatuses;
	};

	const createNewDatabaseStatusHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localDatabaseStatuses = structuredClone(localDatabaseStatuses);
		_localDatabaseStatuses.push({
			dataKey: newKey,
			internal: false,
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalDatabaseStatuses(_localDatabaseStatuses);
	};

	const handleDeleteDatabaseStatus = (e, dataKey) => {
		let _localDatabaseStatuses = structuredClone(localDatabaseStatuses);
		_localDatabaseStatuses = _localDatabaseStatuses.filter((ds) => ds.dataKey !== dataKey);
		setLocalDatabaseStatuses(_localDatabaseStatuses);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localDatabaseStatuses = structuredClone(localDatabaseStatuses);
		for (const ds of _localDatabaseStatuses) {
			delete ds.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localDatabaseStatuses[0] || null);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]['alleleDatabaseStatus'] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalDatabaseStatusData((originalDatabaseStatusData) => {
			return {
				...originalDatabaseStatusData,
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

			for (let i = 0; i < localDatabaseStatuses.length; i++) {
				const _ds = cleanForValidation(localDatabaseStatuses[i]);
				const result = await validationService.validate(Endpoints.SlotAnnotation.ALLELE_DATABASE_STATUS, _ds);
				const dataKey = localDatabaseStatuses[i].dataKey;
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
					label="New Database Status"
					icon="pi pi-plus"
					onClick={createNewDatabaseStatusHandler}
					disabled={localDatabaseStatuses?.length > 0}
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
				<Column header="Database Status" />
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
				className="w-5"
				modal
				onHide={hideDialog}
				closable={false}
				onShow={showDialogHandler}
				footer={footerTemplate}
			>
				<h3>Database Status</h3>
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={localDatabaseStatuses}
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
							editor={(props) => (
								<DeleteAction deletionHandler={handleDeleteDatabaseStatus} id={props?.rowData?.dataKey} />
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
									field="databaseStatus"
									options={databaseStatusTerms}
									showClear={true}
								/>
							)}
							field="databaseStatus"
							header="Database Status"
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
