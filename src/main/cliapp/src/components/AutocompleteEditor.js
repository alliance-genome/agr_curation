import React, {useRef, useState} from 'react';
import {AutoComplete} from "primereact/autocomplete";
import {getEntityType, getRefString, trimWhitespace} from '../utils/utils';
import {Tooltip} from "primereact/tooltip";

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
		valueSelector
	}
) => {
	const [filtered, setFiltered] = useState([]);
	const [query, setQuery] = useState();
	const [fieldValue, setFieldValue] = useState(() => {
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
					;
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

	const onSelectionOver = (event, item) => {
		setAutocompleteSelectedItem(item);
		op.current.show(event);
	};

	const itemTemplate = (item) => {
		let inputValue = trimWhitespace(query.toLowerCase());
		if (autocompleteSelectedItem.synonyms?.length > 0) {
			for (let i in autocompleteSelectedItem.synonyms) {

				let selectedItem = isSubject || isWith ? autocompleteSelectedItem.synonyms[i].name.toString().toLowerCase() :
					autocompleteSelectedItem.synonyms[i].toString().toLowerCase();

				if (selectedItem.indexOf(inputValue) < 0) {
					delete autocompleteSelectedItem.synonyms[i];
				}
			}
		}
		if (autocompleteSelectedItem.crossReferences?.length > 0) {
			for (let i in autocompleteSelectedItem.crossReferences) {
				if (autocompleteSelectedItem.crossReferences[i].curie.toString().toLowerCase().indexOf(inputValue) < 0) {
					delete autocompleteSelectedItem.crossReferences[i];
				}
			}
		}
		if (autocompleteSelectedItem.cross_references?.length > 0) {
			for (let i in autocompleteSelectedItem.cross_references) {
				if (autocompleteSelectedItem.cross_references[i].curie.toString().toLowerCase().indexOf(inputValue) < 0) {
					delete autocompleteSelectedItem.cross_references[i];
				}
			}
		}
		if (autocompleteSelectedItem.secondaryIdentifiers?.length > 0) {
			for (let i in autocompleteSelectedItem.secondaryIdentifiers) {
				if (autocompleteSelectedItem.secondaryIdentifiers[i].toString().toLowerCase().indexOf(inputValue) < 0) {
					delete autocompleteSelectedItem.secondaryIdentifiers[i];
				}
			}
		}
		if (item.abbreviation) {
			return (
				<div>
					<div onMouseOver={(event) => onSelectionOver(event, item)}
						 dangerouslySetInnerHTML={{__html: item.abbreviation + ' - ' + item.name + ' (' + item.curie + ') '}}/>
				</div>
			);
		} else if (item.symbol) {
			return (
				<div>
					<div onMouseOver={(event) => onSelectionOver(event, item)}
						 dangerouslySetInnerHTML={{__html: item.symbol + ' (' + item.curie + ') '}}/>
				</div>
			);
		} else if (item.name) {
			return (
				<div>
					<div onMouseOver={(event) => onSelectionOver(event, item)} dangerouslySetInnerHTML={{__html: item.name + ' (' + item.curie + ') '}}/>
				</div>
			);
		} else if (getEntityType(item) === 'Experiment Condition') {
			return (
				<div>
					<div onMouseOver={(event) => onSelectionOver(event, item)}
						 dangerouslySetInnerHTML={{__html: item.conditionSummary + ' (' + item.id + ') '}}/>
				</div>
			);
		} else if (isReference) {
			
			let displayString = getRefString(item);
			return (
				<div>
					<div onMouseOver={(event) => onSelectionOver(event, item)}>{displayString}</div>
				</div>
			);
		} else {
			return (
				<div>
					<div onMouseOver={(event) => onSelectionOver(event, item)}>{item.curie}</div>
				</div>
			);
		}
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
			<Tooltip ref={op} style={{width: '450px', maxWidth: '450px'}} position={'right'} mouseTrack
					 mouseTrackLeft={30}>
				{autocompleteSelectedItem.curie &&
				<div>Curie: {autocompleteSelectedItem.curie}
					<br/>
				</div>
				}
				{autocompleteSelectedItem.name &&
				<div key={`name${autocompleteSelectedItem.name}`} dangerouslySetInnerHTML={{__html: 'Name: ' + autocompleteSelectedItem.name}}/>
				}
				{autocompleteSelectedItem.conditionSummary &&
				<div key={`name${autocompleteSelectedItem.conditionSummary}`} dangerouslySetInnerHTML={{__html: 'Experimental Condition: ' + autocompleteSelectedItem.conditionSummary}}/>
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
					autocompleteSelectedItem.crossReferences.map((xref) => <div key={`crossReferences${xref}`}>Cross Reference: {xref}</div>)
				}
				{
					autocompleteSelectedItem.cross_references &&
					autocompleteSelectedItem.cross_references.map((xref) => <div key={`cross_references${xref}`}>Cross Reference: {xref}</div>)
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



