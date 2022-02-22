import React, { useState, useEffect, useRef } from 'react';
import { useQuery } from 'react-query';
import classNames from 'classnames';
import { useLocation, useHistory } from 'react-router-dom';
import { CSSTransition } from 'react-transition-group';

import { useOktaAuth } from '@okta/okta-react';

import { AppTopbar } from '../../AppTopbar';
import { AppFooter } from '../../AppFooter';
import { AppMenu } from '../../AppMenu';
import { AppConfig } from '../../AppConfig';

import { ApiVersionService } from '../../service/ApiVersionService';

import PrimeReact from 'primereact/api';
import { Tooltip } from 'primereact/tooltip';

import 'primereact/resources/primereact.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';
import 'prismjs/themes/prism-coy.css';
import '../../assets/demo/flags/flags.css';
import '../../assets/demo/Demos.scss';
import '../../assets/layout/layout.scss';
import '../../App.scss';

export const SiteLayout = (props) => {
  const [layoutMode, setLayoutMode] = useState('static');
  const [layoutColorMode, setLayoutColorMode] = useState('light')
  const [inputStyle, setInputStyle] = useState('outlined');
  const [ripple, setRipple] = useState(true);
  const [staticMenuInactive, setStaticMenuInactive] = useState(false);
  const [overlayMenuActive, setOverlayMenuActive] = useState(false);
  const [mobileMenuActive, setMobileMenuActive] = useState(false);
  const [mobileTopbarMenuActive, setMobileTopbarMenuActive] = useState(false);
  const [userInfo, setUserInfo] = useState(null);
  const copyTooltipRef = useRef();
  const location = useLocation();

  const [apiVersion, setApiVersion] = useState({ "version": "0.0.0" });
  const { authState, oktaAuth } = useOktaAuth();

  const history = useHistory();
  const { children } = props;
  const apiService = new ApiVersionService();

  useQuery(['getApiVersion', apiVersion],
    () => apiService.getApiVersion(), {
    onSuccess: (data) => {
      //console.log(data);
      setApiVersion(data);
    },
    onError: (error) => {
      console.log(error);
    },
    keepPreviousData: true,
    refetchOnWindowFocus: false
  }
  );

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

  useEffect(() => {
    if (!authState || !authState.isAuthenticated) {
      setStaticMenuInactive(true);
    } else {
      setStaticMenuInactive(false);
    }
  }, [authState])

  PrimeReact.ripple = true;

  let menuClick = false;
  let mobileTopbarMenuClick = false;

  useEffect(() => {
    if (mobileMenuActive) {
      addClass(document.body, "body-overflow-hidden");
    } else {
      removeClass(document.body, "body-overflow-hidden");
    }
  }, [mobileMenuActive]);

  useEffect(() => {
    copyTooltipRef && copyTooltipRef.current && copyTooltipRef.current.updateTargetEvents();
  }, [location]);

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
    if (!menuClick) {
      setOverlayMenuActive(false);
      setMobileMenuActive(false);
    }

    if (!mobileTopbarMenuClick) {
      setMobileTopbarMenuActive(false);
    }

    mobileTopbarMenuClick = false;
    menuClick = false;
  }

  const onToggleMenuClick = (event) => {
    menuClick = true;

    if (isDesktop()) {
      if (layoutMode === 'overlay') {
        if (mobileMenuActive === true) {
          setOverlayMenuActive(true);
        }

        setOverlayMenuActive((prevState) => !prevState);
        setMobileMenuActive(false);
      }
      else if (layoutMode === 'static') {
        setStaticMenuInactive((prevState) => !prevState);
      }
    }
    else {
      setMobileMenuActive((prevState) => !prevState);
    }

    event.preventDefault();
  }

  const onSidebarClick = () => {
    menuClick = true;
  }

  const onMobileTopbarMenuClick = (event) => {
    mobileTopbarMenuClick = true;

    setMobileTopbarMenuActive((prevState) => !prevState);
    event.preventDefault();
  }

  const onMobileSubTopbarMenuClick = (event) => {
    mobileTopbarMenuClick = true;

    event.preventDefault();
  }

  const onMenuItemClick = (event) => {
    if (!event.item.items) {
      setOverlayMenuActive(false);
      setMobileMenuActive(false);
    }
  }
  const isDesktop = () => {
    return window.innerWidth >= 992;
  }

  const menu = [
    {
      label: 'Curation', icon: 'pi pi-fw pi-home',
      items: [

        { label: 'Dashboard', icon: 'pi pi-fw pi-home', to: '/' },
        { label: 'Dashboard2', icon: 'pi pi-fw pi-home', to: '/old' },
        {
          label: 'Data Tables', icon: 'pi pi-fw pi-sitemap',
          items: [
            { label: 'Genes', icon: 'pi pi-fw pi-home', to: '/genes' },
            { label: 'Alleles', icon: 'pi pi-fw pi-home', to: '/alleles' },
            { label: 'Affected Genomic Models', icon: 'pi pi-fw pi-home', to: '/agms' },
            { label: 'Disease Annotations', icon: 'pi pi-fw pi-home', to: '/diseaseAnnotations' },
            { label: 'Experimental Conditions', icon: 'pi pi-fw pi-home', to: '/experimentalConditions' },
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
            { label: 'Zebrafish Experimental Conditions Ontology (ZECO)', icon: 'pi pi-fw pi-home', to: '/ontology/zeco' },
            { label: 'NCBI Organismal Classification (NCBITaxon)', icon: 'pi pi-fw pi-home', to: '/ontology/ncbitaxon' }
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
            { label: 'Elastic Search UI', icon: 'pi pi-fw pi-home', url: `http://${window.location.hostname}:9000/#/overview?host=https://${apiVersion.esHost}` },
          ]
        }
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

  const wrapperClass = classNames('layout-wrapper', {
    'layout-overlay': layoutMode === 'overlay',
    'layout-static': layoutMode === 'static',
    'layout-static-sidebar-inactive': staticMenuInactive && layoutMode === 'static',
    'layout-overlay-sidebar-active': overlayMenuActive && layoutMode === 'overlay',
    'layout-mobile-sidebar-active': mobileMenuActive,
    'p-input-filled': inputStyle === 'filled',
    'p-ripple-disabled': ripple === false,
    'layout-theme-light': layoutColorMode === 'light'
  });

  return (
    <div className={wrapperClass} onClick={onWrapperClick}>
      <Tooltip ref={copyTooltipRef} target=".block-action-copy" position="bottom" content="Copied to clipboard" event="focus" />

      <AppTopbar onToggleMenuClick={onToggleMenuClick} layoutColorMode={layoutColorMode}
        mobileTopbarMenuActive={mobileTopbarMenuActive} onMobileTopbarMenuClick={onMobileTopbarMenuClick} onMobileSubTopbarMenuClick={onMobileSubTopbarMenuClick}
        authState={authState} logout={logout} userInfo={userInfo} />

      <div className="layout-sidebar" onClick={onSidebarClick}>
        <AppMenu model={menu} onMenuItemClick={onMenuItemClick} layoutColorMode={layoutColorMode} />
      </div>

      <div className="layout-main-container">
        <div className="layout-main">
          {children}
        </div>

        <AppFooter layoutColorMode={layoutColorMode} />
      </div>

      <AppConfig rippleEffect={ripple} onRippleEffect={onRipple} inputStyle={inputStyle} onInputStyleChange={onInputStyleChange}
        layoutMode={layoutMode} onLayoutModeChange={onLayoutModeChange} layoutColorMode={layoutColorMode} onColorModeChange={onColorModeChange} />

      <CSSTransition classNames="layout-mask" timeout={{ enter: 200, exit: 200 }} in={mobileMenuActive} unmountOnExit>
        <div className="layout-mask p-component-overlay"></div>
      </CSSTransition>

    </div>
  );
}
