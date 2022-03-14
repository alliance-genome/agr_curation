import React from 'react';
import {Tooltip} from "primereact/tooltip";

export function SgdStrainBackgroundTooltip({ op, autocompleteSelectedItem }) {

    return (
        <>
            <Tooltip ref={op} style={{width: '450px', maxWidth: '450px'}} position={'right'} mouseTrack mouseTrackLeft={30}>
                Curie: {autocompleteSelectedItem.curie}<br />
                { autocompleteSelectedItem.name &&
                <div dangerouslySetInnerHTML={{__html: 'Name: ' + autocompleteSelectedItem.name}}/>
                }
                {  autocompleteSelectedItem.synonyms &&
                autocompleteSelectedItem.synonyms.map((syn) => <div>Synonym: {syn.name}</div>)
                }
                {  autocompleteSelectedItem.crossReferences &&
                autocompleteSelectedItem.crossReferences.map((cr) => <div>Cross Reference: {cr.curie}</div>)
                }
                {  autocompleteSelectedItem.secondaryIdentifiers &&
                autocompleteSelectedItem.secondaryIdentifiers.map((si) => <div>Secondary Identifiers: {si}</div>)
                }
            </Tooltip>
        </>
    )
}
