import { InputText } from 'primereact/inputtext';
import React from 'react';

export const FMSForm = ({ hideFMS, newBulkLoad, onChange }) => {
    return (
        <>
            {!hideFMS.current &&
                <>
                    <InputText
                        name="dataType"
                        value={newBulkLoad.dataType}
                        onChange={onChange}
                        placeholder='Enter Data Type'
                    />
                    <InputText
                        name="dataSubType"
                        value={newBulkLoad.dataSubType}
                        onChange={onChange}
                        placeholder='Enter Data SubType'
                    />

                </>
            }
        </>
    );
}
