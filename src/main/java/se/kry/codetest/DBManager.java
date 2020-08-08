package se.kry.codetest;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

/**
 * This class is responsible to perform database operations 
 *
 */
public class DBManager {

	/**
	 * This is the query to create table in database
	 */
	private static final String CREATE_TABLE_QUERY = "create table if not exists services(id integer primary key autoincrement, url varchar(5000), "
			+ "date_time timestamp," + " status varchar(125));";

	private final String DB_PATH = "poller.db";
	private final JDBCClient client;
	
	/**
	 * @param vertx
	 */
	public DBManager(Vertx vertx) {
		client = JDBCClient.createShared(vertx, new JsonObject()
				.put("url", "jdbc:sqlite:" + DB_PATH)
				.put("driver_class", "org.sqlite.JDBC")
				.put("max_pool_size", 30));

	}

	/**
	 * This method is responsible to create the database table, if does not exist
	 */
	public void migrate() {
		execute(CREATE_TABLE_QUERY);
	}

	/**
	 * This method add the service in the database table
	 * @param url
	 */
	public void addURL(String url) {
		String qq = "insert into services (url, date_time, status) values('"+url+"',DATETIME(),'UNKNOWN');";
		execute(qq);
	}
	
	/**
	 * This method delete the service from the database tabe 
	 * @param id
	 */
	public void deleteURL(int id) {
		execute("delete from services where id ="+id+";");
	}
	
	/**
	 * This method update the service status 
	 * @param status
	 * @param id
	 */
	public void update(String status, int id) {
		execute("update services set status ='"+status+"' where id = "+id+";");
	}
	
	
	/**
	 * this method execute the SQLite query
	 * @param query
	 */
	public void execute(final String query) {
		// create connection
		client.getConnection(conn -> { 
			if (conn.failed()) {
				
				System.err.println(conn.cause().getMessage());
				return;
			}
			final SQLConnection connection = conn.result();
			
			// execute query
			connection.execute(query, insert -> {
				
				// close connection
				connection.close(done -> {
					if (done.failed()) {
						throw new RuntimeException(done.cause());
					}
				});
			});
		});
	}

	/**
	 * @return
	 */
	public JDBCClient getClient() {
		return client;
	}
}
