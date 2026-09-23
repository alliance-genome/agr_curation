import { useState } from 'react';
import { TableField } from '../../fields/TableField';
import { VocabularySelect } from '../../widgets/VocabularySelect';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';

/**
 * Dropdown over the condition relations sharing this row's reference. Edits the first
 * entry of the row's `conditionRelations` and stores the whole rebuilt array, while
 * filing errors under `conditionRelationHandle`, which is the key the server reports
 * them against.
 *
 * Options are fetched when the panel opens rather than up front, since they depend on
 * the row's reference. The row's relation and the fetched one are separate objects, so
 * they are matched on id.
 *
 * @param {object} editorOptions - PrimeReact column editor options
 * @returns {JSX.Element|null} null when the row has no handle to edit
 */
export const ConditionHandleTableEditor = ({ editorOptions }) => {
	const [handles, setHandles] = useState([]);

	if (!editorOptions.rowData?.conditionRelations || !editorOptions.rowData.conditionRelations[0]?.handle) {
		return null;
	}

	const loadHandles = () => {
		const singleReferenceCurie = editorOptions.rowData.conditionRelations?.[0]?.singleReference?.curie;
		if (!singleReferenceCurie) return;

		const searchService = new SearchService();
		searchService
			.find(Endpoints.Annotation.CONDITION_RELATION, 15, 0, { 'singleReference.curie': singleReferenceCurie })
			.then((data) => setHandles(data.results?.length > 0 ? data.results : []));
	};

	return (
		<TableField
			editorOptions={editorOptions}
			field="conditionRelations"
			errorField="conditionRelationHandle"
			read={(row) => row.conditionRelations?.[0]}
			write={(selected, row) => {
				const conditionRelations = [...row.conditionRelations];
				conditionRelations[0] = selected;
				return conditionRelations;
			}}
		>
			{(binding) => (
				<VocabularySelect
					{...binding}
					name="Experiments"
					options={handles}
					optionLabel="handle"
					dataKey="id"
					placeholder={binding.value?.handle}
					onShow={loadHandles}
				/>
			)}
		</TableField>
	);
};
