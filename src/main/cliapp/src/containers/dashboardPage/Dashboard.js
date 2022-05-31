import React, { useState, useEffect } from 'react';

import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

import { SystemService } from '../../service/SystemService';

export const Dashboard = () => {

  const [tableData, setTableData] = useState({});

  const lookupMap = {
    Gene: { name: "Genes", link: "/#/genes", type: 'entity', }, 
    Allele: { name: "Alleles", link: "/#/alleles", type: 'entity', }, 
    AffectedGenomicModel: { name: "Affected Genomic Models", link: "/#/agms", type: 'entity', }, 
    DiseaseAnnotation: { name: "Disease Annotations", link: "/#/diseaseAnnotations", type: 'entity', }, 
    AGMDiseaseAnnotation: { name: "AGM Disease Annotations", link: "/#/diseaseAnnotations", type: 'entity', }, 
    AlleleDiseaseAnnotation: { name: "Allele Disease Annotations", link: "/#/diseaseAnnotations", type: 'entity', }, 
    GeneDiseaseAnnotation: { name: "Gene Disease Annotations", link: "/#/diseaseAnnotations", type: 'entity', }, 
    ExperimentalCondition: { name: "Experimental Conditions", link: "/#/experimentalConditions", type: 'entity', }, 
    ConditionRelation: { name: "Condition Relations", link: "/#/conditionRelations", type: 'entity', }, 
    Molecule: { name: "Molecules", link: "/#/molecules", type: 'entity', }, 
    Reference: { name: "Literature Relations", link: "/#/references", type: 'entity', }, 

    DOTerm: { name: "DO", link: "/#/ontology/do", type: 'ontology', }, 
    CHEBITerm: { name: "CHEBI", link: "/#/ontology/chebi", type: 'ontology', }, 
    XSMOTerm: { name: "XSMO", link: "/#/ontology/xsmo", type: 'ontology', }, 
    EcoTerm: { name: "ECO", link: "/#/ontology/eco", type: 'ontology', }, 
    SOTerm: { name: "SO", link: "/#/ontology/so", type: 'ontology', }, 
    GOTerm: { name: "GO", link: "/#/ontology/go", type: 'ontology', }, 
    MATerm: { name: "MA", link: "/#/ontology/ma", type: 'ontology', }, 
    ZfaTerm: { name: "ZFA", link: "/#/ontology/zfa", type: 'ontology', }, 
    MPTerm: { name: "MP", link: "/#/ontology/mp", type: 'ontology', }, 
    DAOTerm: { name: "DAO", link: "/#/ontology/dao", type: 'ontology', }, 
    EMAPATerm: { name: "EMAPA", link: "/#/ontology/emapa", type: 'ontology', }, 
    WBbtTerm: { name: "WBbt", link: "/#/ontology/wbbt", type: 'ontology', }, 
    XBATerm: { name: "XBA", link: "/#/ontology/xba", type: 'ontology', }, 
    XBSTerm: { name: "XBS", link: "/#/ontology/xbs", type: 'ontology', }, 
    XcoTerm: { name: "XCO", link: "/#/ontology/xco", type: 'ontology', }, 
    ZecoTerm: { name: "ZECO", link: "/#/ontology/zeco", type: 'ontology', }, 
    NCBITaxonTerm: { name: "NCBITaxon", link: "/#/ontology/ncbitaxon", type: 'ontology', }, 
    WBlsTaxonTerm: { name: "WBls", link: "/#/ontology/wbls", type: 'ontology', }, 
    FBdvTerm: { name: "FBdv", link: "/#/ontology/fbdv", type: 'ontology', }, 
    MmusDvTerm: { name: "MmusDv", link: "/#/ontology/mmusdv", type: 'ontology', }, 
    ZFSTerm: { name: "ZFS", link: "/#/ontology/zfs", type: 'ontology', }, 
    XPOTerm: { name: "XPO", link: "/#/ontology/xpo", type: 'ontology', }, 
    XBEDTerm: { name: "XBED", link: "/#/ontology/xbed", type: 'ontology', }, 

    CurationReport: { name: "Curation Reports", link: "/#/reports", type: 'system', }, 
    BulkLoad: { name: "Bulk Load", link: "/#/dataloads", type: 'system', }, 
  };

  useEffect(() => {
    const systemService = new SystemService();

    let _tableData = {};

    systemService.getSiteSummary().then((res) => {
      for (const key in res.entity){

        if(!lookupMap[key]) continue;

        const { type } = lookupMap[key];

        if(!_tableData[type]){
          _tableData[type] = [];
        }

        _tableData[type].push({
          name: lookupMap[key].name,
          link: lookupMap[key].link,
          count: res.entity[key]
        });
      }
      setTableData(_tableData);
    });
    // eslint-disable-next-line
  }, []);

  const nameHyperlinkTemplate = (rowData) => {
    return <a href={rowData.link}>{rowData.name}</a>
  }

  return (
    <>
      <div className="grid nested dashboard">
        <div className="col-3">
          <DataTable header="Entities" value={tableData.entity} sortField="name" sortOrder={1}>
            <Column field="name" header="Entity Name" body={nameHyperlinkTemplate}/>
            <Column field="count" header="Entity Count" />
          </DataTable>
        </div>
        <div className="col-3">
          <DataTable header="Ontologies" value={tableData.ontology} sortField="name" sortOrder={1}>
            <Column field="name" header="Ontology Name" body={nameHyperlinkTemplate} />
            <Column field="count" header="Term Count" />
          </DataTable>
        </div>
        <div className="col-3">
          <DataTable header="System" value={tableData.system} sortField="name" sortOrder={1}>
            <Column field="name" header="System Name" body={nameHyperlinkTemplate} />
            <Column field="count" header="Object Count" />
          </DataTable>
        </div>
      </div>
    </>
  );
};
