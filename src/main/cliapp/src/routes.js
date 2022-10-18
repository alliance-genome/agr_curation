import React from 'react';

//import { Route } from 'react-router-dom';
import { SecureRoute } from '@okta/okta-react';


import { DashboardPage } from './containers/dashboardPage';


import { DataLoadsPage } from './containers/dataLoadsPage/';
import { ReportsPage } from './containers/reportsPage/';
import { DiseaseAnnotationsPage } from './containers/diseaseAnnotationsPage';
import { ExperimentalConditionsPage } from './containers/experimentalConditionsPage';
import { ConditionRelationPage } from './containers/conditionRelationPage';
import { AffectedGenomicModelPage } from './containers/affectedGenomicModelPage';
import { AllelesPage } from './containers/allelesPage';
import { GenesPage } from './containers/genesPage';
import { ProfilePage } from './containers/profilePage';
import { MoleculesPage } from './containers/moleculesPage';
import { ReferencePage } from './containers/referencePage';
import { ControlledVocabularyPage } from './containers/controlledVocabularyPage';
import { VocabulariesPage } from './containers/vocabularyPage';

import { FMSComponent } from './components/FMSComponent';
import { MetricsComponent } from './components/MetricsComponent';

import { SiteLayout } from './containers/layout/SiteLayout';
//import { Login } from './Login';

import { GeneralOntologyComponent } from './containers/ontologies/GeneralOntologyComponent';

export default (
	<SiteLayout>
		<SecureRoute exact path="/" render={() => <DashboardPage />} />
		<SecureRoute path="/profile" render={() => <ProfilePage />} />
		<SecureRoute path="/dataloads" render={() => <DataLoadsPage />} />
		<SecureRoute path="/reports" render={() => <ReportsPage />} />
		<SecureRoute path="/diseaseAnnotations" render={() => <DiseaseAnnotationsPage />} />
		<SecureRoute path="/experimentalConditions" render={() => <ExperimentalConditionsPage />} />
		<SecureRoute path="/conditionRelations" render={() => <ConditionRelationPage />} />
		<SecureRoute path="/genes" render={() => <GenesPage />} />
		<SecureRoute path="/alleles" render={() => <AllelesPage />} />
		<SecureRoute path="/molecules" render={() => <MoleculesPage />} />
		<SecureRoute path="/references" render={() => <ReferencePage />} />
		<SecureRoute path="/vocabterms" render={() => <ControlledVocabularyPage />} />
		<SecureRoute path="/vocabularies" render={() => <VocabulariesPage />} />

		<SecureRoute path="/ontology/eco" render={() => <GeneralOntologyComponent showAbbreviation={true} name="ECO" endpoint="ecoterm" />} />

		<SecureRoute path="/ontology/go" render={() => <GeneralOntologyComponent showNamespace={true} name="GO" endpoint="goterm" />} />
		<SecureRoute path="/ontology/so" render={() => <GeneralOntologyComponent showNamespace={true} name="SO" endpoint="soterm" />} />

		<SecureRoute path="/ontology/ncbitaxon" render={() => <GeneralOntologyComponent hideDefinition={true} name="NCBITaxon" endpoint="ncbitaxonterm" />} />

		<SecureRoute path="/ontology/chebi"  render={() => <GeneralOntologyComponent name="ChEBI" endpoint="chebiterm" />} />
		<SecureRoute path="/ontology/do"     render={() => <GeneralOntologyComponent name="Diseases" endpoint="doterm" />} />
		<SecureRoute path="/ontology/ma"     render={() => <GeneralOntologyComponent name="MA" endpoint="materm" />} />
		<SecureRoute path="/ontology/zfa"    render={() => <GeneralOntologyComponent name="ZFA" endpoint="zfaterm" />} />
		<SecureRoute path="/ontology/mp"     render={() => <GeneralOntologyComponent name="MP" endpoint="mpterm" />} />
		<SecureRoute path="/ontology/dao"    render={() => <GeneralOntologyComponent name="DAO" endpoint="daoterm" />} />
		<SecureRoute path="/ontology/emapa"  render={() => <GeneralOntologyComponent name="EMAPA" endpoint="emapaterm" />} />
		<SecureRoute path="/ontology/wbbt"   render={() => <GeneralOntologyComponent name="WBbt" endpoint="wbbtterm" />} />
		<SecureRoute path="/ontology/xco"    render={() => <GeneralOntologyComponent name="XCO" endpoint="xcoterm" />} />
		<SecureRoute path="/ontology/zeco"   render={() => <SiteLayout><GeneralOntologyComponent name="ZECO" endpoint="zecoterm" /></SiteLayout>} />
		<SecureRoute path="/ontology/wbls"   render={() => <SiteLayout><GeneralOntologyComponent name="WBls" endpoint="wblsterm" /></SiteLayout>} />
		<SecureRoute path="/ontology/fbdv"   render={() => <SiteLayout><GeneralOntologyComponent name="FBdv" endpoint="fbdvterm" /></SiteLayout>} />
		<SecureRoute path="/ontology/mmusdv" render={() => <SiteLayout><GeneralOntologyComponent name="MmusDv" endpoint="mmusdvterm" /></SiteLayout>} />
		<SecureRoute path="/ontology/zfs"    render={() => <SiteLayout><GeneralOntologyComponent name="ZFS" endpoint="zfsterm" /></SiteLayout>} />
		<SecureRoute path="/ontology/xba"    render={() => <SiteLayout><GeneralOntologyComponent name="XBA" endpoint="xbaterm" /></SiteLayout>} />
		<SecureRoute path="/ontology/xbs"    render={() => <SiteLayout><GeneralOntologyComponent name="XBS" endpoint="xbsterm" /></SiteLayout>} />
		<SecureRoute path="/ontology/xpo"    render={() => <SiteLayout><GeneralOntologyComponent name="XPO" endpoint="xpoterm" /></SiteLayout>} />
		<SecureRoute path="/ontology/atp"    render={() => <SiteLayout><GeneralOntologyComponent name="ATP" endpoint="atpterm" /></SiteLayout>} />
		<SecureRoute path="/ontology/xbed"   render={() => <SiteLayout><GeneralOntologyComponent name="XBED" endpoint="xbedterm" /></SiteLayout>} />
		<SecureRoute path="/ontology/xsmo"   render={() => <SiteLayout><GeneralOntologyComponent name="XSMO" endpoint="xsmoterm" /></SiteLayout>} />

		<SecureRoute path="/fmspage" render={() => <FMSComponent />} />
		<SecureRoute path="/metricspage" render={() =><MetricsComponent />} />
		<SecureRoute path="/agms" render={() => <AffectedGenomicModelPage />} />
	</SiteLayout>
)
