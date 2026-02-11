import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { ComponentAssociationsFormTable } from './ComponentAssociationsFormTable';
import { useRef } from 'react';

export const ComponentAssociationsForm = ({ state }) => {
	const tableRef = useRef(null);

	const associationsArray = state.construct?.constructGenomicEntityAssociations || [];

	return (
		<FormTableWrapper
			table={<ComponentAssociationsFormTable associations={associationsArray} tableRef={tableRef} />}
			tableName="Component Associations"
			showTable={true}
		/>
	);
};
