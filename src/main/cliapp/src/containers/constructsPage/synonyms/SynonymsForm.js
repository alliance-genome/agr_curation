import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { SynonymsFormTable } from './SynonymsFormTable';
import { useRef } from 'react';

export const SynonymsForm = ({ state }) => {
	const tableRef = useRef(null);

	const synonymsArray = state.construct?.constructSynonyms || [];

	return (
		<FormTableWrapper
			table={
				<SynonymsFormTable
					synonyms={synonymsArray}
					tableRef={tableRef}
				/>
			}
			tableName="Synonyms"
			showTable={true}
		/>
	);
};
