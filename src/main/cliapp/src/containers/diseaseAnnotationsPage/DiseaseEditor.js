import React, {useState} from 'react';
import {AutoComplete} from "primereact/autocomplete";

export const DiseaseEditor = ({ rowProps, searchService, setDiseaseAnnotations, autocompleteFields }) => {    
    const [filteredDiseases, setFilteredDiseases] = useState([]);
    
    const searchDisease = (event) => {
        let diseaseFilter = {};
        autocompleteFields.forEach( field => {
            diseaseFilter[field] = event.query;
        });
        let obsoleteFilter = {"obsolete": false};

        searchService.search('doterm', 15, 0, [], {"diseaseFilter":diseaseFilter, "obsoleteFilter":obsoleteFilter})
            .then((data) => {
                setFilteredDiseases(data.results);
            });
    };
    
    const onDiseaseEditorValueChange = (event) => {
        let updatedAnnotations = [...rowProps.value];
        

        if(event.target.value || event.target.value === '') {
            updatedAnnotations[rowProps.rowIndex].object = {};//this needs to be fixed. Otherwise, we won't have access to the other subject fields
            if(typeof event.target.value === "object"){
                updatedAnnotations[rowProps.rowIndex].object.curie = event.target.value.curie;
            } else {
                updatedAnnotations[rowProps.rowIndex].object.curie = event.target.value;
            }
            setDiseaseAnnotations(updatedAnnotations);
        }
    };
    
    const diseaseItemTemplate = (item) => {
        let inputValue = rowProps.rowData.object.curie.toLowerCase();
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
        if(item.name){
            return <div dangerouslySetInnerHTML={{__html: item.name + ' (' + item.curie + ') ' + str.toString()}}/>;
        }else {
            return <div>{item.curie + str.toString()}</div>;
        }
    };

    return (
        <AutoComplete
            field="curie"
            value={rowProps.rowData.object.curie}
            suggestions={filteredDiseases}
            itemTemplate={diseaseItemTemplate}
            completeMethod={searchDisease}
            onChange={(e) => onDiseaseEditorValueChange(e)}
        />
    )
};
