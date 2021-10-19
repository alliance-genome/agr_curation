import React, { useState, useEffect, useRef } from 'react';
import classNames from 'classnames';
import { Route, useHistory } from 'react-router-dom';
import { CSSTransition } from 'react-transition-group';

import { AppTopbar } from './AppTopbar';
import { AppFooter } from './AppFooter';
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
import '@fullcalendar/core/main.css';
import '@fullcalendar/daygrid/main.css';
import '@fullcalendar/timegrid/main.css';
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
        { label: 'Affected Genomic Models', icon: 'pi pi-fw pi-home', to: '/agms' },
        // { label: 'Table Edit Demo', icon: 'pi pi-fw pi-home', to: '/tableEditDemo' }
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
    // {
    //     label: 'Pages', icon: 'pi pi-fw pi-clone',
    //     items: [
    //         { label: 'DiseaseAnnotations', icon: 'pi pi-fw pi-home', to: '/diseaseAnnotations' }
    //     ]
    // }
/*
    ,{
      label: 'UI Kit', icon: 'pi pi-fw pi-sitemap',
      items: [
        { label: 'Form Layout', icon: 'pi pi-fw pi-id-card', to: '/formlayout' },
        { label: 'Input', icon: 'pi pi-fw pi-check-square', to: '/input' },
        { label: 'Float Label', icon: 'pi pi-fw pi-bookmark', to: '/floatlabel' },
        { label: "Invalid State", icon: "pi pi-fw pi-exclamation-circle", to: "/invalidstate" },
        { label: 'Button', icon: 'pi pi-fw pi-mobile', to: '/button' },
        { label: 'Table', icon: 'pi pi-fw pi-table', to: '/table' },
        { label: 'List', icon: 'pi pi-fw pi-list', to: '/list' },
        { label: 'Tree', icon: 'pi pi-fw pi-share-alt', to: '/tree' },
        { label: 'Panel', icon: 'pi pi-fw pi-tablet', to: '/panel' },
        { label: 'Overlay', icon: 'pi pi-fw pi-clone', to: '/overlay' },
        { label: 'Menu', icon: 'pi pi-fw pi-bars', to: '/menu' },
        { label: 'Message', icon: 'pi pi-fw pi-comment', to: '/messages' },
        { label: 'File', icon: 'pi pi-fw pi-file', to: '/file' },
        { label: 'Chart', icon: 'pi pi-fw pi-chart-bar', to: '/chart' },
        { label: 'Misc', icon: 'pi pi-fw pi-circle-off', to: '/misc' },
      ]
    },
    {
      label: 'Utilities', icon: 'pi pi-fw pi-globe',
      items: [
        { label: 'Display', icon: 'pi pi-fw pi-desktop', to: '/display' },
        { label: 'Elevation', icon: 'pi pi-fw pi-external-link', to: '/elevation' },
        { label: 'Flexbox', icon: 'pi pi-fw pi-directions', to: '/flexbox' },
        { label: 'Icons', icon: 'pi pi-fw pi-search', to: '/icons' },
        { label: 'Grid System', icon: 'pi pi-fw pi-th-large', to: '/grid' },
        { label: 'Spacing', icon: 'pi pi-fw pi-arrow-right', to: '/spacing' },
        { label: 'Typography', icon: 'pi pi-fw pi-align-center', to: '/typography' },
        { label: 'Text', icon: 'pi pi-fw pi-pencil', to: '/text' },
      ]
    },
    {
      label: 'Pages', icon: 'pi pi-fw pi-clone',
      items: [
        { label: 'Crud', icon: 'pi pi-fw pi-user-edit', to: '/crud' },
        { label: 'Calendar', icon: 'pi pi-fw pi-calendar-plus', to: '/calendar' },
        { label: 'Timeline', icon: 'pi pi-fw pi-calendar', to: '/timeline' },
        { label: 'Empty Page', icon: 'pi pi-fw pi-circle-off', to: '/empty' }
      ]
    },
    {
      label: 'Menu Hierarchy', icon: 'pi pi-fw pi-search',
      items: [
        {
          label: 'Submenu 1', icon: 'pi pi-fw pi-bookmark',
          items: [
            {
              label: 'Submenu 1.1', icon: 'pi pi-fw pi-bookmark',
              items: [
                { label: 'Submenu 1.1.1', icon: 'pi pi-fw pi-bookmark' },
                { label: 'Submenu 1.1.2', icon: 'pi pi-fw pi-bookmark' },
                { label: 'Submenu 1.1.3', icon: 'pi pi-fw pi-bookmark' },
              ]
            },
            {
              label: 'Submenu 1.2', icon: 'pi pi-fw pi-bookmark',
              items: [
                { label: 'Submenu 1.2.1', icon: 'pi pi-fw pi-bookmark' },
                { label: 'Submenu 1.2.2', icon: 'pi pi-fw pi-bookmark' }
              ]
            },
          ]
        },
        {
          label: 'Submenu 2', icon: 'pi pi-fw pi-bookmark',
          items: [
            {
              label: 'Submenu 2.1', icon: 'pi pi-fw pi-bookmark',
              items: [
                { label: 'Submenu 2.1.1', icon: 'pi pi-fw pi-bookmark' },
                { label: 'Submenu 2.1.2', icon: 'pi pi-fw pi-bookmark' },
                { label: 'Submenu 2.1.3', icon: 'pi pi-fw pi-bookmark' },
              ]
            },
            {
              label: 'Submenu 2.2', icon: 'pi pi-fw pi-bookmark',
              items: [
                { label: 'Submenu 2.2.1', icon: 'pi pi-fw pi-bookmark' },
                { label: 'Submenu 2.2.2', icon: 'pi pi-fw pi-bookmark' }
              ]
            }
          ]
        }
      ]
    },
    { label: 'Documentation', icon: 'pi pi-fw pi-question', command: () => { window.location = "#/documentation" } },
    { label: 'View Source', icon: 'pi pi-fw pi-search', command: () => { window.location = "https://github.com/primefaces/sigma-react" } }
  */
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

  //const logo = layoutColorMode === 'dark' ? 'assets/layout/images/logo-white.svg' : 'assets/layout/images/logo.svg';

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

      <AppFooter />

    </div>
  );

}

export default App;
