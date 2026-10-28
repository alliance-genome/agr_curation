import { useEffect, useMemo, useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { Message } from 'primereact/message';
import { DeleteAction } from '../../../components/Actions/DeletionAction';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useDataKeyRowsStrategy } from '../../../components/Editors/fields/strategies/useDataKeyRowsStrategy';
import { RelatedNotesEditDialog } from '../../../components/RelatedNotesEditDialog';
import { CassetteService } from '../../../service/CassetteService';
import { useVocabularyTermSetService } from '../../../service/useVocabularyTermSetService';
import { prepareComponentRows, stripComponentRow, mapRowErrors, markRowsWithTypedObject } from './utils';

const NO_MAIN_ROW_ERRORS = {};

/**
 * An editable pop-up for one of a cassette's component lists (SCRUM-6752), opened from the list's column while the
 * cassette's row is being edited. The list is read afresh, as the row holds only a trimmed copy of it, and Save
 * replaces the stored list through its own endpoint; the cassette's row then shows the saved rows.
 *
 * @param {object} data - `{dialog, cassetteId, mainRowProps}`: whether it is open, the cassette, and the row's editor
 *   options, whose `editorCallback` receives the saved rows
 * @param {Function} setData - the data's setter
 * @param {object} config - the list, from componentConfigs: its `title`, `list` (endpoint segment), `field` (the field
 *   the API files row errors under), `objectField` (the linked entity, for associations), `relationTermSet`,
 *   `noteTypeVocabularyTermSet`, `newRow()` and `columns({openRelatedNotes, relationTerms})`
 * @returns {JSX.Element}
 */
export const ComponentsEditDialog = ({ data, setData, config }) => {
	const { dialog, cassetteId, mainRowProps } = data;
	const cassetteService = useMemo(() => new CassetteService(), []);
	const [rows, setRows] = useState([]);
	const [errorMessages, setErrorMessages] = useState({});
	const [isLoading, setIsLoading] = useState(false);
	const [loadError, setLoadError] = useState(null);
	const [isSaving, setIsSaving] = useState(false);
	const [relatedNotesData, setRelatedNotesData] = useState({ dialog: false });
	const toast = useRef(null);
	const tableRef = useRef(null);
	const relationTerms = useVocabularyTermSetService(config.relationTermSet);

	useEffect(() => {
		if (!dialog || !cassetteId) return undefined;
		let cancelled = false;
		setIsLoading(true);
		setLoadError(null);
		setErrorMessages({});
		setRows([]);
		cassetteService
			.getComponentList(cassetteId, config.list)
			.then((response) => !cancelled && setRows(prepareComponentRows(response?.data?.entities)))
			.catch((error) => !cancelled && setLoadError(error))
			.finally(() => !cancelled && setIsLoading(false));
		return () => {
			cancelled = true;
		};
	}, [dialog, cassetteId, config.list, cassetteService]);

	const editingRows = useMemo(() => Object.fromEntries(rows.map((row) => [row.dataKey, true])), [rows]);

	const writeRow = (rowKey, field, value) =>
		setRows((previous) => previous.map((row) => (row.dataKey === rowKey ? { ...row, [field]: value } : row)));

	const strategy = useDataKeyRowsStrategy({
		rows,
		errors: errorMessages,
		write: ({ rowKey, field, value }) => writeRow(rowKey, field, value),
	});

	const hideDialog = () => setData((previous) => ({ ...previous, dialog: false }));

	const addRow = () => setRows((previous) => [...prepareComponentRows([config.newRow()]), ...previous]);

	const deleteRow = (event, dataKey) => {
		event.preventDefault();
		setRows((previous) => previous.filter((row) => row.dataKey !== dataKey));
		setErrorMessages((previous) => {
			const updated = { ...previous };
			delete updated[dataKey];
			return updated;
		});
	};

	// The notes of one row are edited in the shared notes pop-up, which hands them back through the row's callback
	const openRelatedNotes = (event, editorOptions) => {
		event.preventDefault();
		const rowKey = editorOptions.rowData?.dataKey;
		const row = rows.find((candidate) => candidate.dataKey === rowKey);
		setRelatedNotesData({
			originalRelatedNotes: row?.relatedNotes,
			dialog: true,
			isInEdit: true,
			rowIndex: editorOptions.rowIndex,
			mainRowProps: { ...editorOptions, editorCallback: (notes) => writeRow(rowKey, 'relatedNotes', notes) },
		});
	};

	const save = async () => {
		const marked = config.objectField ? markRowsWithTypedObject(rows, errorMessages, config.objectField) : null;
		if (marked) {
			setErrorMessages(marked);
			return;
		}

		setIsSaving(true);
		setErrorMessages({});
		try {
			const response = await cassetteService.replaceComponentList(cassetteId, config.list, rows.map(stripComponentRow));
			const saved = response?.data?.entities ?? [];
			mainRowProps?.editorCallback?.(saved);
			toast.current?.show({ severity: 'success', summary: 'Saved', detail: `${config.title} saved` });
			hideDialog();
		} catch (error) {
			const responseData = error?.response?.data;
			setErrorMessages(mapRowErrors(responseData, config.field, rows));
			toast.current?.show({
				life: 7000,
				severity: 'error',
				summary: `${config.title} not saved: `,
				detail:
					responseData?.errorMessage ?? `${error?.response?.status ?? ''} ${error?.response?.statusText ?? ''}`.trim(),
				sticky: false,
			});
		} finally {
			setIsSaving(false);
		}
	};

	const footer = (
		<div>
			<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" />
			<Button label="Add Row" icon="pi pi-plus" onClick={addRow} disabled={isLoading || Boolean(loadError)} />
			<Button
				label="Save"
				icon={isSaving ? 'pi pi-spin pi-spinner' : 'pi pi-check'}
				onClick={save}
				// Saving replaces the stored list, so it waits for the read: a list still loading, or that failed to load,
				// would be saved empty and delete every row
				disabled={isSaving || isLoading || Boolean(loadError)}
			/>
		</div>
	);

	return (
		<>
			<Toast ref={toast} position="top-right" />
			<Dialog visible={dialog} className="w-11" modal onHide={hideDialog} closable={!isSaving} footer={footer}>
				<h3>{config.title}</h3>
				{loadError && (
					<Message
						severity="error"
						text="Could not load these components, so they cannot be saved. Close and reopen to try again."
					/>
				)}
				<TableEditorProvider strategy={strategy}>
					<DataTable
						value={rows}
						dataKey="dataKey"
						showGridlines
						editMode="row"
						size="small"
						editingRows={editingRows}
						onRowEditChange={() => null}
						resizableColumns
						columnResizeMode="expand"
						cellMemo={false}
						loading={isLoading}
						ref={tableRef}
					>
						<Column
							editor={(editorOptions) => (
								<DeleteAction deletionHandler={deleteRow} id={editorOptions?.rowData?.dataKey} />
							)}
							header="Actions"
							className="max-w-4rem"
							bodyClassName="text-center"
							headerClassName="surface-0"
							frozen
						/>
						{config.columns({ openRelatedNotes, relationTerms }).map(({ field, header, editor }) => (
							<Column key={field} field={field} header={header} headerClassName="surface-0" editor={editor} />
						))}
						<Column field="updatedBy.uniqueId" header="Updated By" headerClassName="surface-0" />
						<Column field="dateUpdated" header="Date Updated" headerClassName="surface-0" />
					</DataTable>
				</TableEditorProvider>
			</Dialog>
			<RelatedNotesEditDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={NO_MAIN_ROW_ERRORS}
				setErrorMessagesMainRow={() => null}
				noteTypeVocabularyTermSet={config.noteTypeVocabularyTermSet}
			/>
		</>
	);
};
