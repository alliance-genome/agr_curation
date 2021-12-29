import { InputText } from 'primereact/inputtext';
import React from 'react';
import { CronFields } from './CronFields';

export const FMSForm = ({ hideFMS, newBulkLoad, onChange }) => {
    return (
        <>
            {!hideFMS.current &&
                <>
                    <div className="p-field">
                        <label htmlFor="dataType">Data Type</label>
                        <InputText
                            name="dataType"
                            value={newBulkLoad.dataType}
                            onChange={onChange}
                            placeholder='Enter Data Type'
                        />
                    </div>
                    <div className="p-field">
                        <label htmlFor="dataSubType">Data SubType</label>
                        <InputText
                            name="dataSubType"
                            value={newBulkLoad.dataSubType}
                            onChange={onChange}
                            placeholder='Enter Data SubType'
                        />
                    </div>
                    <CronFields
                        newBulkLoad={newBulkLoad}
                        onChange={onChange}
                    />
                </>
            }
        </>
    );
}
