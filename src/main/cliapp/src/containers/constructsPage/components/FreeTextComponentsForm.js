import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { FreeTextComponentsFormTable } from './ConstructComponentsFormTable';
import { useRef } from 'react';

export const FreeTextComponentsForm = ({ state }) => {
	const tableRef = useRef(null);

	const componentsArray = state.construct?.constructComponents || [];

	return (
		<FormTableWrapper
			table={<FreeTextComponentsFormTable components={componentsArray} tableRef={tableRef} />}
			tableName="Free Text Components"
			showTable={true}
		/>
	);
};
