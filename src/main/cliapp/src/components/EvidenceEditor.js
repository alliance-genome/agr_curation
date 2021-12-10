import React, {useState} from 'react';
import {AutoComplete} from "primereact/autocomplete";

   export const EvidenceEditor = ({ rowProps, searchService, setDiseaseAnnotations, autocompleteFields }) => { 
        const [filteredEvidenceCodes, setFilteredEvidenceCodes] = useState([]);

        const searchEvidenceCodes = (event) => {
            //console.log(event);
            let evidenceFilter = {};
            autocompleteFields.forEach( field => {
                evidenceFilter[field] = event.query;
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
            return <div>{item.abbreviation} - {item.name} ({item.curie})</div>
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