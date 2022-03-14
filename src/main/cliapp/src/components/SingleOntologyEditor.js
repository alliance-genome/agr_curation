import React, {useState, useRef} from 'react';
import {AutoComplete} from "primereact/autocomplete";
import {trimWhitespace } from '../utils/utils';
import {SingleOntologyTooltip} from './SingleOntologyTooltip';

export const SingleOntologyEditor = ({ rowProps, searchService, setExperimentalConditions, autocompleteFields, fieldname, endpoint }) => {
  const [filteredSingleOntologies, setFilteredSingleOntologies] = useState([]);

  const op = useRef(null);
  const [autocompleteSelectedItem, setAutocompleteSelectedItem] = useState({});
  
  const searchSingleOntology = (event) => {
    let singleOntologyFilter = {};
    autocompleteFields.forEach( field => {
      singleOntologyFilter[field] = {
        queryString: event.query
      }
    });
    let obsoleteFilter = {
      "obsolete": {
        queryString: false
      }
    };

    searchService.search(endpoint, 15, 0, [], {"singleOntologyFilter":singleOntologyFilter, "obsoleteFilter":obsoleteFilter})
      .then((data) => {
        if(data.results && data.results.length >0)
          setFilteredSingleOntologies(data.results);
        else
          setFilteredSingleOntologies([]);
      });
  };

  const onSingleOntologyEditorValueChange = (event) => {//this should propably be generalized so that all of these editor value changes can use the same method
    let updatedConditions = [...rowProps.value];
    if(event.target.value || event.target.value === '') {
      updatedConditions[rowProps.rowIndex][fieldname] = {};//this needs to be fixed. Otherwise, we won't have access to the other subject fields
      if(typeof event.target.value === "object"){
        updatedConditions[rowProps.rowIndex][fieldname] = event.target.value;
      } else {
        if (event.target.value === '') {
          updatedConditions[rowProps.rowIndex][fieldname] = null;
        }
        else {
          updatedConditions[rowProps.rowIndex][fieldname].curie = event.target.value;
        }
      }
      setExperimentalConditions(updatedConditions);
    }
  };

  const onSelectionOver = (event, item) => {
    setAutocompleteSelectedItem(item);
    op.current.show(event);
  };


  const singleOntologyItemTemplate = (item) => {
    if (rowProps.rowData[fieldname]) {
      let inputValue = trimWhitespace(rowProps.rowData[fieldname].curie.toLowerCase());
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
    }       
  };

  return (
    <div>
      <AutoComplete
        id = {rowProps.rowData[fieldname] ? rowProps.rowData[fieldname].curie : null}
        panelStyle={{ width: '15%', display: 'flex', maxHeight: '350px'}}
        field="curie"
        value={rowProps.rowData[fieldname] ? rowProps.rowData[fieldname].curie : null}
        suggestions={filteredSingleOntologies}
        itemTemplate={singleOntologyItemTemplate}
        completeMethod={searchSingleOntology}
        onHide={(e) => op.current.hide(e)}
        onChange={(e) => onSingleOntologyEditorValueChange(e)}
      />
      <SingleOntologyTooltip op={op} autocompleteSelectedItem={autocompleteSelectedItem} inputValue={rowProps.rowData[fieldname] ? trimWhitespace(rowProps.rowData[fieldname].curie.toLowerCase()) : null}/>
    </div>
  )
};
