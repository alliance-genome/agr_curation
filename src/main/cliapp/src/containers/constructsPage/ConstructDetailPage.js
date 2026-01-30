import { useRef, useEffect, useMemo } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ConstructService } from '../../service/ConstructService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { useConstructReducer } from './useConstructReducer';
import { IdentifierFormTemplate } from '../../components/Templates/IdentifierFormTemplate';
import { DataProviderFormTemplate } from '../../components/Templates/DataProviderFormTemplate';
import { UserFormTemplate } from '../../components/Templates/UserFormTemplate';
import { DateFormTemplate } from '../../components/Templates/DateFormTemplate';
import { Divider } from 'primereact/divider';
import { FormFieldWrapper } from '../../components/FormFieldWrapper';
import { SymbolForm } from './symbol/SymbolForm';
import { FullNameForm } from './fullName/FullNameForm';
import { SynonymsForm } from './synonyms/SynonymsForm';
import { SecondaryIdsForm } from './secondaryIds/SecondaryIdsForm';
import { ReferencesForm } from './references/ReferencesForm';
import { ConstructComponentsForm } from './components/ConstructComponentsForm';
import { GenomicAssociationsForm } from './genomicAssociations/GenomicAssociationsForm';

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

	const headerText = () => {
		let prefix = 'Construct: ';
		if (constructState.construct?.constructSymbol?.displayText && constructState.construct?.primaryExternalId) {
			return `${prefix} ${constructState.construct.constructSymbol.displayText} (${constructState.construct.primaryExternalId})`;
		}
		if (constructState.construct?.primaryExternalId) {
			return `${prefix} ${constructState.construct.primaryExternalId}`;
		}
		return 'Construct Detail Page';
	};

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={70} className="flex justify-content-start ml-5 py-3 ">
							<h1 dangerouslySetInnerHTML={{ __html: headerText() }} />
						</SplitterPanel>
						<SplitterPanel size={30} className="flex justify-content-start py-3"></SplitterPanel>
					</Splitter>
				</StickyHeader>
				<form className="mt-8">
					<IdentifierFormTemplate
						identifier={constructState.construct?.primaryExternalId}
						label="Primary External ID"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						showAdditionalData={false}
					/>

					<Divider />

					<IdentifierFormTemplate
						identifier={constructState.construct?.modInternalId}
						label="MOD Internal ID"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						showAdditionalData={false}
					/>

					<Divider />

					<IdentifierFormTemplate
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

					<ConstructComponentsForm state={constructState} />

					<Divider />

					<GenomicAssociationsForm state={constructState} />

					<Divider />

					<DataProviderFormTemplate
						dataProvider={constructState.construct?.dataProvider?.abbreviation}
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<UserFormTemplate
						user={constructState.construct?.createdBy?.uniqueId}
						fieldName="Created By"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<DateFormTemplate
						date={constructState.construct?.dateCreated}
						fieldName="Date Created"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<UserFormTemplate
						user={constructState.construct?.updatedBy?.uniqueId}
						fieldName="Updated By"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<DateFormTemplate
						date={constructState.construct?.dateUpdated}
						fieldName="Date Updated"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>

					<Divider />

					<FormFieldWrapper
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
						widgetColumnSize={widgetColumnSize}
						fieldName="Internal"
						formField={constructState.construct?.internal?.toString() || 'false'}
						additionalDataField={constructState.construct?.internal?.toString() || 'false'}
					/>

					<Divider />

					<FormFieldWrapper
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
