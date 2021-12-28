import { InputText } from 'primereact/inputtext';
import React from 'react';

export const FMSForm = ({ hideFMS, formState, onChange }) => {
    console.log(formState);
    return (
        <>
            {!hideFMS.current &&
                <>
                    <InputText
                        name="dataType"
                        value={formState.dataType}
                        onChange={onChange}
                    />
                    <InputText
                        name="subType"
                        value={formState.subType}
                        onChange={onChange}
                    />

                </>

            }
        </>
    );
}
