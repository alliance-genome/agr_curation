import { useEffect, useMemo, useRef, useState } from 'react';
import { Toast } from 'primereact/toast';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Button } from 'primereact/button';
import { useNavigate } from 'react-router-dom';
import { useQueryClient } from '@tanstack/react-query';
import { TransgenicToolService } from '../../service/TransgenicToolService';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { StickyFooter } from '../../components/StickyFooter';
import { SaveProgressOverlay } from '../../components/SaveProgressOverlay';
import { useSaveProgress } from '../../hooks/useSaveProgress';
import { FormFieldVisibilityMenu, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { seedResourceDescriptors } from '../../components/crossReferences/utils';
import { errorMessageOf, getIdentifier } from '../../utils/utils';
import { processErrors } from '../affectedGenomicModelPage/utils';
import { useTransgenicToolReducer } from './detail/useTransgenicToolReducer';
import { useTransgenicToolAssociations } from './detail/useTransgenicToolAssociations';
import {
	NEW_TRANSGENIC_TOOL,
	buildDuplicateTransgenicTool,
	buildTransgenicToolCreatePayload,
	buildTransgenicToolPayload,
	copyToolAssociationRows,
} from './detail/utils';
import { TransgenicToolForm, TRANSGENIC_TOOL_CREATE_TOGGLEABLE_FIELDS } from './TransgenicToolForm';

const TOOL_STEP = 'transgenicTool';
const ASSOCIATIONS_STEP = 'transgenicToolAssociations';

// How the messages name a saved tool: by its identifier, or, when none could be minted, by its symbol or unique ID
const toolLabel = (tool) => getIdentifier(tool) || tool?.transgenicToolSymbol?.displayText || tool?.uniqueId;

const errorToast = (message) => ({ life: 10000, severity: 'error', sticky: false, ...message });

/**
 * The page that creates a transgenic tool (SCRUM-6691), opened in a new tab from the Transgenic Tools table and the
 * Transgenic Tool detail page. Its fields are edited as on the detail page. Save & Close opens the new tool's detail page.
 *
 * Saving creates the tool with its own fields and lists, which mints its AGRKB curie; the unique ID is built from the
 * symbol and the full name, so a tool that would duplicate another is refused before anything is saved. The tool
 * associations the curator added are then written through their own endpoint, as the detail page does. If they
 * fail, the tool stays created: the page stays, and saving again updates that tool and writes the associations.
 */
export default function TransgenicToolCreatePage() {
	const navigate = useNavigate();
	const queryClient = useQueryClient();
	const transgenicToolService = useMemo(() => new TransgenicToolService(), []);
	const toastSuccess = useRef(null);
	const toastError = useRef(null);
	const { transgenicToolState, transgenicToolDispatch } = useTransgenicToolReducer();
	const transgenicTool = transgenicToolState.transgenicTool;
	const associations = useTransgenicToolAssociations(undefined);
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'TransgenicToolCreate',
		TRANSGENIC_TOOL_CREATE_TOGGLEABLE_FIELDS
	);
	const saveProgress = useSaveProgress();
	// Set once Save & Close has saved everything, while the page shows that before moving on to the tool.
	const [isLeaving, setIsLeaving] = useState(false);
	// Held so that saving again after the associations failed updates the tool that already exists, rather than
	// creating a second one.
	const createdTransgenicTool = useRef(null);

	useEffect(() => {
		transgenicToolDispatch({ type: 'SET', value: NEW_TRANSGENIC_TOOL });
	}, [transgenicToolDispatch]);

	// Shows a tool in the form. Its cross references are first given their descriptors, so the page dropdown can
	// offer each descriptor's pages, as on the detail page.
	const showTransgenicTool = async (tool) => {
		const crossReferences = await seedResourceDescriptors(tool.crossReferences);
		transgenicToolDispatch({ type: 'SET', value: { ...tool, crossReferences } });
	};

	const showSaveError = (error) => {
		saveProgress.settle(TOOL_STEP, { isSuccess: false });
		saveProgress.skipRemaining();
		saveProgress.finish();
		const data = error?.response?.data;
		const messages = [{ summary: 'Transgenic Tool not saved: ', detail: errorMessageOf(error) }];
		// The form has no Unique ID field to show this against: it is built from the symbol and the full name
		if (data?.errorMessages?.uniqueId) {
			messages.push({
				summary: 'Unique ID: ',
				detail: 'A transgenic tool with this symbol and full name already exists',
			});
		}
		toastError.current.show(messages.map(errorToast));

		try {
			processErrors(data, transgenicToolDispatch, transgenicTool);
		} catch (processingError) {
			console.error(processingError);
		}
	};

	const handleSubmit = async (event, closeAfterSubmit) => {
		event.preventDefault();
		transgenicToolDispatch({ type: 'SUBMIT' });

		// An association to a tool typed rather than picked could only fail, so nothing is saved while one is held
		const refusal = associations.refuseTypedTools();
		if (refusal) {
			toastError.current.show(errorToast({ summary: 'Tool associations not saved: ', detail: refusal.message }));
			return;
		}

		const isNew = !createdTransgenicTool.current;
		const savesAssociations = associations.isDirty;
		saveProgress.begin([
			{ key: TOOL_STEP, label: 'Transgenic Tool' },
			...(savesAssociations ? [{ key: ASSOCIATIONS_STEP, label: 'Tool Associations' }] : []),
		]);
		saveProgress.start(TOOL_STEP);

		let saved;
		try {
			const result = isNew
				? await transgenicToolService.createTransgenicTool(buildTransgenicToolCreatePayload(transgenicTool))
				: await transgenicToolService.saveTransgenicTool(buildTransgenicToolPayload(transgenicTool));
			saved = result?.data?.entity;
		} catch (error) {
			showSaveError(error);
			return;
		}
		createdTransgenicTool.current = saved;
		saveProgress.settle(TOOL_STEP, { isSuccess: true });
		if (isNew) {
			queryClient.invalidateQueries({ queryKey: ['TransgenicTools'] });
			toastSuccess.current.show({
				severity: 'success',
				summary: 'Successful',
				detail: `Transgenic Tool ${toolLabel(saved)} created`,
			});
		}

		if (savesAssociations) {
			saveProgress.start(ASSOCIATIONS_STEP);
			const outcome = await associations.save(saved.id);
			saveProgress.settle(ASSOCIATIONS_STEP, outcome);
			if (!outcome.isSuccess) {
				saveProgress.finish();
				// The form takes on the saved tool, so its rows carry the ids that saving again needs
				await showTransgenicTool(saved);
				toastError.current.show(
					errorToast({
						summary: 'Tool associations not saved: ',
						detail: `${outcome.message}. Transgenic Tool ${toolLabel(saved)} was created - fix the rows and save again, or finish them on its detail page.`,
					})
				);
				return;
			}
		}

		createdTransgenicTool.current = null;
		if (closeAfterSubmit) {
			// The curator sees everything saved before the page moves on; saving again meanwhile is not offered.
			setIsLeaving(true);
			// As on the other create pages, Save & Close opens the new tool's detail page. A tool whose curie could not
			// be minted has no identifier to open it by, so the curator goes back to the table instead.
			const identifier = getIdentifier(saved);
			if (await saveProgress.finish()) navigate(identifier ? `/transgenic-tool/${identifier}` : '/transgenicTools');
		} else {
			// The form starts over for the next tool from the one just saved, so it can be entered as a small change to it
			await showTransgenicTool(buildDuplicateTransgenicTool(saved));
			associations.startOver(copyToolAssociationRows(associations.rows));
			saveProgress.finish();
		}
	};

	const handleClear = (event) => {
		event.preventDefault();
		transgenicToolDispatch({ type: 'SET', value: NEW_TRANSGENIC_TOOL });
		associations.reset();
		createdTransgenicTool.current = null;
	};

	const handleCancel = (event) => {
		event.preventDefault();
		navigate('/transgenicTools');
	};

	const isSaveDisabled = saveProgress.isSaving || isLeaving;

	return (
		<>
			<Toast ref={toastError} position="top-left" />
			<Toast ref={toastSuccess} position="top-right" />
			<SaveProgressOverlay isOpen={saveProgress.isOpen} steps={saveProgress.steps} />
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={45} className="flex justify-content-start ml-5 py-3 ">
							<h1>Add Transgenic Tool</h1>
						</SplitterPanel>
						<SplitterPanel size={55} className="flex align-items-center justify-content-end gap-2 pr-5 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={TRANSGENIC_TOOL_CREATE_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<div className="pb-8">
					<TransgenicToolForm
						state={transgenicToolState}
						dispatch={transgenicToolDispatch}
						associations={associations}
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
