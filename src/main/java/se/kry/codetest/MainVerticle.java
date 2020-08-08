package se.kry.codetest;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Class which holds the rest endpoints, the poller also initiated by this class
 *
 */
public class MainVerticle extends AbstractVerticle {

	private DBManager dbManager;
	private BackgroundPoller poller = new BackgroundPoller();
	List<URL> services = new ArrayList<URL>();

	/**
	 * The overrided method which load the settings
	 */
	@Override
	public void start(Future<Void> startFuture) {
		//initilization
		dbManager = new DBManager(vertx);
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		//schedule the poller
		vertx.setPeriodic(1000 * 60, timerId -> poller.pollServices(vertx));
		
		// configure the rest end points
		setRoutes(router);

		// create http server
		vertx.createHttpServer().requestHandler(router).listen(8080, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});
	}

	/**
	 * This method creates the rest endpoints (to list services, to delte service, to add service)
	 * @param router
	 */
	private void setRoutes(Router router) {
		
		router.route("/*").handler(StaticHandler.create());
		
		// end point to get all services
		router.get("/service").handler(req -> {

			// open connection
			dbManager.getClient().getConnection(conn -> {
				if (conn.failed()) {
					System.err.println(conn.cause().getMessage());
					return;
				}
				final SQLConnection connection = conn.result();
				// query for services
				connection.query("select * from services;", result -> {
					if (!result.failed()) {
						
						List<JsonArray> array = result.result().getResults();
						services.clear();
						// add service in an array
						for (JsonArray jsonArray : array) {
							Integer id = jsonArray.getInteger(0);
							String url = jsonArray.getString(1);
							String date = jsonArray.getString(2);
							String status = jsonArray.getString(3);
							services.add(new URL(id, url, date, status));
						}
					}
					// close connection
					connection.close(done -> {
						if (done.failed()) {
							throw new RuntimeException(done.cause());
						}
					});
				});
			});
			req.response().putHeader("content-type", "application/json")
					.end(new JsonArray(services).encode());
		});
		
		// rest endpoint to add a service in the database
		router.post("/service").handler(req -> {
			JsonObject jsonBody = req.getBodyAsJson();
			String url = jsonBody.getString("url");
			
			// call database manager to save service
			dbManager.addURL(url);
			req.response().putHeader("content-type", "text/plain").end("OK");
		});
		
		// rest endpoint to delete a service in the database
		router.post("/delete").handler(req -> {
			JsonObject jsonBody = req.getBodyAsJson();
			Integer id = jsonBody.getInteger("id");
			
			// call database manager to delete service
			dbManager.deleteURL(id);
			req.response().putHeader("content-type", "text/plain").end("OK");
		});
		
		
	}
}
