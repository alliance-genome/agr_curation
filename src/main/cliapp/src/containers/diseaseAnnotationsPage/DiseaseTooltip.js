import React from 'react';
import {Tooltip} from "primereact/tooltip";

export function DiseaseTooltip({ op, autocompleteSelectedItem }) {

    return (
        <>
            <Tooltip ref={op} style={{width: '450px', maxWidth: '450px'}} position={'right'} mouseTrack mouseTrackLeft={30}>
                Curie: {autocompleteSelectedItem.curie}<br />
                { autocompleteSelectedItem.name &&
                <div key={`name${autocompleteSelectedItem.name}`} dangerouslySetInnerHTML={{__html: 'Name: ' + autocompleteSelectedItem.name}}/>
                }
                { autocompleteSelectedItem.symbol &&
                <div key={`symbol${autocompleteSelectedItem.symbol}`} dangerouslySetInnerHTML={{__html: 'Symbol: ' + autocompleteSelectedItem.symbol }} />
                }
                {  autocompleteSelectedItem.synonyms &&
                autocompleteSelectedItem.synonyms.map((syn) => <div key={`synonyms${syn}`}>Synonym: {syn}</div>)
                }
                {  autocompleteSelectedItem.crossReferences &&
                autocompleteSelectedItem.crossReferences.map((cr) => <div key={`crossReferences${cr.curie}`}>Cross Reference: {cr.curie}</div>)
                }
                {  autocompleteSelectedItem.secondaryIdentifiers &&
                autocompleteSelectedItem.secondaryIdentifiers.map((si) => <div key={`secondaryIdentifiers${si}`}>Secondary Identifiers: {si}</div>)
                }
            </Tooltip>
        </>
    )
}
