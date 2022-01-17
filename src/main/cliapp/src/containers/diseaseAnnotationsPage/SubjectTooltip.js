import React, {useState} from 'react';
import {Tooltip} from "primereact/tooltip";

export function SubjectTooltip({ op, autocompleteSelectedItem, inputValue }) {

    return (
        <>
            <Tooltip ref={op} style={{width: '450px', maxWidth: '450px'}} position={'right'} mouseTrack mouseTrackLeft={100}>
                Curie: {autocompleteSelectedItem.curie}<br />
                { autocompleteSelectedItem.name &&
                <div dangerouslySetInnerHTML={{__html: 'Name: ' + autocompleteSelectedItem.name}}/>
                }
                { autocompleteSelectedItem.symbol &&
                <div dangerouslySetInnerHTML={{__html: 'Symbol: ' + autocompleteSelectedItem.symbol }} />
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
