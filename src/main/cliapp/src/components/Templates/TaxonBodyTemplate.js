import React from 'react'
import { Tooltip } from 'primereact/tooltip';
import { EllipsisTableCell } from "../EllipsisTableCell";

export const TaxonBodyTemplate = ({taxon, modEntityId}) => {
    if (taxon) {
        return (
            <>
                <EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${modEntityId.replace(':', '')}${taxon.curie.replace(':', '')}`}>
                    {taxon.name} ({taxon.curie})
                </EllipsisTableCell>
                <Tooltip target={`.${"TAXON_NAME_"}${modEntityId.replace(':', '')}${taxon.curie.replace(':', '')}`} content= {`${taxon.name} (${taxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
            </>
        );
    }
};