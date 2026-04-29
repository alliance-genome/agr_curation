import React, { useState } from 'react';
import { Dropdown } from 'primereact/dropdown';
import { SearchService } from '../../../../service/SearchService';
import { Endpoints } from '../../../../constants/Endpoints';

export function ConditionHandleDropdown({
	field,
	options,
	editorChange,
	editorOptions,
	showClear,
	placeholderText,
	dataKey,
}) {
	const [selectedValue, setSelectedValue] = useState(editorOptions.rowData[field]);
	const searchService = new SearchService();
	const [handles, setHandles] = useState([]);

	const onShow = () => {
		setSelectedValue(editorOptions.rowData[field]);
		const singleReferenceCurie = editorOptions.rowData.conditionRelations?.[0]?.singleReference?.curie;
		if (singleReferenceCurie) {
			searchService
				.find(Endpoints.Annotation.CONDITION_RELATION, 15, 0, {
					'singleReference.curie': singleReferenceCurie,
				})
				.then((data) => {
					if (data.results?.length > 0) {
						setHandles(data.results);
					} else {
						setHandles([]);
					}
				});
		}
	};
	const onChange = (e) => {
		setSelectedValue(e.value);
		editorChange(editorOptions, e);
	};

	return (
		<>
			<Dropdown
				value={selectedValue}
				dataKey={dataKey}
				options={handles}
				onShow={onShow}
				onChange={(e) => onChange(e)}
				optionLabel="handle"
				showClear={showClear}
				placeholder={placeholderText}
				style={{ width: '100%' }}
			/>
		</>
	);
}
