import React from 'react'
import { EllipsisTableCell } from './EllipsisTableCell';

export const synonymScopeTemplate = (entity) => {
	return <EllipsisTableCell>{entity.synonymScope?.name}</EllipsisTableCell>;
};

export const nameTypeTemplate = (entity) => {
	return <EllipsisTableCell>{entity.nameType?.name}</EllipsisTableCell>;
};

export const synonymUrlTemplate = (entity) => {
	return <EllipsisTableCell>{entity.synonymUrl}</EllipsisTableCell>;
};


export const displayTextTemplate = (entity) => {
	return <EllipsisTableCell>{entity.displayText}</EllipsisTableCell>;
};


export const formatTextTemplate = (entity) => {
	return <EllipsisTableCell>{entity.formatText}</EllipsisTableCell>;
};
