import React from 'react'
import { Tooltip } from 'primereact/tooltip';

export const NameTemplate = ({name}) => {
    const targetClass = `a${global.crypto.randomUUID()}`;
    if(!name) return null;
    return (
        <>
            <div className={`overflow-hidden text-overflow-ellipsis ${targetClass}`} dangerouslySetInnerHTML={{ __html: name }} />
            <Tooltip target={`.${targetClass}`}>
                <div dangerouslySetInnerHTML={{ __html: name }} />
            </Tooltip>
        </>
    )
}