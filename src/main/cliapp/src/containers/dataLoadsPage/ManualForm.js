import React from "react";
import { DataLoadService } from '../../service/DataLoadService';
import { Dropdown } from "primereact/dropdown";

export const ManualForm = ({ hideManual, newBulkLoad, onChange }) => {
	const dataLoadService = new DataLoadService();

	const dataTypes = dataLoadService.getDataTypes();
	return (
		<>
			{!hideManual.current && (
				<div className="field">
					<label htmlFor="dataType">Data Type</label>
					<Dropdown
						id="dataType"
						value={newBulkLoad.dataType}
						options={dataTypes}
						onChange={onChange}
						placeholder={"Select Data Type"}
						className='p-col-12'
						name='dataType'
					/>
				</div>
			)}
		</>
	);
};
