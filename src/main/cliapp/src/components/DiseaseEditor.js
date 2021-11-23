import React, {useState} from 'react';
import {AutoComplete} from "primereact/autocomplete";
import {Message} from "primereact/message";

export const DiseaseEditor = (props) => {    
    const [filteredDiseases, setFilteredDiseases] = useState([]);
    
    const searchDisease = (event) => {
        props.searchService.search('doterm', 15, 0, [], {"curieFilter":{"curie": event.query}})
            .then((data) => {
                setFilteredDiseases(data.results);
            });
    };
    
    const onDiseaseEditorValueChange = (event) => {
        let updatedAnnotations = [...props.rowProps.value];
        

        if(event.target.value || event.target.value === '') {
            updatedAnnotations[props.rowProps.rowIndex].object = {};//this needs to be fixed. Otherwise, we won't have access to the other subject fields
            if(typeof event.target.value === "object"){
                updatedAnnotations[props.rowProps.rowIndex].object.curie = event.target.value.curie;
            } else {
                updatedAnnotations[props.rowProps.rowIndex].object.curie = event.target.value;
            }
            props.setDiseaseAnnotations(updatedAnnotations);
        }
    };
    
    const diseaseItemTemplate = (item) => {
        return <div>{item.curie} ({item.name})</div>;
    };

    return (<div><AutoComplete
        field="curie"
        value={props.rowProps.rowData.object.curie}
        suggestions={filteredDiseases}
        itemTemplate={diseaseItemTemplate}
        completeMethod={searchDisease}
        onChange={(e) => onDiseaseEditorValueChange(e)}
    /><Message severity={props.rowProps.rowData.object.errorSeverity ? props.rowProps.rowData.object.errorSeverity : ""} text={props.rowProps.rowData.object.errorMessage} /></div>)
};
