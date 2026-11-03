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
import { InheritanceModesFormTable } from './InheritanceModesFormTable';

/**
 * The allele detail and create pages' inheritance modes section.
 *
 * Its rows are saved by the page along with the allele, and on the detail page also by this section's own
 * button, which saves them alone. Inheritance modes are written through their own sub-resource rather than in the
 * allele payload, so either save makes that call itself.
 *
 * @param {Object} props
 * @param {'detail'|'create'} [props.mode]
 */
export const InheritanceModesForm = ({ mode = 'detail' }) => {
	const subResource = useSubResource('alleleInheritanceModes');
	const { rows: inheritanceModes, isDirty } = subResource;
	const { editingRows, addRow, deletionHandler } = useSubResourceRows(subResource, { prepend: true });
	const strategy = useSubResourceFormStrategy(subResource);
	const tableRef = useRef(null);
	const isDetail = mode === 'detail';
	const inheritanceModeTerms = useControlledVocabularyService('allele_inheritance_mode');

	const createNewRowHandler = (event) => {
		event.preventDefault();
		const newRow = {
			internal: false,
			obsolete: false,
			inheritanceMode: null,
			phenotypeTerm: null,
			phenotypeStatement: '',
		};

		addDataKey(newRow);
		addRow(newRow);
	};

	return (
		<FormTableWrapper
			table={
				<TableEditorProvider strategy={strategy}>
					<InheritanceModesFormTable
						inheritanceModes={inheritanceModes}
						editingRows={editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
						inheritanceModeTerms={inheritanceModeTerms}
					/>
				</TableEditorProvider>
			}
			tableName="Inheritance Modes"
			showTable={inheritanceModes.length > 0}
			isPending={isDetail && isDirty}
			titleAction={isDetail && <SubResourceSaveButton type="alleleInheritanceModes" />}
			button={
				<>
					<Button label="Add Inheritance Mode" onClick={createNewRowHandler} className="w-4  p-button-text" />
					<SubResourceLoadError type="alleleInheritanceModes" />
				</>
			}
		/>
	);
};
