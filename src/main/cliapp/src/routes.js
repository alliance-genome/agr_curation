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

import { CHEBIOntologyComponent } from './containers/ontologies/CHEBIOntologyComponent';
import { DiseaseOntologyComponent } from './containers/ontologies/DiseaseOntologyComponent';
import { ECOOntologyComponent } from './containers/ontologies/ECOOntologyComponent';
import { GOOntologyComponent } from './containers/ontologies/GOOntologyComponent';
import { SOOntologyComponent } from './containers/ontologies/SOOntologyComponent';
import { MAOntologyComponent } from './containers/ontologies/MAOntologyComponent';
import { ZFAOntologyComponent } from './containers/ontologies/ZFAOntologyComponent';
import { MPOntologyComponent } from './containers/ontologies/MPOntologyComponent';
import { DAOOntologyComponent } from './containers/ontologies/DAOOntologyComponent';
import { EMAPAOntologyComponent } from './containers/ontologies/EMAPAOntologyComponent';
import { WBbtOntologyComponent } from './containers/ontologies/WBbtOntologyComponent';
import { XCOOntologyComponent } from './containers/ontologies/XCOOntologyComponent';
import { ZECOOntologyComponent } from './containers/ontologies/ZECOOntologyComponent';
import { WBlsOntologyComponent } from './containers/ontologies/WBlsOntologyComponent';
import { FBdvOntologyComponent } from './containers/ontologies/FBdvOntologyComponent';
import { MmusDvOntologyComponent } from './containers/ontologies/MmusDvOntologyComponent';
import { ZFSOntologyComponent } from './containers/ontologies/ZFSOntologyComponent';
import { XBAOntologyComponent } from './containers/ontologies/XBAOntologyComponent';
import { XBSOntologyComponent } from './containers/ontologies/XBSOntologyComponent';
import { XPOOntologyComponent } from './containers/ontologies/XPOOntologyComponent';
import { ATPOntologyComponent } from './containers/ontologies/ATPOntologyComponent';
import { XBEDOntologyComponent } from './containers/ontologies/XBEDOntologyComponent';
import { XSMOOntologyComponent } from './containers/ontologies/XSMOOntologyComponent';
import { NCBITaxonOntologyComponent } from './containers/ontologies/NCBITaxonOntologyComponent';


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
		<SecureRoute path="/ontology/chebi" component={CHEBIOntologyComponent} />
		<SecureRoute path="/ontology/do" component={DiseaseOntologyComponent} />
		<SecureRoute path="/ontology/eco" component={ECOOntologyComponent} />
		<SecureRoute path="/ontology/go" component={GOOntologyComponent} />
		<SecureRoute path="/ontology/so" component={SOOntologyComponent} />
		<SecureRoute path="/ontology/ma" component={MAOntologyComponent} />
		<SecureRoute path="/ontology/zfa" component={ZFAOntologyComponent} />
		<SecureRoute path="/ontology/mp" component={MPOntologyComponent} />
		<SecureRoute path="/ontology/dao" component={DAOOntologyComponent} />
		<SecureRoute path="/ontology/emapa" component={EMAPAOntologyComponent} />
		<SecureRoute path="/ontology/wbbt" component={WBbtOntologyComponent} />
		<SecureRoute path="/ontology/xco" component={XCOOntologyComponent} />
		<SecureRoute path="/ontology/zeco" component={ZECOOntologyComponent} />
		<SecureRoute path="/ontology/wbls" component={WBlsOntologyComponent} />
		<SecureRoute path="/ontology/fbdv" component={FBdvOntologyComponent} />
		<SecureRoute path="/ontology/mmusdv" component={MmusDvOntologyComponent} />
		<SecureRoute path="/ontology/zfs" component={ZFSOntologyComponent} />
		<SecureRoute path="/ontology/xba" component={XBAOntologyComponent} />
		<SecureRoute path="/ontology/xbs" component={XBSOntologyComponent} />
		<SecureRoute path="/ontology/xpo" component={XPOOntologyComponent} />
		<SecureRoute path="/ontology/atp" component={ATPOntologyComponent} />
		<SecureRoute path="/ontology/xbed" component={XBEDOntologyComponent} />
		<SecureRoute path="/ontology/xsmo" component={XSMOOntologyComponent} />
		<SecureRoute path="/ontology/ncbitaxon" component={NCBITaxonOntologyComponent} />
		<SecureRoute path="/fmspage" component={FMSComponent} />
		<SecureRoute path="/metricspage" component={MetricsComponent} />
		<SecureRoute path="/agms" component={AffectedGenomicModelPage} />
		<Route path='/login' render={() => <Login config={oktaSignInConfig} />} />
	</>
)
