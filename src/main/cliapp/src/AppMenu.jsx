import React, { useRef, useState } from 'react';
import { NavLink } from 'react-router-dom';
import { CSSTransition } from 'react-transition-group';
import classNames from 'classnames';
import { Ripple } from 'primereact/ripple';
import { Badge } from 'primereact/badge';

const AppSubmenu = (props) => {
	const [activeIndex, setActiveIndex] = useState(null);

	//CSSTransition React 19 work around
	const submenuRefs = useRef({});

	const onMenuItemClick = (event, item, index) => {
		//avoid processing disabled items
		if (item.disabled) {
			event.preventDefault();
			return true;
		}

		//execute command
		if (item.command) {
			item.command({ originalEvent: event, item: item });
		}

		// If this is a leaf node (has 'to' or 'url'), don't toggle parent state
		if (item.to || item.url) {
			// This is a link - don't change activeIndex, just call parent handler
			event.stopPropagation(); // Prevent bubbling to parent menu items
			if (props.onMenuItemClick) {
				props.onMenuItemClick({
					originalEvent: event,
					item: item,
				});
			}
			return; // Exit early - don't toggle activeIndex
		}

		// Only toggle activeIndex for parent items (items with children)
		if (item.items && item.items.length > 0) {
			if (index === activeIndex) setActiveIndex(null);
			else setActiveIndex(index);
		}

		if (props.onMenuItemClick) {
			props.onMenuItemClick({
				originalEvent: event,
				item: item,
			});
		}
	};

	const onKeyDown = (event) => {
		if (event.code === 'Enter' || event.code === 'Space') {
			event.preventDefault();
			event.target.click();
		}
	};

	const renderLinkContent = (item) => {
		let submenuIcon = item.items && <i className="pi pi-fw pi-angle-down menuitem-toggle-icon"></i>;
		let badge = item.badge && <Badge value={item.badge} />;

		return (
			<>
				<i className={item.icon}></i>
				<span>{item.label}</span>
				{submenuIcon}
				{badge}
				<Ripple />
			</>
		);
	};

	const renderLink = (item, i) => {
		let content = renderLinkContent(item);

		if (item.to) {
			return (
				<NavLink
					aria-label={item.label}
					onKeyDown={onKeyDown}
					role="menuitem"
					className={({ isActive }) => (isActive ? 'p-ripple router-link-active router-link-exact-active' : 'p-ripple')}
					to={item.to}
					onClick={(e) => onMenuItemClick(e, item, i)}
					end
					target={item.target}
				>
					{content}
				</NavLink>
			);
		} else {
			return (
				<a
					tabIndex="0"
					aria-label={item.label}
					onKeyDown={onKeyDown}
					role="menuitem"
					href={item.url}
					className="p-ripple"
					onClick={(e) => onMenuItemClick(e, item, i)}
					target={item.target}
				>
					{content}
				</a>
			);
		}
	};

	let items =
		props.items &&
		props.items.map((item, i) => {
			let active = activeIndex === i;

			if (!submenuRefs.current[i]) {
				submenuRefs.current[i] = React.createRef();
			}

			const itemRef = submenuRefs.current[i];

			let styleClass = classNames(item.badgeStyleClass, {
				'layout-menuitem-category': props.root,
				'active-menuitem': active && !item.to,
			});

			if (props.root) {
				return (
					<li className={styleClass} key={i} role="none">
						{props.root === true && (
							<>
								<div className="layout-menuitem-root-text" aria-label={item.label}>
									{item.label}
								</div>
								<AppSubmenu items={item.items} onMenuItemClick={props.onMenuItemClick} />
							</>
						)}
					</li>
				);
			} else {
				return (
					<li className={styleClass} key={i} role="none">
						{renderLink(item, i)}
						<CSSTransition
							classNames="layout-submenu-wrapper"
							timeout={{ enter: 1000, exit: 450 }}
							in={active}
							unmountOnExit
							nodeRef={itemRef}
						>
							<div ref={itemRef}>
								<AppSubmenu items={item.items} onMenuItemClick={props.onMenuItemClick} />
							</div>
						</CSSTransition>
					</li>
				);
			}
		});

	return items ? (
		<ul className={props.className} role="menu">
			{items}
		</ul>
	) : null;
};

export const AppMenu = (props) => {
	return (
		<div className="layout-menu-container">
			<AppSubmenu
				items={props.model}
				className="layout-menu"
				onMenuItemClick={props.onMenuItemClick}
				root={true}
				role="menu"
			/>
		</div>
	);
};
