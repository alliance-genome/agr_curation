import React, {useRef, useState} from 'react';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import {Button} from 'primereact/button';
import {DiseaseAnnotationService} from '../service/DiseaseAnnotationService'
import {useMutation, useQuery} from 'react-query';
import {Messages} from "primereact/messages";
import {InputText} from "primereact/inputtext";
import {AutoComplete} from "primereact/autocomplete";
import {BiologicalEntityService} from "../service/BiologicalEntityService";
import classNames from "classnames";
import { Toast } from 'primereact/toast';


export const DiseaseAnnotationsComponent = () => {

    let [diseaseAnnotations, setDiseaseAnnotations] = useState(null);

    const [page, setPage] = useState(0);
    const [multiSortMeta, setMultiSortMeta] = useState([]);
    const [filters, setFilters] = useState({});
    const [rows, setRows] = useState(50);
    const [totalRecords, setTotalRecords] = useState(0);
    const [first, setFirst] = useState(0);
    const [expandedRows, setExpandedRows] = useState(null);
    const [selectedSubject, setSelectedSubject] = useState(null);
    const [filteredSubjects, setFilteredSubjects] = useState(null);
    const [subjects, setSubjects] = useState(null);
    let [originalRows, setOriginalRows] = useState({});
    const [submitted, setSubmitted] = useState(false);
    const [editingRows, setEditingRows] = useState({});
    //let originalRows = {};

    const diseaseAnnotationService = new DiseaseAnnotationService();
    const biologicalEntityService = new BiologicalEntityService();

    const errorMessage = useRef(null);
    const toast = useRef(null);

    useQuery(['diseaseAnnotations', rows, page, multiSortMeta, filters],
        () => diseaseAnnotationService.getDiseaseAnnotations(rows, page, multiSortMeta, filters), {
            onSuccess: (data) => {
                setDiseaseAnnotations(data.results);
                setTotalRecords(data.totalResults);
            },
            onError: (error) => {
                errorMessage.current.show([
                    {severity: 'error', summary: 'Error', detail: error.message, sticky: true}
                ])
            },
            keepPreviousData: true

        }
    );

    // const { refetch } = useQuery(['diseaseAnnotations', rows, page, multiSortMeta, filters],
    //     () => biologicalEntityService.getBiologicalEntities(rows, page, null, filters), {
    //         onSuccess: (data) => {
    //             setFilteredSubjects(data.results);
    //         },
    //         onError: (error) => {
    //             errorMessage.current.show([
    //                 {severity: 'error', summary: 'Error', detail: error.message, sticky: true}
    //             ])
    //         },
    //         keepPreviousData: true,
    //         enabled:false
    //
    //     }
    // );

    const mutation = useMutation(newAnnotation => {
        return diseaseAnnotationService.saveDiseaseAnnotation(newAnnotation);
    });

    const onLazyLoad = (event) => {
        setRows(event.rows);
        setPage(event.page);
        setFirst(event.first);
        setSubmitted(false);
    }


    const onFilter = (event) => {
        setFilters(event.filters);
    }

    const onSort = (event) => {
        let found = false;
        const newSort = [...multiSortMeta];

        newSort.forEach((o) => {
            if (o.field === event.multiSortMeta[0].field) {
                o.order = event.multiSortMeta[0].order;
                found = true;
            }
        });

        if (!found) {
            setMultiSortMeta(newSort.concat(event.multiSortMeta));
        } else {
            setMultiSortMeta(newSort);
        }
    }

    const publicationTemplate = (rowData) => {
        if (rowData && rowData.referenceList) {
            return <div>{rowData.referenceList.map(a => a.curie)}</div>
        }
    };

    const negatedTemplate = (rowData) => {
        if(rowData && rowData.negated !== null && rowData.negated !== undefined){
            return <div>{JSON.stringify(rowData.negated)}</div>
        }
    };

    // const searchSubject = (event) => {
    //     setTimeout(() => {
    //         let _filteredSubjects;
    //         if (!event.query.trim().length) {
    //             _filteredSubjects = [...subjects];
    //         }
    //         else {
    //             refetch()
    //         }
    //
    //         setFilteredSubjects(_filteredSubjects);
    //     }, 250);
    // } this was from when I was working on autocomplete

    const onEditorValueChange = (props, value) => {
        let updatedAnnotations = [...props.value];
        if(value != null || value != '') {
            updatedAnnotations[props.rowIndex].subject = {};//this needs to be fixed. Otherwise, we won't have access to the other subject fields
            updatedAnnotations[props.rowIndex].subject.curie = value;
            setDiseaseAnnotations(updatedAnnotations);
            setSubmitted(true);
        }else{
            setSubmitted(false);
        }
        console.log(originalRows);
    }

    const requiredValidator = (rowData) => {
        let value = rowData['subject']['curie'];
        return value.length > 0;
    }

    const subjectEditor = (props) => {
        return <InputText type="text" value={props.rowData.subject.curie} onChange={(e) => onEditorValueChange(props, e.target.value)} required autoFocus
                           className={classNames({ 'p-invalid': submitted && !props.rowData.subject.curie })} />;
    }

    const paginatorLeft = <Button type="button" icon="pi pi-refresh" className="p-button-text"/>;
    const paginatorRight = <Button type="button" icon="pi pi-cloud" className="p-button-text"/>;

    const onRowEditInit = (event) => {
        originalRows[event.index] = { ...diseaseAnnotations[event.index] };
        setOriginalRows(originalRows);
    }

    const onRowEditCancel = (event) => {
        let annotations = [...diseaseAnnotations];
        annotations[event.index] = originalRows[event.index];
        delete originalRows[event.index];
        setOriginalRows(originalRows);
        setDiseaseAnnotations(annotations);
    }

    const onRowEditSave = (props) =>{
        setSubmitted(true);
        mutation.mutate(props.data, {
            onSuccess: (data, variables, context) => {
                toast.current.show({ severity: 'success', summary: 'Successful', detail: 'Product Updated', life: 3000 });
            },
            onError: (error, variables, context) => {
                console.log(error.response.data.details);
                errorMessage.current.show([
                    {severity: 'error', summary: 'Error', detail: error.response.data.details, sticky: true}
                ]);
                setDiseaseAnnotations(diseaseAnnotations);
                setSubmitted(false);
            },
            onSettled: (data, error, variables, context) => {
                console.log(data);
                console.log(error.response.data);
            },
        })
    }

    return (
        <div>
            <div className="card">
                <Toast ref={toast} />
                <h3>Disease Annotations Table</h3>
                <Messages ref={errorMessage}/>
                <DataTable value={diseaseAnnotations} className="p-datatable-md"
                           editMode="row" onRowEditInit={onRowEditInit} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}
                           editingRows={editingRows} onRowEditChange={(e) => setEditingRows(e.data)} rowEditorValidator={(data) => requiredValidator(data)}
                           sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
                           first={first} onFilter={onFilter} filters={filters}
                           dataKey="id" expandedRows={expandedRows} onRowToggle={(e) => setExpandedRows(e.data)}
                           paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
                           paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
                           currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
                           paginatorLeft={paginatorLeft} paginatorRight={paginatorRight}>
                    <Column field="curie" header="Curie" style={{whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word'}} sortable filter></Column>
                    <Column field="subject.curie" header="Subject" sortable filter editor={(props) => subjectEditor(props)}></Column>
                    <Column field="object.curie" header="Disease" sortable filter></Column>
                    <Column field="referenceList.curie" header="Reference" body={publicationTemplate} sortable filter></Column>
                    <Column field="negated" header="Negated" body={negatedTemplate} sortable ></Column>
                    <Column field="diseaseRelation" header="Disease Relation" sortable filter></Column>
                    <Column field="created" header="Creation Date" sortable ></Column>
                    <Column rowEditor headerStyle={{ width: '7rem' }} bodyStyle={{ textAlign: 'center' }}></Column>
                </DataTable>
            </div>
        </div>
    )
}
