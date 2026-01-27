import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { FullNameFormTable } from './FullNameFormTable';
import { useRef } from 'react';

export const FullNameForm = ({ labelColumnSize, state }) => {
	const tableRef = useRef(null);
	console.log('full name ', state.construct.constructFullName);

	const fullNameArray = [state.construct?.constructFullName];

	return (
		<FormTableWrapper
			labelColumnSize={labelColumnSize}
			table={
				<FullNameFormTable
					name={fullNameArray}
					tableRef={tableRef}
				/>
			}
			tableName="Full Name"
			showTable={true}
		/>
	);
};
