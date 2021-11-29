import React, {useState} from 'react';
import {AutoComplete} from "primereact/autocomplete";

   export const SubjectEditor = ({ rowProps, searchService, setDiseaseAnnotations }) => { 
        const [filteredSubjects, setFilteredSubjects] = useState([]);

        const searchSubject = (event) => {
            console.log(event);
            searchService.search("biologicalentity", 15, 0, null, {"subjectFilter":{"curie": event.query}})
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
            if(item.symbol){
                return <div dangerouslySetInnerHTML={{__html: item.curie + ' (' + item.symbol + ')'}}/>;
            } else if(item.name){
                return <div dangerouslySetInnerHTML={{__html: item.curie + ' (' + item.name + ')'}}/>;
            }else {
                return <div>{item.curie}</div>;
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