import React, { useState, useEffect } from 'react';

import { SearchService } from '../service/SearchService'

export const Dashboard = () => {
    const [geneCount, setGeneCount] = useState(0);
    const [alleleCount, setAlleleCount] = useState(0);
    const [diseaseAnnotationCount, setDiseaseAnnotationCount] = useState(0);
    const [agmCount, setAgmCount] = useState(0);
    const [moleculeCount, setMoleculeCount] = useState(0);

    const [ECOCount, setECOCount] = useState(0);
    const [DOCount, setDOCount] = useState(0);
    const [MACount, setMACount] = useState(0);

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

        searchService.search('ecoterm', 0, 0).then(results => {
          setECOCount(results.totalResults);
        });

        searchService.search('doterm', 0, 0).then(results => {
          setDOCount(results.totalResults);
        });

        searchService.search('materm', 0, 0).then(results => {
          setMACount(results.totalResults);
        });
    }, []);

    return (
        <div className="p-grid p-fluid dashboard">
            <div className="p-col-12 p-lg-3">
                <div className="card summary">
                    <span className="title">Genes</span>
                    <span className="detail">Total number of genes</span>
                    <span className="count visitors">{ geneCount }</span>
                </div>
            </div>
            <div className="p-col-12 p-lg-3">
                <div className="card summary">
                    <span className="title">Alleles</span>
                    <span className="detail">Total number of alleles</span>
                    <span className="count purchases">{ alleleCount }</span>
                </div>
            </div>
            <div className="p-col-12 p-lg-3">
                <div className="card summary">
                    <span className="title">Disease Annotations</span>
                    <span className="detail">Total number of disease annotations</span>
                    <span className="count revenue">{ diseaseAnnotationCount }</span>
                </div>
            </div>
            <div className="p-col-12 p-lg-3">
                <div className="card summary">
                    <span className="title">Affected Genomic Models</span>
                    <span className="detail">Total number of Affected Genomic Models</span>
                    <span className="count agm">{ agmCount }</span>
                </div>
            </div>

            <div className="p-col-12 p-lg-3">
                <div className="card summary">
                    <span className="title">Molecules</span>
                    <span className="detail">Total number of Molecules</span>
                    <span className="count agm">{ moleculeCount }</span>
                </div>
            </div>

            <div className="p-col-12 p-md-6 p-xl-3">
                <div className="highlight-box">
                    <div className="initials" style={{ backgroundColor: '#007be5', color: '#00448f' }}><span>ECO</span></div>
                    <div className="highlight-details ">
                        <i className="pi pi-search"></i>
                        <span>Total Term Count</span>
                        <span className="count">{ECOCount}</span>
                    </div>
                </div>
            </div>
            <div className="p-col-12 p-md-6 p-xl-3">
                <div className="highlight-box">
                    <div className="initials" style={{ backgroundColor: '#ef6262', color: '#a83d3b' }}><span>DO</span></div>
                    <div className="highlight-details ">
                        <i className="pi pi-question-circle"></i>
                        <span>Total Term Count</span>
                        <span className="count">{DOCount}</span>
                    </div>
                </div>
            </div>
            <div className="p-col-12 p-md-6 p-xl-3">
                <div className="highlight-box">
                    <div className="initials" style={{ backgroundColor: '#ef6262', color: '#a83d3b' }}><span>MA</span></div>
                    <div className="highlight-details ">
                        <i className="pi pi-question-circle"></i>
                        <span>Total Term Count</span>
                        <span className="count">{MACount}</span>
                    </div>
                </div>
            </div>
        </div>
    );
}
