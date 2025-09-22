import React from 'react';
import { Routes, Route } from 'react-router-dom';

import { RequiredAuth } from './components/RequiredAuth';

import { DashboardPage } from './containers/dashboardPage';

import { DataLoadsPage } from './containers/dataLoadsPage/';
import { ReportsPage } from './containers/reportsPage/';
import { DiseaseAnnotationsPage } from './containers/diseaseAnnotationsPage';
import { PhenotypeAnnotationsPage } from './containers/phenotypeAnnotationsPage';
import { GeneGeneticInteractionsPage } from './containers/geneGeneticInteractionsPage/GeneGeneticInteractionsPage';
import { GeneMolecularInteractionsPage } from './containers/geneMolecularInteractionsPage/GeneMolecularInteractionsPage';
import { ExperimentalConditionsPage } from './containers/experimentalConditionsPage';
import { ConditionRelationPage } from './containers/conditionRelationPage';
import { AffectedGenomicModelPage } from './containers/affectedGenomicModelPage';
import { AllelesPage, AlleleDetailPage } from './containers/allelesPage';
import { GenesPage } from './containers/genesPage';
import { VariantsPage } from './containers/variantsPage';
import { ConstructsPage } from './containers/constructsPage';
import { ProfilePage } from './containers/profilePage';
import { MoleculesPage } from './containers/moleculesPage';
import { SpeciesPage } from './containers/speciesPage';
import { ReferencePage } from './containers/referencePage';
import { ControlledVocabularyPage } from './containers/controlledVocabularyPage';
import { VocabulariesPage } from './containers/vocabularyPage';
import { VocabularyTermSetPage } from './containers/vocabularyTermSetPage';

import { FMSComponent } from './components/FMSComponent';
import { EntityCountsComponent } from './components/EntityCountsComponent';
import { FMSDataTypesComponent } from './components/FMSDataTypesComponent';
import { MetricsComponent } from './components/MetricsComponent';
import { HealthComponent } from './components/HealthComponent';

import { SiteLayout } from './containers/layout/SiteLayout';

import { GeneralOntologyComponent } from './containers/ontologies/GeneralOntologyComponent';
import { ResourceDescriptorsPage } from './containers/resourceDescriptorPage';
import { ResourceDescriptorPagesPage } from './containers/resourceDescriptorPagePage';
import ErrorBoundary from './components/Error/ErrorBoundary';

export default function AppRoutes() {
	return (
		<SiteLayout>
			<Routes>
				<Route element={<RequiredAuth />}>
					<Route
						path="/"
						element={
							<ErrorBoundary>
								<DashboardPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/profile"
						element={
							<ErrorBoundary>
								<ProfilePage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/diseaseAnnotations"
						element={
							<ErrorBoundary>
								<DiseaseAnnotationsPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/phenotypeAnnotations"
						element={
							<ErrorBoundary>
								<PhenotypeAnnotationsPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/geneGeneticInteractions"
						element={
							<ErrorBoundary>
								<GeneGeneticInteractionsPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/geneMolecularInteractions"
						element={
							<ErrorBoundary>
								<GeneMolecularInteractionsPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/experimentalconditions"
						element={
							<ErrorBoundary>
								<ExperimentalConditionsPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/conditionrelation"
						element={
							<ErrorBoundary>
								<ConditionRelationPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/alleles"
						element={
							<ErrorBoundary>
								<AllelesPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/allele/:identifier"
						element={
							<ErrorBoundary>
								<AlleleDetailPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/genes"
						element={
							<ErrorBoundary>
								<GenesPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/variants"
						element={
							<ErrorBoundary>
								<VariantsPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/constructs"
						element={
							<ErrorBoundary>
								<ConstructsPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/molecules"
						element={
							<ErrorBoundary>
								<MoleculesPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/species"
						element={
							<ErrorBoundary>
								<SpeciesPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/references"
						element={
							<ErrorBoundary>
								<ReferencePage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/controlledvocabulary"
						element={
							<ErrorBoundary>
								<ControlledVocabularyPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/vocabularies"
						element={
							<ErrorBoundary>
								<VocabulariesPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/vocabularyTermSets/:vocabulary"
						element={
							<ErrorBoundary>
								<VocabularyTermSetPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/dataloads"
						element={
							<ErrorBoundary>
								<DataLoadsPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/reports"
						element={
							<ErrorBoundary>
								<ReportsPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/fms"
						element={
							<ErrorBoundary>
								<FMSComponent />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/fmsdatatypes"
						element={
							<ErrorBoundary>
								<FMSDataTypesComponent />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/entitycounts"
						element={
							<ErrorBoundary>
								<EntityCountsComponent />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/metricspage"
						element={
							<ErrorBoundary>
								<MetricsComponent />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/healthpage"
						element={
							<ErrorBoundary>
								<HealthComponent />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/agms"
						element={
							<ErrorBoundary>
								<AffectedGenomicModelPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/eco"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent showAbbreviation={true} name="ECO" endpoint="ecoterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/go"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent showNamespace={true} name="GO" endpoint="goterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/so"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent showNamespace={true} name="SO" endpoint="soterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/ncbitaxon"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent hideDefinition={true} name="NCBITaxon" endpoint="ncbitaxonterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/chebi"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="ChEBI" endpoint="chebiterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/do"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="Diseases" endpoint="doterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/ma"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="MA" endpoint="materm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/zfa"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="ZFA" endpoint="zfaterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/mp"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="MP" endpoint="mpterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/dao"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="DAO" endpoint="daoterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/emapa"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="EMAPA" endpoint="emapaterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/wbbt"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="WBbt" endpoint="wbbtterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/xco"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="XCO" endpoint="xcoterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/ro"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="RO" endpoint="roterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/zeco"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="ZECO" endpoint="zecoterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/wbls"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="WBls" endpoint="wblsterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/fbdv"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="FBdv" endpoint="fbdvterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/mmusdv"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="MmusDv" endpoint="mmusdvterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/zfs"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="ZFS" endpoint="zfsterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/xba"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="XBA" endpoint="xbaterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/xbs"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="XBS" endpoint="xbsterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/xpo"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="XPO" endpoint="xpoterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/atp"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="ATP" endpoint="atpterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/xbed"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="XBED" endpoint="xbedterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/xsmo"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="XSMO" endpoint="xsmoterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/vt"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="VT" endpoint="vtterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/obi"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="OBI" endpoint="obiterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/wbpheno"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="WBPhenotype" endpoint="wbphenotypeterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/pato"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="PATO" endpoint="patoterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/hp"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="HP" endpoint="hpterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/fbcv"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent showNamespace={true} name="FBcv" endpoint="fbcvterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/mmo"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="MMO" endpoint="mmoterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/apo"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent showNamespace={true} name="APO" endpoint="apoterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/mi"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="MI" endpoint="miterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/mpath"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="MPATH" endpoint="mpathterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/mod"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="MOD" endpoint="modterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/uberon"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="UBERON" endpoint="uberonterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/rs"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="RS" endpoint="rsterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/pw"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="PW" endpoint="pwterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/cl"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="CL" endpoint="clterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/cmo"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="CMO" endpoint="cmoterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/bto"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="BTO" endpoint="btoterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/bspo"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="BSPO" endpoint="bspoterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/geno"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent name="GENO" endpoint="genoterm" />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/resourcedescriptors"
						element={
							<ErrorBoundary>
								<ResourceDescriptorsPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/resourcedescriptorpages"
						element={
							<ErrorBoundary>
								<ResourceDescriptorPagesPage />
							</ErrorBoundary>
						}
					/>
				</Route>
			</Routes>
		</SiteLayout>
	);
}
