import React from "react";
import { DataLoadService } from '../../service/DataLoadService';
import { Dropdown } from "primereact/dropdown";

export const ManualForm = ({ hideManual, newBulkLoad, onChange }) => {
	const dataLoadService = new DataLoadService();

	const dataProviders = dataLoadService.getDataProviders();
	return (
		<>
			{!hideManual.current && (
				<div className="field">
					<label htmlFor="dataProvider">Data Provider</label>
					<Dropdown
						id="dataProvider"
						value={newBulkLoad.dataProvider}
						options={dataProviders}
						onChange={onChange}
						placeholder={"Select Data Provider"}
						className='p-col-12'
						name='dataProvider'
					/>
				</div>
			)}
		</>
	);
};
