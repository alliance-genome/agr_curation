import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { SymbolFormTable } from './SymbolFormTable';
import { useRef } from 'react';

export const SymbolForm = ({ state }) => {
	const tableRef = useRef(null);

	const symbolArray = state.construct?.constructSymbol ? [state.construct.constructSymbol] : [];

	return (
		<FormTableWrapper
			table={<SymbolFormTable symbol={symbolArray} tableRef={tableRef} />}
			tableName="Symbol"
			showTable={true}
		/>
	);
};
