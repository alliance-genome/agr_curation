import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { GenomicAssociationsFormTable } from './GenomicAssociationsFormTable';
import { useRef } from 'react';

export const GenomicAssociationsForm = ({ state }) => {
	const tableRef = useRef(null);

	const associationsArray = state.construct?.constructGenomicEntityAssociations || [];

	return (
		<FormTableWrapper
			table={
				<GenomicAssociationsFormTable
					associations={associationsArray}
					tableRef={tableRef}
				/>
			}
			tableName="Genomic Entity Associations"
			showTable={true}
		/>
	);
};
