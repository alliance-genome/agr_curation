import React, {useState, useRef} from 'react';
import {AutoComplete} from "primereact/autocomplete";
import {trimWhitespace } from '../../utils/utils';
import {ConditionGeneOntologyTooltip} from './ConditionGeneOntologyTooltip';

export const ConditionGeneOntologyEditor = ({ rowProps, searchService, setExperimentalConditions, autocompleteFields }) => {
  const [filteredConditionGeneOntologies, setFilteredConditionGeneOntologies] = useState([]);

  const op = useRef(null);
  const [autocompleteSelectedItem, setAutocompleteSelectedItem] = useState({});
  
  const searchConditionGeneOntology = (event) => {
  let conditionGeneOntologyFilter = {};
  autocompleteFields.forEach( field => {
    conditionGeneOntologyFilter[field] = event.query;
  });
  let obsoleteFilter = {"obsolete": false};

  searchService.search('goterm', 15, 0, [], {"conditionGeneOntologyFilter":conditionGeneOntologyFilter, "obsoleteFilter":obsoleteFilter})
    .then((data) => {
    if(data.results && data.results.length >0)
      setFilteredConditionGeneOntologies(data.results);
    else
      setFilteredConditionGeneOntologies([]);
    });
  };

  const onConditionGeneOntologyEditorValueChange = (event) => {//this should propably be generalized so that all of these editor value changes can use the same method
    let updatedConditions = [...rowProps.value];
    if(event.target.value || event.target.value === '') {
      updatedConditions[rowProps.rowIndex].conditionGeneOntology = {};//this needs to be fixed. Otherwise, we won't have access to the other subject fields
      if(typeof event.target.value === "object"){
        updatedConditions[rowProps.rowIndex].conditionGeneOntology.curie = event.target.value.curie;
      } else {
        updatedConditions[rowProps.rowIndex].conditionGeneOntology.curie = event.target.value;
      }
      setExperimentalConditions(updatedConditions);
    }
  };

  const onSelectionOver = (event, item) => {
    setAutocompleteSelectedItem(item);
    op.current.show(event);
  };


  const conditionGeneOntologyItemTemplate = (item) => {
    let inputValue = trimWhitespace(rowProps.rowData.conditionGeneOntology.curie.toLowerCase());
    if (autocompleteSelectedItem.synonyms && autocompleteSelectedItem.synonyms.length>0){
      for(let i in autocompleteSelectedItem.synonyms){
        if(autocompleteSelectedItem.synonyms[i].toString().toLowerCase().indexOf(inputValue)<0){
          delete autocompleteSelectedItem.synonyms[i];
        }
      }
    }
    if (autocompleteSelectedItem.crossReferences && autocompleteSelectedItem.crossReferences.length>0){
      for(let i in autocompleteSelectedItem.crossReferences){
        if(autocompleteSelectedItem.crossReferences[i].curie.toString().toLowerCase().indexOf(inputValue)< 0){
          delete autocompleteSelectedItem.crossReferences[i];
        }
      }
    }
      
    if(autocompleteSelectedItem.secondaryIdentifiers && autocompleteSelectedItem.secondaryIdentifiers.length>0){
      for(let i in autocompleteSelectedItem.secondaryIdentifiers){
        if(autocompleteSelectedItem.secondaryIdentifiers[i].toString().toLowerCase().indexOf(inputValue)< 0){
          delete autocompleteSelectedItem.secondaryIdentifiers[i];
        }
      }
    }
      
    if (item.name){
      return (
        <div>
          <div onMouseOver={(event) => onSelectionOver(event, item)} dangerouslySetInnerHTML={{__html: item.name + ' (' + item.curie + ') '}}/>
        </div>
      );
    }else {
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
        panelStyle={{ width: '15%', display: 'flex', maxHeight: '350px'}}
        field="curie"
        value={rowProps.rowData.conditionGeneOntology.curie}
        suggestions={filteredConditionGeneOntologies}
        itemTemplate={conditionGeneOntologyItemTemplate}
        completeMethod={searchConditionGeneOntology}
        onHide={(e) => op.current.hide(e)}
        onChange={(e) => onConditionGeneOntologyEditorValueChange(e)}
      />
      <ConditionGeneOntologyTooltip op={op} autocompleteSelectedItem={autocompleteSelectedItem}/>
    </div>
  )
};
