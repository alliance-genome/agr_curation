import { useMemo, useRef } from 'react';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { Message } from 'primereact/message';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { CrossReferencesTable } from './CrossReferencesTable';
import { applyCrossReferenceFieldChange, buildNewCrossReference } from './utils';

/**
 * The allele detail and create pages' cross references section.
 *
 * It saves on its own button on the detail page, because cross references are written through their
 * own sub-resource rather than with the allele. Create has no allele to write to yet, so its rows
 * are saved by the page once the allele exists.
 *
 * @param {Object} props
 * @param {'detail'|'create'} [props.mode]
 */
export const CrossReferencesForm = ({ mode = 'detail' }) => {
	const { crossReferences, setCrossReferences, errorMessages, isLoading, loadError, isSaving, save } =
		useSubResource('crossReferences');
	const tableRef = useRef(null);
	const toast = useRef(null);
	const isDetail = mode === 'detail';

	const editingRows = useMemo(
		() => Object.fromEntries(crossReferences.map((crossReference) => [crossReference.dataKey, true])),
		[crossReferences]
	);

	const onRowEditChange = () => {
		return null;
	};

	const onFieldChange = (dataKey, field, value) => {
		setCrossReferences((previous) =>
			previous.map((crossReference) =>
				crossReference.dataKey === dataKey
					? applyCrossReferenceFieldChange(crossReference, field, value)
					: crossReference
			)
		);
	};

	const createNewCrossReferenceHandler = (event) => {
		event.preventDefault();
		setCrossReferences((previous) => [...previous, buildNewCrossReference()]);
	};

	const deletionHandler = (event, dataKey) => {
		event.preventDefault();
		setCrossReferences((previous) => previous.filter((crossReference) => crossReference.dataKey !== dataKey));
	};

	const saveHandler = async (event) => {
		event.preventDefault();
		const outcome = await save();

		if (outcome.isSuccess) {
			toast.current.show({ severity: 'success', summary: 'Successful', detail: 'Cross References Saved' });
		} else {
			toast.current.show([
				{ life: 7000, severity: 'error', summary: 'Cross references: ', detail: outcome.message, sticky: false },
			]);
		}
	};

	return (
		<>
			<Toast ref={toast} position="top-right" />
			<FormTableWrapper
				table={
					<CrossReferencesTable
						crossReferences={crossReferences}
						editingRows={editingRows}
						onRowEditChange={onRowEditChange}
						tableRef={tableRef}
						errorMessages={errorMessages}
						deletionHandler={deletionHandler}
						onFieldChange={onFieldChange}
						showObsolete={isDetail}
					/>
				}
				tableName="Cross References"
				showTable
				button={
					<div className="flex gap-2">
						<Button label="Add Cross Reference" onClick={createNewCrossReferenceHandler} className="p-button-text" />
						{isDetail && (
							<Button
								label="Save Cross References"
								icon={isSaving ? 'pi pi-spin pi-spinner' : 'pi pi-check'}
								onClick={saveHandler}
								// Saving replaces the stored list with what is on screen, so it has to wait for
								// the read. Saving a table that is still loading, or that failed to load, would
								// submit an empty list and delete every cross reference the allele has.
								disabled={isSaving || isLoading || Boolean(loadError)}
								className="p-button-text"
							/>
						)}
						{loadError && (
							<Message
								severity="error"
								text="Could not load these cross references, so they cannot be saved. Reload the page to try again."
							/>
						)}
					</div>
				}
			/>
		</>
	);
};
