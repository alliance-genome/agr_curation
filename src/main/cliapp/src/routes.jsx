import { Routes, Route } from 'react-router-dom';

import { RequiredAuth } from './components/RequiredAuth';

import { DashboardPage } from './containers/dashboardPage';

import { DataLoadsPage } from './containers/dataLoadsPage/';
import { ReportsPage } from './containers/reportsPage/';
import { AlleleGeneAssociationsPage } from './containers/alleleGeneAssociationsPage';
import { DiseaseAnnotationsPage } from './containers/diseaseAnnotationsPage';
import { PhenotypeAnnotationsPage } from './containers/phenotypeAnnotationsPage';
import { GeneExpressionAnnotationsPage } from './containers/geneExpressionAnnotationsPage';
import { GeneGeneticInteractionsPage } from './containers/geneGeneticInteractionsPage/GeneGeneticInteractionsPage';
import { GeneMolecularInteractionsPage } from './containers/geneMolecularInteractionsPage/GeneMolecularInteractionsPage';
import { ExperimentalConditionsPage } from './containers/experimentalConditionsPage';
import { ConditionRelationPage } from './containers/conditionRelationPage';
import { AffectedGenomicModelPage } from './containers/affectedGenomicModelPage';
import { AllelesPage, AlleleDetailPage } from './containers/allelesPage';
import { GenesPage } from './containers/genesPage';
import { VariantsPage } from './containers/variantsPage';
import { ConstructsPage, ConstructDetailPage } from './containers/constructsPage';
import { CassettesPage } from './containers/cassettesPage';
import { ProfilePage } from './containers/profilePage';
import { MoleculesPage } from './containers/moleculesPage';
import { SpeciesPage } from './containers/speciesPage';
import { GenomeAssembliesPage } from './containers/genomeAssemblyPage';
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
import { Endpoints } from './constants/Endpoints';
import { ResourceDescriptorsPage } from './containers/resourceDescriptorPage';
import { ResourceDescriptorPagesPage } from './containers/resourceDescriptorPagePage';
import { LoadDependencyPage } from './containers/loadDependencyPage';
import ErrorBoundary from './components/Error/ErrorBoundary';

export default function AppRoutes() {
	return (
		<SiteLayout>
			<Routes>
				{/* RequiredAuth replaces SecureRoute which is not compatible with ReactRouter V6 */}
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
						path="/alleleGeneAssociations"
						element={
							<ErrorBoundary>
								<AlleleGeneAssociationsPage />
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
						path="/geneExpressionAnnotations"
						element={
							<ErrorBoundary>
								<GeneExpressionAnnotationsPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/experimentalConditions"
						element={
							<ErrorBoundary>
								<ExperimentalConditionsPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/conditionRelations"
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
						path="/construct/:identifier"
						element={
							<ErrorBoundary>
								<ConstructDetailPage />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/cassettes"
						element={
							<ErrorBoundary>
								<CassettesPage />
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
						path="/genomeassemblies"
						element={
							<ErrorBoundary>
								<GenomeAssembliesPage />
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
						path="/vocabterms"
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
						path="/vocabularytermsets"
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
						path="/fmsdatafiles"
						element={
							<ErrorBoundary>
								<FMSComponent />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/fmsdatatypes/:dataType"
						element={
							<ErrorBoundary>
								<FMSDataTypesComponent />
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
								<GeneralOntologyComponent
									key="eco"
									showAbbreviation={true}
									name="ECO"
									endpoint={Endpoints.Ontology.ECO}
								/>
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/go"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="go" showNamespace={true} name="GO" endpoint={Endpoints.Ontology.GO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/so"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="so" showNamespace={true} name="SO" endpoint={Endpoints.Ontology.SO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/ncbitaxon"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent
									key="ncbitaxon"
									hideDefinition={true}
									name="NCBITaxon"
									endpoint={Endpoints.Ontology.NCBI_TAXON}
								/>
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/chebi"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="chebi" name="ChEBI" endpoint={Endpoints.Ontology.CHEBI} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/do"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="do" name="Diseases" endpoint={Endpoints.Ontology.DO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/ma"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="ma" name="MA" endpoint={Endpoints.Ontology.MA} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/zfa"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="zfa" name="ZFA" endpoint={Endpoints.Ontology.ZFA} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/mp"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="mp" name="MP" endpoint={Endpoints.Ontology.MP} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/dao"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="dao" name="DAO" endpoint={Endpoints.Ontology.DAO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/emapa"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="emapa" name="EMAPA" endpoint={Endpoints.Ontology.EMAPA} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/wbbt"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="wbbt" name="WBbt" endpoint={Endpoints.Ontology.WBBT} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/xco"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="xco" name="XCO" endpoint={Endpoints.Ontology.XCO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/ro"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="ro" name="RO" endpoint={Endpoints.Ontology.RO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/zeco"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="zeco" name="ZECO" endpoint={Endpoints.Ontology.ZECO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/wbls"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="wbls" name="WBls" endpoint={Endpoints.Ontology.WBLS} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/fbdv"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="fbdv" name="FBdv" endpoint={Endpoints.Ontology.FBDV} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/mmusdv"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="mmusdv" name="MmusDv" endpoint={Endpoints.Ontology.MMUSDV} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/zfs"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="zfs" name="ZFS" endpoint={Endpoints.Ontology.ZFS} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/xba"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="xba" name="XBA" endpoint={Endpoints.Ontology.XBA} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/xbs"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="xbs" name="XBS" endpoint={Endpoints.Ontology.XBS} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/xpo"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="xpo" name="XPO" endpoint={Endpoints.Ontology.XPO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/atp"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="atp" name="ATP" endpoint={Endpoints.Ontology.ATP} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/xbed"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="xbed" name="XBED" endpoint={Endpoints.Ontology.XBED} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/xsmo"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="xsmo" name="XSMO" endpoint={Endpoints.Ontology.XSMO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/vt"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="vt" name="VT" endpoint={Endpoints.Ontology.VT} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/obi"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="obi" name="OBI" endpoint={Endpoints.Ontology.OBI} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/wbpheno"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="wbpheno" name="WBPhenotype" endpoint={Endpoints.Ontology.WB_PHENOTYPE} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/pato"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="pato" name="PATO" endpoint={Endpoints.Ontology.PATO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/hp"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="hp" name="HP" endpoint={Endpoints.Ontology.HP} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/fbcv"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent
									key="fbcv"
									showNamespace={true}
									name="FBcv"
									endpoint={Endpoints.Ontology.FBCV}
								/>
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/mmo"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="mmo" name="MMO" endpoint={Endpoints.Ontology.MMO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/apo"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="apo" showNamespace={true} name="APO" endpoint={Endpoints.Ontology.APO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/mi"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="mi" name="MI" endpoint={Endpoints.Ontology.MI} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/mpath"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="mpath" name="MPATH" endpoint={Endpoints.Ontology.MPATH} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/mod"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="mod" name="MOD" endpoint={Endpoints.Ontology.MOD} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/uberon"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="uberon" name="UBERON" endpoint={Endpoints.Ontology.UBERON} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/rs"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="rs" name="RS" endpoint={Endpoints.Ontology.RS} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/pw"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="pw" name="PW" endpoint={Endpoints.Ontology.PW} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/cl"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="cl" name="CL" endpoint={Endpoints.Ontology.CL} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/cmo"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="cmo" name="CMO" endpoint={Endpoints.Ontology.CMO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/bto"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="bto" name="BTO" endpoint={Endpoints.Ontology.BTO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/bspo"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="bspo" name="BSPO" endpoint={Endpoints.Ontology.BSPO} />
							</ErrorBoundary>
						}
					/>
					<Route
						path="/ontology/geno"
						element={
							<ErrorBoundary>
								<GeneralOntologyComponent key="geno" name="GENO" endpoint={Endpoints.Ontology.GENO} />
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
					<Route
						path="/loadDependencies"
						element={
							<ErrorBoundary>
								<LoadDependencyPage />
							</ErrorBoundary>
						}
					/>
				</Route>
			</Routes>
		</SiteLayout>
	);
}
