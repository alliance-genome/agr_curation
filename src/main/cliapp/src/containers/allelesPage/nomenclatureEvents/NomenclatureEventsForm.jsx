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
import { NomenclatureEventsFormTable } from './NomenclatureEventsFormTable';

/**
 * The allele detail and create pages' nomenclature events section.
 *
 * Its rows are saved by the page along with the allele, and on the detail page also by this section's own
 * button, which saves them alone. Nomenclature events are written through their own sub-resource rather than in the
 * allele payload, so either save makes that call itself.
 *
 * @param {Object} props
 * @param {'detail'|'create'} [props.mode]
 */
export const NomenclatureEventsForm = ({ mode = 'detail' }) => {
	const subResource = useSubResource('alleleNomenclatureEvents');
	const { rows: nomenclatureEvents, isDirty } = subResource;
	const { editingRows, addRow, deletionHandler } = useSubResourceRows(subResource, { prepend: true });
	const strategy = useSubResourceFormStrategy(subResource);
	const tableRef = useRef(null);
	const isDetail = mode === 'detail';
	const nomenclatureEventTerms = useControlledVocabularyService('allele_nomenclature_event');

	const createNewRowHandler = (event) => {
		event.preventDefault();
		const newRow = {
			internal: false,
			obsolete: false,
			nomenclatureEvent: null,
		};

		addDataKey(newRow);
		addRow(newRow);
	};

	return (
		<FormTableWrapper
			table={
				<TableEditorProvider strategy={strategy}>
					<NomenclatureEventsFormTable
						nomenclatureEvents={nomenclatureEvents}
						editingRows={editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
						nomenclatureEventTerms={nomenclatureEventTerms}
					/>
				</TableEditorProvider>
			}
			tableName="Nomenclature Events"
			showTable={nomenclatureEvents.length > 0}
			isPending={isDetail && isDirty}
			titleAction={isDetail && <SubResourceSaveButton type="alleleNomenclatureEvents" />}
			button={
				<>
					<Button label="Add Nomenclature Event" onClick={createNewRowHandler} className="w-4 p-button-text" />
					<SubResourceLoadError type="alleleNomenclatureEvents" />
				</>
			}
		/>
	);
};
