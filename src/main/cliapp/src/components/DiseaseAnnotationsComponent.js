import React, {useRef, useState} from 'react';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import {DiseaseAnnotationService} from '../service/DiseaseAnnotationService'
import {useMutation, useQuery} from 'react-query';
import {BiologicalEntityService} from "../service/BiologicalEntityService";
import { Toast } from 'primereact/toast';
import {OntologyService} from "../service/OntologyService";
import { InputText } from 'primereact/inputtext';
import { returnSorted } from '../utils/utils';
import { SubjectEditor } from './SubjectEditor';
import { DiseaseEditor } from './DiseaseEditor';
import { FilterComponent } from './FilterComponent'
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

    const rowsInEdit = useRef(0);

    const diseaseAnnotationService = new DiseaseAnnotationService();
    const biologicalEntityService = new BiologicalEntityService();
    const ontologyService = new OntologyService();

    const toast_topleft = useRef(null);
    const toast_topright = useRef(null);

    useQuery(['diseaseAnnotations', rows, page, multiSortMeta, filters],
        () => diseaseAnnotationService.getDiseaseAnnotations(rows, page, multiSortMeta, filters), {
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

    const mutation = useMutation(updatedAnnotation => {
        return diseaseAnnotationService.saveDiseaseAnnotation(updatedAnnotation);
    });

    const onLazyLoad = (event) => { //extract into it's own hook? 
        setRows(event.rows);
        setPage(event.page);
        setFirst(event.first);
    };


    const onFilter = (filterName, filter) => { 
        const filtersCopy = filters; 
        if(filter===null){
            delete filtersCopy[filterName];
        }else {
            filtersCopy[filterName] = filter;
        }
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

    const onRowEditInit = (event) => {//can these row logic methods be extracted? Either into a lib file or into custom hooks?
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
    };

    const onRowEditSave = (event) =>{//possible to shrink?
        rowsInEdit.current--;
        if(rowsInEdit.current === 0){
            setIsEnabled(true);
        } 
        let updatedRow = JSON.parse(JSON.stringify(event.data));//deep copy
        if(Object.keys(event.data.subject).length > 1){
            updatedRow.subject = {};
            updatedRow.subject.curie = event.data.subject.curie;
        }
        if(Object.keys(event.data.object).length > 1){
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
            },
            onError: (error, variables, context) => {
                rowsInEdit.current++;
                setIsEnabled(false);
                toast_topright.current.show([
                    {life: 7000, severity: 'error', summary: 'Update error: ', detail: error.response.data.errorMessage, sticky: false}
                ]);

                let annotations = [...diseaseAnnotations];
                if(error.response.data.errorMessages.subject) {
                    annotations[event.index].subject.errorSeverity = "error";
                    annotations[event.index].subject.errorMessage = error.response.data.errorMessages.subject;
                }
                if(error.response.data.errorMessages.object){
                    annotations[event.index].object.errorSeverity = "error";
                    annotations[event.index].object.errorMessage = error.response.data.errorMessages.object;
                }
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

    
    const diseaseBodyTemplate = (rowData) => {//put into it's own component?
        if(rowData.object){
            return <div>{rowData.object.curie} ({rowData.object.name})</div>;
        }
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
                    <Column field="curie" header="Curie" style={{whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word'}} sortable={isEnabled} 
                        filter filterElement={<FilterComponent 
                                                    isEnabled={isEnabled} 
                                                    fields={["curie"]} 
                                                    filterName={"curie"}
                                                    onFilter={onFilter}
                                                />}>
                    </Column>
                    <Column field="subject.curie" header="Subject" sortable={isEnabled} 
                        filter filterElement={<FilterComponent 
                                                    isEnabled={isEnabled}
                                                    filterName={"subject"}
                                                    fields={["subject.curie", "subject.name", "subject.symbol"]} 
                                                    onFilter={onFilter}
                                                />}
                        editor={(props) => <SubjectEditor 
                                                rowProps={props} 
                                                biologicalEntityService={biologicalEntityService} 
                                                setDiseaseAnnotations={setDiseaseAnnotations}
                                            />} 
                        body={subjectBodyTemplate}  style={{whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word'}} >
                    </Column>
                    <Column field="diseaseRelation" header="Disease Relation" sortable={isEnabled} 
                        filter filterElement={<FilterComponent 
                                                    isEnabled={isEnabled} 
                                                    fields={["diseaseRelation"]}
                                                    filterName={"diseaseRelation"} 
                                                    onFilter={onFilter}
                                                />}>
                    </Column>
                    <Column field="negated" header="Negated" body={negatedTemplate} sortable={isEnabled} ></Column>
                    <Column field="object.curie" header="Disease" sortable={isEnabled} 
                        filter filterElement={<FilterComponent 
                                                isEnabled={isEnabled} 
                                                fields={["object.curie", "object.name"]} 
                                                filterName={"object"} 
                                                onFilter={onFilter}
                                            />}
                        editor={(props) => <DiseaseEditor
                                                rowProps={props} 
                                                ontologyService={ontologyService} 
                                                setDiseaseAnnotations={setDiseaseAnnotations}
                                            />} 
                        body={diseaseBodyTemplate}>
                    </Column>
                    <Column field="evidenceCodes.curie" header="Evidence Code" body={evidenceTemplate} sortable={isEnabled} 
                        filter filterElement={<FilterComponent 
                                                isEnabled={isEnabled} 
                                                fields={["evidenceCodes.curie"]} 
                                                filterName={"evidenceCodes"} 
                                                onFilter={onFilter}
                                            />}>
                    </Column>
                    <Column field="referenceList.curie" header="Reference" body={publicationTemplate} sortable={isEnabled} 
                        filter filterElement={<FilterComponent 
                                                isEnabled={isEnabled} 
                                                fields={["referenceList.curie"]}
                                                filterName={"referenceList"} 
                                                onFilter={onFilter}
                                            />}>
                    </Column>
                    <Column rowEditor headerStyle={{ width: '7rem' }} bodyStyle={{ textAlign: 'center' }}></Column>
                </DataTable>
            </div>
        </div>
    )
};
