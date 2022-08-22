import React from 'react';
import { Route } from 'react-router-dom';
import { SecureRoute } from '@okta/okta-react';
import { oktaSignInConfig } from './oktaAuthConfig';

import { DashboardPage } from './containers/dashboardPage';
import { Login } from './Login'

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

import { GeneralOntologyComponent } from './containers/ontologies/GeneralOntologyComponent';

export default (
	<>

		<SecureRoute path="/" exact component={DashboardPage} />
		<SecureRoute path="/profile" component={ProfilePage} />
		<SecureRoute path="/dataloads" component={DataLoadsPage} />
		<SecureRoute path="/reports" component={ReportsPage} />
		<SecureRoute path="/diseaseAnnotations" component={DiseaseAnnotationsPage} />
		<SecureRoute path="/experimentalConditions" component={ExperimentalConditionsPage} />
		<SecureRoute path="/conditionRelations" component={ConditionRelationPage} />
		<SecureRoute path="/genes" component={GenesPage} />
		<SecureRoute path="/alleles" component={AllelesPage} />
		<SecureRoute path="/molecules" component={MoleculesPage} />
		<SecureRoute path="/references" component={ReferencePage} />
		<SecureRoute path="/vocabterms" component={ControlledVocabularyPage} />
		<SecureRoute path="/vocabularies" component={VocabulariesPage} />

		<SecureRoute path="/ontology/eco" render={() => <GeneralOntologyComponent showAbbreviation={true} name="ECO" endpoint="ecoterm" />} />

		<SecureRoute path="/ontology/go" render={() => <GeneralOntologyComponent showNamespace={true} name="GO" endpoint="goterm" />} />
		<SecureRoute path="/ontology/so" render={() => <GeneralOntologyComponent showNamespace={true} name="SO" endpoint="soterm" />} />

		<SecureRoute path="/ontology/ncbitaxon" render={() => <GeneralOntologyComponent hideDefinition={true} name="NCBITaxon" endpoint="ncbitaxonterm" />} />

		<SecureRoute path="/ontology/chebi" render={() => <GeneralOntologyComponent name="ChEBI" endpoint="chebiterm" />} />
		<SecureRoute path="/ontology/do" render={() => <GeneralOntologyComponent name="Diseases" endpoint="doterm" />} />
		<SecureRoute path="/ontology/ma" render={() => <GeneralOntologyComponent name="MA" endpoint="materm" />} />
		<SecureRoute path="/ontology/zfa" render={() => <GeneralOntologyComponent name="ZFA" endpoint="zfaterm" />} />
		<SecureRoute path="/ontology/mp" render={() => <GeneralOntologyComponent name="MP" endpoint="mpterm" />} />
		<SecureRoute path="/ontology/dao" render={() => <GeneralOntologyComponent name="DAO" endpoint="daoterm" />} />
		<SecureRoute path="/ontology/emapa" render={() => <GeneralOntologyComponent name="EMAPA" endpoint="emapaterm" />} />
		<SecureRoute path="/ontology/wbbt" render={() => <GeneralOntologyComponent name="WBbt" endpoint="wbbtterm" />} />
		<SecureRoute path="/ontology/xco" render={() => <GeneralOntologyComponent name="XCO" endpoint="xcoterm" />} />
		<SecureRoute path="/ontology/zeco" render={() => <GeneralOntologyComponent name="ZECO" endpoint="zecoterm" />} />
		<SecureRoute path="/ontology/wbls" render={() => <GeneralOntologyComponent name="WBls" endpoint="wblsterm" />} />
		<SecureRoute path="/ontology/fbdv" render={() => <GeneralOntologyComponent name="FBdv" endpoint="fbdvterm" />} />
		<SecureRoute path="/ontology/mmusdv" render={() => <GeneralOntologyComponent name="MmusDv" endpoint="mmusdvterm" />} />
		<SecureRoute path="/ontology/zfs" render={() => <GeneralOntologyComponent name="ZFS" endpoint="zfsterm" />} />
		<SecureRoute path="/ontology/xba" render={() => <GeneralOntologyComponent name="XBA" endpoint="xbaterm" />} />
		<SecureRoute path="/ontology/xbs" render={() => <GeneralOntologyComponent name="XBS" endpoint="xbsterm" />} />
		<SecureRoute path="/ontology/xpo" render={() => <GeneralOntologyComponent name="XPO" endpoint="xpoterm" />} />
		<SecureRoute path="/ontology/atp" render={() => <GeneralOntologyComponent name="ATP" endpoint="atpterm" />} />
		<SecureRoute path="/ontology/xbed" render={() => <GeneralOntologyComponent name="XBED" endpoint="xbedterm" />} />
		<SecureRoute path="/ontology/xsmo" render={() => <GeneralOntologyComponent name="XSMO" endpoint="xsmoterm" />} />


		<SecureRoute path="/fmspage" component={FMSComponent} />
		<SecureRoute path="/metricspage" component={MetricsComponent} />
		<SecureRoute path="/agms" component={AffectedGenomicModelPage} />
		<Route path='/login' render={() => <Login config={oktaSignInConfig} />} />
	</>
)
