package se.kry.codetest;

import java.util.List;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLConnection;

/**
 * This class is called by the scheduler. This class get the list of all services 
 * loop through the list and check the status of each service 
 *
 */
public class BackgroundPoller {

	@SuppressWarnings("deprecation")
	public void pollServices(Vertx vertx) {
		
		DBManager dbManager = new DBManager(vertx);
		
		// create HTTP client
		HttpClientOptions options = new HttpClientOptions().setKeepAlive(false);
		HttpClient client = vertx.createHttpClient(options);
		
		System.out.println("polling");
		
		// get database connection
		dbManager.getClient().getConnection(conn -> {
			if (conn.failed()) {
				System.err.println(conn.cause().getMessage());
				return;
			}
			
			// fetch services from database
			final SQLConnection connection = conn.result();
			
			connection.query("select * from services;", result -> {
				if (!result.failed()) {
					List<JsonArray> array = result.result().getResults();
					
					// interate the services list
					for (JsonArray jsonArray : array) {
						Integer id = jsonArray.getInteger(0);
						String url = jsonArray.getString(1);
						String date = jsonArray.getString(2);
						String status = jsonArray.getString(3);
						URL ur = new URL(id, url, date, status);
						
						// check the status of ther service
						client.get(ur.getUrl(), response -> {
							if(response.statusCode()!= 200)
								// if status is not OK, then set this to fails
								dbManager.update("FAIL", ur.getId());
							else 
								// if status is  OK, then set this to pass
								dbManager.update("PASS", ur.getId());
						});
					}
				}
				// close database connection
				connection.close(done -> {
					if (done.failed()) {
						throw new RuntimeException(done.cause());
					}
				});
			});
		});
	}
}
