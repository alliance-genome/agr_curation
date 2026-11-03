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
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../utils';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { TableField } from '../../../components/Editors/fields/TableField';
import { useLocalRowsStrategy } from '../../../components/Editors/fields/strategies/useLocalRowsStrategy';
import { MultiAutocomplete } from '../../../components/Editors/widgets/MultiAutocomplete';
import { BooleanTableEditor } from '../../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { EvidenceTableEditor } from '../../../components/Editors/autocomplete/references/EvidenceTableEditor';
import { mutationTypeLabel, mutationTypesSearch } from '../../../components/Editors/autocomplete/ontology/utils';

/**
 * Dialog for editing a main-table row's mutation types. Edits stay local until Keep Edits
 * validates every row and hands them back through the row's editor callback.
 *
 * @param {object} originalMutationTypesData - the row's mutation types, dialog visibility,
 *   row index and PrimeReact editor options
 * @param {Function} setOriginalMutationTypesData - its setter
 * @param {object} errorMessagesMainRow - the main table's errors, keyed by row index
 * @param {Function} setErrorMessagesMainRow - its setter
 * @returns {JSX.Element}
 */
export const MutationTypesEditDialog = ({
	originalMutationTypesData,
	setOriginalMutationTypesData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalMutationTypes, isInEdit, dialog, rowIndex, mainRowProps } = originalMutationTypesData;
	const [localMutationTypes, setLocalMutationTypes] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const strategy = useLocalRowsStrategy({
		rows: localMutationTypes,
		setRows: setLocalMutationTypes,
		errors: errorMessages,
	});

	const showDialogHandler = () => {
		let _localMutationTypes = cloneMutationTypes(originalMutationTypes);
		setLocalMutationTypes(_localMutationTypes);

		let rowsObject = {};
		if (_localMutationTypes) {
			_localMutationTypes.forEach((mt) => {
				rowsObject[`${mt.dataKey}`] = true;
			});
		}
		setEditingRows(rowsObject);
		setErrorMessages({});
	};

	const hideDialog = () => {
		setErrorMessages({});
		setOriginalMutationTypesData((originalMutationTypesData) => {
			return {
				...originalMutationTypesData,
				dialog: false,
			};
		});
		setLocalMutationTypes([]);
	};

	const cloneMutationTypes = (clonableMutationTypes) => {
		let _clonableMutationTypes = structuredClone(clonableMutationTypes);
		if (_clonableMutationTypes) {
			_clonableMutationTypes.forEach((mt) => {
				addDataKey(mt);
			});
		} else {
			_clonableMutationTypes = [];
		}
		return _clonableMutationTypes;
	};

	const createNewMutationTypeHandler = () => {
		const _localMutationTypes = structuredClone(localMutationTypes);
		const newRow = {
			internal: false,
		};
		addDataKey(newRow);
		_localMutationTypes.push(newRow);
		let _editingRows = { ...editingRows, ...{ [`${newRow.dataKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalMutationTypes(_localMutationTypes);
	};

	const handleDeleteMutationType = (e, dataKey) => {
		let _localMutationTypes = structuredClone(localMutationTypes);
		_localMutationTypes = _localMutationTypes.filter((mt) => mt.dataKey !== dataKey);
		setLocalMutationTypes(_localMutationTypes);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localMutationTypes = structuredClone(localMutationTypes);
		for (const mt of _localMutationTypes) {
			delete mt.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localMutationTypes);
		}

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		let messageObject = {
			severity: 'warn',
			message: 'Pending Edits!',
		};
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex]['alleleMutationTypes'] = messageObject;
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalMutationTypesData((originalMutationTypesData) => {
			return {
				...originalMutationTypesData,
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

			for (let i = 0; i < localMutationTypes.length; i++) {
				const _mt = cleanForValidation(localMutationTypes[i]);
				const result = await validationService.validate(Endpoints.SlotAnnotation.ALLELE_MUTATION_TYPE, _mt);
				const dataKey = localMutationTypes[i].dataKey;
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
				<Button label="New Mutation Type" icon="pi pi-plus" onClick={createNewMutationTypeHandler} />
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
				<Column header="Mutation Types" />
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
				<h3>Mutation Types</h3>
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={localMutationTypes}
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
								<DeleteAction deletionHandler={handleDeleteMutationType} id={editorOptions?.rowData?.dataKey} />
							)}
							className="max-w-4rem"
							bodyClassName="text-center"
							headerClassName="surface-0"
							frozen
						/>
						<Column
							editor={(editorOptions) => (
								<TableField editorOptions={editorOptions} field="mutationTypes">
									{(binding) => (
										<MultiAutocomplete
											{...binding}
											search={mutationTypesSearch}
											selectedItemTemplate={mutationTypeLabel}
										/>
									)}
								</TableField>
							)}
							field="mutationTypes"
							header="Mutation Types"
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
