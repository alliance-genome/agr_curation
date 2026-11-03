import { Button } from 'primereact/button';
import { useRef } from 'react';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useSubResourceRows } from '../../../hooks/useSubResourceRows';
import { useControlledVocabularyService } from '../../../service/useControlledVocabularyService';
import { SubResourceSaveButton } from '../../../components/SubResourceSaveButton';
import { SubResourceLoadError } from '../../../components/SubResourceLoadError';
import { useSubResourceFormStrategy } from '../../../hooks/useSubResourceFormStrategy';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../../../utils/rows';
import { SynonymsFormTable } from './SynonymsFormTable';

/**
 * A detail or create page's synonyms section.
 *
 * Its rows are saved by the page along with the entity, and on the detail page also by this section's own
 * button, which saves them alone. Synonyms are written through their own sub-resource rather than in the
 * entity payload, so either save makes that call itself.
 *
 * @param {Object} props
 * @param {string} props.type the name the page provides the list under
 * @param {string} [props.labelColumnSize] grid class for the section label column
 * @param {'detail'|'create'} [props.mode]
 */
export const SynonymsForm = ({ type, labelColumnSize, mode = 'detail' }) => {
	const subResource = useSubResource(type);
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
			titleAction={isDetail && <SubResourceSaveButton type={type} />}
			button={
				<>
					<Button label="Add Synonym" onClick={createNewRowHandler} className="w-4 p-button-text" />
					<SubResourceLoadError type={type} />
				</>
			}
		/>
	);
};
