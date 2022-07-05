import { InputText } from 'primereact/inputtext';
import React from 'react';
import { CronFields } from '../../components/CronFields';

export const FMSForm = ({ hideFMS, newBulkLoad, onChange, disableFormFields }) => {
		return (
				<>
						{!hideFMS.current &&
								<>
										<CronFields
												newItem={newBulkLoad}
												onChange={onChange}
										/>
										<div className="field">
												<label htmlFor="dataType">Data Type</label>
												<InputText
														name="dataType"
														value={newBulkLoad.dataType}
														onChange={onChange}
														placeholder='Enter Data Type'
														disabled={disableFormFields}
												/>
										</div>
										<div className="field">
												<label htmlFor="dataSubType">Data SubType</label>
												<InputText
														name="dataSubType"
														value={newBulkLoad.dataSubType}
														onChange={onChange}
														placeholder='Enter Data SubType'
														disabled={disableFormFields}
												/>
										</div>
								</>
						}
				</>
		);
}
