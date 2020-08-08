package se.kry.codetest;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * Main class which runs the code 
 *
 */
public class Start {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//settings 
		final VertxOptions vertOptions = new VertxOptions();
		vertOptions.setMaxEventLoopExecuteTime(3000000000L);
		vertOptions.setMaxWorkerExecuteTime(3000000000L);
		Vertx vertex = Vertx.vertx(vertOptions);
		
		// deploy vertex
		vertex.deployVerticle(new MainVerticle(), new DeploymentOptions().setWorker(true));

		// create table
		DBManager manager = new DBManager(vertex);
		manager.migrate();
	}
}
