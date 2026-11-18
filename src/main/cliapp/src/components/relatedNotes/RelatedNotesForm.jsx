import { Button } from 'primereact/button';
import { FormTableWrapper } from '../FormTableWrapper';
import { useSubResource } from '../SubResourcesContext';
import { useSubResourceRows } from '../../hooks/useSubResourceRows';
import { SubResourceSaveButton } from '../SubResourceSaveButton';
import { SubResourceLoadError } from '../SubResourceLoadError';
import { RelatedNotesFormTable } from './RelatedNotesFormTable';
import { useRef } from 'react';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../../utils/rows';
import { TableEditorProvider } from '../Editors/fields/TableEditorContext';
import { useDataKeyRowsStrategy } from '../Editors/fields/strategies/useDataKeyRowsStrategy';
import { useVocabularyTermSetService } from '../../service/useVocabularyTermSetService';

/**
 * A detail or create page's related notes section.
 *
 * Its rows are saved by the page along with the entity, and on the detail page also by this section's own
 * button, which saves them alone. Related notes are written through their own sub-resource rather than in the
 * entity payload, so either save makes that call itself.
 *
 * @param {Object} props
 * @param {string} props.type the name the page provides the list under
 * @param {string} [props.noteTypeVocabType] the vocabulary term set the note types come from; the allele's when
 *   omitted
 * @param {'detail'|'create'} [props.mode]
 */
export const RelatedNotesForm = ({ type, noteTypeVocabType, mode = 'detail' }) => {
	const subResource = useSubResource(type);
	const { rows: relatedNotes, errorMessages, isDirty } = subResource;
	const { editingRows, addRow, updateRow, deletionHandler } = useSubResourceRows(subResource, {
		prepend: true,
	});
	const tableRef = useRef(null);
	const isDetail = mode === 'detail';

	const noteTypeTerms = useVocabularyTermSetService(noteTypeVocabType ?? 'allele_note_type');

	const strategy = useDataKeyRowsStrategy({
		rows: relatedNotes,
		errors: errorMessages,
		write: ({ rowKey, field, value }) => updateRow(rowKey, (note) => ({ ...note, [field]: value })),
	});

	const createNewRelatedNoteHandler = (e) => {
		e.preventDefault();
		const newRelatedNote = {
			internal: false,
			obsolete: false,
			noteType: null,
			freeText: '',
			references: null,
		};

		addDataKey(newRelatedNote);
		addRow(newRelatedNote);
	};

	return (
		<FormTableWrapper
			table={
				<TableEditorProvider strategy={strategy}>
					<RelatedNotesFormTable
						relatedNotes={relatedNotes}
						editingRows={editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
						noteTypeTerms={noteTypeTerms}
					/>
				</TableEditorProvider>
			}
			tableName="Related Notes"
			showTable={relatedNotes.length > 0}
			isPending={isDetail && isDirty}
			titleAction={isDetail && <SubResourceSaveButton type={type} />}
			button={
				<>
					<Button label="Add Related Note" onClick={createNewRelatedNoteHandler} className="w-4 p-button-text" />
					<SubResourceLoadError type={type} />
				</>
			}
		/>
	);
};
