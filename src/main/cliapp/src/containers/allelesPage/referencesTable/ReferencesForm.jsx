import { useRef } from 'react';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { useSubResourceRows } from '../../../hooks/useSubResourceRows';
import { SubResourceSaveButton } from '../SubResourceSaveButton';
import { SubResourceLoadError } from '../SubResourceLoadError';
import { ReferencesFormTable } from './ReferencesFormTable';
import { NewReferenceField } from './NewReferenceField';

/**
 * The allele detail and create pages' references section.
 *
 * Its rows are saved by the page along with the allele, and on the detail page also by this section's
 * own button, which saves them alone. References are written through their own sub-resource rather than
 * in the allele payload, so either save makes that call itself.
 *
 * @param {Object} props
 * @param {'detail'|'create'} [props.mode]
 */
export const ReferencesForm = ({ mode = 'detail' }) => {
	const subResource = useSubResource('references');
	const { rows: references, errorMessages, isDirty } = subResource;
	const { deletionHandler, onRowEditChange } = useSubResourceRows(subResource);
	const tableRef = useRef(null);
	const isDetail = mode === 'detail';

	return (
		<FormTableWrapper
			table={
				<ReferencesFormTable
					references={references}
					editingRows={{}}
					errorMessages={errorMessages}
					onRowEditChange={onRowEditChange}
					tableRef={tableRef}
					deletionHandler={deletionHandler}
					className="w-1"
				/>
			}
			tableName="References"
			showTable={references.length > 0}
			isPending={isDetail && isDirty}
			titleAction={isDetail && <SubResourceSaveButton type="references" />}
			button={
				<>
					<NewReferenceField />
					<SubResourceLoadError type="references" />
				</>
			}
			includeField={true}
		/>
	);
};
