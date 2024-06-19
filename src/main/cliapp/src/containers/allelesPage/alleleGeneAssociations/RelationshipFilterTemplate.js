import { MultiSelect } from 'primereact/multiselect';

export const RelationshipFilterTemplate = ({ alleleGeneAssociations, options }) => {
	const getRelationFilterOptions = () => {
		if (!alleleGeneAssociations) return [];
		const relationNames = new Set();
		for (const association of alleleGeneAssociations) {
			const name = association?.relation?.name;
			if (!name) continue;
			relationNames.add(name);
		}
		return Array.from(relationNames);
	};
	return (
		<MultiSelect
			value={options.value}
			options={getRelationFilterOptions()}
			onChange={(e) => options.filterApplyCallback(e.value)}
			className="p-column-filter"
		/>
	);
};
