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
import { UsesTableEditor } from '../../../components/Editors/autocomplete/ontology/UsesTableEditor';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';

/**
 * Dialog for editing the uses of a main-table row's cassette or transgenic tool: FBcv terms from the
 * experimental tool descriptor namespace, with their evidence. Edits stay local until Keep Edits validates every
 * use and hands the list back through the row's editor callback.
 *
 * @param {object} originalUsesData - the row's uses, dialog visibility, row index and PrimeReact editor options
 * @param {Function} setOriginalUsesData - its setter
 * @param {object} errorMessagesMainRow - the main table's errors, keyed by row index
 * @param {Function} setErrorMessagesMainRow - its setter
 * @param {string} [field='cassetteUses'] - the row's field holding the uses, under which Pending Edits is marked
 * @param {string} [endpoint] - the slot annotation endpoint each use is validated against; the cassette use's by
 *   default
 * @returns {JSX.Element}
 */
export const UsesEditDialog = ({
	originalUsesData,
	setOriginalUsesData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
	field = 'cassetteUses',
	endpoint = Endpoints.SlotAnnotation.CASSETTE_USE,
}) => {
	const { originalUses, isInEdit, dialog, rowIndex, mainRowProps } = originalUsesData;
	const [localUses, setLocalUses] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const strategy = useLocalRowsStrategy({
		rows: localUses,
		setRows: setLocalUses,
		errors: errorMessages,
	});

	const showDialogHandler = () => {
		let _localUses = cloneUses(originalUses);
		setLocalUses(_localUses);

		let rowsObject = {};
		if (_localUses) {
			_localUses.forEach((item) => {
				rowsObject[`${item.dataKey}`] = true;
			});
		}
		setEditingRows(rowsObject);
		setErrorMessages({});
	};

	const hideDialog = () => {
		setErrorMessages({});
		setOriginalUsesData((originalUsesData) => {
			return {
				...originalUsesData,
				dialog: false,
			};
		});
		setLocalUses([]);
	};

	const cloneUses = (clonableUses) => {
		let _clonableUses = structuredClone(clonableUses);
		if (_clonableUses) {
			_clonableUses.forEach((item) => {
				item.dataKey = dataKeyCounter.current++;
			});
		} else {
			_clonableUses = [];
		}
		return _clonableUses;
	};

	const createNewUseHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localUses = structuredClone(localUses);
		_localUses.push({
			dataKey: newKey,
			internal: false,
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalUses(_localUses);
	};

	const handleDeleteUse = (e, dataKey) => {
		let _localUses = structuredClone(localUses);
		_localUses = _localUses.filter((item) => item.dataKey !== dataKey);
		setLocalUses(_localUses);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localUses = structuredClone(localUses);
		for (const item of _localUses) {
			delete item.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localUses);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex][field] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalUsesData((originalUsesData) => {
			return {
				...originalUsesData,
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

			for (let i = 0; i < localUses.length; i++) {
				const _item = cleanForValidation(localUses[i]);
				const result = await validationService.validate(endpoint, _item);
				const dataKey = localUses[i].dataKey;
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
				<Button label="New Use" icon="pi pi-plus" onClick={createNewUseHandler} />
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
				<Column header="Uses" />
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
				<h3>Uses</h3>
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={localUses}
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
								<DeleteAction deletionHandler={handleDeleteUse} id={editorOptions?.rowData?.dataKey} />
							)}
							className="max-w-4rem"
							bodyClassName="text-center"
							headerClassName="surface-0"
							frozen
						/>
						<Column
							editor={(editorOptions) => <UsesTableEditor editorOptions={editorOptions} />}
							field="uses"
							header="Uses"
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
