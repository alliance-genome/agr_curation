import React, { useRef, useState } from 'react';
import { AutoComplete } from "primereact/autocomplete";
import { onSelectionOver, getRefString } from '../../utils/utils';
import { Tooltip } from "primereact/tooltip";

export const AutocompleteEditor = (
	{
		rowProps,
		searchService,
		autocompleteFields,
		endpoint,
		filterName,
		fieldName,
		subField = "curie",
		otherFilters = [],
		isSubject = false,
		isWith = false,
		isMultiple = false,
		isReference = false,
		isSgdStrainBackground = false,
		valueSelector,
		valueDisplay
	}
) => {
	const [filtered, setFiltered] = useState([]);
	const [query, setQuery] = useState();
	const [fieldValue, setFieldValue] = useState(() => {
			if (isReference)
				return getRefString(rowProps.rowData[fieldName]);
			return isMultiple ?
				rowProps.rowData[fieldName] :
				rowProps.rowData[fieldName]?.curie
		}
	);

	const op = useRef(null);
	const [autocompleteSelectedItem, setAutocompleteSelectedItem] = useState({});		
	const search = (event) => {
		setQuery(event.query);
		let filter = {};
		autocompleteFields.forEach(field => {
			filter[field] = {
				queryString: event.query,
				...((isSubject || isWith || isReference) && {tokenOperator: "AND"})
			}
		});

		searchService.search(endpoint, 15, 0, [], {[filterName]: filter, ...otherFilters})
			.then((data) => {
				if (data.results?.length > 0) {
					if (isWith) {
						setFiltered(data.results.filter((gene) => Boolean(gene.curie.startsWith("HGNC:"))));
					} else if (isSgdStrainBackground) {
						setFiltered(data.results.filter((agm) => Boolean(agm.curie.startsWith("SGD:"))));
					} else {
						setFiltered(data.results);
					}
				} else {
					setFiltered([]);
				}
			});
	};

	const onValueChange = (event) => {
		let updatedRows = [...rowProps.props.value];

		if (!event.target.value) {
			updatedRows[rowProps.rowIndex][fieldName] = null;
			setFieldValue('');
			return;
		}

		if (isMultiple) {
			updatedRows[rowProps.rowIndex][fieldName] = event.target.value;
			setFieldValue(updatedRows[rowProps.rowIndex][fieldName]);
			return;
		}

		updatedRows[rowProps.rowIndex][fieldName] = {};//this needs to be fixed. Otherwise, we won't have access to the other subject fields

		if (typeof event.target.value === "object") {
			if (valueSelector) {
				updatedRows[rowProps.rowIndex][fieldName].curie = valueSelector(event.target.value);
				setFieldValue(valueSelector(event.target.value));
			} else {
				updatedRows[rowProps.rowIndex][fieldName].curie = event.target.value.curie;
				setFieldValue(updatedRows[rowProps.rowIndex][fieldName]?.curie);
			}
		} else {
			updatedRows[rowProps.rowIndex][fieldName].curie = event.target.value;
				setFieldValue(updatedRows[rowProps.rowIndex][fieldName]?.curie);
		}
	};

	const itemTemplate = (item) => {
		if(valueDisplay) return valueDisplay(item, setAutocompleteSelectedItem, op, query);
		return (
			<div>
				<div onMouseOver={(event) => onSelectionOver(event, item, query, op, setAutocompleteSelectedItem)} 
					dangerouslySetInnerHTML={{__html: item.name + ' (' + item.curie + ') '}}/>
			</div>
		);
	};

	return (
		<div>
			<AutoComplete
				multiple={isMultiple}
				panelStyle={{width: '15%', display: 'flex', maxHeight: '350px'}}
				field={subField}
				value={fieldValue}
				suggestions={filtered}
				itemTemplate={itemTemplate}
				completeMethod={search}
				onHide={(e) => op.current.hide(e)}
				onChange={(e) => onValueChange(e)}
			/>
			<EditorTooltip op={op} autocompleteSelectedItem={autocompleteSelectedItem} dataType={fieldName}/>
		</div>
	)
}

const EditorTooltip = ({op, autocompleteSelectedItem}) => {
	return (
		<>
			<Tooltip ref={op} style={{width: '450px', maxWidth: '450px'}} position={'right'} mouseTrack mouseTrackLeft={30}>
				{autocompleteSelectedItem.curie &&
				<div>Curie: {autocompleteSelectedItem.curie}
					<br/>
				</div>
				}
				{autocompleteSelectedItem.name &&
				<div key={`name${autocompleteSelectedItem.name}`} dangerouslySetInnerHTML={{__html: 'Name: ' + autocompleteSelectedItem.name}}/>
				}
				{autocompleteSelectedItem.handle &&
				<div key={`name${autocompleteSelectedItem.handle}`} dangerouslySetInnerHTML={{__html: 'Handle: ' + autocompleteSelectedItem.handle + '(' + autocompleteSelectedItem.singleReference + ')'}}/>
				}
				{autocompleteSelectedItem.conditionStatement &&
				<div key={`name${autocompleteSelectedItem.conditionStatement}`} dangerouslySetInnerHTML={{__html: 'Experimental Condition: ' + autocompleteSelectedItem.conditionStatement}}/>
				}
				{autocompleteSelectedItem.symbol &&
				<div key={`symbol${autocompleteSelectedItem.symbol}`} dangerouslySetInnerHTML={{__html: 'Symbol: ' + autocompleteSelectedItem.symbol}}/>
				}
				{
					autocompleteSelectedItem.synonyms &&
					autocompleteSelectedItem.synonyms.map((syn) => <div key={`synonyms${syn.name ? syn.name : syn}`}>
					Synonym: {syn.name ? syn.name : syn}</div>)
				}
				{
					autocompleteSelectedItem.crossReferences &&
					autocompleteSelectedItem.crossReferences.map((crossReference) => <div key={`crossReferences${crossReference.curie}`}>
					Cross Reference: {crossReference.curie}</div>)
				}

				{
					autocompleteSelectedItem.cross_references &&
					autocompleteSelectedItem.cross_references.map((xref) => <div key={`cross_references${xref.curie}`}>Cross Reference: {xref.curie}</div>)
				}
				{
					autocompleteSelectedItem.secondaryIdentifiers &&
					autocompleteSelectedItem.secondaryIdentifiers.map((si) => {
						return <div key={`secondaryIdentifiers${si.name ? si.name : si}`}>Secondary
						Identifiers: {si.name ? si.name : si}</div>
					})
				}
			</Tooltip>
		</>
	)
};



