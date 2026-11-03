import { Button } from 'primereact/button';
import { useRef } from 'react';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useSubResourceRows } from '../../../hooks/useSubResourceRows';
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';
import { SubResourceSaveButton } from '../SubResourceSaveButton';
import { SubResourceLoadError } from '../SubResourceLoadError';
import { useSubResourceFormStrategy } from '../useSubResourceFormStrategy';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../utils';
import { SynonymsFormTable } from './SynonymsFormTable';

/**
 * The allele detail and create pages' synonyms section.
 *
 * Its rows are saved by the page along with the allele, and on the detail page also by this section's own
 * button, which saves them alone. Synonyms are written through their own sub-resource rather than in the
 * allele payload, so either save makes that call itself.
 *
 * @param {Object} props
 * @param {string} [props.labelColumnSize] grid class for the section label column
 * @param {'detail'|'create'} [props.mode]
 */
export const SynonymsForm = ({ labelColumnSize, mode = 'detail' }) => {
	const subResource = useSubResource('alleleSynonyms');
	const { rows: synonyms, isDirty } = subResource;
	const { editingRows, addRow, deletionHandler } = useSubResourceRows(subResource, { prepend: true });
	const strategy = useSubResourceFormStrategy(subResource);
	const tableRef = useRef(null);
	const isDetail = mode === 'detail';
	const synonymScopeTerms = useControlledVocabularyService('synonym_scope');
	// Synonym name types come from the name_type vocabulary, where symbol and full name use a
	// vocabulary term set.
	const nameTypeTerms = useControlledVocabularyService('name_type');

	const createNewRowHandler = (event) => {
		event.preventDefault();
		const newRow = {
			synonymUrl: '',
			internal: false,
			obsolete: false,
			nameType: null,
			formatText: '',
			displayText: '',
		};

		addDataKey(newRow);
		addRow(newRow);
	};

	return (
		<FormTableWrapper
			labelColumnSize={labelColumnSize}
			table={
				<TableEditorProvider strategy={strategy}>
					<SynonymsFormTable
						synonyms={synonyms}
						editingRows={editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
						synonymScopeTerms={synonymScopeTerms}
						nameTypeTerms={nameTypeTerms}
					/>
				</TableEditorProvider>
			}
			tableName="Synonyms"
			showTable={synonyms.length > 0}
			isPending={isDetail && isDirty}
			titleAction={isDetail && <SubResourceSaveButton type="alleleSynonyms" />}
			button={
				<>
					<Button label="Add Synonym" onClick={createNewRowHandler} className="w-4 p-button-text" />
					<SubResourceLoadError type="alleleSynonyms" />
				</>
			}
		/>
	);
};
