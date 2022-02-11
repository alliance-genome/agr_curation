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
      setEntityCounts((list) => [...list, { name: "Genes", count: results.totalResults }]);
    });

    searchService.search('allele', 0, 0).then(results => {
      setEntityCounts((list) => [...list, { name: "Alleles", count: results.totalResults }]);
    });

    searchService.search('agm', 0, 0).then(results => {
      setEntityCounts((list) => [...list, { name: "Affected Genomic Models", count: results.totalResults }]);
    });

    searchService.search('disease-annotation', 0, 0).then(results => {
      setEntityCounts((list) => [...list, { name: "Disease Annotations", count: results.totalResults }]);
    });

    searchService.search('experimental-condition', 0, 0).then(results => {
      setEntityCounts((list) => [...list, { name: "Experimental Conditions", count: results.totalResults }]);
    });

    searchService.search("molecule", 0, 0).then(results => {
      setEntityCounts((list) => [...list, { name: "Molecules", count: results.totalResults }]);
    });

    // Term Counts
    searchService.search('chebiterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "CHEBI", count: results.totalResults }]);
    });

    searchService.search('ecoterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "ECO", count: results.totalResults }]);
    });

    searchService.search('doterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "DO", count: results.totalResults }]);
    });

    searchService.search('soterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "SO", count: results.totalResults }]);
    });

    searchService.search('goterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "GO", count: results.totalResults }]);
    });

    searchService.search('materm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "MA", count: results.totalResults }]);
    });

    searchService.search('zfaterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "ZFA", count: results.totalResults }]);
    });

    searchService.search('mpterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "MP", count: results.totalResults }]);
    });

    searchService.search('daoterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "DAO", count: results.totalResults }]);
    });

    searchService.search('emapaterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "EMAPA", count: results.totalResults }]);
    });

    searchService.search('wbbtterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "WBbt", count: results.totalResults }]);
    });

    searchService.search('xcoterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "XCO", count: results.totalResults }]);
    });

    searchService.search('zecoterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "ZECO", count: results.totalResults }]);
    });

    searchService.search('ncbitaxonterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "NCBITaxon", count: results.totalResults }]);
    });
  }, []);

  return (

    <div className="p-grid p-nested dashboard">

      <div className="p-col-3">
        <DataTable value={entityCounts}>
          <Column field="name" header="Entity Name" />
          <Column field="count" header="Entity Count" />
        </DataTable>
      </div>

      <div className="p-col-3">
        <DataTable value={termCounts}>
          <Column field="name" header="Ontology Name" />
          <Column field="count" header="Term Count" />
        </DataTable>
      </div>
    </div>

  );
};
