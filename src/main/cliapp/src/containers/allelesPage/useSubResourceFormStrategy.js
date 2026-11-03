import { useDataKeyRowsStrategy } from '../../components/Editors/fields/strategies/useDataKeyRowsStrategy';

/**
 * Table editing strategy for an allele page section whose rows are one of the allele's sub-resource lists: rows
 * come from the list and an edit replaces the field on its row there.
 *
 * @param {Object} subResource what `useSubResourceList` returns
 * @returns {object} a strategy for TableEditorProvider
 */
export function useSubResourceFormStrategy({ rows, setRows, errorMessages }) {
	return useDataKeyRowsStrategy({
		rows,
		errors: errorMessages,
		write: ({ rowKey, field, value }) =>
			setRows((previous) => previous.map((row) => (row.dataKey === rowKey ? { ...row, [field]: value } : row))),
	});
}
