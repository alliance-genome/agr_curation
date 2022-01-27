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
      let str = "";
      let synonymsStr = "";
            let isSynonym = false;
            let crossReferencesStr = "";
            let isCrossReference = false;
            let secondaryIdentifiersStr = "";
            let isSecondaryIdentifier = false;
            autocompleteFields.forEach( field => {
                if(field === "synonyms" && item["synonyms"]){
                    for(let i=0; i< item["synonyms"].length ; i++ ) {
                        if(item["synonyms"][i]) {
                            if (item["synonyms"][i].toLowerCase().indexOf(inputValue) >= 0) {
                                synonymsStr +=  item["synonyms"][i] + ", ";
                                isSynonym = true;
                            }
                        }
                    }
                if(isSynonym){
                    str += "Synonym: " + synonymsStr;
                }
                }else if(field === "crossReferences.curie" && item["crossReferences"]){
                    for(let i=0; i< item["crossReferences"].length ; i++ ) {
                        if(item["crossReferences"][i].curie) {
                            if (item["crossReferences"][i].curie.toString().toLowerCase().indexOf(inputValue) >= 0) {
                                crossReferencesStr += item["crossReferences"][i].curie.toString() + ", ";
                                isCrossReference = true;
                            }
                        }
                    }
                    if(isCrossReference){
                        str += "CrossReferences: " + crossReferencesStr;
                    }
                }else {
                    if (item[field]) {
                        if(field === "secondaryIdentifiers") {
                            for(var i=0; i< item["secondaryIdentifiers"].length ; i++ ) {
                                if (item["secondaryIdentifiers"][i].toLowerCase().indexOf(inputValue) >= 0) {
                                    secondaryIdentifiersStr += item["secondaryIdentifiers"][i] + ", ";
                                    isSecondaryIdentifier = true;
                                }
                            }
                            if(isSecondaryIdentifier){
                                str += "SecondaryIdentifiers: " + secondaryIdentifiersStr;
                            }
                        }else {
                            if (item[field].toString().toLowerCase().indexOf(inputValue) >= 0) {
                                if (field !== "curie" && field !== "name")
                                    str += field + ": " + item[field].toString() + ", ";
                            }
                        }
                    }
                }
            });
      str = str.length > 0 ? str.substring(0 , str.length-2) : " "; //To remove trailing comma
            return <div dangerouslySetInnerHTML={{__html: item.abbreviation + ' - ' + item.name + ' (' + item.curie + ') ' + str.toString()}}/>;
        };

    return (
      <div>
        <AutoComplete
          id={rowProps.rowData.conditionGeneOntology.curie}
          panelStyle={{ width: '15%', display: 'flex', maxHeight: '350px'}}
          field="curie"
          value={rowProps.rowData.conditionGeneOntology.curie}
          suggestions={filteredConditionGeneOntologies}
          itemTemplate={conditionGeneOntologyItemTemplate}
          completeMethod={searchConditionGeneOntology}
          onHide={(e) => op.current.hide(e)}
          onChange={(e) => onConditionGeneOntologyEditorValueChange(e)}
      />
      <ConditionGeneOntologyTooltip op={op} autocompleteSelectedItem={autocompleteSelectedItem} inputValue={trimWhitespace(rowProps.rowData.conditionGeneOntology.curie.toLowerCase())}
            />
      </div>
    )
};
