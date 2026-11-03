import { Button } from 'primereact/button';
import { useRef } from 'react';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { TableEditorProvider } from '../../../components/Editors/fields/TableEditorContext';
import { useSubResourceRows } from '../../../hooks/useSubResourceRows';
import { SubResourceSaveButton } from '../SubResourceSaveButton';
import { SubResourceLoadError } from '../SubResourceLoadError';
import { useSubResourceFormStrategy } from '../useSubResourceFormStrategy';
import { addDataKey, KEEP_ROWS_IN_EDIT_MODE } from '../utils';
import { SecondaryIdsFormTable } from './SecondaryIdsFormTable';

/**
 * The allele detail and create pages' secondary IDs section.
 *
 * Its rows are saved by the page along with the allele, and on the detail page also by this section's own
 * button, which saves them alone. Secondary IDs are written through their own sub-resource rather than in the
 * allele payload, so either save makes that call itself.
 *
 * @param {Object} props
 * @param {'detail'|'create'} [props.mode]
 */
export const SecondaryIdsForm = ({ mode = 'detail' }) => {
	const subResource = useSubResource('alleleSecondaryIds');
	const { rows: secondaryIds, isDirty } = subResource;
	const { editingRows, addRow, deletionHandler } = useSubResourceRows(subResource, { prepend: true });
	const strategy = useSubResourceFormStrategy(subResource);
	const tableRef = useRef(null);
	const isDetail = mode === 'detail';

	const createNewRowHandler = (event) => {
		event.preventDefault();
		const newRow = {
			internal: false,
			obsolete: false,
			secondaryId: '',
		};

		addDataKey(newRow);
		addRow(newRow);
	};

	return (
		<FormTableWrapper
			table={
				<TableEditorProvider strategy={strategy}>
					<SecondaryIdsFormTable
						secondaryIds={secondaryIds}
						editingRows={editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
					/>
				</TableEditorProvider>
			}
			tableName="Secondary IDs"
			showTable={secondaryIds.length > 0}
			isPending={isDetail && isDirty}
			titleAction={isDetail && <SubResourceSaveButton type="alleleSecondaryIds" />}
			button={
				<>
					<Button label="Add Secondary ID" onClick={createNewRowHandler} className="w-4 p-button-text" />
					<SubResourceLoadError type="alleleSecondaryIds" />
				</>
			}
		/>
	);
};
