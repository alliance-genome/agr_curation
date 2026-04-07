import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { SecondaryIdsFormTable } from './SecondaryIdsFormTable';
import { useRef } from 'react';

export const SecondaryIdsForm = ({ state }) => {
	const tableRef = useRef(null);

	// Convert string array to object array for DataTable
	const secondaryIdsArray = (state.construct?.secondaryIdentifiers || []).map((id, index) => ({
		id: index,
		identifier: id,
	}));

	return (
		<FormTableWrapper
			table={<SecondaryIdsFormTable secondaryIds={secondaryIdsArray} tableRef={tableRef} />}
			tableName="Secondary IDs"
			showTable={true}
		/>
	);
};
