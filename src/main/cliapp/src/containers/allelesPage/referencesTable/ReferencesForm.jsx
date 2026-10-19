import { useRef } from 'react';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { Message } from 'primereact/message';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { ReferencesFormTable } from './ReferencesFormTable';
import { NewReferenceField } from './NewReferenceField';

/**
 * The allele detail and create pages' references section.
 *
 * Its rows are saved by the page along with the allele, and on the detail page also by this section's
 * own button, which saves them alone. References are written through their own sub-resource rather than
 * in the allele payload, so either save makes that call itself.
 *
 * @param {Object} props
 * @param {'detail'|'create'} [props.mode]
 */
export const ReferencesForm = ({ mode = 'detail' }) => {
	const {
		rows: references,
		setRows: setReferences,
		errorMessages,
		isLoading,
		loadError,
		isSaving,
		isDirty,
		save,
	} = useSubResource('references');
	const tableRef = useRef(null);
	const toast = useRef(null);
	const isDetail = mode === 'detail';

	const onRowEditChange = () => {
		return null;
	};

	const deletionHandler = (event, dataKey) => {
		event.preventDefault();
		setReferences((previous) => previous.filter((reference) => reference.dataKey !== dataKey));
	};

	const saveHandler = async (event) => {
		event.preventDefault();
		const outcome = await save();

		if (outcome.isSuccess) {
			toast.current.show({ severity: 'success', summary: 'Successful', detail: 'References Saved' });
		} else {
			toast.current.show([
				{ life: 7000, severity: 'error', summary: 'References: ', detail: outcome.message, sticky: false },
			]);
		}
	};

	return (
		<>
			<Toast ref={toast} position="top-right" />
			<FormTableWrapper
				table={
					<ReferencesFormTable
						references={references}
						editingRows={{}}
						errorMessages={errorMessages}
						onRowEditChange={onRowEditChange}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
						className="w-1"
					/>
				}
				tableName="References"
				showTable={references.length > 0}
				isPending={isDetail && isDirty}
				titleAction={
					isDetail && (
						<Button
							label="Save References"
							icon={isSaving ? 'pi pi-spin pi-spinner' : 'pi pi-check'}
							onClick={saveHandler}
							// Saving replaces the stored list with what is on screen, so it has to wait for
							// the read. Saving a table that is still loading, or that failed to load, would
							// submit an empty list and detach every reference the allele has. With nothing
							// edited there is nothing to save.
							disabled={!isDirty || isSaving || isLoading || Boolean(loadError)}
							severity="success"
						/>
					)
				}
				button={
					<>
						<NewReferenceField />
						{loadError && (
							<Message
								severity="error"
								text="Could not load these references, so they cannot be saved. Reload the page to try again."
							/>
						)}
					</>
				}
				includeField={true}
			/>
		</>
	);
};
