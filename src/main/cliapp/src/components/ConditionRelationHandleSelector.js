import React, { useState } from 'react';
import { Dropdown } from "primereact/dropdown"
import { SearchService } from '../service/SearchService';


export function ConditionRelationHandleDropdown({ field, options, editorChange, props, showClear, placeholderText, dataKey}) {
		const [selectedValue, setSelectedValue] = useState(props.rowData[field]);
		const searchService = new SearchService();
		const [handles, setHandles] = useState(null);
			
			
		const onShow = () => {
				setSelectedValue(props.rowData[field])
				if (props.props.value[props.rowIndex]?.singleReference?.curie) {
					searchService.find("condition-relation", 15 ,0, {"singleReference.curie" : props.props.value[props.rowIndex].singleReference.curie})
						.then((data) => {
					if (data.results?.length > 0) {
						setHandles(data.results);
					} else {
						setHandles(null);
					}
				});
			}
		}
		const onChange = (e) => {
				setSelectedValue(e.value)
				editorChange(props, e)
		}

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
		)
}
