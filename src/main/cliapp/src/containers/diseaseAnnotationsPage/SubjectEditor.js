import React, {useState, useRef} from 'react';
import {AutoComplete} from "primereact/autocomplete";
import {trimWhitespace } from '../../utils/utils';
import {SubjectTooltip} from './SubjectTooltip';


   export const SubjectEditor = ({ rowProps, searchService, setDiseaseAnnotations, autocompleteFields }) => {
        const [filteredSubjects, setFilteredSubjects] = useState([]);

        const op = useRef(null);
        const [autocompleteSelectedItem, setAutocompleteSelectedItem] = useState({});

        const searchSubject = (event) => {
            //console.log(event);
            let subjectFilter = {};
            autocompleteFields.forEach( field => {
                subjectFilter[field] = {
                  queryString : event.query,
                  tokenOperator : "AND"
                }
            });

            searchService.search("biologicalentity", 15, 0, null, {"subjectFilter":subjectFilter})
                .then((data) => {
                    //console.log(data);
                    if(data.results && data.results.length >0)
                        setFilteredSubjects(data.results);
                    else
                        setFilteredSubjects([]);
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

        const onSelectionOver = (event, item) => {
          setAutocompleteSelectedItem(item);
          op.current.show(event);
        };

        const subjectItemTemplate = (item) => {
            let inputValue = trimWhitespace(rowProps.rowData.subject.curie.toLowerCase());
            if(autocompleteSelectedItem.synonyms && autocompleteSelectedItem.synonyms.length>0){
                for(let i in autocompleteSelectedItem.synonyms){
                    if(autocompleteSelectedItem.synonyms[i].name.toString().toLowerCase().indexOf(inputValue)< 0){
                        delete autocompleteSelectedItem.synonyms[i];
                    }
                }
            }
            if(autocompleteSelectedItem.crossReferences && autocompleteSelectedItem.crossReferences.length>0){
                for(let i in autocompleteSelectedItem.crossReferences){
                    if(autocompleteSelectedItem.crossReferences[i].curie.toString().toLowerCase().indexOf(inputValue)< 0){
                        delete autocompleteSelectedItem.crossReferences[i];
                    }
                }
            }

            if(autocompleteSelectedItem.secondaryIdentifiers && autocompleteSelectedItem.secondaryIdentifiers.length>0){
                for(let i in autocompleteSelectedItem.secondaryIdentifiers){
                    if(autocompleteSelectedItem.secondaryIdentifiers[i].toString().toLowerCase().indexOf(inputValue)< 0){
                        delete autocompleteSelectedItem.secondaryIdentifiers[i];
                    }
                }
            }

            if(item.symbol){
                return (
                  <div>
                    <div onMouseOver={(event) => onSelectionOver(event, item)} dangerouslySetInnerHTML={{__html: item.symbol + ' (' + item.curie + ') '}}/>
                  </div>
                );
            } else if(item.name){
                return (
                  <div>
                    <div onMouseOver={(event) => onSelectionOver(event, item)} dangerouslySetInnerHTML={{__html: item.name + ' (' + item.curie + ') '}}/>
                  </div>
                );
            }else {
                return (
                  <div>
                    <div onMouseOver={(event) => onSelectionOver(event, item)}>{item.curie}</div>
                  </div>
                );
            }
        };

        return (
            <div>
              <AutoComplete
                id={rowProps.rowData.subject.curie}
                panelStyle={{ width: '15%', display: 'flex', maxHeight: '350px'}}
                //panelStyle={{ width : "15%", height : "350px"}}
                //scrollable scrollHeight="350px" virtualScrollerOptions={{ itemSize: 15, orientation:'horizontal'}}
                field="curie"
                value={rowProps.rowData.subject.curie}
                suggestions={filteredSubjects}
                itemTemplate={subjectItemTemplate}
                completeMethod={searchSubject}
                onHide={(e) => op.current.hide(e)}
                onChange={(e) => onSubjectEditorValueChange(e)}
              />
              <SubjectTooltip op={op} autocompleteSelectedItem={autocompleteSelectedItem} inputValue={trimWhitespace(rowProps.rowData.subject.curie.toLowerCase())}
              />
            </div>

        )
    };
