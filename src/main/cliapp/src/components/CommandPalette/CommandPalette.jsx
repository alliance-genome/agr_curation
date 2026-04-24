import { useState, useEffect, useRef, useMemo, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import Fuse from 'fuse.js';
import { flattenMenu } from '../../utils/flattenMenu';
import './CommandPalette.scss';

function highlightMatches(text, indices) {
	if (!indices || indices.length === 0) {
		return text;
	}

	const result = [];
	let lastIndex = 0;

	for (const [start, end] of indices) {
		if (start > lastIndex) {
			result.push(text.slice(lastIndex, start));
		}
		result.push(<mark key={start}>{text.slice(start, end + 1)}</mark>);
		lastIndex = end + 1;
	}

	if (lastIndex < text.length) {
		result.push(text.slice(lastIndex));
	}

	return result;
}

function getMatchIndices(fuseMatches, key) {
	if (!fuseMatches) return null;
	const match = fuseMatches.find((m) => m.key === key);
	return match ? match.indices : null;
}

export const CommandPalette = ({ menu, visible, onHide }) => {
	const [query, setQuery] = useState('');
	const [selectedIndex, setSelectedIndex] = useState(0);
	const inputRef = useRef(null);
	const resultsRef = useRef(null);
	const navigate = useNavigate();

	const items = useMemo(() => flattenMenu(menu), [menu]);

	const fuse = useMemo(
		() =>
			new Fuse(items, {
				keys: [
					{ name: 'label', weight: 0.7 },
					{ name: 'breadcrumb', weight: 0.3 },
				],
				includeMatches: true,
				threshold: 0.4,
				ignoreLocation: true,
				minMatchCharLength: 1,
			}),
		[items]
	);

	const results = useMemo(() => {
		if (!query.trim()) return [];
		return fuse.search(query, { limit: 25 });
	}, [fuse, query]);

	useEffect(() => {
		setSelectedIndex(0);
	}, [query]);

	useEffect(() => {
		if (visible) {
			setQuery('');
			setSelectedIndex(0);
		}
	}, [visible]);

	useEffect(() => {
		if (resultsRef.current) {
			const selected = resultsRef.current.querySelector('.command-palette-item.selected');
			if (selected) {
				selected.scrollIntoView({ block: 'nearest' });
			}
		}
	}, [selectedIndex]);

	const handleSelect = useCallback(
		(item, newTab = false) => {
			onHide();
			if (newTab) {
				const href = item.to ? `${window.location.pathname}#${item.to}` : item.url;
				window.open(href, '_blank');
			} else if (item.to) {
				navigate(item.to);
			} else if (item.url) {
				window.open(item.url, item.target || '_blank');
			}
		},
		[navigate, onHide]
	);

	const handleKeyDown = useCallback(
		(e) => {
			if (e.key === 'Escape') {
				e.preventDefault();
				onHide();
			} else if (e.key === 'ArrowDown' && results.length > 0) {
				e.preventDefault();
				setSelectedIndex((prev) => (prev < results.length - 1 ? prev + 1 : 0));
			} else if (e.key === 'ArrowUp' && results.length > 0) {
				e.preventDefault();
				setSelectedIndex((prev) => (prev > 0 ? prev - 1 : results.length - 1));
			} else if (e.key === 'Enter' && results.length > 0 && selectedIndex < results.length) {
				e.preventDefault();
				handleSelect(results[selectedIndex].item, e.ctrlKey || e.metaKey);
			}
		},
		[results, selectedIndex, handleSelect, onHide]
	);

	const handleShow = () => {
		setTimeout(() => inputRef.current?.focus(), 50);
	};

	return (
		<Dialog
			visible={visible}
			onHide={onHide}
			modal
			dismissableMask
			closable={false}
			showHeader={false}
			className="command-palette-dialog"
			contentClassName="command-palette-content"
			position="top"
			onShow={handleShow}
			aria-label="Command palette search"
		>
			<div className="command-palette-input-wrapper">
				<i className="pi pi-search" />
				<InputText
					ref={inputRef}
					value={query}
					onChange={(e) => setQuery(e.target.value)}
					onKeyDown={handleKeyDown}
					placeholder="Search pages..."
					className="command-palette-input"
					role="combobox"
					aria-expanded={results.length > 0}
					aria-controls="command-palette-results"
					aria-activedescendant={results.length > 0 ? `command-palette-item-${selectedIndex}` : undefined}
				/>
			</div>
			<div className="command-palette-results" ref={resultsRef} id="command-palette-results" role="listbox">
				{!query.trim() && <div className="command-palette-placeholder">Start typing to search pages...</div>}
				{query.trim() && results.length === 0 && <div className="command-palette-empty">No pages found</div>}
				{results.map((result, index) => {
					const labelIndices = getMatchIndices(result.matches, 'label');
					const breadcrumbIndices = getMatchIndices(result.matches, 'breadcrumb');
					const isExternal = !!result.item.url;

					return (
						<div
							key={result.item.to || result.item.url}
							id={`command-palette-item-${index}`}
							role="option"
							aria-selected={index === selectedIndex}
							className={`command-palette-item${index === selectedIndex ? ' selected' : ''}`}
							onClick={(e) => handleSelect(result.item, e.ctrlKey || e.metaKey)}
							onMouseEnter={() => setSelectedIndex(index)}
						>
							<i className={`command-palette-item-icon ${result.item.icon || 'pi pi-fw pi-file'}`} />
							<div className="command-palette-item-text">
								<div className="command-palette-item-label">{highlightMatches(result.item.label, labelIndices)}</div>
								{result.item.breadcrumb && (
									<div className="command-palette-item-breadcrumb">
										{highlightMatches(result.item.breadcrumb, breadcrumbIndices)}
									</div>
								)}
							</div>
							{isExternal && <i className="pi pi-external-link command-palette-external-icon" />}
						</div>
					);
				})}
			</div>
		</Dialog>
	);
};
