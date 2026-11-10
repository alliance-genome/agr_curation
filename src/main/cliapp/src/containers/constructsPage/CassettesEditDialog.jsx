import { useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { DeleteAction } from '../../components/Actions/DeletionAction';
import { ValidationService } from '../../service/ValidationService';
import { useVocabularyTermSetService } from '../../service/useVocabularyTermSetService';
import { KEEP_ROWS_IN_EDIT_MODE, addDataKey } from '../../utils/rows';
import { TableEditorProvider } from '../../components/Editors/fields/TableEditorContext';
import { useLocalRowsStrategy } from '../../components/Editors/fields/strategies/useLocalRowsStrategy';
import { ControlledVocabularyTableEditor } from '../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { AutocompleteSingleTableEditor } from '../../components/Editors/autocomplete/base/AutocompleteSingleTableEditor';
import { Endpoints } from '../../constants/Endpoints';
import { AUTOCOMPLETE_CONFIGS, getAutocompleteFields } from '../../constants/FilterFields';
import { cassetteDisplayValue } from './utils';

const OBJECT_FIELD = 'constructCassetteAssociationObject';

const cassetteSearchConfig = {
	endpoint: Endpoints.Entity.CASSETTE,
	autocompleteFields: getAutocompleteFields(AUTOCOMPLETE_CONFIGS.cassetteAutocompleteConfig),
	filterName: 'constructCassetteFilter',
	valueDisplay: (item) => <div>{cassetteDisplayValue(item)}</div>,
};

export const CassettesEditDialog = ({
	originalCassettesData,
	setOriginalCassettesData,
	errorMessagesMainRow,
	setErrorMessagesMainRow,
}) => {
	const { originalCassettes, isInEdit, dialog, rowIndex, mainRowProps } = originalCassettesData;
	const [localCassettes, setLocalCassettes] = useState([]);
	const [editingRows, setEditingRows] = useState({});
	const [errorMessages, setErrorMessages] = useState({});
	const [isValidating, setIsValidating] = useState(false);
	const validationService = new ValidationService();
	const toast_topright = useRef(null);

	const relationTerms = useVocabularyTermSetService('construct_cassette_relation');

	const strategy = useLocalRowsStrategy({
		rows: localCassettes,
		setRows: setLocalCassettes,
		errors: errorMessages,
	});

	const showDialogHandler = () => {
		const _localCassettes = structuredClone(originalCassettes || []);
		_localCassettes.forEach(addDataKey);
		setLocalCassettes(_localCassettes);
		setEditingRows({});
		setErrorMessages({});
	};

	const hideDialog = () => {
		setErrorMessages({});
		setOriginalCassettesData((originalCassettesData) => ({
			...originalCassettesData,
			dialog: false,
		}));
		setLocalCassettes([]);
	};

	const createNewCassetteHandler = () => {
		const newRow = {
			relation: null,
			[OBJECT_FIELD]: null,
			evidence: [],
			relatedNotes: [],
			internal: false,
			obsolete: false,
		};
		addDataKey(newRow);
		setEditingRows((previous) => ({ ...previous, [newRow.dataKey]: true }));
		setLocalCassettes((previous) => [newRow, ...previous]);
	};

	const handleDeleteCassette = (event, dataKey) => {
		setLocalCassettes((previous) => previous.filter((row) => row.dataKey !== dataKey));
		setEditingRows((previous) => {
			const updated = { ...previous };
			delete updated[dataKey];
			return updated;
		});
	};

	const saveDataHandler = () => {
		const _localCassettes = structuredClone(localCassettes);
		_localCassettes.forEach((row) => delete row.dataKey);
		mainRowProps?.editorCallback?.(_localCassettes);

		const errorMessagesCopy = structuredClone(errorMessagesMainRow);
		errorMessagesCopy[rowIndex] = {};
		errorMessagesCopy[rowIndex].constructCassetteAssociations = { severity: 'warn', message: 'Pending Edits!' };
		setErrorMessagesMainRow({ ...errorMessagesCopy });

		setOriginalCassettesData((originalCassettesData) => ({
			...originalCassettesData,
			dialog: false,
		}));
	};

	const validateAndSave = async () => {
		setIsValidating(true);
		try {
			const newErrorMessages = {};
			for (const row of localCassettes.filter((row) => !row.id)) {
				if (row[OBJECT_FIELD] && !row[OBJECT_FIELD].id) {
					newErrorMessages[row.dataKey] = {
						[OBJECT_FIELD]: { severity: 'error', message: 'Select from the autosuggest' },
					};
					continue;
				}
				const { dataKey, ...association } = row;
				const result = await validationService.validate(Endpoints.Entity.CONSTRUCT_CASSETTE_ASSOCIATION, association);
				if (result.isError) {
					newErrorMessages[dataKey] = {};
					Object.keys(result.data || {}).forEach((field) => {
						newErrorMessages[dataKey][field] = { severity: 'error', message: result.data[field] };
					});
				}
			}

			if (Object.keys(newErrorMessages).length > 0) {
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

	const footerTemplate = () => (
		<div>
			<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
			<Button label="Add Cassette" icon="pi pi-plus" onClick={createNewCassetteHandler} />
			<Button
				label="Keep Edits"
				icon={isValidating ? 'pi pi-spin pi-spinner' : 'pi pi-check'}
				onClick={validateAndSave}
				disabled={isValidating}
			/>
		</div>
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
				<h3>Cassettes</h3>
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={localCassettes}
						dataKey="dataKey"
						showGridlines
						editMode="row"
						editingRows={editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						cellMemo={false}
					>
						<Column
							editor={(editorOptions) => (
								<DeleteAction deletionHandler={handleDeleteCassette} id={editorOptions?.rowData?.dataKey} />
							)}
							header="Actions"
							className="max-w-4rem"
							bodyClassName="text-center"
							headerClassName="surface-0"
							frozen
						/>
						<Column
							field="relation"
							header="Relation"
							headerClassName="surface-0"
							body={(rowData) => rowData.relation?.name}
							editor={(editorOptions) => (
								<ControlledVocabularyTableEditor
									editorOptions={editorOptions}
									field="relation"
									options={relationTerms}
									dataKey="id"
									showClear={false}
								/>
							)}
						/>
						<Column
							field={OBJECT_FIELD}
							header="Cassette"
							headerClassName="surface-0"
							body={(rowData) => cassetteDisplayValue(rowData[OBJECT_FIELD])}
							editor={(editorOptions) => (
								<AutocompleteSingleTableEditor
									editorOptions={editorOptions}
									field={OBJECT_FIELD}
									subField="primaryExternalId"
									toDisplay={(entity) => (entity?.id ? cassetteDisplayValue(entity) : entity?.primaryExternalId ?? '')}
									{...cassetteSearchConfig}
								/>
							)}
						/>
						<Column field="updatedBy.uniqueId" header="Updated By" headerClassName="surface-0" />
						<Column field="dateUpdated" header="Date Updated" headerClassName="surface-0" />
					</DataTable>
				</TableEditorProvider>
			</Dialog>
		</div>
	);
};
