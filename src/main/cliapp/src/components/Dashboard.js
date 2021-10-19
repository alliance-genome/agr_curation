import React, { useState, useEffect } from 'react';
import { ProductService } from '../service/ProductService';
import { EventService } from '../service/EventService';
import { GeneService } from '../service/GeneService';
import { AlleleService } from '../service/AlleleService';
import { DiseaseAnnotationService } from '../service/DiseaseAnnotationService'
import { AffectedGenomicModelService } from '../service/AffectedGenomicModelService'

export const Dashboard = () => {

    const [tasksCheckbox, setTasksCheckbox] = useState([]);
    const [events, setEvents] = useState(null);
    const [products, setProducts] = useState(null);

    const [geneCount, setGeneCount] = useState(0);
    const [alleleCount, setAlleleCount] = useState(0);
    const [diseaseAnnotationCount, setDiseaseAnnotationCount] = useState(0);
    const [agmCount, setAgmCount] = useState(0);

    useEffect(() => {
        const productService = new ProductService();
        const eventService = new EventService();
        productService.getProductsSmall().then(data => setProducts(data));
        eventService.getEvents().then(data => setEvents(data));

        const geneService = new GeneService();
        geneService.getGenes(0, 0).then(searchReults => {
            setGeneCount(searchReults.totalResults);
        });

        const alleleService = new AlleleService();
        alleleService.getAlleles(0, 0).then(searchReults => {
            setAlleleCount(searchReults.totalResults);
        });
        const diseaseAnnotationService = new DiseaseAnnotationService();
        diseaseAnnotationService.getDiseaseAnnotations(0, 0).then(searchResults => {
            setDiseaseAnnotationCount(searchResults.totalResults);
        });

        const agmService = new AffectedGenomicModelService();
        agmService.getAgms(0, 0).then(searchResults => {
            setAgmCount(searchResults.totalResults);
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

            <div className="p-col-12 p-md-6 p-xl-3">
                <div className="highlight-box">
                    <div className="initials" style={{ backgroundColor: '#007be5', color: '#00448f' }}><span>TV</span></div>
                    <div className="highlight-details ">
                        <i className="pi pi-search"></i>
                        <span>Total Queries</span>
                        <span className="count">523</span>
                    </div>
                </div>
            </div>
            <div className="p-col-12 p-md-6 p-xl-3">
                <div className="highlight-box">
                    <div className="initials" style={{ backgroundColor: '#ef6262', color: '#a83d3b' }}><span>TI</span></div>
                    <div className="highlight-details ">
                        <i className="pi pi-question-circle"></i>
                        <span>Total Issues</span>
                        <span className="count">81</span>
                    </div>
                </div>
            </div>
            <div className="p-col-12 p-md-6 p-xl-3">
                <div className="highlight-box">
                    <div className="initials" style={{ backgroundColor: '#20d077', color: '#038d4a' }}><span>OI</span></div>
                    <div className="highlight-details ">
                        <i className="pi pi-filter"></i>
                        <span>Open Issues</span>
                        <span className="count">21</span>
                    </div>
                </div>
            </div>
            <div className="p-col-12 p-md-6 p-xl-3">
                <div className="highlight-box">
                    <div className="initials" style={{ backgroundColor: '#f9c851', color: '#b58c2b' }}><span>CI</span></div>
                    <div className="highlight-details ">
                        <i className="pi pi-check"></i>
                        <span>Closed Issues</span>
                        <span className="count">60</span>
                    </div>
                </div>
            </div>
        </div>
    );
}
