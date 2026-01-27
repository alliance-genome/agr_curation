import { useRef, useEffect } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { ProgressSpinner } from 'primereact/progressspinner';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery } from '@tanstack/react-query';
import { ConstructService } from '../../service/ConstructService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { useConstructReducer } from './useConstructReducer';
import { IdentifierFormTemplate } from '../../components/Templates/IdentifierFormTemplate';

export default function ConstructDetailPage() {
	const { identifier } = useParams();
	const { constructState, constructDispatch } = useConstructReducer();
	const constructService = new ConstructService();
	const toastSuccess = useRef(null);
	const toastError = useRef(null);

	const labelColumnSize = 'col-3';
	const widgetColumnSize = 'col-4';
	const fieldDetailsColumnSize = 'col-5';

	console.log('identifier', identifier);
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
						<SplitterPanel size={30} className="flex justify-content-start py-3">
							{/* <Button label="Save" icon="pi pi-check" className="p-button-text" size="large" onClick={handleSubmit} /> */}
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<form className="mt-8">
					<IdentifierFormTemplate
						identifier={constructState.construct?.primaryExternalId}
						label="Primary External ID"
						widgetColumnSize={widgetColumnSize}
						labelColumnSize={labelColumnSize}
						fieldDetailsColumnSize={fieldDetailsColumnSize}
					/>
				</form>
			</ErrorBoundary>
		</>
	);
}
