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
import { VocabularyTermSetPage } from './containers/vocabularyTermSetPage';

import { FMSComponent } from './components/FMSComponent';
import { MetricsComponent } from './components/MetricsComponent';

import { SiteLayout } from './containers/layout/SiteLayout';
//import { Login } from './Login';

import { GeneralOntologyComponent } from './containers/ontologies/GeneralOntologyComponent';
import { ResourceDescriptorsPage } from './containers/resourceDescriptorPage';
import { ResourceDescriptorPagesPage } from './containers/resourceDescriptorPagePage';
import ErrorBoundary from './components/Error/ErrorBoundary';

export default (
	<SiteLayout>
		<SecureRoute exact path="/" render={() => <ErrorBoundary><DashboardPage /></ErrorBoundary>} />
		<SecureRoute path="/profile" render={() => <ErrorBoundary><ProfilePage /></ErrorBoundary>} />
		<SecureRoute path="/dataloads" render={() => <ErrorBoundary><DataLoadsPage /></ErrorBoundary>} />
		<SecureRoute path="/reports" render={() => <ErrorBoundary><ReportsPage /></ErrorBoundary>} />
		<SecureRoute path="/diseaseAnnotations" render={() => <ErrorBoundary><DiseaseAnnotationsPage /></ErrorBoundary>} />
		<SecureRoute path="/experimentalConditions" render={() => <ErrorBoundary><ExperimentalConditionsPage /></ErrorBoundary>} />
		<SecureRoute path="/conditionRelations" render={() => <ErrorBoundary><ConditionRelationPage /></ErrorBoundary>} />
		<SecureRoute path="/genes" render={() => <ErrorBoundary><GenesPage /></ErrorBoundary>} />
		<SecureRoute path="/alleles" render={() => <ErrorBoundary><AllelesPage /></ErrorBoundary>} />
		<SecureRoute path="/molecules" render={() => <ErrorBoundary><MoleculesPage /></ErrorBoundary>} />
		<SecureRoute path="/references" render={() => <ErrorBoundary><ReferencePage /></ErrorBoundary>} />
		<SecureRoute path="/vocabterms" render={() => <ErrorBoundary><ControlledVocabularyPage /></ErrorBoundary>} />
		<SecureRoute path="/vocabularies" render={() => <ErrorBoundary><VocabulariesPage /></ErrorBoundary>} />
		<SecureRoute path="/vocabularytermsets" render={() => <ErrorBoundary><VocabularyTermSetPage /></ErrorBoundary>} />
		<SecureRoute path="/resourcedescriptors" render={() => <ErrorBoundary><ResourceDescriptorsPage /></ErrorBoundary>} />
		<SecureRoute path="/resourcedescriptorpages" render={() => <ErrorBoundary><ResourceDescriptorPagesPage /></ErrorBoundary>} />

		<SecureRoute path="/ontology/eco" render={() => <ErrorBoundary><GeneralOntologyComponent showAbbreviation={true} name="ECO" endpoint="ecoterm" /></ErrorBoundary>} />

		<SecureRoute path="/ontology/go" render={() => <ErrorBoundary><GeneralOntologyComponent showNamespace={true} name="GO" endpoint="goterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/so" render={() => <ErrorBoundary><GeneralOntologyComponent showNamespace={true} name="SO" endpoint="soterm" /></ErrorBoundary>} />

		<SecureRoute path="/ontology/ncbitaxon" render={() => <ErrorBoundary><GeneralOntologyComponent hideDefinition={true} name="NCBITaxon" endpoint="ncbitaxonterm" /></ErrorBoundary>} />

		<SecureRoute path="/ontology/chebi"	 render={() => <ErrorBoundary><GeneralOntologyComponent name="ChEBI" endpoint="chebiterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/do"		 render={() => <ErrorBoundary><GeneralOntologyComponent name="Diseases" endpoint="doterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/ma"		 render={() => <ErrorBoundary><GeneralOntologyComponent name="MA" endpoint="materm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/zfa"		 render={() => <ErrorBoundary><GeneralOntologyComponent name="ZFA" endpoint="zfaterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/mp"		 render={() =><ErrorBoundary> <GeneralOntologyComponent name="MP" endpoint="mpterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/dao"		 render={() => <ErrorBoundary><GeneralOntologyComponent name="DAO" endpoint="daoterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/emapa"	 render={() => <ErrorBoundary><GeneralOntologyComponent name="EMAPA" endpoint="emapaterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/wbbt"	 render={() => <ErrorBoundary><GeneralOntologyComponent name="WBbt" endpoint="wbbtterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/xco"		 render={() => <ErrorBoundary><GeneralOntologyComponent name="XCO" endpoint="xcoterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/ro"		 render={() => <ErrorBoundary><GeneralOntologyComponent name="RO" endpoint="roterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/zeco"	 render={() => <ErrorBoundary><GeneralOntologyComponent name="ZECO" endpoint="zecoterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/wbls"	 render={() => <ErrorBoundary><GeneralOntologyComponent name="WBls" endpoint="wblsterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/fbdv"	 render={() => <ErrorBoundary><GeneralOntologyComponent name="FBdv" endpoint="fbdvterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/mmusdv" render={() => <ErrorBoundary><GeneralOntologyComponent name="MmusDv" endpoint="mmusdvterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/zfs"		 render={() => <ErrorBoundary><GeneralOntologyComponent name="ZFS" endpoint="zfsterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/xba"		 render={() => <ErrorBoundary><GeneralOntologyComponent name="XBA" endpoint="xbaterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/xbs"		 render={() => <ErrorBoundary><GeneralOntologyComponent name="XBS" endpoint="xbsterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/xpo"		 render={() => <ErrorBoundary><GeneralOntologyComponent name="XPO" endpoint="xpoterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/atp"		 render={() => <ErrorBoundary><GeneralOntologyComponent name="ATP" endpoint="atpterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/xbed"	 render={() => <ErrorBoundary><GeneralOntologyComponent name="XBED" endpoint="xbedterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/xsmo"	 render={() => <ErrorBoundary><GeneralOntologyComponent name="XSMO" endpoint="xsmoterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/obi"	 	 render={() => <ErrorBoundary><GeneralOntologyComponent name="OBI" endpoint="obiterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/wbpheno"	 render={() => <ErrorBoundary><GeneralOntologyComponent name="WBPhenotype" endpoint="wbphenotypeterm" /></ErrorBoundary>} />
		<SecureRoute path="/ontology/pato"	 render={() => <ErrorBoundary><GeneralOntologyComponent name="PATO" endpoint="patoterm" /></ErrorBoundary>} />

		<SecureRoute path="/fmspage" render={() => <ErrorBoundary><FMSComponent /></ErrorBoundary>} />
		<SecureRoute path="/metricspage" render={() => <ErrorBoundary><MetricsComponent /></ErrorBoundary>} />
		<SecureRoute path="/agms" render={() => <ErrorBoundary><AffectedGenomicModelPage /></ErrorBoundary>} />
		
	</SiteLayout>
)
