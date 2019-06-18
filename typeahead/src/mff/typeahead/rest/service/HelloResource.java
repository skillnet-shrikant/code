package mff.typeahead.rest.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@Path("/")
@Produces("text/plain")
public class HelloResource {
@GET
public String hello() {
	return "Typeahead Restful Service";
}
}
