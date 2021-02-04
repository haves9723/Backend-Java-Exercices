package be.vinci.pae.main;

import java.io.IOException;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;



/**
 * Main class.
 *
 */
public class Main {
	
	public static final String BASE_URI = "http://localhost:8080/";

	/**
	 * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
	 * application.
	 * 
	 * @return Grizzly HTTP server.
	 */
	public static HttpServer startServer() {
		// Create a resource config that scans for JAX-RS resources and providers
		final ResourceConfig rc = new ResourceConfig().packages("be.vinci.pae.api")				
				.register(JacksonFeature.class)
				.property("jersey.config.server.wadl.disableWadl", true);

		// Create and start a new instance of grizzly http server
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {		
		// Start the server
		final HttpServer server = startServer();
		System.out.println("Jersey app started at " + BASE_URI);
		// Listen to key press and shutdown server
		System.out.println("Hit enter to stop it...");
		System.in.read();
		server.shutdownNow();
	}

}
