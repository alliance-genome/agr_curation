import React, {useState} from 'react';
import {AutoComplete} from "primereact/autocomplete";
import {trimWhitespace } from '../../utils/utils';

   export const EvidenceEditor = ({ rowProps, searchService, setDiseaseAnnotations, autocompleteFields }) => {
        const [filteredEvidenceCodes, setFilteredEvidenceCodes] = useState([]);

        const searchEvidenceCodes = (event) => {
            //console.log(event);
            let evidenceFilter = {};
            autocompleteFields.forEach( field => {
              evidenceFilter[field] = {
                queryString : event.query,
                tokenOperator : "AND"
              }
            });
            let obsoleteFilter = {"obsolete": false};
            let subsetFilter = {"subsets": "agr_eco_terms"};

            searchService.search("ecoterm", 15, 0, null, {"evidenceFilter":evidenceFilter, "obsoleteFilter:":obsoleteFilter, "subsetFilter":subsetFilter})
                .then((data) => {
                    //console.log(data)
                    setFilteredEvidenceCodes(data.results);
                });
        };

        const onEvidenceEditorValueChange = (event) => { //this should propably be generalized so that all of these editor value changes can use the same method
            let updatedAnnotations = [...rowProps.value];
            if(event.value) {
                updatedAnnotations[rowProps.rowIndex].evidenceCodes = event.value;
            }
            setDiseaseAnnotations(updatedAnnotations);
        };

        const evidenceItemTemplate = (item) => {
            let inputValue = trimWhitespace(rowProps.rowData.object.curie.toLowerCase());
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

            <AutoComplete
                multiple
                value={rowProps.rowData.evidenceCodes}
                field="curie"
                suggestions={filteredEvidenceCodes}
                itemTemplate={evidenceItemTemplate}
                completeMethod={searchEvidenceCodes}
                onChange={(e) => onEvidenceEditorValueChange(e)}
            />

        )
    };
