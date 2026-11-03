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
import { FunctionalImpactsFormTable } from './FunctionalImpactsFormTable';

/**
 * The allele detail and create pages' functional impacts section.
 *
 * Its rows are saved by the page along with the allele, and on the detail page also by this section's own
 * button, which saves them alone. Functional impacts are written through their own sub-resource rather than in the
 * allele payload, so either save makes that call itself.
 *
 * @param {Object} props
 * @param {'detail'|'create'} [props.mode]
 */
export const FunctionalImpactsForm = ({ mode = 'detail' }) => {
	const subResource = useSubResource('alleleFunctionalImpacts');
	const { rows: functionalImpacts, isDirty } = subResource;
	const { editingRows, addRow, deletionHandler } = useSubResourceRows(subResource, { prepend: true });
	const strategy = useSubResourceFormStrategy(subResource);
	const tableRef = useRef(null);
	const isDetail = mode === 'detail';

	const createNewRowHandler = (event) => {
		event.preventDefault();
		const newRow = {
			phenotypeStatement: '',
			internal: false,
		};

		addDataKey(newRow);
		addRow(newRow);
	};

	return (
		<FormTableWrapper
			table={
				<TableEditorProvider strategy={strategy}>
					<FunctionalImpactsFormTable
						functionalImpacts={functionalImpacts}
						editingRows={editingRows}
						onRowEditChange={KEEP_ROWS_IN_EDIT_MODE}
						tableRef={tableRef}
						deletionHandler={deletionHandler}
					/>
				</TableEditorProvider>
			}
			tableName="Functional Impacts"
			showTable={functionalImpacts.length > 0}
			isPending={isDetail && isDirty}
			titleAction={isDetail && <SubResourceSaveButton type="alleleFunctionalImpacts" />}
			button={
				<>
					<Button label="Add Functional Impact" onClick={createNewRowHandler} className="w-4 p-button-text" />
					<SubResourceLoadError type="alleleFunctionalImpacts" />
				</>
			}
		/>
	);
};
