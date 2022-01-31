import React, { useRef, useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { InputTextEditor } from '../../components/InputTextEditor';
import { useMutation, useQuery } from 'react-query';
import { useOktaAuth } from '@okta/okta-react';
import { Toast } from 'primereact/toast';
import { SearchService } from '../../service/SearchService';
import { Messages } from 'primereact/messages';
import { FilterComponent } from '../../components/FilterComponent'
import { MultiSelect } from 'primereact/multiselect';
import { ErrorMessageComponent } from '../../components/ErrorMessageComponent';
import { returnSorted, trimWhitespace } from '../../utils/utils';
import { ConditionGeneOntologyEditor } from './ConditionGeneOntologyEditor';
import { ExperimentalConditionService } from '../../service/ExperimentalConditionService';

export const ExperimentalConditionsTable = () => {

  let [experimentalConditions, setExperimentalConditions] = useState(null);
  
  const [errorMessages, setErrorMessages] = useState({});
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [filters, setFilters] = useState({});
  const [page, setPage] = useState(0);
  const [first, setFirst] = useState(0);
  const [rows, setRows] = useState(50);
  const [originalRows, setOriginalRows] = useState([]);
  const [editingRows, setEditingRows] = useState({});
  const [totalRecords, setTotalRecords] = useState(0);
  const [isEnabled, setIsEnabled] = useState(true);
  const searchService = new SearchService();
  const errorMessage = useRef(null);
  const { authState } = useOktaAuth();
  const columnNames = ["Unique ID", "Statement", "Class", "ID", "Gene Ontology", "Chemical", "Anatomy", "Condition Taxon", "Quantity"];
  const [selectedColumnNames, setSelectedColumnNames] = useState(columnNames);
  const toast_topleft = useRef(null);
  const toast_topright = useRef(null);
  const rowsInEdit = useRef(0);
  
  let experimentalConditionService = null;
  
  const sortMapping = {
    'conditionGeneOntology.name' : ['conditionGeneOntology.curie', 'conditionGeneOntology.namespace']
  }

  
  useQuery(['experimentalConditions', rows, page, multiSortMeta, filters],
    () => searchService.search('experimental-condition', rows, page, multiSortMeta, filters, sortMapping), {
    onSuccess: (data) => {
      setExperimentalConditions(data.results);
      setTotalRecords(data.totalResults);
    },
    onError: (error) => {
      toast_topleft.current.show([
            { life: 7000, severity: 'error', summary: 'Page error: ', detail: error.message, sticky: false }
          ]);
    },
    onSettled: () => {
      setOriginalRows([]);
    },
    keepPreviousData: true,
    refetchOnWindowFocus: false
  });

  const mutation = useMutation(updatedCondition => {
      if (!experimentalConditionService) {
        experimentalConditionService = new ExperimentalConditionService(authState);
      }
      return experimentalConditionService.saveExperimentalCondition(updatedCondition);
    });

  const onLazyLoad = (event) => {
    setRows(event.rows);
    setPage(event.page);
    setFirst(event.first);
  }


  const onFilter = (filtersCopy) => { 
    setFilters({...filtersCopy});
  };

  const onSort = (event) => {
    setMultiSortMeta(returnSorted(event, multiSortMeta));
  };
  
  const onRowEditInit = (event) => {
      rowsInEdit.current++;
      setIsEnabled(false);
      originalRows[event.index] = { ...experimentalConditions[event.index] };
      setOriginalRows(originalRows);
      console.log("in onRowEditInit");
    };

    const onRowEditCancel = (event) => {
      rowsInEdit.current--;
      if (rowsInEdit.current === 0) {
          setIsEnabled(true);
      };

      let conditions = [...experimentalConditions];
      conditions[event.index] = originalRows[event.index];
      delete originalRows[event.index];
      setOriginalRows(originalRows);
      setExperimentalConditions(conditions);
      const errorMessagesCopy = errorMessages;
      errorMessagesCopy[event.index] = {};
      setErrorMessages({ ...errorMessagesCopy });

    };


    const onRowEditSave = (event) => {
      rowsInEdit.current--;
      if (rowsInEdit.current === 0) {
        setIsEnabled(true);
      }
      if (event.data.conditionGeneOntology === null) {
        delete event.data.conditionGeneOntology;
      }
      let updatedRow = JSON.parse(JSON.stringify(event.data));//deep copy
      if (event.data.conditionGeneOntology && Object.keys(event.data.conditionGeneOntology).length >= 1) {
          event.data.conditionGeneOntology.curie = trimWhitespace(event.data.conditionGeneOntology.curie);
          updatedRow.conditionGeneOntology = {};
          updatedRow.conditionGeneOntology = event.data.conditionGeneOntology;
      }

      mutation.mutate(updatedRow, {
          onSuccess: (data, variables, context) => {
            toast_topright.current.show({ severity: 'success', summary: 'Successful', detail: 'Row Updated' });

            let conditions = [...experimentalConditions];
            conditions[event.index].conditionGeneOntology = data.data.entity.conditionGeneOntology;
            setExperimentalConditions(conditions);
            const errorMessagesCopy = errorMessages;
            errorMessagesCopy[event.index] = {};
            setErrorMessages({ ...errorMessagesCopy });
          },
          onError: (error, variables, context) => {
            rowsInEdit.current++;
            setIsEnabled(false);
            toast_topright.current.show([
                { life: 7000, severity: 'error', summary: 'Update error: ', detail: error.response.data.errorMessage, sticky: false }
            ]);

            let conditions = [...experimentalConditions];

            const errorMessagesCopy = errorMessages;  

            console.log(errorMessagesCopy);
            errorMessagesCopy[event.index] = {};
            Object.keys(error.response.data.errorMessages).forEach((field) => {
                let messageObject = {
                  severity: "error",
                  message: error.response.data.errorMessages[field]
                };
                errorMessagesCopy[event.index][field] = messageObject;
            });

            console.log(errorMessagesCopy);
            setErrorMessages({ ...errorMessagesCopy });
            
            setExperimentalConditions(conditions);
            let _editingRows = { ...editingRows, ...{ [`${conditions[event.index].id}`]: true } };
            setEditingRows(_editingRows);
          },
          onSettled: (data, error, variables, context) => {
      
        },
      });
    };

    const onRowEditChange = (event) => {
      setEditingRows(event.data);
    };

  const conditionQuantityEditor = (props) => {
    return (
      <>
        <InputTextEditor
          editorChange={onConditionQuantityEditorValueChange}
          props={props}
        />
        <ErrorMessageComponent errorMessages={errorMessages[props.rowIndex]} errorField={"conditionQuantity"} />
      </>
    );
  };
  
  const onConditionQuantityEditorValueChange = (props, event) => {
    let updatedConditions = [...props.value];
    if (event.target.value || event.target.value === '') {
      updatedConditions[props.rowIndex].conditionQuantity = event.target.value;
      setExperimentalConditions(updatedConditions);
    }
  }
  
  const filterComponentTemplate = (filterName, fields) => {
    return (<FilterComponent 
          isEnabled={isEnabled} 
          fields={fields} 
          filterName={filterName}
          currentFilters={filters}
          onFilter={onFilter}
      />);
  };                                 

  const conditionClassBodyTemplate = (rowData) => {
      if (rowData.conditionClass) {
          return <div>{rowData.conditionClass.name} ({rowData.conditionClass.curie})</div>;
      }
    };
  
  const conditionIdBodyTemplate = (rowData) => {
      if (rowData.conditionId) {
          return <div>{rowData.conditionId.name} ({rowData.conditionId.curie})</div>;
      }
    };
  
  const conditionGeneOntologyBodyTemplate = (rowData) => {
      if (rowData.conditionGeneOntology) {
          return <div>{rowData.conditionGeneOntology.name} ({rowData.conditionGeneOntology.curie})</div>;
      }
    };
  
  const conditionChemicalBodyTemplate = (rowData) => {
      if (rowData.conditionChemical) {
          return <div>{rowData.conditionChemical.name} ({rowData.conditionChemical.curie})</div>;
      }
    };
  
  const conditionAnatomyBodyTemplate = (rowData) => {
      if (rowData.conditionAnatomy) {
          return <div>{rowData.conditionAnatomy.name} ({rowData.conditionAnatomy.curie})</div>;
      }
    };
  
  const conditionTaxonBodyTemplate = (rowData) => {
      if (rowData.conditionTaxon) {
          return <div>{rowData.conditionTaxon.curie} ({rowData.conditionTaxon.name})</div>;
      }
    };

  const conditionGeneOntologyEditorTemplate = (props) => {
      return (
          <>
            <ConditionGeneOntologyEditor
                autocompleteFields={["curie", "name", "crossReferences.curie", "secondaryIdentifiers", "synonyms"]}
                rowProps={props}
                searchService={searchService}
                setExperimentalConditions={setExperimentalConditions}
            />
              <ErrorMessageComponent
                errorMessages={errorMessages[props.rowIndex]}
                errorField={"conditionGeneOntology"}
            />
          </>
      );
  };
  
  const columns = [
    {
      field: "uniqueId",
      header: "Unique ID",
      style: { whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word' },
      sortable: isEnabled,
      filter: true,
      filterElement: filterComponentTemplate("uniqueIdFilter", ["uniqueId"])
    },
    {
      field:"conditionStatement",
      header:"Statement",
      style: { whiteSpace: 'pr.e-wrap', overflowWrap: 'break-word' },
      sortable: isEnabled,  
      filter: true,
      filterElement: filterComponentTemplate("conditionStatementFilter", ["conditionStatement"])
    }, 
    {
      field:"conditionClass.name",
      header:"Class",
      sortable: isEnabled, 
      body: conditionClassBodyTemplate,
      filter : true, 
      filterElement: filterComponentTemplate("conditionClassFilter", ["conditionClass.curie", "conditionClass.name"])
    },
    {
      field:"conditionId.name",
      header:"ID",
      sortable: isEnabled,
      body: conditionIdBodyTemplate,
      filter: true, 
      filterElement: filterComponentTemplate("conditionIdFilter", ["conditionId.curie", "conditionId.name"])
    },
    {
      field:"conditionGeneOntology.name",
      header:"Gene Ontology",
      sortable: isEnabled,
      filter: true, 
      filterElement: filterComponentTemplate("conditionGeneOntologyFilter", ["conditionGeneOntology.curie", "conditionGeneOntology.name"]),
      editor: (props) => conditionGeneOntologyEditorTemplate(props),
      body: conditionGeneOntologyBodyTemplate
    },
    {
      field:"conditionChemical.name",
      header:"Chemical",
      sortable: isEnabled,
      body: conditionChemicalBodyTemplate,
      filter: true, 
      filterElement: filterComponentTemplate("conditionChemicalFilter", ["conditionChemical.curie", "conditionChemical.name"])
    },
    {
      field:"conditionAnatomy.name",
      header:"Anatomy",
      sortable: isEnabled,
      body: conditionAnatomyBodyTemplate,
      filter: true, 
      filterElement: filterComponentTemplate("conditionAnatomyFilter", ["conditionAnatomy.curie", "conditionAnatomy.name"])
    },
    {
      field:"conditionTaxon.curie",
      header:"Condition Taxon",
      sortable: isEnabled,
      body: conditionTaxonBodyTemplate,
      filter: true, 
      filterElement: filterComponentTemplate("conditionTaxonFilter", ["conditionTaxon.curie", "conditionTaxon.name"])
    },
    {
      field:"conditionQuantity",
      header:"Quantity",
      sortable: isEnabled,
      filter: true, 
      filterElement: filterComponentTemplate("conditionQuantityFilter", ["conditionQuantity"]),
      editor: (props) => conditionQuantityEditor(props)
    }


  ];
  
  const header = (
    <div style={{ textAlign: 'left' }}>
      <MultiSelect
        value={selectedColumnNames}
        options={columnNames}
        onChange={e => setSelectedColumnNames(e.value)}
        style={{ width: '20em' }}
        disabled={!isEnabled}
      />
    </div>
  );

  const filteredColumns = columns.filter((col) => {
    return selectedColumnNames.includes(col.header);
  });

  const columnMap = filteredColumns.map((col) => {
    return <Column
      columnKey={col.field}
      key={col.field}
      field={col.field}
      header={col.header}
      sortable={isEnabled}
      filter={col.filter}
      filterElement={col.filterElement}
      editor={col.editor}
      style={col.style}
      body={col.body}
    />;
  })                       

  return (
    <div>
      <div className="card">
        <Toast ref={toast_topleft} position="top-left" />
            <Toast ref={toast_topright} position="top-right" />
            <h3>Experimental Conditions Table</h3>
        <Messages ref={errorMessage} />
        <DataTable value={experimentalConditions} className="p-datatable-sm" header={header} reorderableColumns  
          editMode="row" onRowEditInit={onRowEditInit} onRowEditCancel={onRowEditCancel} onRowEditSave={(props) => onRowEditSave(props)}
          editingRows={editingRows} onRowEditChange={onRowEditChange}
          sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
          first={first}
          dataKey="id" resizableColumns columnResizeMode="fit" showGridlines
          paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy
          paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
        >
          {columnMap}
          <Column rowEditor headerStyle={{ width: '7rem' }} bodyStyle={{ textAlign: 'center' }}></Column>
        </DataTable>
      </div>
    </div>
  )
}
