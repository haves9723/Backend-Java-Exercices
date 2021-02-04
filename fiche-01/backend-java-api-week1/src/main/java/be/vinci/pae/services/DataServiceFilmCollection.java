package be.vinci.pae.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import be.vinci.pae.domain.Film;

public class DataServiceFilmCollection {
	private static final String DB_FILE_PATH = "db.json";
	private static final String COLLECTION_NAME = "films";
	private final static ObjectMapper jsonMapper = new ObjectMapper();

	private static List<Film> films ;
	static {
		films = loadDataFromFile();
	}	

	public static Film getFilm(int id) {
		return films.stream().filter(item -> item.getId() == id).findAny().orElse(null);
	}

	public static List<Film> getFilms() {
		return films;
	}

	public static List<Film> getFilms(int minimumDuration) {
		return films.stream().filter(item -> item.getDuration() >= minimumDuration).collect(Collectors.toList());
	}

	public static Film addFilm(Film film) {
		film.setId(nexFilmtId());
		// escape dangerous chars to protect against XSS attacks
		film.setTitle(StringEscapeUtils.escapeHtml4(film.getTitle()));
		film.setLink(StringEscapeUtils.escapeHtml4(film.getLink()));
		films.add(film);
		saveDataToFile();
		return film;
	}

	public static int nexFilmtId() {
		if (films.size() == 0)
			return 1;
		return films.get(films.size() - 1).getId() + 1;
	}

	public static Film deleteFilm(int id) {
		if (films.size() == 0 | id == 0)
			return null;
		Film filmToDelete = getFilm(id);
		if (filmToDelete == null)
			return null;
		int index = films.indexOf(filmToDelete);
		films.remove(index);
		saveDataToFile();
		return filmToDelete;
	}

	public static Film updateFilm(Film film) {
		if (films.size() == 0 | film == null)
			return null;
		Film filmToUpdate = getFilm(film.getId());
		if (filmToUpdate == null)
			return null;
		// escape dangerous chars to protect against XSS attacks
		film.setTitle(StringEscapeUtils.escapeHtml4(film.getTitle()));
		film.setLink(StringEscapeUtils.escapeHtml4(film.getLink()));
		// update the data structure
		int index = films.indexOf(filmToUpdate);
		films.set(index, film);
		saveDataToFile();
		return film;
	}

	private static List<Film> loadDataFromFile() {
		try {
			JsonNode node = jsonMapper.readTree(Paths.get(DB_FILE_PATH).toFile());
			JsonNode collection = node.get(COLLECTION_NAME);
			if (collection == null)
				return new ArrayList<Film>();
			return jsonMapper.readerForListOf(Film.class).readValue(node.get(COLLECTION_NAME));

		} catch (FileNotFoundException e) {
			return new ArrayList<Film>();
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<Film>();
		}
	}

	private static void saveDataToFile() {
		try {

			// get all collections
			Path pathToDb = Paths.get(DB_FILE_PATH);
			if (!Files.exists(pathToDb)) {
				// write a new collection to the db file
				ObjectNode newCollection = jsonMapper.createObjectNode().putPOJO(COLLECTION_NAME, films);
				jsonMapper.writeValue(pathToDb.toFile(), newCollection);
				return;

			}
			// get all collections
			JsonNode allCollections = jsonMapper.readTree(pathToDb.toFile());

			if (allCollections.has(COLLECTION_NAME)) {// remove current collection
				((ObjectNode) allCollections).remove(COLLECTION_NAME);
			}

			// create a new JsonNode and add it to allCollections
			String currentCollectionAsString = jsonMapper.writeValueAsString(films);
			JsonNode updatedCollection = jsonMapper.readTree(currentCollectionAsString);
			((ObjectNode) allCollections).putPOJO(COLLECTION_NAME, updatedCollection);

			// write to the db file
			jsonMapper.writeValue(pathToDb.toFile(), allCollections);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
