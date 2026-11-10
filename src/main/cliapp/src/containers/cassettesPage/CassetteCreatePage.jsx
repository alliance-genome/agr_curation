import { useEffect, useMemo, useRef, useState } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { useNavigate } from 'react-router-dom';
import { useQueryClient } from '@tanstack/react-query';
import { CassetteService } from '../../service/CassetteService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { StickyFooter } from '../../components/StickyFooter';
import { LoadingOverlay } from '../../components/LoadingOverlay';
import { SaveProgressOverlay } from '../../components/SaveProgressOverlay';
import { useSaveProgress } from '../../hooks/useSaveProgress';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { errorMessageOf, getIdentifier } from '../../utils/utils';
import { processErrors } from '../affectedGenomicModelPage/utils';
import {
	FREE_TEXT_COMPONENTS,
	GENOMIC_ENTITY_COMPONENTS,
	TRANSGENIC_TOOL_COMPONENTS,
	STR_COMPONENTS,
} from './components/componentConfigs';
import { useCassetteReducer } from './detail/useCassetteReducer';
import { useCassetteComponentList } from './detail/useCassetteComponentList';
import {
	NEW_CASSETTE,
	buildCassetteCreatePayload,
	buildCassettePayload,
	buildDuplicateCassette,
	copyComponentRows,
} from './detail/utils';
import { CassetteForm, CASSETTE_CREATE_TOGGLEABLE_FIELDS } from './CassetteForm';
import { useDuplicateCassetteSource } from './useDuplicateCassetteSource';
import { mapRowErrors } from './components/utils';

const CASSETTE_STEP = 'cassette';

/**
 * The page that creates a cassette (SCRUM-6748), opened in a new tab from the Cassettes table and the Cassette detail
 * page, blank or, through Clone, filled from another cassette. Its fields are edited as on the detail page.
 *
 * Saving creates the cassette with its free text components, which mints its AGRKB curie. The unique ID is built from
 * the symbol, the name and the free text components, so a cassette that would duplicate another is refused before
 * anything is saved. Each other component list the curator filled in is then written through the list's own endpoint,
 * as the detail page does. A list that fails leaves the cassette created: the page stays, and saving again updates
 * that cassette and writes only the lists not yet saved.
 */
export default function CassetteCreatePage() {
	const navigate = useNavigate();
	const queryClient = useQueryClient();
	const cassetteService = useMemo(() => new CassetteService(), []);
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const { cassetteState, cassetteDispatch } = useCassetteReducer();
	const cassette = cassetteState.cassette;
	const freeTextComponents = useCassetteComponentList(undefined, FREE_TEXT_COMPONENTS);
	const genomicEntityComponents = useCassetteComponentList(undefined, GENOMIC_ENTITY_COMPONENTS);
	const transgenicToolComponents = useCassetteComponentList(undefined, TRANSGENIC_TOOL_COMPONENTS);
	const strComponents = useCassetteComponentList(undefined, STR_COMPONENTS);
	const componentSections = [
		{ config: FREE_TEXT_COMPONENTS, components: freeTextComponents },
		{ config: GENOMIC_ENTITY_COMPONENTS, components: genomicEntityComponents },
		{ config: TRANSGENIC_TOOL_COMPONENTS, components: transgenicToolComponents },
		{ config: STR_COMPONENTS, components: strComponents },
	];
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'CassetteCreate',
		CASSETTE_CREATE_TOGGLEABLE_FIELDS
	);
	const saveProgress = useSaveProgress();
	// Set once Save & Close has saved everything, while the page shows that before moving on to the cassette.
	const [isLeaving, setIsLeaving] = useState(false);
	// Held so that saving again after a component list failed updates the cassette that already exists, rather than
	// creating a second one.
	const createdCassette = useRef(null);
	const { isLoading: duplicateSourceIsLoading } = useDuplicateCassetteSource({
		cassetteDispatch,
		componentSections,
		toastError,
	});

	useEffect(() => {
		cassetteDispatch({ type: 'SET', value: NEW_CASSETTE });
	}, [cassetteDispatch]);

	const showSaveError = (error) => {
		saveProgress.settle(CASSETTE_STEP, { isSuccess: false });
		saveProgress.skipRemaining();
		saveProgress.finish();
		const data = error?.response?.data;
		const messages = [{ summary: 'Cassette not saved: ', detail: errorMessageOf(error) }];
		// The form has no Unique ID field to show this against: it is built from the symbol, the name and the free text
		// components
		if (data?.errorMessages?.uniqueId) {
			messages.push({
				summary: 'Unique ID: ',
				detail: 'A cassette with this symbol, name and free text components already exists',
			});
		}
		toastError.current.show(messages.map((message) => ({ life: 10000, severity: 'error', sticky: false, ...message })));

		try {
			processErrors(data, cassetteDispatch, cassette);
		} catch (processingError) {
			console.error(processingError);
		}
		// A create sends the free text components with the cassette, so their rows' errors come back with it
		freeTextComponents.setErrorMessages(mapRowErrors(data, FREE_TEXT_COMPONENTS.field, freeTextComponents.rows));
	};

	// Starts the form over for the next cassette from the one just saved, as Clone would copy it, so it can be entered
	// as a small change to it.
	const carryOver = (saved) => {
		cassetteDispatch({ type: 'SET', value: buildDuplicateCassette(saved) });
		componentSections.forEach(({ components }) => components.startOver(copyComponentRows(components.rows)));
	};

	const handleSubmit = async (event, closeAfterSubmit) => {
		event.preventDefault();
		cassetteDispatch({ type: 'SUBMIT' });

		const isNew = !createdCassette.current;
		// A new cassette takes its free text components with it; saving again writes them through their endpoint
		const editedSections = componentSections.filter(
			({ config, components }) => components.isDirty && !(isNew && config === FREE_TEXT_COMPONENTS)
		);
		saveProgress.begin([
			{ key: CASSETTE_STEP, label: 'Cassette' },
			...editedSections.map(({ config }) => ({ key: config.list, label: config.title })),
		]);
		saveProgress.start(CASSETTE_STEP);

		let saved;
		try {
			const result = isNew
				? await cassetteService.createCassette(buildCassetteCreatePayload(cassette, freeTextComponents.rows))
				: await cassetteService.saveCassette(buildCassettePayload(cassette));
			saved = result?.data?.entity;
		} catch (error) {
			showSaveError(error);
			return;
		}
		createdCassette.current = saved;
		saveProgress.settle(CASSETTE_STEP, { isSuccess: true });
		if (isNew) {
			freeTextComponents.setErrorMessages({});
			await freeTextComponents.showSaved(saved.cassetteComponents);
			queryClient.invalidateQueries({ queryKey: ['Cassettes'] });
			toastSuccess.current.show({
				severity: 'success',
				summary: 'Successful',
				detail: `Cassette ${getIdentifier(saved)} created`,
			});
		}

		const failures = [];
		for (const { config, components } of editedSections) {
			saveProgress.start(config.list);
			const outcome = await components.save(saved.id);
			saveProgress.settle(config.list, outcome);
			if (!outcome.isSuccess) failures.push({ config, outcome });
		}

		if (failures.length > 0) {
			saveProgress.finish();
			// The form takes on the saved cassette, so its rows carry the ids that saving again needs
			cassetteDispatch({ type: 'SET', value: saved });
			toastError.current.show(
				failures.map(({ config, outcome }) => ({
					life: 10000,
					severity: 'error',
					summary: `${config.title} not saved: `,
					detail: `${outcome.message}. Cassette ${getIdentifier(saved)} was created - fix the rows and save again, or finish them on its detail page.`,
					sticky: false,
				}))
			);
			return;
		}

		createdCassette.current = null;
		if (closeAfterSubmit) {
			// The curator sees everything saved before the page moves on; saving again meanwhile is not offered.
			setIsLeaving(true);
			if (await saveProgress.finish()) navigate(`/cassette/${getIdentifier(saved)}`);
		} else {
			carryOver(saved);
			saveProgress.finish();
		}
	};

	const handleClear = (event) => {
		event.preventDefault();
		cassetteDispatch({ type: 'SET', value: NEW_CASSETTE });
		componentSections.forEach(({ components }) => components.reset());
		createdCassette.current = null;
	};

	const handleCancel = (event) => {
		event.preventDefault();
		navigate('/cassettes');
	};

	const isSaveDisabled = saveProgress.isSaving || isLeaving;

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<SaveProgressOverlay isOpen={saveProgress.isOpen} steps={saveProgress.steps} />
			<LoadingOverlay isLoading={duplicateSourceIsLoading} message="Loading cassette to clone..." />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={45} className="flex justify-content-start ml-5 py-3 ">
							<h1>Add Cassette</h1>
						</SplitterPanel>
						<SplitterPanel size={55} className="flex align-items-center justify-content-end gap-2 pr-5 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={CASSETTE_CREATE_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<div className="pb-8">
					<CassetteForm
						state={cassetteState}
						dispatch={cassetteDispatch}
						componentSections={componentSections}
						isVisible={isVisible}
						mode="create"
					/>
				</div>
				<StickyFooter>
					<Splitter className="bg-primary-reverse border-none" gutterSize={0}>
						<SplitterPanel size={50} className="flex justify-content-start ml-5 py-3">
							<Button label="Clear" icon="pi pi-undo" className="p-button-text" onClick={handleClear} />
							<Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={handleCancel} />
						</SplitterPanel>
						<SplitterPanel size={50} className="flex justify-content-end mr-5 py-3">
							<Button
								label="Save & Close"
								icon="pi pi-check"
								severity="success"
								onClick={(event) => handleSubmit(event, true)}
								disabled={isSaveDisabled}
							/>
							<Button
								label="Save & Add Another"
								icon="pi pi-check"
								severity="success"
								className="ml-2"
								onClick={(event) => handleSubmit(event, false)}
								disabled={isSaveDisabled}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyFooter>
			</ErrorBoundary>
		</>
	);
}
