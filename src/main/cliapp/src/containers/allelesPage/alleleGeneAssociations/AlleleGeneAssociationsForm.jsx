import { useRef } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { useSubResourceRows } from '../../../hooks/useSubResourceRows';
import { SubResourceSaveButton } from '../SubResourceSaveButton';
import { SubResourceLoadError } from '../SubResourceLoadError';
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
	const subResource = useSubResource('alleleGeneAssociations');
	const { rows: alleleGeneAssociations, errorMessages, setErrorMessages, isDirty } = subResource;
	const { editingRows, addRow, updateRow, deletionHandler } = useSubResourceRows(subResource, {
		prepend: true,
	});
	const tableRef = useRef(null);
	const isDetail = mode === 'detail';
	const relationTerms = useVocabularyTermSetService('allele_gene_relation');

	const changeRow = (dataKey, changes) => updateRow(dataKey, (association) => ({ ...association, ...changes }));

	const createNewGeneAssociationHandler = (event) => {
		event.preventDefault();
		const newAlleleGeneAssociation = {};
		addDataKey(newAlleleGeneAssociation);
		addRow(newAlleleGeneAssociation);
	};

	const strategy = useDataKeyRowsStrategy({
		rows: alleleGeneAssociations,
		errors: errorMessages,
		write: ({ rowKey, field, value }) =>
			changeRow(
				rowKey,
				field === 'evidence'
					? { evidence: value, evidenceCurieSearchFilter: generateCurieSearchField(value) }
					: { [field]: value }
			),
	});

	const relatedNoteOnChangeHandler = (value, props) => {
		props.editorCallback(value[0]);
		changeRow(props.rowData?.dataKey, { relatedNote: value[0] });
	};

	return (
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
			titleAction={isDetail && <SubResourceSaveButton type="alleleGeneAssociations" />}
			button={
				<>
					<Button
						label="Add Gene Association"
						onClick={createNewGeneAssociationHandler}
						className="p-button-text -mt-3"
					/>
					<SubResourceLoadError type="alleleGeneAssociations" />
				</>
			}
		/>
	);
};
