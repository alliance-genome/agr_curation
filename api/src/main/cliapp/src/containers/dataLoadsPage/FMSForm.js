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
												<label htmlFor="fmsDataType">FMS Data Type</label>
												<InputText
														name="fmsDataType"
														value={newBulkLoad.fmsDataType}
														onChange={onChange}
														placeholder='Enter Data Type'
														disabled={disableFormFields}
												/>
										</div>
										<div className="field">
												<label htmlFor="fmsDataSubType">FMS Data SubType</label>
												<InputText
														name="fmsDataSubType"
														value={newBulkLoad.fmsDataSubType}
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
