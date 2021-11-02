import React, {useState} from 'react';
import {AutoComplete} from "primereact/autocomplete";
import {Message} from "primereact/message";

   export const SubjectEditor = (props) => { //it's own component?
        const [filteredSubjects, setFilteredSubjects] = useState([]);

        const searchSubject = (event) => {
            props.biologicalEntityService.getBiologicalEntities(15, 0, null, {"curie":{"value": event.query}})
                .then((data) => {
                    setFilteredSubjects(data.results);
                });
        };

        const onSubjectEditorValueChange = (event) => {//this should propably be generalized so that all of these editor value changes can use the same method
            let updatedAnnotations = [...props.rowProps.value];
            if(event.target.value || event.target.value === '') {
                updatedAnnotations[props.rowProps.rowIndex].subject = {};//this needs to be fixed. Otherwise, we won't have access to the other subject fields
                if(typeof event.target.value === "object"){
                    updatedAnnotations[props.rowProps.rowIndex].subject.curie = event.target.value.curie;
                } else {
                    updatedAnnotations[props.rowProps.rowIndex].subject.curie = event.target.value;
                }
                props.setDiseaseAnnotations(updatedAnnotations);
            }
        };

        const subjectItemTemplate = (item) => {//put into it's own component?
            if(item.symbol){
                return <div dangerouslySetInnerHTML={{__html: item.curie + ' (' + item.symbol + ')'}}/>;
            } else if(item.name){
                return <div dangerouslySetInnerHTML={{__html: item.curie + ' (' + item.name + ')'}}/>;
            }else {
                return <div>{item.curie}</div>;
            }
        };


        return (<><AutoComplete
                field="curie"
                value={props.rowProps.rowData.subject.curie}
                suggestions={filteredSubjects}
                itemTemplate={subjectItemTemplate}
                completeMethod={searchSubject}
                onChange={(e) => onSubjectEditorValueChange(e)}
            /><Message severity={props.rowProps.rowData.subject.errorSeverity ? props.rowProps.rowData.subject.errorSeverity : ""} text={props.rowProps.rowData.subject.errorMessage} /></>)
        };