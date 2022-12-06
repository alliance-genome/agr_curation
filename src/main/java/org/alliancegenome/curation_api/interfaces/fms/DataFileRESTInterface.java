package org.alliancegenome.curation_api.interfaces.fms;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.alliancegenome.curation_api.model.fms.DataFile;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/datafile")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "DataFile Endpoints")
public interface DataFileRESTInterface {

	@GET
	@Path("/{id}")
	public DataFile get(@Parameter(in = ParameterIn.PATH, name = "id", description = "Long Id or md5Sum", required = true, schema = @Schema(type = SchemaType.STRING)) @PathParam("id") String id);

	@GET
	@Path("/all")
	public List<DataFile> getDataFiles();

	@GET
	@Path("/by/{dataType}")
	public List<DataFile> getDataTypeFiles(@PathParam("dataType") String dataType,
		@DefaultValue("false") @Parameter(in = ParameterIn.QUERY, name = "latest", description = "Latest File or All", required = false, schema = @Schema(type = SchemaType.BOOLEAN)) @QueryParam("latest") Boolean latest);

	@GET
	@Path("/by/release/{releaseVersion}")
	public List<DataFile> getDataFilesByRelease(@PathParam("releaseVersion") String releaseVersion,
		@DefaultValue("false") @Parameter(in = ParameterIn.QUERY, name = "latest", description = "Latest File or All", required = false, schema = @Schema(type = SchemaType.BOOLEAN)) @QueryParam("latest") Boolean latest);

	@GET
	@Path("/by/{dataType}/{dataSubtype}")
	@Operation(summary = "Get list of DataFile's", description = "Get list of DataFile's")
	public List<DataFile> getDataTypeSubTypeFiles(
		@Parameter(in = ParameterIn.PATH, name = "dataType", description = "Data Type Name", required = true, schema = @Schema(type = SchemaType.STRING)) @PathParam("dataType") String dataType,
		@Parameter(in = ParameterIn.PATH, name = "dataSubtype", description = "Data Sub Type Name", required = true, schema = @Schema(type = SchemaType.STRING)) @PathParam("dataSubtype") String dataSubType,
		@DefaultValue("false") @Parameter(in = ParameterIn.QUERY, name = "latest", description = "Latest File or All", required = false, schema = @Schema(type = SchemaType.BOOLEAN)) @QueryParam("latest") Boolean latest);

	@GET
	@Path("/by/{releaseVersion}/{dataType}/{dataSubtype}")
	@Operation(summary = "Get list of DataFile's", description = "Get list of DataFile's")
	public List<DataFile> getReleaseDataTypeSubTypeFiles(
		@Parameter(in = ParameterIn.PATH, name = "releaseVersion", description = "Release Version Name", required = true, schema = @Schema(type = SchemaType.STRING)) @PathParam("releaseVersion") String releaseVersion,
		@Parameter(in = ParameterIn.PATH, name = "dataType", description = "Data Type Name", required = true, schema = @Schema(type = SchemaType.STRING)) @PathParam("dataType") String dataType,
		@Parameter(in = ParameterIn.PATH, name = "dataSubtype", description = "Data Sub Type Name", required = true, schema = @Schema(type = SchemaType.STRING)) @PathParam("dataSubtype") String dataSubType,
		@DefaultValue("false") @Parameter(in = ParameterIn.QUERY, name = "latest", description = "Latest File or All", required = false, schema = @Schema(type = SchemaType.BOOLEAN)) @QueryParam("latest") Boolean latest);
}
