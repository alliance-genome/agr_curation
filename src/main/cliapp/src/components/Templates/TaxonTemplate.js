import React from 'react';
import { Tooltip } from 'primereact/tooltip';
import { EllipsisTableCell } from "../EllipsisTableCell";

export const TaxonTemplate = ({ taxon }) => {
    if (!taxon) return null;
    const targetClass = `a${global.crypto.randomUUID()}`;
    const textString = getTextString(taxon);

    return (
        <>
            <EllipsisTableCell otherClasses={targetClass}> {textString} </EllipsisTableCell>
            <Tooltip target={`.${targetClass}`} content={textString} style={{ width: '250px', maxWidth: '450px' }} />
        </>
    );
};

const getTextString = (taxon) => {
    if (!taxon.name) return taxon.curie;
    if (!taxon.curie) return taxon.name;
    if (!taxon.curie && !taxon.name) return "";
    return `${taxon.name} (${taxon.curie})`;
};