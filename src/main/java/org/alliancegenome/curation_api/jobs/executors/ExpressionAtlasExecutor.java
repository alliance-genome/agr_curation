package org.alliancegenome.curation_api.jobs.executors;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.alliancegenome.curation_api.services.ResourceDescriptorService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JBossLog
@ApplicationScoped
public class ExpressionAtlasExecutor extends LoadFileExecutor {

	@Inject
	AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject
	DataProviderService service;
	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;
	@Inject
	ResourceDescriptorService resourceDescriptorService;
	@Inject
	OrganizationService organizationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) throws IOException {

		String url = ((BulkURLLoad) bulkLoadFileHistory.getBulkLoad()).getBulkloadUrl();

		XmlMapper mapper = new XmlMapper();
		URL src = new URL(url);
		Urlset urlset = mapper.readValue(src, Urlset.class);
		List<String> accessionUrlList = urlset.url.stream().map(UrlElement::getLoc).toList();
		List<String> accessions = accessionUrlList.stream().map(sUrl -> sUrl.substring(sUrl.lastIndexOf("/") + 1)).toList();
/*
		String loc = accessionUrlList.get(0);
		String defaultUrlTemplate = loc.substring(0, loc.lastIndexOf("/"));
*/

		String name = bulkLoadFileHistory.getBulkLoad().getName();
		String dataProviderName = name.substring(0, name.indexOf(" "));

		Organization organization = organizationService.getByAbbr(dataProviderName).getEntity();
		ResourceDescriptorPage page = resourceDescriptorPageService.getPageForResourceDescriptor("ENSEMBL", "expression_atlas");

		List<Long> dataProviderIdsBefore = new ArrayList<>();
		dataProviderIdsBefore.addAll(service.getDataProviderMap(organization, page).values().stream().map(DataProvider::getId).toList());
		dataProviderIdsBefore.removeIf(Objects::isNull);

		List<Long> dataProviderIdsLoaded = new ArrayList<>();
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess(name, accessions.size());
		accessions.forEach(accession -> {
			CrossReference reference = new CrossReference();
			reference.setReferencedCurie(accession);
			reference.setResourceDescriptorPage(page);
			DataProvider provider = new DataProvider();
			provider.setSourceOrganization(organization);
			provider.setCrossReference(reference);
			dataProviderIdsLoaded.add(service.upsert(provider).getEntity().getId());
			ph.progressProcess();
		});
		runCleanup(service, bulkLoadFileHistory, dataProviderName, dataProviderIdsBefore, dataProviderIdsLoaded, "Atlas Load Type");
		ph.finishProcess();
		bulkLoadFileHistory.setCount(accessions.size());
		updateHistory(bulkLoadFileHistory);

		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}

class Urlset {
	@JacksonXmlElementWrapper(useWrapping = false)
	public List<UrlElement> url = new ArrayList<>();
}

class UrlElement {
	public String loc;
	public String changefreq;

	public String getLoc() {
		return loc;
	}
}
