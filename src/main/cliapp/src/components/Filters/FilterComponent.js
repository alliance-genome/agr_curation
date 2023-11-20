import React from 'react';
import { FilterComponentInputText } from './FilterComponentInputText';
import { FilterComponentBinaryDropDown } from './FilterComponentBinaryDropDown';
import { FilterComponentMultiSelect } from './FilterComponentMultiSelect';
import { Splitter, SplitterPanel } from 'primereact/splitter';

export const FilterComponent = ({
	filterConfig,
	isInEditMode,
	onFilter,
	aggregationFields,
	tableState,
	endpoint,
}) => {
	if (filterConfig && filterConfig.filterComponentType) {
		switch (filterConfig.filterComponentType) {
			case "input":
				return (
					<Splitter className='border-none' gutterSize={0}>
						<SplitterPanel size={95} className="text-left">
							<FilterComponentInputText
								filterConfig={filterConfig}
								isInEditMode={isInEditMode}
								currentFilters={tableState.filters}
								onFilter={onFilter}
							/>
						</SplitterPanel>
						<SplitterPanel size={5} className="text-right">
							<i className="pi pi-filter text-base mt-3 ml-3" ></i>
						</SplitterPanel>
					</Splitter>
				);

			case "dropdown":
				return (
					<Splitter className='border-none' gutterSize={0}>
						<SplitterPanel size={95} className="text-left">
							<FilterComponentBinaryDropDown
								filterConfig={filterConfig}
								isInEditMode={isInEditMode}
								currentFilters={tableState.filters}
								onFilter={onFilter}
							/>
						</SplitterPanel>
						<SplitterPanel size={5} className="text-right">
							<i className="pi pi-filter text-base mt-3 ml-3" ></i>
						</SplitterPanel>
					</Splitter >
				);

			case "multiselect":
				return (
					<Splitter className='border-none' gutterSize={0}>
						<SplitterPanel size={95} className="text-left">
							<FilterComponentMultiSelect
								filterConfig={filterConfig}
								isInEditMode={isInEditMode}
								currentFilters={tableState.filters}
								onFilter={onFilter}
								aggregationFields={aggregationFields}
								endpoint={endpoint}
							/>
						</SplitterPanel>
						<SplitterPanel size={5} className="text-right">
							<i className="pi pi-filter text-base mt-3 ml-3" ></i>
						</SplitterPanel>
					</Splitter >
				);
			default:
				return null;
		};
	} else {
		return null;
	}
};
