import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { ReferencesFormTable } from './ReferencesFormTable';
import { useRef } from 'react';

export const ReferencesForm = ({ state }) => {
	const tableRef = useRef(null);

	const referencesArray = state.construct?.references || [];

	return (
		<FormTableWrapper
			table={
				<ReferencesFormTable
					references={referencesArray}
					tableRef={tableRef}
				/>
			}
			tableName="References"
			showTable={true}
		/>
	);
};
