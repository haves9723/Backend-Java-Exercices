package be.vinci.pae.api;

import java.util.List;

import be.vinci.pae.domain.Film;
import be.vinci.pae.services.DataServiceFilmCollection;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Singleton
@Path("/films")
public class FilmResource {	

    @POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Film create(Film film) {
		if (film == null || film.getTitle() == null || film.getTitle().isEmpty())
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity("Lacks of mandatory info").type("text/plain").build());
		DataServiceFilmCollection.addFilm(film);

		return film;
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Film getFilm(@PathParam("id") int id) {
		if (id == 0)
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("Lacks of mandatory id info")
					.type("text/plain").build());
		Film filmFound = DataServiceFilmCollection.getFilm(id);

		if (filmFound == null)
			throw new WebApplicationException(Response.status(Status.NOT_FOUND)
					.entity("Ressource with id = " + id + " could not be found").type("text/plain").build());

		return filmFound;
	}

	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Film updateFilm(Film film, @PathParam("id") int id) {

		if (film == null || film.getTitle() == null || film.getTitle().isEmpty())
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity("Lacks of mandatory info").type("text/plain").build());

		film.setId(id);
		Film updatedFilm = DataServiceFilmCollection.updateFilm(film);

		if (updatedFilm == null)
			throw new WebApplicationException(Response.status(Status.NOT_FOUND)
					.entity("Ressource with id = " + id + " could not be found").type("text/plain").build());

		return updatedFilm;
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Film deleteFilm(@PathParam("id") int id) {
		if (id == 0)
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("Lacks of mandatory id info")
					.type("text/plain").build());

		Film deletedFilm = DataServiceFilmCollection.deleteFilm(id);

		if (deletedFilm == null)
			throw new WebApplicationException(Response.status(Status.NOT_FOUND)
					.entity("Ressource with id = " + id + " could not be found").type("text/plain").build());

		return deletedFilm;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Film> getAllFilms(@DefaultValue("-1") @QueryParam("minimum-duration") int minimumDuration) {
		if (minimumDuration != -1)
			return DataServiceFilmCollection.getFilms(minimumDuration);
		return DataServiceFilmCollection.getFilms();

	}

}
