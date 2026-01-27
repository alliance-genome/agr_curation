import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { ConstructComponentsFormTable } from './ConstructComponentsFormTable';
import { useRef } from 'react';

export const ConstructComponentsForm = ({ state }) => {
	const tableRef = useRef(null);

	const componentsArray = state.construct?.constructComponents || [];

	return (
		<FormTableWrapper
			table={
				<ConstructComponentsFormTable
					components={componentsArray}
					tableRef={tableRef}
				/>
			}
			tableName="Construct Components"
			showTable={true}
		/>
	);
};
