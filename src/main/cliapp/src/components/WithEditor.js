import React, {useState} from 'react';
import {AutoComplete} from "primereact/autocomplete";

   export const WithEditor = ({ rowProps, searchService, setDiseaseAnnotations, autocompleteFields }) => { 
        const [filteredWithGenes, setFilteredWithGenes] = useState([]);
        const [query, setQuery] = useState();

        const searchWithGenes = (event) => {
            setQuery(event.query);
            let withFilter = {};
            autocompleteFields.forEach( field => {
                withFilter[field] = event.query;
            });

            searchService.search("gene", 15, 0, null, {"withFilter":withFilter})
                .then((data) => {
                    if (data.results) {
                        setFilteredWithGenes(data.results.filter((gene) => Boolean(gene.curie.startsWith("HGNC:"))));
                    }
                    else {
                        setFilteredWithGenes([]);
                    }
                });
        };

        const onWithEditorValueChange = (event) => {//this should propably be generalized so that all of these editor value changes can use the same method
            let updatedAnnotations = [...rowProps.value];
            if(event.value) {
                updatedAnnotations[rowProps.rowIndex].with = event.value;
            }
            setDiseaseAnnotations(updatedAnnotations);
        };

        const withItemTemplate = (item) => {
            let inputValue = query.toLowerCase();
            let str = "";
            let synonymsStr = "";
            let isSynonym = false;
            let crossReferencesStr = "";
            let isCrossReference = false;
            let secondaryIdentifiersStr = "";
            let isSecondaryIdentifier = false;
            autocompleteFields.forEach( field => {
                if(field == "synonyms.name" && item["synonyms"]){
                    for(let i=0; i< item["synonyms"].length ; i++ ) {
                        if(item["synonyms"][i].name) {
                            if (item["synonyms"][i].name.toString().toLowerCase().indexOf(inputValue) >= 0) {
                                synonymsStr +=  item["synonyms"][i].name.toString() + ", ";
                                isSynonym = true;
                             }
                        }
                    }
                    if(isSynonym){
                        str += "Synonym" + ": " + synonymsStr;
                    }
                }else if(field == "crossReferences.curie" && item["crossReferences"]){
                    for(let i=0; i< item["crossReferences"].length ; i++ ) {
                        if(item["crossReferences"][i].curie) {
                            if (item["crossReferences"][i].curie.toString() != item["curie"].toString()){
                                if (item["crossReferences"][i].curie.toString().toLowerCase().indexOf(inputValue) >= 0) {
                                    crossReferencesStr += item["crossReferences"][i].curie.toString() + ", ";
                                    isCrossReference = true;
                                }    
                            }
                        }
                    }
                    if(isCrossReference){
                        str += "CrossReferences" + ": " + crossReferencesStr;
                    }
                }else {
                    if (item[field]) {
                        if(field == "secondaryIdentifiers") {
                            for(var i=0; i< item["secondaryIdentifiers"].length ; i++ ) {
                                if (item["secondaryIdentifiers"][i].toLowerCase().indexOf(inputValue) >= 0) {
                                    secondaryIdentifiersStr += item["secondaryIdentifiers"][i] + ", ";
                                    isSecondaryIdentifier = true;
                                }
                            }
                            if(isSecondaryIdentifier){
                                str += "SecondaryIdentifiers" + ": " + secondaryIdentifiersStr;
                            }
                        }else {
                            if (item[field].toString().toLowerCase().indexOf(inputValue) >= 0) {
                                if (field != "curie" && field != "symbol")
                                    str += field + ": " + item[field].toString() + ", ";
                            }
                        }
                    }
                }
            });
            str = str.length > 0 ? str.substring(0 , str.length-2) : " "; //To remove trailing comma
            if(item.name){
                return <div dangerouslySetInnerHTML={{__html: item.name + ' (' + item.curie + ') ' + str.toString()}}/>;
            }else if(item.symbol){
                return <div dangerouslySetInnerHTML={{_html: item.symbol + ' (' +item.curie + ') ' + str.toString()}}/>;
            }else{
                return <div>{item.curie + str.toString()}</div>;
            }

            //return <div>{item.curie} ({item.symbol})</div>
        };


        return (
            
            <AutoComplete
                multiple
                value={rowProps.rowData.with}
                field="curie"
                suggestions={filteredWithGenes}
                itemTemplate={withItemTemplate}
                completeMethod={searchWithGenes}
                onChange={(e) => onWithEditorValueChange(e)}
            />
           
        )
    };