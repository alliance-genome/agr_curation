import { useMemo, useRef } from 'react';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { Message } from 'primereact/message';
import { TableEditorProvider } from '../../components/Editors/fields/TableEditorContext';
import { useDataKeyRowsStrategy } from '../../components/Editors/fields/strategies/useDataKeyRowsStrategy';
import { useVocabularyTermSetService } from '../../service/useVocabularyTermSetService';
import { addDataKey } from '../allelesPage/utils';
import { useTransgenicToolAssociations } from './detail/useTransgenicToolAssociations';
import { TransgenicToolAssociationsFormTable } from './detail/TransgenicToolAssociationsFormTable';
import { toAssociatedTransgenicTools } from './utils';

const TITLE = 'Transgenic Tool Associations';

// The open pop-up. It is mounted only while open, so each opening reads the tool's associations afresh rather
// than showing the unsaved rows of an earlier one.
const AssociationsEditor = ({ transgenicToolId, mainRowProps, hideDialog, toast }) => {
	const tableRef = useRef(null);
	const relationTerms = useVocabularyTermSetService('transgenic_tool_association_relation');
	const { rows, setRows, errorMessages, setErrorMessages, save, isLoading, isSaving, loadError } =
		useTransgenicToolAssociations(transgenicToolId);

	const editingRows = useMemo(() => Object.fromEntries(rows.map((row) => [row.dataKey, true])), [rows]);

	const strategy = useDataKeyRowsStrategy({
		rows,
		errors: errorMessages,
		write: ({ rowKey, field, value }) =>
			setRows((previous) => previous.map((row) => (row.dataKey === rowKey ? { ...row, [field]: value } : row))),
	});

	const addRow = () => {
		// is_compatible_with is the only relation the term set holds, so a new row starts with it
		const newAssociation = {
			relation: relationTerms?.length === 1 ? relationTerms[0] : null,
			transgenicToolTransgenicToolAssociationObject: null,
			internal: false,
			obsolete: false,
		};
		addDataKey(newAssociation);
		setRows((previous) => [newAssociation, ...previous]);
	};

	const deleteRow = (event, dataKey) => {
		event.preventDefault();
		setRows((previous) => previous.filter((row) => row.dataKey !== dataKey));
		const updatedErrorMessages = structuredClone(errorMessages);
		delete updatedErrorMessages[dataKey];
		setErrorMessages(updatedErrorMessages);
	};

	const saveAssociations = async () => {
		const savedRows = rows;
		const outcome = await save();
		if (!outcome.isSuccess) {
			toast.current?.show({
				life: 7000,
				severity: 'error',
				summary: `${TITLE} not saved: `,
				detail: outcome.message,
				sticky: false,
			});
			return;
		}
		// The tool's row shows its associations as the search results summarise them. The rows just saved hold
		// each relation and picked tool, which is all the summary needs.
		mainRowProps?.editorCallback?.(toAssociatedTransgenicTools(savedRows));
		toast.current?.show({ severity: 'success', summary: 'Saved', detail: `${TITLE} saved` });
		hideDialog();
	};

	const footer = (
		<div>
			<Button label="Cancel" icon="pi pi-times" onClick={hideDialog} className="p-button-text" disabled={isSaving} />
			<Button label="Add Association" icon="pi pi-plus" onClick={addRow} disabled={isLoading || Boolean(loadError)} />
			<Button
				label="Save"
				icon={isSaving ? 'pi pi-spin pi-spinner' : 'pi pi-check'}
				onClick={saveAssociations}
				// Saving replaces the stored list, so it waits for the read: a list still loading, or that failed to load,
				// would be saved empty and delete every association
				disabled={isSaving || isLoading || Boolean(loadError)}
			/>
		</div>
	);

	return (
		<Dialog visible className="w-11" modal onHide={hideDialog} closable={!isSaving} footer={footer}>
			<h3>{TITLE}</h3>
			{loadError && (
				<Message
					severity="error"
					text="Could not load these associations, so they cannot be saved. Close and reopen to try again."
				/>
			)}
			<TableEditorProvider strategy={strategy}>
				<TransgenicToolAssociationsFormTable
					associations={rows}
					editingRows={editingRows}
					tableRef={tableRef}
					deletionHandler={deleteRow}
					relationTerms={relationTerms}
				/>
			</TableEditorProvider>
		</Dialog>
	);
};

/**
 * An editable pop-up for the associations a transgenic tool is the subject of (SCRUM-6654), opened from their
 * column while the tool's row is being edited. As on the detail page, they are read and saved through their own
 * endpoint, not with the row: Save replaces the stored list, and the row then shows the saved associations.
 *
 * @param {object} data - `{dialog, transgenicToolId, mainRowProps}`: whether it is open, the tool, and the row's
 *   editor options, whose `editorCallback` receives the saved associations as the row summarises them
 * @param {Function} setData - the data's setter
 * @returns {JSX.Element}
 */
export const TransgenicToolAssociationsEditDialog = ({ data, setData }) => {
	const { dialog, transgenicToolId, mainRowProps } = data;
	const toast = useRef(null);

	const hideDialog = () => setData((previous) => ({ ...previous, dialog: false }));

	return (
		<>
			<Toast ref={toast} position="top-right" />
			{dialog && (
				<AssociationsEditor
					transgenicToolId={transgenicToolId}
					mainRowProps={mainRowProps}
					hideDialog={hideDialog}
					toast={toast}
				/>
			)}
		</>
	);
};
