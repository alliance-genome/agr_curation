import React, { useState, useEffect, useRef } from 'react';

import PrimeReact from 'primereact/api';

import classNames from 'classnames';
import { useHistory } from 'react-router-dom';
import { CSSTransition } from 'react-transition-group';
import { useOktaAuth } from '@okta/okta-react';

import { AppTopbar } from '../../components/AppTopbar';
import { AppMenu } from '../../components/AppMenu';
import { AppProfile } from '../../AppProfile';
import { AppConfig } from '../../components/AppConfig';

import { ApiVersionService } from '../../service/ApiVersionService';

export const SiteLayout = (props) => {

  const [layoutMode, setLayoutMode] = useState('static');
  const [layoutColorMode, setLayoutColorMode] = useState('dark');
  const [inputStyle, setInputStyle] = useState('outlined');
  const [ripple, setRipple] = useState(false);
  const [sidebarActive, setSidebarActive] = useState(true);
  const sidebar = useRef();
  const [apiVersion, setApiVersion] = useState({ "version": "0.0.0" });
  const { authState, oktaAuth } = useOktaAuth();
  const [userInfo, setUserInfo] = useState(null);


  useEffect(() => {
    if (!authState || !authState.isAuthenticated) {
      setUserInfo(null);
    } else {
      oktaAuth.getUser().then((info) => {
        setUserInfo(info);
      }).catch((err) => {
        console.error(err);
      });
    }
  }, [authState, oktaAuth]);

  const logout = async () => await oktaAuth.signOut();
  const appProfile = userInfo ? <AppProfile authState={authState} logout={logout} email={userInfo.email} /> :
    null;

  useEffect(() => {
    if (!authState || !authState.isAuthenticated) {
      setSidebarActive(false);
    } else {
      setSidebarActive(true);
    }
  }, [authState])

  const menu = [
    { label: 'Dashboard', icon: 'pi pi-fw pi-home', to: '/' },
    {
      label: 'Data Tables', icon: 'pi pi-fw pi-sitemap',
      items: [
        { label: 'Genes', icon: 'pi pi-fw pi-home', to: '/genes' },
        { label: 'Alleles', icon: 'pi pi-fw pi-home', to: '/alleles' },
        { label: 'Disease Annotations', icon: 'pi pi-fw pi-home', to: '/diseaseAnnotations' },
        { label: 'Affected Genomic Models', icon: 'pi pi-fw pi-home', to: '/agms' },
        { label: 'Molecules', icon: 'pi pi-fw pi-home', to: '/molecules' }
      ]
    },
    {
      label: 'Ontologies', icon: 'pi pi-fw pi-sitemap',
      items: [
        { label: 'Chemical Entities of Biological Interest (ChEBI)', icon: 'pi pi-fw pi-home', to: '/ontology/chebi' },
        { label: 'Disease Ontology (DO)', icon: 'pi pi-fw pi-home', to: '/ontology/do' },
        { label: 'Evidence & Conclusion Ontology (ECO)', icon: 'pi pi-fw pi-home', to: '/ontology/eco' },
        { label: 'Gene Ontology (GO)', icon: 'pi pi-fw pi-home', to: '/ontology/go' },
        { label: 'Sequence Ontology (SO)', icon: 'pi pi-fw pi-home', to: '/ontology/so' },
        { label: 'Mouse adult gross anatomy Ontology (MA)', icon: 'pi pi-fw pi-home', to: '/ontology/ma' },
        { label: 'The Mammalian Phenotype Ontology (MP)', icon: 'pi pi-fw pi-home', to: '/ontology/mp' },
        { label: 'Drosophila Anatomy Ontology (DAO)', icon: 'pi pi-fw pi-home', to: '/ontology/dao' },
        { label: 'Zebrafish Anatomy Ontology (ZFA)', icon: 'pi pi-fw pi-home', to: '/ontology/zfa' },
        { label: 'Mouse Developmental Anatomy Ontology (EMAPA)', icon: 'pi pi-fw pi-home', to: '/ontology/emapa' },
        { label: 'C. elegans Gross Anatomy Ontology (WBbt)', icon: 'pi pi-fw pi-home', to: '/ontology/wbbt' },
        { label: 'Experimental condition ontology (XCO)', icon: 'pi pi-fw pi-home', to: '/ontology/xco' },
        { label: 'Zebrafish Experimental Conditions Ontology (ZECO)', icon: 'pi pi-fw pi-home', to: '/ontology/zeco' }
      ]
    },
    {
      label: 'Controlled Vocabularies', icon: 'pi pi-fw pi-sitemap',
      items: [
        { label: 'Terms', icon: 'pi pi-fw pi-home', to: '/vocabterms' }
      ]
    },
    {
      label: 'Other Links', icon: 'pi pi-fw pi-sitemap',
      items: [
        { label: 'FMS Data Files', icon: 'pi pi-fw pi-home', to: '/fmspage' },
        { label: 'Data Loads', icon: 'pi pi-fw pi-home', to: '/dataloads' },
        { label: 'Swagger UI', icon: 'pi pi-fw pi-home', url: '/swagger-ui' },
      ]
    }
  ]

  const onMenuItemClick = (event) => {
    if (!event.item.items && layoutMode === "overlay") {
      setSidebarActive(false);
    }
  };

  const appMenu = authState && authState.isAuthenticated ?
    <AppMenu model={menu} onMenuItemClick={onMenuItemClick} /> :
    null;


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
  };

  const onRipple = (e) => {
    PrimeReact.ripple = e.value;
    setRipple(e.value);
  };

  const onLayoutModeChange = (mode) => {
    setLayoutMode(mode);
  };

  const onColorModeChange = (mode) => {
    setLayoutColorMode(mode);
  };

  const onWrapperClick = (event) => {
    if (!menuClick && layoutMode === "overlay") {
      setSidebarActive(false);
    }
    menuClick = false;
  };

  const onToggleMenu = (event) => {
    menuClick = true;

    setSidebarActive((prevState) => !prevState);

    event.preventDefault();
  };

  const onSidebarClick = () => {
    menuClick = true;
  };

  const addClass = (element, className) => {
    if (element.classList)
      element.classList.add(className);
    else
      element.className += ' ' + className;
  };

  const removeClass = (element, className) => {
    if (element.classList)
      element.classList.remove(className);
    else
      element.className = element.className.replace(new RegExp('(^|\\b)' + className.split(' ').join('|') + '(\\b|$)', 'gi'), ' ');
  };

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

  const { children } = props;

  return (
    <div className={wrapperClass} onClick={onWrapperClick}>

      <AppTopbar logout={logout} onToggleMenu={onToggleMenu} authState={authState} />

      <CSSTransition classNames="layout-sidebar" timeout={{ enter: 200, exit: 200 }} in={isSidebarVisible()} unmountOnExit>
        <div ref={sidebar} className={sidebarClassName} onClick={onSidebarClick}>
          <div style={{ cursor: 'pointer' }} onClick={() => history.push('/')}>
            <div class="card">AGR Curation: {apiVersion.version}<br /></div>
          </div>
          {appProfile}
          {appMenu}
        </div>
      </CSSTransition>

      <AppConfig rippleEffect={ripple} onRippleEffect={onRipple} inputStyle={inputStyle} onInputStyleChange={onInputStyleChange}
        layoutMode={layoutMode} onLayoutModeChange={onLayoutModeChange} layoutColorMode={layoutColorMode} onColorModeChange={onColorModeChange} />

      <div className="layout-main">
        {children}
      </div>

    </div>

  );
};
