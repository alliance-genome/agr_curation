import React, { useRef, useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { DiseaseAnnotationService } from '../service/DiseaseAnnotationService'
import { useMutation, useQuery } from 'react-query';
import { BiologicalEntityService } from "../service/BiologicalEntityService";
import { Toast } from 'primereact/toast';

import { returnSorted } from '../utils/utils';
import { SubjectEditor } from './SubjectEditor';
import { DiseaseEditor } from './DiseaseEditor';
import { FilterComponent } from './FilterComponent'
import { SearchService } from '../service/SearchService';

import { ControlledVocabularyDropdown } from './ControlledVocabularySelector';
import { ControlledVocabularyService } from '../service/ControlledVocabularyService';
import { ErrorMessageComponent } from './ErrorMessageComponent';

export const DiseaseAnnotationsComponent = () => {

    let [diseaseAnnotations, setDiseaseAnnotations] = useState(null);

    const [page, setPage] = useState(0);
    const [multiSortMeta, setMultiSortMeta] = useState([]);
    const [filters, setFilters] = useState({});
    const [rows, setRows] = useState(50);
    const [totalRecords, setTotalRecords] = useState(0);
    const [first, setFirst] = useState(0);
    const [originalRows, setOriginalRows] = useState([]);    const [editingRows, setEditingRows] = useState({});
    const [isEnabled, setIsEnabled] = useState(true); //needs better name
    const [diseaseRelationsTerms, setDiseaseRelationTerms] = useState();

    const [errorMessages, setErrorMessages] = useState({});

    const rowsInEdit = useRef(0);

    const diseaseAnnotationService = new DiseaseAnnotationService();
    const biologicalEntityService = new BiologicalEntityService();
    const searchService = new SearchService();

    const controlledVocabularyService = new ControlledVocabularyService();

    const toast_topleft = useRef(null);
    const toast_topright = useRef(null);



    useQuery(['diseaseAnnotations', rows, page, multiSortMeta, filters],
        () => searchService.search('disease-annotation', rows, page, multiSortMeta, filters), {
            onSuccess: (data) => {

                setDiseaseAnnotations(data.results);
                setTotalRecords(data.totalResults);
            },
            onError: (error) => {
                toast_topleft.current.show([
                    {life: 7000, severity: 'error', summary: 'Page error: ', detail: error.message, sticky: false }
                ])
            },
            onSettled:() => {
                setOriginalRows([]);
            },
            keepPreviousData: true,
            refetchOnWindowFocus: false

        }
    );

    useQuery(['diseaseRelationTerms'],
        () => controlledVocabularyService.getTerms('disease_relation_terms'), {
            onSuccess: (data) => {
                setDiseaseRelationTerms(data)
            }
        }
    )

    const onRelationEditorValueChange = (props, event) => {
        let updatedAnnotations = [...props.value];
        if(event.value || event.value === '') {
            updatedAnnotations[props.rowIndex].diseaseRelation = event.value.name;
            setDiseaseAnnotations(updatedAnnotations);
        }
    };

    const relationEditor = (props, disabled=false) => {
        return (
            <ControlledVocabularyDropdown
                options={diseaseRelationsTerms}
                editorChange={onRelationEditorValueChange}
                props={props}
            />
        )
    };

    const mutation = useMutation(updatedAnnotation => {
        return diseaseAnnotationService.saveDiseaseAnnotation(updatedAnnotation);
    });

    const onLazyLoad = (event) => { //extract into it's own hook? 
        setRows(event.rows);
        setPage(event.page);
        setFirst(event.first);
    };

    const onFilter = (filtersCopy) => { 
        setFilters({...filtersCopy});
    };

    const onSort = (event) => { //also extracted into hook
        setMultiSortMeta(
            returnSorted(event, multiSortMeta)
        )
    };

    const publicationTemplate = (rowData) => { 
        if (rowData && rowData.referenceList) {
            return <div>{rowData.referenceList.map(a => a.curie)}</div>
        }
        
    };
        

    const evidenceTemplate = (rowData) => { 
        if (rowData && rowData.evidenceCodes) {
            return (<div>
                <ul style={{listStyleType : 'none'}}>
                    {rowData.evidenceCodes.map((a,index) =>
                        <li key={index}>{a.curie}</li>
                    )}
                </ul>
            </div>);
        }
    };

    const negatedTemplate = (rowData) => { 
        if(rowData && rowData.negated !== null && rowData.negated !== undefined){
            return <div>{JSON.stringify(rowData.negated)}</div>
        }
    };

    const editorValidator = (value) => {
        value = value.replace(/\s{2,}/g,' ').trim(); //SCRUM-601 Removing leading & trailing extra spaces from input string
        /*if(value.toString().includes(':')) { //SCRUM-600 Making it case insensitive by defaulting to Uppercase
            let subStr = value.split(':');
            value = subStr[0].toUpperCase().concat(':').concat(subStr[1]);
        }else{
            value = value.toUpperCase();
        }*/
        return value;
    }

    const searchSubject = (event) => {
        event.query = editorValidator(event.query);
        biologicalEntityService.getBiologicalEntities(15, 0, null, {"curie":{"value": event.query}})
            .then((data) => {
                setFilteredSubjects(data.results);
            });
    };

    const searchDisease = (event) => {
        event.query = editorValidator(event.query);
        ontologyService.getTerms('doterm', 15, 0, null, {"curie":{"value": event.query}})
            .then((data) => {
                setFilteredDiseases(data.results);
            });
    };

    const onSubjectEditorValueChange = (props, event) => {
        let updatedAnnotations = [...props.value];
        if(event.target.value || event.target.value === '') {
            updatedAnnotations[props.rowIndex].subject = {};//this needs to be fixed. Otherwise, we won't have access to the other subject fields
            if(typeof event.target.value === "object"){
                updatedAnnotations[props.rowIndex].subject.curie = event.target.value.curie;
            } else {
                updatedAnnotations[props.rowIndex].subject.curie = event.target.value;
            }
            setDiseaseAnnotations(updatedAnnotations);
        }
    };


    const subjectEditor = (props) => {
        return (<div><AutoComplete
            field="curie"
            value={props.rowData.subject.curie}
            suggestions={filteredSubjects}
            itemTemplate={subjectItemTemplate}
            completeMethod={searchSubject}
            onChange={(e) => onSubjectEditorValueChange(props, e)}
        /><ErrorMessageComponent  errorMessages={errorMessages[props.rowIndex]} errorField={"subject"} /></div>)
    };

    const onDiseaseEditorValueChange = (props, event) => {
        let updatedAnnotations = [...props.value];
        if(event.target.value || event.target.value === '') {
            updatedAnnotations[props.rowIndex].object = {};//this needs to be fixed. Otherwise, we won't have access to the other subject fields
            if(typeof event.target.value === "object"){
                updatedAnnotations[props.rowIndex].object.curie = event.target.value.curie;
            } else {
                updatedAnnotations[props.rowIndex].object.curie = event.target.value;
            }
            setDiseaseAnnotations(updatedAnnotations);
        }
    };

    const diseaseEditor = (props) => {
        return (<div><AutoComplete
            field="curie"
            value={props.rowData.object.curie}
            suggestions={filteredDiseases}
            itemTemplate={diseaseItemTemplate}
            completeMethod={searchDisease}
            onChange={(e) => onDiseaseEditorValueChange(props, e)}
        /> <ErrorMessageComponent  errorMessages={errorMessages[props.rowIndex]} errorField={"object"} /></div>)
    };

    const onDiseaseRelationEditorValueChange = (props, event) => {
        let updatedAnnotations = [...props.value];
        if(event.value || event.value === '') {
            updatedAnnotations[props.rowIndex].diseaseRelation = event.value.name;//this needs to be fixed. Otherwise, we won't have access to the other subject fields
            setDiseaseAnnotations(updatedAnnotations);
        }
    };

    const diseaseRelationEditor = (props, disabled=false) => {
        return (
            <>
                <ControlledVocabularyDropdown
                    options={diseaseRelationsTerms}
                    editorChange={onDiseaseRelationEditorValueChange}
                    props={props}
                />
                <ErrorMessageComponent  errorMessages={errorMessages[props.rowIndex]} errorField={"diseaseRelation"} />
            </>
        )
    };

    const onRowEditInit = (event) => {
        rowsInEdit.current++;
        setIsEnabled(false);
        originalRows[event.index] = { ...diseaseAnnotations[event.index] };
        setOriginalRows(originalRows);
        console.log("in onRowEditInit")
    };

    const onRowEditCancel = (event) => {
        rowsInEdit.current--;
        if(rowsInEdit.current === 0){
            setIsEnabled(true);
        };

        let annotations = [...diseaseAnnotations];
        annotations[event.index] = originalRows[event.index];
        delete originalRows[event.index];
        setOriginalRows(originalRows);
        setDiseaseAnnotations(annotations);
        const errorMessagesCopy = errorMessages;
        errorMessagesCopy[event.index] = {};
        setErrorMessages({...errorMessagesCopy});

    };


    const onRowEditSave = (event) =>{//possible to shrink?
        rowsInEdit.current--;
        if(rowsInEdit.current === 0){
            setIsEnabled(true);
        }
        let updatedRow = JSON.parse(JSON.stringify(event.data));//deep copy
        if(Object.keys(event.data.subject).length >= 1){
            event.data.subject.curie = editorValidator(event.data.subject.curie);
            updatedRow.subject = {};
            updatedRow.subject.curie = event.data.subject.curie;
        }
        if(Object.keys(event.data.object).length >= 1){
            event.data.object.curie = editorValidator(event.data.object.curie);
            updatedRow.object = {};
            updatedRow.object.curie = event.data.object.curie;
        }


        mutation.mutate(updatedRow, {
            onSuccess: (data, variables, context) => {
                console.log(data);
                toast_topright.current.show({ severity: 'success', summary: 'Successful', detail: 'Row Updated' });

                let annotations = [...diseaseAnnotations];
                annotations[event.index].subject = data.data.entity.subject;
                annotations[event.index].object = data.data.entity.object;
                setDiseaseAnnotations(annotations);
                const errorMessagesCopy = errorMessages;
                errorMessagesCopy[event.index] = {};
                setErrorMessages({...errorMessagesCopy});
            },
            onError: (error, variables, context) => {
                rowsInEdit.current++;
                setIsEnabled(false);
                toast_topright.current.show([
                    {life: 7000, severity: 'error', summary: 'Update error: ', detail: error.response.data.errorMessage, sticky: false}
                ]);
                
                let annotations = [...diseaseAnnotations];
                
                const errorMessagesCopy = errorMessages;
                
                console.log(errorMessagesCopy);
                errorMessagesCopy[event.index] = {};
                Object.keys(error.response.data.errorMessages).forEach((field) => {
                    let messageObject = {
                        severity: "error",
                        message: error.response.data.errorMessages[field]
                    }
                    errorMessagesCopy[event.index][field] = messageObject;
                });

                console.log(errorMessagesCopy);
                setErrorMessages({...errorMessagesCopy});

                
                
                setDiseaseAnnotations(annotations);
                let _editingRows = { ...editingRows, ...{ [`${annotations[event.index].id}`]: true } };
                setEditingRows(_editingRows);
            },
            onSettled: (data, error, variables, context) => {

            },
        })
    };

    const onRowEditChange = (event) => {
        setEditingRows(event.data);
    }

    const subjectBodyTemplate = (rowData) => {//put into it's own component?
        if(rowData.subject){
            if(rowData.subject.symbol){
                return <div dangerouslySetInnerHTML={{__html: rowData.subject.curie + ' (' + rowData.subject.symbol + ')'}}/>;
            } else if(rowData.subject.name) {
                return <div dangerouslySetInnerHTML={{__html: rowData.subject.curie + ' (' + rowData.subject.name + ')'}}/>;
            }else {
                return <div>{rowData.subject.curie}</div>;
            }
        }
    };

    
    const diseaseBodyTemplate = (rowData) => {
            if(rowData.object){
            return <div>{rowData.object.curie} ({rowData.object.name})</div>;
        }
    };

    const filterComponentTemplate = (filterName, fields) => {
      return (<FilterComponent 
            isEnabled={isEnabled} 
            fields={fields} 
            filterName={filterName}
            currentFilters={filters}
            onFilter={onFilter}
        />);
    };

    return (
        <div>
            <div className="card">
                <Toast ref={toast_topleft} position="top-left" />
                <Toast ref={toast_topright} position="top-right" />
                <h3>Disease Annotations Table</h3>
                <DataTable value={diseaseAnnotations} className="p-datatable-md"
                  editMode="row" onRowEditInit={onRowEditInit} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}
                  editingRows={editingRows} onRowEditChange={onRowEditChange}
                  sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
                  first={first} 
                  dataKey="id"
                  paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
                  paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
                  currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[1, 10, 20, 50, 100, 250, 1000]}
                >

                  <Column field="curie" header="Curie" 
                    style={{whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word'}} 
                    sortable={isEnabled} 
                    filter 
                    filterElement={filterComponentTemplate("curieFilter", ["curie"])}
                  />

                  <Column
                    header="Subject"
                    sortable={isEnabled} 
                    filter filterElement={filterComponentTemplate("subject", ["subject.curie", "subject.name", "subject.symbol"])}                    
                    editor={(props) => <SubjectEditor rowProps={props} biologicalEntityService={biologicalEntityService} setDiseaseAnnotations={setDiseaseAnnotations} />} 
                    body={subjectBodyTemplate}  
                    style={{whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word'}}
                  />

                  <Column 
                    field="diseaseRelation"
                    header="Disease Relation"
                    sortable={isEnabled}
                    filter 
                    filterElement={filterComponentTemplate("diseaseRelation", ["diseaseRelation"])}
                    editor={(props) => relationEditor(props)}
                  />
                  
                  <Column header="Negated" body={negatedTemplate} sortable={isEnabled} ></Column>
                  
                  <Column 
                    header="Disease"
                    sortable={isEnabled} 
                    filter filterElement={filterComponentTemplate("object", ["object.curie", "object.name"])}
                    editor={(props) => <DiseaseEditor rowProps={props} searchService={searchService} setDiseaseAnnotations={setDiseaseAnnotations} />} 
                    body={diseaseBodyTemplate} 
                  />

                  <Column
                    field="evidenceCodes.curie"
                    header="Evidence Code"
                    body={evidenceTemplate} sortable={isEnabled} 
                    filter filterElement={filterComponentTemplate("evidenceCodes", ["evidenceCodes.curie"])}
                  />

                  <Column 
                    field="referenceList.curie"
                    header="Reference"
                    body={publicationTemplate} 
                    sortable={isEnabled} 
                    filter filterElement={filterComponentTemplate("referenceList", ["referenceList.curie"])}

                  />
                  <Column rowEditor headerStyle={{ width: '7rem' }} bodyStyle={{ textAlign: 'center' }}></Column>
                </DataTable>
            </div>
        </div>
    )
};
