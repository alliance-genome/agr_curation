import { useEffect, useMemo, useRef, useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { Message } from 'primereact/message';
import { CassetteService } from '../../../service/CassetteService';
import { ComponentsTable } from './ComponentsTable';
import { prepareComponentRows, stripComponentRow, mapRowErrors, markRowsWithTypedObject } from './utils';

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
	const toast = useRef(null);

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

	const hideDialog = () => setData((previous) => ({ ...previous, dialog: false }));

	const addRow = () => setRows((previous) => [...prepareComponentRows([config.newRow()]), ...previous]);

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
				<ComponentsTable
					rows={rows}
					setRows={setRows}
					errorMessages={errorMessages}
					setErrorMessages={setErrorMessages}
					isLoading={isLoading}
					config={config}
				/>
			</Dialog>
		</>
	);
};
