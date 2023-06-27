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
				{autocompleteHoverItem.geneFullName?.displayText &&
				<div key={`name${autocompleteHoverItem.geneFullName.displayText}`} dangerouslySetInnerHTML={{__html: 'Name: ' + autocompleteHoverItem.geneFullName.displayText}}/>
				}
				{autocompleteHoverItem.geneSystematicName?.displayText &&
				<div key={`name${autocompleteHoverItem.geneSystematicName.displayText}`} dangerouslySetInnerHTML={{__html: 'Systematic name: ' + autocompleteHoverItem.geneSystematicName.displayText}}/>
				}
				{autocompleteHoverItem.alleleFullName?.displayText &&
				<div key={`name${autocompleteHoverItem.alleleFullName.displayText}`} dangerouslySetInnerHTML={{__html: 'Name: ' + autocompleteHoverItem.alleleFullName.displayText}}/>
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
					autocompleteHoverItem.geneSynonyms &&
					autocompleteHoverItem.geneSynonyms.map((syn) => <div key={`synonyms${syn.displayText}`} dangerouslySetInnerHTML={{__html: 'Synonym: ' + syn.displayText}}/>)
				}
				{
					autocompleteHoverItem.alleleSynonyms &&
					autocompleteHoverItem.alleleSynonyms.map((syn) => <div key={`synonyms${syn.displayText}`} dangerouslySetInnerHTML={{__html: 'Synonym: ' + syn.displayText}}/>)
				}
				{
					autocompleteHoverItem.alleleSecondaryIds &&
					autocompleteHoverItem.alleleSecondaryIds.map((sid) => <div key={`secondaryIds${sid.secondaryId}`} dangerouslySetInnerHTML={{__html: 'Secondary ID: ' + sid.secondaryId}}/>)
				}
				{
					autocompleteHoverItem.geneSecondaryIds &&
					autocompleteHoverItem.geneSecondaryIds.map((sid) => <div key={`secondaryIds${sid.secondaryId}`} dangerouslySetInnerHTML={{__html: 'Secondary ID: ' + sid.secondaryId}}/>)
				}
				{
					autocompleteHoverItem.crossReferences &&
					autocompleteHoverItem.crossReferences.map((crossReference) => <div key={`crossReferences${crossReference.displayName}`}>
					Cross Reference: {crossReference.displayName}</div>)
				}
				{
					autocompleteHoverItem.cross_references &&
					autocompleteHoverItem.cross_references.map((crossReference) => <div key={`crossReferences${crossReference.curie}`}>
					Cross Reference: {crossReference.curie}</div>)
				}
				{
					autocompleteHoverItem.secondaryIdentifiers &&
					autocompleteHoverItem.secondaryIdentifiers.map((si) => 
					<div key={`secondaryIdentifiers${si.name ? si.name : si}`}>Secondary
						Identifiers: {si.name ? si.name : si}</div>
					)
				}
			</Tooltip>
		</>
	)
};
