import React from "react";
import {Tooltip} from "primereact/tooltip";

export const EditorTooltip = ({op, autocompleteHoverItem}) => {
	return (
		<>
			<Tooltip ref={op} style={{width: '450px', maxWidth: '450px'}} position={'right'} mouseTrack mouseTrackLeft={30}>
				{autocompleteHoverItem.curie &&
				<div>Curie: {autocompleteHoverItem.curie}
					<br/>
				</div>
				}
				{autocompleteHoverItem.name &&
				<div key={`name${autocompleteHoverItem.name}`} dangerouslySetInnerHTML={{__html: 'Name: ' + autocompleteHoverItem.name}}/>
				}
				{autocompleteHoverItem.handle &&
				<div key={`name${autocompleteHoverItem.handle}`} dangerouslySetInnerHTML={{__html: 'Handle: ' + autocompleteHoverItem.handle + ' (' + autocompleteHoverItem.singleReference + ')'}}/>
				}
				{autocompleteHoverItem.conditionSummary &&
				<div key={`name${autocompleteHoverItem.conditionSummary}`} dangerouslySetInnerHTML={{__html: 'Condition: ' + autocompleteHoverItem.conditionSummary + ' (' + autocompleteHoverItem.uniqueId + ')'}}/>
				}
				{autocompleteHoverItem.symbol &&
				<div key={`symbol${autocompleteHoverItem.symbol}`} dangerouslySetInnerHTML={{__html: 'Symbol: ' + autocompleteHoverItem.symbol}}/>
				}
				{
					autocompleteHoverItem.synonyms &&
					autocompleteHoverItem.synonyms.map((syn) => <div key={`synonyms${syn.name ? syn.name : syn}`}>
					Synonym: {syn.name ? syn.name : syn}</div>)
				}
				{
					autocompleteHoverItem.crossReferences &&
					autocompleteHoverItem.crossReferences.map((crossReference) => <div key={`crossReferences${crossReference.curie}`}>
					Cross Reference: {crossReference.curie}</div>)
				}

				{
					autocompleteHoverItem.cross_references &&
					autocompleteHoverItem.cross_references.map((xref) => <div key={`cross_references${xref.curie}`}>Cross Reference: {xref.curie}</div>)
				}
				{
					autocompleteHoverItem.secondaryIdentifiers &&
					autocompleteHoverItem.secondaryIdentifiers.map((si) => {
						return <div key={`secondaryIdentifiers${si.name ? si.name : si}`}>Secondary
						Identifiers: {si.name ? si.name : si}</div>
					})
				}
			</Tooltip>
		</>
	)
};
