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
					<Route path="/" element={
						<ErrorBoundary>
							<DashboardPage />
						</ErrorBoundary>
					} />
					<Route path="/profile" element={
						<ErrorBoundary>
							<ProfilePage />
						</ErrorBoundary>
					} />
					<Route path="/diseaseAnnotations" element={
						<ErrorBoundary>
							<DiseaseAnnotationsPage />
						</ErrorBoundary>
					} />
					<Route path="/phenotypeAnnotations" element={
						<ErrorBoundary>
							<PhenotypeAnnotationsPage />
						</ErrorBoundary>
					} />
					<Route path="/geneGeneticInteractions" element={
						<ErrorBoundary>
							<GeneGeneticInteractionsPage />
						</ErrorBoundary>
					} />
					<Route path="/geneMolecularInteractions" element={
						<ErrorBoundary>
							<GeneMolecularInteractionsPage />
						</ErrorBoundary>
					} />
					<Route path="/experimentalconditions" element={
						<ErrorBoundary>
							<ExperimentalConditionsPage />
						</ErrorBoundary>
					} />
					<Route path="/conditionrelation" element={
						<ErrorBoundary>
							<ConditionRelationPage />
						</ErrorBoundary>
					} />
					<Route path="/alleles" element={
						<ErrorBoundary>
							<AllelesPage />
						</ErrorBoundary>
					} />
					<Route path="/allele/:identifier" element={
						<ErrorBoundary>
							<AlleleDetailPage />
						</ErrorBoundary>
					} />
					<Route path="/genes" element={
						<ErrorBoundary>
							<GenesPage />
						</ErrorBoundary>
					} />
					<Route path="/variants" element={
						<ErrorBoundary>
							<VariantsPage />
						</ErrorBoundary>
					} />
					<Route path="/constructs" element={
						<ErrorBoundary>
							<ConstructsPage />
						</ErrorBoundary>
					} />
					<Route path="/molecules" element={
						<ErrorBoundary>
							<MoleculesPage />
						</ErrorBoundary>
					} />
					<Route path="/species" element={
						<ErrorBoundary>
							<SpeciesPage />
						</ErrorBoundary>
					} />
					<Route path="/references" element={
						<ErrorBoundary>
							<ReferencePage />
						</ErrorBoundary>
					} />
					<Route path="/controlledvocabulary" element={
						<ErrorBoundary>
							<ControlledVocabularyPage />
						</ErrorBoundary>
					} />
					<Route path="/vocabularies" element={
						<ErrorBoundary>
							<VocabulariesPage />
						</ErrorBoundary>
					} />
					<Route path="/vocabularyTermSets/:vocabulary" element={
						<ErrorBoundary>
							<VocabularyTermSetPage />
						</ErrorBoundary>
					} />
					<Route path="/dataloads" element={
						<ErrorBoundary>
							<DataLoadsPage />
						</ErrorBoundary>
					} />
					<Route path="/reports" element={
						<ErrorBoundary>
							<ReportsPage />
						</ErrorBoundary>
					} />
					<Route path="/fms" element={
						<ErrorBoundary>
							<FMSComponent />
						</ErrorBoundary>
					} />
					<Route path="/fmsdatatypes" element={
						<ErrorBoundary>
							<FMSDataTypesComponent />
						</ErrorBoundary>
					} />
					<Route path="/entitycounts" element={
						<ErrorBoundary>
							<EntityCountsComponent />
						</ErrorBoundary>
					} />
					<Route path="/metricspage" element={
						<ErrorBoundary>
							<MetricsComponent />
						</ErrorBoundary>
					} />
					<Route path="/healthpage" element={
						<ErrorBoundary>
							<HealthComponent />
						</ErrorBoundary>
					} />
					<Route path="/agms" element={
						<ErrorBoundary>
							<AffectedGenomicModelPage />
						</ErrorBoundary>
					} />
					<Route path="/ontology/:ontology_name" element={
						<ErrorBoundary>
							<GeneralOntologyComponent />
						</ErrorBoundary>
					} />
					<Route path="/resourcedescriptors" element={
						<ErrorBoundary>
							<ResourceDescriptorsPage />
						</ErrorBoundary>
					} />
					<Route path="/resourcedescriptorpages" element={
						<ErrorBoundary>
							<ResourceDescriptorPagesPage />
						</ErrorBoundary>
					} />
				</Route>
			</Routes>
		</SiteLayout>
	);
}