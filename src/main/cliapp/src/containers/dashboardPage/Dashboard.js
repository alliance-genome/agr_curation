import React, { useState, useEffect } from 'react';

import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

import { SearchService } from '../../service/SearchService';

export const Dashboard = () => {
  const [geneCount, setGeneCount] = useState(0);
  const [alleleCount, setAlleleCount] = useState(0);
  const [diseaseAnnotationCount, setDiseaseAnnotationCount] = useState(0);
  const [agmCount, setAgmCount] = useState(0);
  const [moleculeCount, setMoleculeCount] = useState(0);

  const [termCounts, setTermCounts] = useState([]);

  useEffect(() => {
    const searchService = new SearchService();

    searchService.search('gene', 0, 0).then(searchResults => {
      setGeneCount(searchResults.totalResults);
    });

    searchService.search('allele', 0, 0).then(searchResults => {
      setAlleleCount(searchResults.totalResults);
    });

    searchService.search('disease-annotation', 0, 0).then(searchResults => {
      setDiseaseAnnotationCount(searchResults.totalResults);
    });

    searchService.search('agm', 0, 0).then(searchResults => {
      setAgmCount(searchResults.totalResults);
    });

    searchService.search("molecule", 0, 0).then(searchResults => {
      setMoleculeCount(searchResults.totalResults);
    });



    searchService.search('chebiterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "CHEBI", count: results.totalResults }]);
    });

    searchService.search('ecoterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "ECO", count: results.totalResults }]);
    });

    searchService.search('doterm', 0, 0).then(results => {
      setTermCounts((list) => [...list, { name: "DO", count: results.totalResults }]);
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
  }, []);

  return (

    <div className="p-grid p-nested dashboard">

      <div className="p-col-9">
        <div className="p-grid">
          <div className="p-col-12 p-lg-4">
            <div className="card summary">
              <span className="title">Genes</span>
              <span className="detail">Total number of genes</span>
              <span className="count visitors">{geneCount}</span>
            </div>
          </div>
          <div className="p-col-12 p-lg-4">
            <div className="card summary">
              <span className="title">Alleles</span>
              <span className="detail">Total number of alleles</span>
              <span className="count purchases">{alleleCount}</span>
            </div>
          </div>
          <div className="p-col-12 p-lg-4">
            <div className="card summary">
              <span className="title">Disease Annotations</span>
              <span className="detail">Total number of disease annotations</span>
              <span className="count revenue">{diseaseAnnotationCount}</span>
            </div>
          </div>
          <div className="p-col-12 p-lg-4">
            <div className="card summary">
              <span className="title">Affected Genomic Models</span>
              <span className="detail">Total number of Affected Genomic Models</span>
              <span className="count agm">{agmCount}</span>
            </div>
          </div>
          <div className="p-col-12 p-lg-4">
            <div className="card summary">
              <span className="title">Molecules</span>
              <span className="detail">Total number of Molecules</span>
              <span className="count agm">{moleculeCount}</span>
            </div>
          </div>
        </div>
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
