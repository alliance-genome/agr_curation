import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ColumnGroup } from 'primereact/columngroup';
import { Row } from 'primereact/row';
import { ValidationService } from '../service/ValidationService';
import { DeleteAction } from './Actions/DeletionAction';
import { Endpoints } from '../constants/Endpoints';
import { useControlledVocabularyService } from '../service/useControlledVocabularyService';
import { KEEP_ROWS_IN_EDIT_MODE } from '../utils/rows';
import { TableEditorProvider } from './Editors/fields/TableEditorContext';
import { useLocalRowsStrategy } from './Editors/fields/strategies/useLocalRowsStrategy';
import { ControlledVocabularyTableEditor } from './Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { ConditionsTableEditor } from './Editors/autocomplete/experimentalCondition/ConditionsTableEditor';
import { conditionsDetailedSearchConfig } from './Editors/autocomplete/experimentalCondition/utils';
import { BooleanTableEditor } from './Editors/dropdown/boolean/BooleanTableEditor';
import { markPendingEdits } from '../utils/pendingEdits';

/**
 * Dialog for editing a disease annotation row's condition relations. Edits stay local until Keep Edits validates
 * every relation and hands the list back through the row's editor callback.
 *
 * @param {object} originalConditionRelationsData - the row's condition relations, dialog visibility, row index and
 *   PrimeReact editor options
 * @param {Function} setOriginalConditionRelationsData - its setter
 * @param {object} errorMessagesMainRow - the main table's errors, keyed by row index
 * @param {Function} setErrorMessagesMainRow - its setter
 * @returns {JSX.Element}
 */
export const ConditionRelationsEditDialog = ({
	originalConditionRelationsData,
	setOriginalConditionRelationsData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalConditionRelations, isInEdit, dialog, rowIndex, mainRowProps } = originalConditionRelationsData;
	const [localConditionRelations, setLocalConditionRelations] = useState(null);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const dataKeyCounter = useRef(0);
	const tableRef = useRef(null);
	const toast_topright = useRef(null);

	const conditionRelationTypeTerms = useControlledVocabularyService('condition_relation');

	const strategy = useLocalRowsStrategy({
		rows: localConditionRelations,
		setRows: setLocalConditionRelations,
		errors: errorMessages,
	});

	const showDialogHandler = () => {
		let _localConditionRelations = cloneRelations(originalConditionRelations);
		setLocalConditionRelations(_localConditionRelations);

		let rowsObject = {};
		if (_localConditionRelations) {
			_localConditionRelations.forEach((relation) => {
				rowsObject[`${relation.dataKey}`] = true;
			});
		}
		setEditingRows(rowsObject);
		setErrorMessages({});
	};

	const hideDialog = () => {
		setErrorMessages({});
		setOriginalConditionRelationsData((originalConditionRelationsData) => {
			return {
				...originalConditionRelationsData,
				dialog: false,
			};
		});
		setLocalConditionRelations([]);
	};

	const cloneRelations = (clonableRelations) => {
		let _clonableRelations = structuredClone(clonableRelations);
		if (_clonableRelations) {
			_clonableRelations.forEach((relation) => {
				relation.dataKey = dataKeyCounter.current++;
			});
		} else {
			_clonableRelations = [];
		}
		return _clonableRelations;
	};

	const createNewRelationHandler = () => {
		const newKey = dataKeyCounter.current++;
		const _localConditionRelations = structuredClone(localConditionRelations);
		_localConditionRelations.push({
			dataKey: newKey,
			internal: false,
		});
		let _editingRows = { ...editingRows, ...{ [`${newKey}`]: true } };
		setEditingRows(_editingRows);
		setLocalConditionRelations(_localConditionRelations);
	};

	const handleDeleteConditionRelation = (e, dataKey) => {
		let _localConditionRelations = structuredClone(localConditionRelations);
		_localConditionRelations = _localConditionRelations.filter((relation) => relation.dataKey !== dataKey);
		setLocalConditionRelations(_localConditionRelations);
	};

	const saveDataHandler = () => {
		setErrorMessages({});
		const _localConditionRelations = structuredClone(localConditionRelations);
		for (const relation of _localConditionRelations) {
			delete relation.dataKey;
		}
		if (mainRowProps.editorCallback) {
			mainRowProps.editorCallback(_localConditionRelations);
		}

		setErrorMessagesMainRow(markPendingEdits(errorMessagesMainRow, rowIndex, 'conditionRelations'));

		setOriginalConditionRelationsData((originalConditionRelationsData) => {
			return {
				...originalConditionRelationsData,
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
		return _obj;
	};

	const validateAndSave = async () => {
		setIsValidating(true);
		try {
			let hasErrors = false;
			const newErrorMessages = {};

			for (let i = 0; i < localConditionRelations.length; i++) {
				const _relation = cleanForValidation(localConditionRelations[i]);
				const result = await validationService.validate(Endpoints.Annotation.CONDITION_RELATION, _relation);
				const dataKey = localConditionRelations[i].dataKey;
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
				<Button label="New Condition" icon="pi pi-plus" onClick={createNewRelationHandler} />
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
				<Column header="Relation" />
				<Column header="Conditions" />
				<Column header="Internal" />
			</Row>
		</ColumnGroup>
	);

	return (
		<div>
			<Toast ref={toast_topright} position="top-right" />
			<Dialog
				visible={dialog && isInEdit}
				className="w-10 min-w-40rem"
				modal
				onHide={hideDialog}
				closable={false}
				onShow={showDialogHandler}
				footer={footerTemplate}
			>
				<h3>Experimental Conditions</h3>
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={localConditionRelations}
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
								<DeleteAction deletionHandler={handleDeleteConditionRelation} id={editorOptions?.rowData?.dataKey} />
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
									field="conditionRelationType"
									options={conditionRelationTypeTerms}
									showClear={false}
								/>
							)}
							field="conditionRelationType"
							header="Relation"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => (
								<ConditionsTableEditor editorOptions={editorOptions} searchConfig={conditionsDetailedSearchConfig} />
							)}
							field="conditions"
							header="Conditions"
							headerClassName="surface-0"
						/>
						<Column
							editor={(editorOptions) => <BooleanTableEditor editorOptions={editorOptions} field="internal" />}
							field="internal"
							header="Internal"
							headerClassName="surface-0"
						/>
					</DataTable>
				</TableEditorProvider>
			</Dialog>
		</div>
	);
};
