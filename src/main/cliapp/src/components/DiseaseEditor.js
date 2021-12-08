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
        return <div>{item.curie} ({item.name})</div>;
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
