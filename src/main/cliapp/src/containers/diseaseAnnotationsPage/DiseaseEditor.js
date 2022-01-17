import React, {useState, useRef} from 'react';
import {AutoComplete} from "primereact/autocomplete";
import {trimWhitespace } from '../../utils/utils';
import {DiseaseTooltip} from './DiseaseTooltip';

export const DiseaseEditor = ({ rowProps, searchService, setDiseaseAnnotations, autocompleteFields }) => {
    const [filteredDiseases, setFilteredDiseases] = useState([]);

    const op = useRef(null);
    const [autocompleteSelectedItem, setAutocompleteSelectedItem] = useState({});

    const searchDisease = (event) => {
        let diseaseFilter = {};
        autocompleteFields.forEach( field => {
            diseaseFilter[field] = event.query;
        });
        let obsoleteFilter = {"obsolete": false};

        searchService.search('doterm', 15, 0, [], {"diseaseFilter":diseaseFilter, "obsoleteFilter":obsoleteFilter})
            .then((data) => {
                if(data.results && data.results.length >0)
                    setFilteredDiseases(data.results);
                else
                    setFilteredDiseases([]);
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

    const onSelectionOver = (event, item) => {
        setAutocompleteSelectedItem(item);
        op.current.show(event);
    };

    const diseaseItemTemplate = (item) => {
        let inputValue = trimWhitespace(rowProps.rowData.object.curie.toLowerCase());
        if(autocompleteSelectedItem.synonyms && autocompleteSelectedItem.synonyms.length>0){
            for(let i in autocompleteSelectedItem.synonyms){
                if(autocompleteSelectedItem.synonyms[i].toString().toLowerCase().indexOf(inputValue)< 0){
                    autocompleteSelectedItem.synonyms.splice(i,1);
                }
            }
        }
        if(autocompleteSelectedItem.crossReferences && autocompleteSelectedItem.crossReferences.length>0){
            for(let i in autocompleteSelectedItem.crossReferences){
                if(autocompleteSelectedItem.crossReferences[i].curie.toString().toLowerCase().indexOf(inputValue)< 0){
                    autocompleteSelectedItem.crossReferences.splice(i,1);
                }
            }
        }

        if(autocompleteSelectedItem.secondaryIdentifiers && autocompleteSelectedItem.secondaryIdentifiers.length>0){
            for(let i in autocompleteSelectedItem.secondaryIdentifiers){
                if(autocompleteSelectedItem.secondaryIdentifiers[i].toString().toLowerCase().indexOf(inputValue)< 0){
                    autocompleteSelectedItem.secondaryIdentifiers.splice(i,1);
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
            id={rowProps.rowData.object.curie}
            panelStyle={{ width: '15%', display: 'flex', maxHeight: '350px'}}
            field="curie"
            value={rowProps.rowData.object.curie}
            suggestions={filteredDiseases}
            itemTemplate={diseaseItemTemplate}
            completeMethod={searchDisease}
            onHide={(e) => op.current.hide(e)}
            onChange={(e) => onDiseaseEditorValueChange(e)}
        />
            <DiseaseTooltip op={op} autocompleteSelectedItem={autocompleteSelectedItem} inputValue={trimWhitespace(rowProps.rowData.object.curie.toLowerCase())}
            />
        </div>
    )
};
