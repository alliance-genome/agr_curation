import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { CassettesTable } from '../../cassettesPage/CassettesTable';
import { constructCassettesFilter } from '../utils';

// The Cassettes data table, restricted to the construct's cassettes. It shares its saved settings
// with the construct table's Cassettes pop-up.
export const CassettesForm = ({ state }) => {
	const fixedFilters = constructCassettesFilter(state.construct?.constructCassetteAssociations);

	return (
		<FormTableWrapper
			table={<CassettesTable tableName="Construct Cassettes" fixedFilters={fixedFilters} />}
			tableName="Cassettes"
			showTable={!!fixedFilters}
		/>
	);
};
