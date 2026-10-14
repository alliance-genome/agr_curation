import { useRef, useEffect, useMemo } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ConstructService } from '../../service/ConstructService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { DetailPageHeading } from '../../components/DetailPageHeading';
import { useConstructReducer } from './useConstructReducer';
import { IdentifierDetailPageTemplate } from '../../components/Templates/IdentifierDetailPageTemplate';
import { DataProviderDetailPageTemplate } from '../../components/Templates/DataProviderDetailPageTemplate';
import { UserDetailPageTemplate } from '../../components/Templates/UserDetailPageTemplate';
import { DateDetailPageTemplate } from '../../components/Templates/DateDetailPageTemplate';
import { Divider } from 'primereact/divider';
import { DetailPageFieldWrapper } from '../../components/DetailPageFieldWrapper';
import { SymbolForm } from './symbol/SymbolForm';
import { FullNameForm } from './fullName/FullNameForm';
import { SynonymsForm } from './synonyms/SynonymsForm';
import { SecondaryIdsForm } from './secondaryIds/SecondaryIdsForm';
import { ReferencesForm } from './references/ReferencesForm';
import { CassettesForm } from './cassettes/CassettesForm';

export default function ConstructDetailPage() {
	const { identifier } = useParams();
	const { constructState, constructDispatch } = useConstructReducer();
	const constructService = useMemo(() => new ConstructService(), []);
	const toastSuccess = useRef(null);
	const toastError = useRef(null);

	const labelColumnSize = 'col-3';
	const widgetColumnSize = 'col-4';
	const fieldDetailsColumnSize = 'col-5';

	const { isPending: getRequestIsLoading, data: constructQueryData } = useQuery({
		queryKey: [identifier],
		queryFn: () => constructService.getConstruct(identifier),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	// Handle query success in useEffect (v5 removed onSuccess from useQuery)
	useEffect(() => {
		if (constructQueryData) {
			constructDispatch({ type: 'SET', value: constructQueryData?.data?.entity });
		}
	}, [constructQueryData, constructDispatch]);

	if (getRequestIsLoading)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={70} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<DetailPageHeading
								entityType="Construct"
								entity={constructState.construct}
								label={constructState.construct?.constructSymbol?.displayText}
							/>
						</SplitterPanel>
						<SplitterPanel size={30} className="flex justify-content-start py-3"></SplitterPanel>
					</Splitter>
				</StickyHeader>
				{/* The page saves nothing, and Enter in the Cassettes table's filters must not submit and reload it. */}
				<form className="mt-8" onSubmit={(event) => event.preventDefault()}>
					<IdentifierDetailPageTemplate
						identifier={constructState.construct?.primaryExternalId}
						label="Primary External ID"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						showAdditionalData={false}
					/>

					<Divider />

					<IdentifierDetailPageTemplate
						identifier={constructState.construct?.modInternalId}
						label="MOD Internal ID"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						showAdditionalData={false}
					/>

					<Divider />

					<IdentifierDetailPageTemplate
						identifier={constructState.construct?.uniqueId}
						label="Unique ID"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						showAdditionalData={false}
					/>

					<Divider />

					<SymbolForm state={constructState} />

					<Divider />

					<FullNameForm state={constructState} />

					<Divider />

					<SynonymsForm state={constructState} />

					<Divider />

					<SecondaryIdsForm state={constructState} />

					<Divider />

					<ReferencesForm state={constructState} />

					<Divider />

					<CassettesForm state={constructState} />

					<Divider />

					<DataProviderDetailPageTemplate
						dataProvider={constructState.construct?.dataProvider?.abbreviation}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<UserDetailPageTemplate
						user={constructState.construct?.createdBy?.uniqueId}
						fieldName="Created By"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<DateDetailPageTemplate
						date={constructState.construct?.dateCreated}
						fieldName="Date Created"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<UserDetailPageTemplate
						user={constructState.construct?.updatedBy?.uniqueId}
						fieldName="Updated By"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<DateDetailPageTemplate
						date={constructState.construct?.dateUpdated}
						fieldName="Date Updated"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						widgetColumnSize={widgetColumnSize}
						fieldName="Internal"
						formField={constructState.construct?.internal?.toString() || 'false'}
						additionalDataField={constructState.construct?.internal?.toString() || 'false'}
					/>

					<Divider />

					<DetailPageFieldWrapper
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						widgetColumnSize={widgetColumnSize}
						fieldName="Obsolete"
						formField={constructState.construct?.obsolete?.toString() || 'false'}
						additionalDataField={constructState.construct?.obsolete?.toString() || 'false'}
					/>
				</form>
			</ErrorBoundary>
		</>
	);
}
