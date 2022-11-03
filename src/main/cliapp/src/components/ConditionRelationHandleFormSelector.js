import React, { useState } from 'react';
import { Dropdown } from "primereact/dropdown"
import { SearchService } from '../service/SearchService';

export function ConditionRelationHandleFormDropdown({ editorChange, referenceCurie, value, name, showClear, placeholderText, isEnabled, customRef }) {
	const [selectedValue, setSelectedValue] = useState(value);
	const searchService = new SearchService();
	const [handles, setHandles] = useState(null);

	const onShow = () => {
			searchService.find(
				"condition-relation",
				15,
				0,
				{"singleReference.curie": referenceCurie}
			).then((data) => {
				if (data.results?.length > 0) {
					setHandles(data.results);
				} else {
					setHandles(null);
				}
			})
	}

	const onChange = (e) => {
		setSelectedValue(e.value);
		editorChange(e);
	}

	return (
		<>
			<Dropdown
				ref={customRef}
				name={name}
				value={selectedValue}
				disabled={!isEnabled}
				options={handles}
				onShow={onShow}
				onChange={(e) => onChange(e)}
				optionLabel="handle"
				showClear={showClear}
				placeholder={placeholderText}
				style={{ width: '100%' }}
			/>
		</>
	)
}

