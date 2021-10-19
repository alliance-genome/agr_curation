import React, { useState, useEffect, useRef } from 'react';
import classNames from 'classnames';
import { Route, useHistory } from 'react-router-dom';
import { CSSTransition } from 'react-transition-group';

import { AppTopbar } from './AppTopbar';
import { AppMenu } from './AppMenu';
import { AppConfig } from './AppConfig';

import { Dashboard } from './components/Dashboard';

import { GenesComponent } from './components/GenesComponent';
import { AllelesComponent } from './components/AllelesComponent';
import { DiseaseAnnotationsComponent } from './components/DiseaseAnnotationsComponent';
import { FMSComponent } from './components/FMSComponent';
import { AffectedGenomicModelComponent } from './components/AffectedGenomicModelComponent';
import { DiseaseOntologyComponent } from './components/DiseaseOntologyComponent';
import { ECOOntologyComponent } from './components/ECOOntologyComponent';
import { MPOntologyComponent } from './components/MPOntologyComponent';
import { ApiVersionService } from './service/ApiVersionService';


import PrimeReact from 'primereact/api';

import 'primereact/resources/themes/saga-blue/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';
import 'react-transition-group'
import 'prismjs/themes/prism-coy.css';
import './layout/flags/flags.css';
import './layout/layout.scss';
import './App.scss';

const App = () => {

    const [layoutMode, setLayoutMode] = useState('static');
    const [layoutColorMode, setLayoutColorMode] = useState('dark')
    const [inputStyle, setInputStyle] = useState('outlined');
    const [ripple, setRipple] = useState(false);
    const [sidebarActive, setSidebarActive] = useState(true);
    const sidebar = useRef();
    const [apiVersion, setApiVersion] = useState({"version": "0.0.0"});

    const history = useHistory();

    let menuClick = false;

    useEffect(() => {
        if (sidebarActive) {
            addClass(document.body, "body-overflow-hidden");
        } else {
            removeClass(document.body, "body-overflow-hidden");
        }

        const service = new ApiVersionService();

        service.getApiVersion().then(json => {
            setApiVersion(json);
        });

    }, [sidebarActive]);

    const onInputStyleChange = (inputStyle) => {
        setInputStyle(inputStyle);
    }

    const onRipple = (e) => {
        PrimeReact.ripple = e.value;
        setRipple(e.value)
    }

    const onLayoutModeChange = (mode) => {
        setLayoutMode(mode)
    }

    const onColorModeChange = (mode) => {
        setLayoutColorMode(mode)
    }

    const onWrapperClick = (event) => {
        if (!menuClick && layoutMode === "overlay") {
            setSidebarActive(false);
        }
        menuClick = false;
    }

    const onToggleMenu = (event) => {
        menuClick = true;

        setSidebarActive((prevState) => !prevState);

        event.preventDefault();
    }

    const onSidebarClick = () => {
        menuClick = true;
    }

    const onMenuItemClick = (event) => {
        if (!event.item.items && layoutMode === "overlay") {
            setSidebarActive(false);
        }
    }

    const menu = [
        { label: 'Dashboard', icon: 'pi pi-fw pi-home', to: '/' },
        {
            label: 'Search Forms', icon: 'pi pi-fw pi-sitemap',
            items: [
                { label: 'Genes', icon: 'pi pi-fw pi-home', to: '/genes' },
                { label: 'Alleles', icon: 'pi pi-fw pi-home', to: '/alleles' },
                { label: 'Disease Annotations', icon: 'pi pi-fw pi-home', to: '/diseaseAnnotations' },
                { label: 'Affected Genomic Models', icon: 'pi pi-fw pi-home', to: '/agms' }
            ]
        },
        {
            label: 'Ontologies', icon: 'pi pi-fw pi-sitemap',
            items: [
                { label: 'Disease Ontology (DO)', icon: 'pi pi-fw pi-home', to: '/ontology/do' },
                { label: 'Evidence & Conclusion Ontology (ECO)', icon: 'pi pi-fw pi-home', to: '/ontology/eco' },
                { label: 'The Mammalian Phenotype Ontology (MP)', icon: 'pi pi-fw pi-home', to: '/ontology/mp' }
            ]
        },
        {
            label: 'Other Links', icon: 'pi pi-fw pi-sitemap',
            items: [
                { label: 'FMS Data Files', icon: 'pi pi-fw pi-home', to: '/fmspage' },
                { label: 'Swagger UI', icon: 'pi pi-fw pi-home', url: '/swagger-ui' },
            ]
        }
    ];

    const addClass = (element, className) => {
        if (element.classList)
            element.classList.add(className);
        else
            element.className += ' ' + className;
    }

    const removeClass = (element, className) => {
        if (element.classList)
            element.classList.remove(className);
        else
            element.className = element.className.replace(new RegExp('(^|\\b)' + className.split(' ').join('|') + '(\\b|$)', 'gi'), ' ');
    }

    const isSidebarVisible = () => {
        return sidebarActive;
    };

    const wrapperClass = classNames('layout-wrapper', {
        'layout-overlay': layoutMode === 'overlay',
        'layout-static': layoutMode === 'static',
        'layout-active': sidebarActive,
        'p-input-filled': inputStyle === 'filled',
        'p-ripple-disabled': ripple === false
    });

    const sidebarClassName = classNames('layout-sidebar', {
        'layout-sidebar-dark': layoutColorMode === 'dark',
        'layout-sidebar-light': layoutColorMode === 'light'
    });

    return (
        <div className={wrapperClass} onClick={onWrapperClick}>
            <AppTopbar onToggleMenu={onToggleMenu} />

            <CSSTransition classNames="layout-sidebar" timeout={{ enter: 200, exit: 200 }} in={isSidebarVisible()} unmountOnExit>
                <div ref={sidebar} className={sidebarClassName} onClick={onSidebarClick}>
                    <div style={{cursor: 'pointer'}} onClick={() => history.push('/')}>
                        <div class="card">AGR Curation: {apiVersion.version}<br /></div>
                    </div>
                    <AppMenu model={menu} onMenuItemClick={onMenuItemClick} />
                </div>
            </CSSTransition>

            <AppConfig rippleEffect={ripple} onRippleEffect={onRipple} inputStyle={inputStyle} onInputStyleChange={onInputStyleChange}
                       layoutMode={layoutMode} onLayoutModeChange={onLayoutModeChange} layoutColorMode={layoutColorMode} onColorModeChange={onColorModeChange} />

            <div className="layout-main">
                <Route path="/" exact component={Dashboard} />
                <Route path="/diseaseAnnotations" component={DiseaseAnnotationsComponent} />
                <Route path="/genes" component={GenesComponent} />
                <Route path="/alleles" component={AllelesComponent} />
                <Route path="/ontology/do" component={DiseaseOntologyComponent} />
                <Route path="/ontology/eco" component={ECOOntologyComponent} />
                <Route path="/ontology/mp" component={MPOntologyComponent} />
                <Route path="/fmspage" component={FMSComponent} />
                <Route path="/agms" component={AffectedGenomicModelComponent} />

            </div>

        </div>
    );

}

export default App;
