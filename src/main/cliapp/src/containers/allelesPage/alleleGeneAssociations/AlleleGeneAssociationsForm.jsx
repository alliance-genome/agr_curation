import { useMemo, useRef } from 'react';
import { Button } from 'primereact/button';
import { Message } from 'primereact/message';
import { Toast } from 'primereact/toast';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { AlleleGeneAssociationsFormTable } from './AlleleGeneAssociationsFormTable';
import { addDataKey, generateCurieSearchField, KEEP_ROWS_IN_EDIT_MODE } from '../utils';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useDataKeyRowsStrategy } from '../../../components/Editors/fields/strategies/useDataKeyRowsStrategy';
import { useVocabularyTermSetService } from '../../../service/useVocabularyTermSetService';

/**
 * The allele detail and create pages' gene associations section.
 *
 * Its rows are saved by the page along with the allele, and on the detail page also by this section's own
 * button, which saves them alone. Gene associations are written through their own sub-resource rather than in
 * the allele payload, so either save makes that call itself.
 *
 * @param {Object} props
 * @param {number} [props.labelColumnSize]
 * @param {'detail'|'create'} [props.mode]
 */
export const AlleleGeneAssociationsForm = ({ labelColumnSize, mode = 'detail' }) => {
	const {
		rows: alleleGeneAssociations,
		setRows: setAlleleGeneAssociations,
		errorMessages,
		setErrorMessages,
		isLoading,
		loadError,
		isSaving,
		isDirty,
		save,
	} = useSubResource('alleleGeneAssociations');
	const tableRef = useRef(null);
	const toast = useRef(null);
	const isDetail = mode === 'detail';
	const relationTerms = useVocabularyTermSetService('allele_gene_relation');

	const editingRows = useMemo(
		() => Object.fromEntries(alleleGeneAssociations.map((association) => [association.dataKey, true])),
		[alleleGeneAssociations]
	);

	const updateRow = (dataKey, changes) => {
		setAlleleGeneAssociations((previous) =>
			previous.map((association) => (association.dataKey === dataKey ? { ...association, ...changes } : association))
		);
	};

	const createNewGeneAssociationHandler = (event) => {
		event.preventDefault();
		const newAlleleGeneAssociation = {};
		addDataKey(newAlleleGeneAssociation);
		setAlleleGeneAssociations((previous) => [newAlleleGeneAssociation, ...previous]);
	};

	const strategy = useDataKeyRowsStrategy({
		rows: alleleGeneAssociations,
		errors: errorMessages,
		write: ({ rowKey, field, value }) =>
			updateRow(
				rowKey,
				field === 'evidence'
					? { evidence: value, evidenceCurieSearchFilter: generateCurieSearchField(value) }
					: { [field]: value }
			),
	});

	const relatedNoteOnChangeHandler = (value, props) => {
		props.editorCallback(value[0]);
		updateRow(props.rowData?.dataKey, { relatedNote: value[0] });
	};

	const deletionHandler = (event, dataKey) => {
		event.preventDefault();
		setAlleleGeneAssociations((previous) => previous.filter((association) => association.dataKey !== dataKey));
		setErrorMessages((previous) => {
			const remaining = { ...previous };
			delete remaining[dataKey];
			return remaining;
		});
	};

	const saveHandler = async (event) => {
		event.preventDefault();
		const outcome = await save();

		if (outcome.isSuccess) {
			toast.current.show({ severity: 'success', summary: 'Successful', detail: 'Gene Associations Saved' });
		} else {
			toast.current.show([
				{ life: 7000, severity: 'error', summary: 'Gene associations: ', detail: outcome.message, sticky: false },
			]);
		}
	};

	return (
		<>
			<Toast ref={toast} position="top-right" />
			<FormTableWrapper
				labelColumnSize={labelColumnSize}
				table={
					<TableEditorProvider strategy={strategy}>
						<AlleleGeneAssociationsFormTable
							alleleGeneAssociations={alleleGeneAssociations}
							editingRows={editingRows}
							onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
							tableRef={tableRef}
							deletionHandler={deletionHandler}
							errorMessages={errorMessages}
							setErrorMessages={setErrorMessages}
							relatedNoteOnChangeHandler={relatedNoteOnChangeHandler}
							relationTerms={relationTerms}
						/>
					</TableEditorProvider>
				}
				tableName="Allele Gene Associations"
				showTable={alleleGeneAssociations.length > 0}
				isPending={isDetail && isDirty}
				titleAction={
					isDetail && (
						<Button
							label="Save Gene Associations"
							icon={isSaving ? 'pi pi-spin pi-spinner' : 'pi pi-check'}
							onClick={saveHandler}
							// Saving replaces the stored list with what is on screen, so it has to wait for the read.
							// Saving a table that is still loading, or that failed to load, would submit an empty list
							// and delete every gene association the allele has. With nothing edited there is nothing
							// to save.
							disabled={!isDirty || isSaving || isLoading || Boolean(loadError)}
							severity="success"
						/>
					)
				}
				button={
					<>
						<Button
							label="Add Gene Association"
							onClick={createNewGeneAssociationHandler}
							className="p-button-text -mt-3"
						/>
						{loadError && (
							<Message
								severity="error"
								text="Could not load these gene associations, so they cannot be saved. Reload the page to try again."
							/>
						)}
					</>
				}
			/>
		</>
	);
};
