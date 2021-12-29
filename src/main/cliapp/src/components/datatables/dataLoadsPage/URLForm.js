import React from 'react';
import { InputText } from 'primereact/inputtext';
import { CronFields } from './CronFields';


export const URLForm = ({ hideURL, newBulkLoad, onChange }) => {
    return (
        <>
            {!hideURL.current &&
                <>
                    <CronFields
                        newBulkLoad={newBulkLoad}
                        onChange={onChange}
                    />
                    <div className="p-field">
                        <label htmlFor="url">URL</label>
                        <InputText
                            name="url"
                            value={newBulkLoad.url}
                            onChange={onChange}
                            placeholder='Enter URL'
                        />
                    </div>

                </>
            }
        </>
    );
}
