import { useMemo, useRef } from 'react';
import { Button } from 'primereact/button';
import { Message } from 'primereact/message';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { CrossReferencesTable } from './CrossReferencesTable';
import { applyCrossReferenceFieldChange, buildNewCrossReference } from './utils';

/**
 * The allele detail and create pages' cross references section.
 *
 * Its rows are saved by the page, with the allele. Cross references are written through their own
 * sub-resource rather than in the allele payload, so the page makes that second call itself once the
 * allele is saved.
 *
 * @param {Object} props
 * @param {'detail'|'create'} [props.mode]
 */
export const CrossReferencesForm = ({ mode = 'detail' }) => {
	const { crossReferences, setCrossReferences, errorMessages, loadError } = useSubResource('crossReferences');
	const tableRef = useRef(null);
	const isDetail = mode === 'detail';

	const editingRows = useMemo(
		() => Object.fromEntries(crossReferences.map((crossReference) => [crossReference.dataKey, true])),
		[crossReferences]
	);

	const onRowEditChange = () => {
		return null;
	};

	const onFieldChange = (dataKey, field, value) => {
		setCrossReferences((previous) =>
			previous.map((crossReference) =>
				crossReference.dataKey === dataKey
					? applyCrossReferenceFieldChange(crossReference, field, value)
					: crossReference
			)
		);
	};

	const createNewCrossReferenceHandler = (event) => {
		event.preventDefault();
		setCrossReferences((previous) => [...previous, buildNewCrossReference()]);
	};

	const deletionHandler = (event, dataKey) => {
		event.preventDefault();
		setCrossReferences((previous) => previous.filter((crossReference) => crossReference.dataKey !== dataKey));
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
			button={
				<div className="flex gap-2">
					<Button label="Add Cross Reference" onClick={createNewCrossReferenceHandler} className="p-button-text" />
					{loadError && (
						<Message
							severity="error"
							text="Could not load these cross references, so they cannot be saved. Reload the page to try again."
						/>
					)}
				</div>
			}
		/>
	);
};
