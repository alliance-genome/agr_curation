import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { useSubResourceRows } from '../../../hooks/useSubResourceRows';
import { SubResourceSaveButton } from '../SubResourceSaveButton';
import { SubResourceLoadError } from '../SubResourceLoadError';
import { RelatedNotesFormTable } from '../relatedNotes/RelatedNotesFormTable';
import { useRef } from 'react';
import { addDataKey } from '../utils';

/**
 * The allele detail and create pages' related notes section.
 *
 * Its rows are saved by the page along with the allele, and on the detail page also by this section's own
 * button, which saves them alone. Related notes are written through their own sub-resource rather than in the
 * allele payload, so either save makes that call itself.
 *
 * @param {Object} props
 * @param {'detail'|'create'} [props.mode]
 */
export const RelatedNotesForm = ({ mode = 'detail' }) => {
	const subResource = useSubResource('relatedNotes');
	const { rows: relatedNotes, errorMessages, isDirty } = subResource;
	const { editingRows, addRow, updateRow, deletionHandler, onRowEditChange } = useSubResourceRows(subResource, {
		prepend: true,
	});
	const tableRef = useRef(null);
	const isDetail = mode === 'detail';

	const changeRow = (dataKey, changes) => updateRow(dataKey, (note) => ({ ...note, ...changes }));

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

	const noteTypeOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		changeRow(props.rowData?.dataKey, { noteType: event.target.value });
	};

	const internalOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value?.name);
		changeRow(props.rowData?.dataKey, { internal: event.target?.value?.name });
	};

	const referencesOnChangeHandler = (event, setFieldValue, props) => {
		//updates value in table input box
		setFieldValue(event.target.value);
		changeRow(props.rowData?.dataKey, { references: event.target.value });
	};

	// The text editor reports the row by its index in the table, which shows the rows in the order held.
	const textOnChangeHandler = (rowIndex, event, field) => {
		changeRow(relatedNotes[rowIndex]?.dataKey, { [field]: event.target.value });
	};

	return (
		<FormTableWrapper
			table={
				<RelatedNotesFormTable
					relatedNotes={relatedNotes}
					editingRows={editingRows}
					onRowEditChange={onRowEditChange}
					tableRef={tableRef}
					deletionHandler={deletionHandler}
					errorMessages={errorMessages}
					noteTypeOnChangeHandler={noteTypeOnChangeHandler}
					textOnChangeHandler={textOnChangeHandler}
					internalOnChangeHandler={internalOnChangeHandler}
					referencesOnChangeHandler={referencesOnChangeHandler}
				/>
			}
			tableName="Related Notes"
			showTable={relatedNotes.length > 0}
			isPending={isDetail && isDirty}
			titleAction={isDetail && <SubResourceSaveButton type="relatedNotes" />}
			button={
				<>
					<Button label="Add Related Note" onClick={createNewRelatedNoteHandler} className="w-4 p-button-text" />
					<SubResourceLoadError type="relatedNotes" />
				</>
			}
		/>
	);
};
