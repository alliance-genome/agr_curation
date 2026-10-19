import { useRef } from 'react';
import { Button } from 'primereact/button';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { useSubResourceRows } from '../../../hooks/useSubResourceRows';
import { SubResourceSaveButton } from '../SubResourceSaveButton';
import { SubResourceLoadError } from '../SubResourceLoadError';
import { CrossReferencesTable } from './CrossReferencesTable';
import { applyCrossReferenceFieldChange, buildNewCrossReference } from './utils';

/**
 * The allele detail and create pages' cross references section.
 *
 * Its rows are saved by the page along with the allele, and on the detail page also by this section's
 * own button, which saves them alone. Cross references are written through their own sub-resource
 * rather than in the allele payload, so either save makes that call itself.
 *
 * @param {Object} props
 * @param {'detail'|'create'} [props.mode]
 */
export const CrossReferencesForm = ({ mode = 'detail' }) => {
	const subResource = useSubResource('crossReferences');
	const { rows: crossReferences, errorMessages, isDirty } = subResource;
	const { editingRows, addRow, updateRow, deletionHandler, onRowEditChange } = useSubResourceRows(subResource);
	const tableRef = useRef(null);
	const isDetail = mode === 'detail';

	const onFieldChange = (dataKey, field, value) => {
		updateRow(dataKey, (crossReference) => applyCrossReferenceFieldChange(crossReference, field, value));
	};

	const createNewCrossReferenceHandler = (event) => {
		event.preventDefault();
		addRow(buildNewCrossReference());
	};

	return (
		<FormTableWrapper
			table={
				<CrossReferencesTable
					crossReferences={crossReferences}
					editingRows={editingRows}
					onRowEditChange={onRowEditChange}
					tableRef={tableRef}
					errorMessages={errorMessages}
					deletionHandler={deletionHandler}
					onFieldChange={onFieldChange}
					showObsolete={isDetail}
				/>
			}
			tableName="Cross References"
			showTable={crossReferences.length > 0}
			isPending={isDetail && isDirty}
			titleAction={isDetail && <SubResourceSaveButton type="crossReferences" />}
			button={
				<div className="flex gap-2">
					<Button label="Add Cross Reference" onClick={createNewCrossReferenceHandler} className="p-button-text" />
					<SubResourceLoadError type="crossReferences" />
				</div>
			}
		/>
	);
};
