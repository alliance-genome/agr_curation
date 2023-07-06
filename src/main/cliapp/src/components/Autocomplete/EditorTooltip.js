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
				<div key={`name${autocompleteHoverItem.name.replace(/[^a-z0-9]/gi, '')}`} dangerouslySetInnerHTML={{__html: 'Name: ' + autocompleteHoverItem.name}}/>
				}
				{autocompleteHoverItem.geneFullName?.displayText &&
				<div key={`genefullname${autocompleteHoverItem.geneFullName.displayText.replace(/[^a-z0-9]/gi, '')}`} dangerouslySetInnerHTML={{__html: 'Name: ' + autocompleteHoverItem.geneFullName.displayText}}/>
				}
				{autocompleteHoverItem.geneSystematicName?.displayText &&
				<div key={`genesystematicname${autocompleteHoverItem.geneSystematicName.displayText.replace(/[^a-z0-9]/gi, '')}`} dangerouslySetInnerHTML={{__html: 'Systematic name: ' + autocompleteHoverItem.geneSystematicName.displayText}}/>
				}
				{autocompleteHoverItem.alleleFullName?.displayText &&
				<div key={`allelefullname${autocompleteHoverItem.alleleFullName.displayText.replace(/[^a-z0-9]/gi, '')}`} dangerouslySetInnerHTML={{__html: 'Name: ' + autocompleteHoverItem.alleleFullName.displayText}}/>
				}
				{autocompleteHoverItem.handle &&
				<div key={`reference${autocompleteHoverItem.handle.replace(/[^a-z0-9]/gi, '')}`} dangerouslySetInnerHTML={{__html: 'Handle: ' + autocompleteHoverItem.handle + ' (' + autocompleteHoverItem.singleReference + ')'}}/>
				}
				{autocompleteHoverItem.conditionSummary &&
				<div key={`condition${autocompleteHoverItem.conditionSummary.replace(/[^a-z0-9]/gi, '')}`} dangerouslySetInnerHTML={{__html: 'Condition: ' + autocompleteHoverItem.conditionSummary + ' (' + autocompleteHoverItem.uniqueId + ')'}}/>
				}
				{autocompleteHoverItem.symbol &&
				<div key={`symbol${autocompleteHoverItem.symbol.replace(/[^a-z0-9]/gi, '')}`} dangerouslySetInnerHTML={{__html: 'Symbol: ' + autocompleteHoverItem.symbol}}/>
				}
				{
					autocompleteHoverItem.synonyms &&
					autocompleteHoverItem.synonyms.map((syn) => <div key={`synonyms${syn.name ? syn.name.replace(/[^a-z0-9]/gi, '') : syn.replace(/[^a-z0-9]/gi, '')}`}>
					Synonym: {syn.name ? syn.name : syn}</div>)
				}
				{
					autocompleteHoverItem.geneSynonyms &&
					autocompleteHoverItem.geneSynonyms.map((syn) => <div key={`genesynonyms${syn.displayText.replace(/[^a-z0-9]/gi, '')}`} dangerouslySetInnerHTML={{__html: 'Synonym: ' + syn.displayText}}/>)
				}
				{
					autocompleteHoverItem.alleleSynonyms &&
					autocompleteHoverItem.alleleSynonyms.map((syn) => <div key={`allelesynonyms${syn.displayText.replace(/[^a-z0-9]/gi, '')}`} dangerouslySetInnerHTML={{__html: 'Synonym: ' + syn.displayText}}/>)
				}
				{
					autocompleteHoverItem.alleleSecondaryIds &&
					autocompleteHoverItem.alleleSecondaryIds.map((sid) => <div key={`genesecondaryIds${sid.secondaryId.replace(/[^a-z0-9]/gi, '')}`} dangerouslySetInnerHTML={{__html: 'Secondary ID: ' + sid.secondaryId}}/>)
				}
				{
					autocompleteHoverItem.geneSecondaryIds &&
					autocompleteHoverItem.geneSecondaryIds.map((sid) => <div key={`allelesecondaryIds${sid.secondaryId.replace(/[^a-z0-9]/gi, '')}`} dangerouslySetInnerHTML={{__html: 'Secondary ID: ' + sid.secondaryId}}/>)
				}
				{
					autocompleteHoverItem.crossReferences &&
					autocompleteHoverItem.crossReferences.map((crossReference) => <div key={`crossReferences${crossReference.displayName.replace(/[^a-z0-9]/gi, '')}`}>
					Cross Reference: {crossReference.displayName}</div>)
				}
				{
					autocompleteHoverItem.cross_references &&
					autocompleteHoverItem.cross_references.map((crossReference) => <div key={`crossReferences${crossReference.curie.replace(/[^a-z0-9]/gi, '')}`}>
					Cross Reference: {crossReference.curie}</div>)
				}
				{
					autocompleteHoverItem.secondaryIdentifiers &&
					autocompleteHoverItem.secondaryIdentifiers.map((si) => 
					<div key={`secondaryIdentifiers${si.name ? si.name.replace(/[^a-z0-9]/gi, '') : si.replace(/[^a-z0-9]/gi, '')}`}>Secondary
						Identifiers: {si.name ? si.name : si}</div>
					)
				}
			</Tooltip>
		</>
	)
};
