import React, { useState, useEffect } from 'react';

import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

import { SearchService } from '../../service/SearchService';

export const Dashboard = () => {

  const [entityCounts, setEntityCounts] = useState([]);
  const [termCounts, setTermCounts] = useState([]);

  useEffect(() => {
    const searchService = new SearchService();

    // Entity Counts
    searchService.search('gene', 0, 0).then(results => {
      setEntityCounts((list) => [...list, { name: "Genes", count: results.totalResults, link: '/#/genes' }]);
    });

    searchService.search('allele', 0, 0).then(results => {
      setEntityCounts((list) => [...list, { name: "Alleles", count: results.totalResults, link: '/#/alleles' }]);
    });

    searchService.search('agm', 0, 0).then(results => {
      setEntityCounts((list) => [...list, { name: "Affected Genomic Models", count: results.totalResults, link: '/#/agms' }]);
    });

    searchService.search('disease-annotation', 0, 0).then(results => {
      setEntityCounts((list) => [...list, { name: "Disease Annotations", count: results.totalResults, link: '/#/diseaseAnnotations' }]);
    });

    searchService.search('experimental-condition', 0, 0).then(results => {
      setEntityCounts((list) => [...list, { name: "Experimental Conditions", count: results.totalResults, link: '/#/experimentalConditions' }]);
    });

    searchService.search("molecule", 0, 0).then(results => {
      setEntityCounts((list) => [...list, { name: "Molecules", count: results.totalResults, link: '/#/molecules' }]);
    });

    searchService.search("literature-reference", 0, 0).then(results => {
      setEntityCounts((list) => [...list, { name: "Literature References", count: results.totalResults, link: '/#/references' }]);
    });

    // Term Counts
    searchService.search('chebiterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "CHEBI", count: results.totalResults, link: '/#/ontology/chebi' }]);
    });

    searchService.search('xsmoterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "XSMO", count: results.totalResults, link: '/#/ontology/xsmo' }]);
    });

    searchService.search('ecoterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "ECO", count: results.totalResults, link: '/#/ontology/eco' }]);
    });

    searchService.search('doterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "DO", count: results.totalResults, link: '/#/ontology/do' }]);
    });

    searchService.search('soterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "SO", count: results.totalResults, link: '/#/ontology/so' }]);
    });

    searchService.search('goterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "GO", count: results.totalResults, link: '/#/ontology/go' }]);
    });

    searchService.search('materm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "MA", count: results.totalResults, link: '/#/ontology/ma' }]);
    });

    searchService.search('zfaterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "ZFA", count: results.totalResults, link: '/#/ontology/zfa' }]);
    });

    searchService.search('mpterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "MP", count: results.totalResults, link: '/#/ontology/mp' }]);
    });

    searchService.search('daoterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "DAO", count: results.totalResults, link: '/#/ontology/dao' }]);
    });

    searchService.search('emapaterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "EMAPA", count: results.totalResults, link: '/#/ontology/emapa' }]);
    });

    searchService.search('wbbtterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "WBbt", count: results.totalResults, link: '/#/ontology/wbbt' }]);
    });

    searchService.search('xaoterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "XAO", count: results.totalResults, link: '/#/ontology/xao' }]);
    });
    
    
    searchService.search('xaodsterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "XAODs", count: results.totalResults, link: '/#/ontology/xaods' }]);
    });
    
    searchService.search('xcoterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "XCO", count: results.totalResults, link: '/#/ontology/xco' }]);
    });

    searchService.search('zecoterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "ZECO", count: results.totalResults, link: '/#/ontology/zeco' }]);
    });

    searchService.search('ncbitaxonterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "NCBITaxon", count: results.totalResults, link: '/#/ontology/ncbitaxon' }]);
    });

    searchService.search('wblsterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "WBls", count: results.totalResults, link: '/#/ontology/wbls' }]);
    });

    searchService.search('fbdvterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "FBdv", count: results.totalResults, link: '/#/ontology/fbdv' }]);
    });

    searchService.search('mmusdvterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "MmusDv", count: results.totalResults, link: '/#/ontology/mmusdv' }]);
    });

    searchService.search('zfsterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "ZFS", count: results.totalResults, link: '/#/ontology/zfs' }]);
    });

    searchService.search('xpoterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "XPO", count: results.totalResults, link: '/#/ontology/xpo' }]);
    });

    searchService.search('xbedterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "XBED", count: results.totalResults, link: '/#/ontology/xbed' }]);
    });
  }, []);

  const nameHyperlinkTemplate = (rowData) => {
    return <a href={rowData.link}>{rowData.name}</a>
  }

  return (

    <div className="grid nested dashboard">

      <div className="col-3">
        <DataTable value={entityCounts} sortField="name" sortOrder={1}>
          <Column field="name" header="Entity Name" body={nameHyperlinkTemplate}/>
          <Column field="count" header="Entity Count" />
        </DataTable>
      </div>

      <div className="col-3">
        <DataTable value={termCounts} sortField="name" sortOrder={1}>
          <Column field="name" header="Ontology Name" body={nameHyperlinkTemplate} />
          <Column field="count" header="Term Count" />
        </DataTable>
      </div>
    </div>

  );
};
