import { rest } from 'msw'
import { server } from './server';

export const setupSearchHandler = (override = {}) => {
	server.use(
		rest.post("*/api/:endpoint/search", (req, res, ctx) => {
			return res(
				ctx.status(200),
				ctx.json(
					{...override}
				)
			)
		}),

	)
}

export const setupFindHandler = () => {
	server.use(
		rest.post("*/api/:endpoint/find", (req, res, ctx) => {
			return res(ctx.status(200))
		}),
	)

}

export const setupSettingsHandler = () => {
	server.use(
		rest.get("*/api/personsettings/:settingsKey", (req, res, ctx) => {
			return res(ctx.status(200))
		}),
	)
}

export const setupSaveSettingsHandler = () => {
	server.use(
		rest.put("*/api/personsettings/:settingsKey", (req, res, ctx) => {
			return res(ctx.status(200))
		}),
	)
}

export const setupSiteSummaryHandler = (override = {}) => {
	server.use(
		rest.get("*/api/system/sitesummary", (req, res, ctx) => {
			return res(
				ctx.status(200),
				ctx.json(
					{...override}
				)
			)
		}),
	)
}
