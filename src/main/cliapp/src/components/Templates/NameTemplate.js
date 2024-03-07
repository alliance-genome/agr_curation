import React from 'react'
import { Tooltip } from 'primereact/tooltip';

export const NameTemplate = (props) => {
    const { name, modEntityId } = props;
    return (
        <>
            <div className={`overflow-hidden text-overflow-ellipsis ${modEntityId.replace(':', '')}`} dangerouslySetInnerHTML={{ __html: name }} />
            <Tooltip target={`.${modEntityId.replace(':', '')}`}>
                <div dangerouslySetInnerHTML={{ __html: name }} />
            </Tooltip>
        </>
    )
}