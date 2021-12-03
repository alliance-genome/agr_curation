import React, {useState} from 'react';
import {AutoComplete} from "primereact/autocomplete";

   export const WithEditor = ({ rowProps, searchService, setDiseaseAnnotations, autocompleteFields }) => { 
        const [filteredWithGenes, setFilteredWithGenes] = useState([]);

        const searchWithGenes = (event) => {
            console.log(event);
            let withFilter = {};
            autocompleteFields.forEach( field => {
                withFilter[field] = event.query;
            });

            searchService.search("gene", 15, 0, null, {"withFilter":withFilter})
                .then((data) => {
                    console.log(data);
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
            return <div>{item.curie} ({item.symbol})</div>
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