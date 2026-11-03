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
import { KEEP_ROWS_IN_EDIT_MODE, addDataKey } from '../utils';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useLocalRowsStrategy } from '../../../components/Editors/fields/strategies/useLocalRowsStrategy';
import { InputTextTableEditor } from '../../../components/Editors/text/InputTextTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';

/**
 * Dialog for editing a main-table row's secondary IDs. Edits stay local until Keep Edits
 * validates every row and hands them back through the row's editor callback.
 *
 * @param {string} field - the main row property holding the secondary IDs, and the key the
 *   main row's pending-edit warning is filed under
 * @param {string} endpoint - the slot annotation endpoint each row is validated against
 * @param {object} originalSecondaryIdsData - the row's secondary IDs, dialog visibility, row
 *   index and PrimeReact editor options
 * @param {Function} setOriginalSecondaryIdsData - its setter
 * @param {object} errorMessagesMainRow - the main table's errors, keyed by row index
 * @param {Function} setErrorMessagesMainRow - its setter
 * @returns {JSX.Element}
 */
export const SecondaryIdsEditDialog = ({
	field,
	endpoint,
	originalSecondaryIdsData,
	setOriginalSecondaryIdsData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalSecondaryIds, isInEdit, dialog, rowIndex, mainRowProps } = originalSecondaryIdsData;
	const [localSecondaryIds, setLocalSecondaryIds] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const strategy = useLocalRowsStrategy({
		rows: localSecondaryIds,
		setRows: setLocalSecondaryIds,
		errors: errorMessages,
	});

	const showDialogHandler = () => {
		let _localSecondaryIds = cloneSecondaryIds(originalSecondaryIds);
		setLocalSecondaryIds(_localSecondaryIds);

		let rowsObject = {};
		if (_localSecondaryIds) {
			_localSecondaryIds.forEach((sid) => {
				rowsObject[`${sid.dataKey}`] = true;
			});
		}
		setEditingRows(rowsObject);
		setErrorMessages({});
	};

	const hideDialog = () => {
		setErrorMessages({});
		setOriginalSecondaryIdsData((originalSecondaryIdsData) => {
			return {
				...originalSecondaryIdsData,
				dialog: false,
			};
		});
		setLocalSecondaryIds([]);
	};

	const cloneSecondaryIds = (clonableSecondaryIds) => {
		let _clonableSecondaryIds = structuredClone(clonableSecondaryIds);
		if (_clonableSecondaryIds) {
			_clonableSecondaryIds.forEach((sid) => {
				addDataKey(sid);
			});
		} else {
			_clonableSecondaryIds = [];
		}
		return _clonableSecondaryIds;
	};

	const createNewSecondaryIdHandler = () => {
		const _localSecondaryIds = structuredClone(localSecondaryIds);
		const newRow = {
			internal: false,
			secondaryId: '',
		};
		addDataKey(newRow);
		_localSecondaryIds.push(newRow);
		let _editingRows = { ...editingRows, ...{ [`${newRow.dataKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalSecondaryIds(_localSecondaryIds);
	};

	const handleDeleteSecondaryId = (e, dataKey) => {
		let _localSecondaryIds = structuredClone(localSecondaryIds);
		_localSecondaryIds = _localSecondaryIds.filter((sid) => sid.dataKey !== dataKey);
		setLocalSecondaryIds(_localSecondaryIds);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localSecondaryIds = structuredClone(localSecondaryIds);
		for (const sid of _localSecondaryIds) {
			delete sid.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localSecondaryIds);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex][field] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalSecondaryIdsData((originalSecondaryIdsData) => {
			return {
				...originalSecondaryIdsData,
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

			for (let i = 0; i < localSecondaryIds.length; i++) {
				const _sid = cleanForValidation(localSecondaryIds[i]);
				const result = await validationService.validate(endpoint, _sid);
				const dataKey = localSecondaryIds[i].dataKey;
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
				<Button label="New Secondary ID" icon="pi pi-plus" onClick={createNewSecondaryIdHandler} />
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
				<Column header="Secondary ID" />
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
				<h3>Secondary IDs</h3>
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={localSecondaryIds}
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
								<DeleteAction deletionHandler={handleDeleteSecondaryId} id={editorOptions?.rowData?.dataKey} />
							)}
							className="max-w-4rem"
							bodyClassName="text-center"
							headerClassName="surface-0"
							frozen
						/>
						<Column
							editor={(editorOptions) => <InputTextTableEditor editorOptions={editorOptions} field="secondaryId" />}
							field="secondaryId"
							header="Secondary ID"
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
