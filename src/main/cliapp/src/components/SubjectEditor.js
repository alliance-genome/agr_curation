import React, {useState} from 'react';
import {AutoComplete} from "primereact/autocomplete";

   export const SubjectEditor = ({ rowProps, searchService, setDiseaseAnnotations, autocompleteFields }) => {
        const [filteredSubjects, setFilteredSubjects] = useState([]);

        const searchSubject = (event) => {
            console.log(event);
            let subjectFilter = {};
            autocompleteFields.forEach( field => {
                subjectFilter[field] = event.query;
            });




            searchService.search("biologicalentity", 15, 0, null, {"subjectFilter":subjectFilter})
                .then((data) => {
                    console.log(data);
                    setFilteredSubjects(data.results);
                });
        };

        const onSubjectEditorValueChange = (event) => {//this should propably be generalized so that all of these editor value changes can use the same method
            let updatedAnnotations = [...rowProps.value];
            if(event.target.value || event.target.value === '') {
                updatedAnnotations[rowProps.rowIndex].subject = {};//this needs to be fixed. Otherwise, we won't have access to the other subject fields
                if(typeof event.target.value === "object"){
                    updatedAnnotations[rowProps.rowIndex].subject.curie = event.target.value.curie;
                } else {
                    updatedAnnotations[rowProps.rowIndex].subject.curie = event.target.value;
                }
                setDiseaseAnnotations(updatedAnnotations);
            }
        };

        const subjectItemTemplate = (item) => {
            let inputValue = rowProps.rowData.subject.curie.toLowerCase();
            let str = "";
            let synonymsStr = "";
            let isSynonym = false;
            let crossReferencesStr = "";
            let isCrossReference = false;
            let secondaryIdentifiersStr = "";
            let isSecondaryIdentifier = false;
            autocompleteFields.forEach( field => {
                if(field === "synonyms.name" && item["synonyms"]){
                    for(let i=0; i< item["synonyms"].length ; i++ ) {
                        if(item["synonyms"][i].name) {
                            if (item["synonyms"][i].name.toString().toLowerCase().indexOf(inputValue) >= 0) {
                                synonymsStr +=  item["synonyms"][i].name.toString() + ", ";
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
                                if (field !== "curie" && field !== "symbol")
                                    str += field + ": " + item[field].toString() + ", ";
                            }
                        }
                    }
                }
            });
            str = str.length > 0 ? str.substring(0 , str.length-2) : " "; //To remove trailing comma
            if(item.symbol){
                return <div dangerouslySetInnerHTML={{__html: item.symbol + ' (' + item.curie + ') ' + str.toString()}}/>;
            } else if(item.name){
                return <div dangerouslySetInnerHTML={{__html: item.name + ' (' + item.curie + ') ' + str.toString()}}/>;
            }else {
                return <div>{item.curie + str.toString()}</div>;
            }
        };

        return (

            <AutoComplete
                    field="curie"
                    value={rowProps.rowData.subject.curie}
                    suggestions={filteredSubjects}
                    itemTemplate={subjectItemTemplate}
                    completeMethod={searchSubject}
                    onChange={(e) => onSubjectEditorValueChange(e)}
            />

        )
    };
