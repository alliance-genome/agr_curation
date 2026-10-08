import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { ReferencesFormTable } from './ReferencesFormTable';
import { useRef } from 'react';
import { NewReferenceField } from './NewReferenceField';

/**
 * The allele detail and create pages' references section. Its rows are written through the allele's
 * references sub-resource rather than in the allele payload.
 */
export const ReferencesForm = () => {
	const { references, setReferences, errorMessages } = useSubResource('references');
	const tableRef = useRef(null);

	const onRowEditChange = () => {
		return null;
	};

	const deletionHandler = (event, dataKey) => {
		event.preventDefault();
		setReferences((previous) => previous.filter((reference) => reference.dataKey !== dataKey));
	};

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
			button={<NewReferenceField />}
			includeField={true}
		/>
	);
};
