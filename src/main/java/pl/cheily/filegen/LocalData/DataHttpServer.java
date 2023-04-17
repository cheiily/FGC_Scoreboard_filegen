package pl.cheily.filegen.LocalData;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.*;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;
import static pl.cheily.filegen.LocalData.MetaKey.*;

/**
 * Basic HTTP server from which JSON data about the current status of the controller can be fetched.
 * Use {@link DataHttpServer#start(InetSocketAddress)} to start a new server.
 * Endpoints implemented:
 * <ul>
 * <li>/metadata: Returns the current metadata stored in the DataManager.</li>
 * </ul>
 * 
 */
public class DataHttpServer {

    private static HttpServer serverInstance;

    /**
     * Starts the HTTP Server within a DataHttpServer instance.
     * 
     * @param address {@link InetSocketAddress} for the server to listen on.
     */

    // TODO: Handle exception on address already being in use.
    public static void start(InetSocketAddress address) throws IOException {
        serverInstance = HttpServer.create(address, 0);
        serverInstance.createContext("/metadata", new MetadataHandler());
        serverInstance.setExecutor(null);
        serverInstance.start();
    }

    public void close() {
        serverInstance.stop(0);
    }
    
    static class MetadataHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            // Create JSON object
            JSONObject jsonObject = new JSONObject();

            // And add all the data needed from the DataManager.

            //Round data
            JSONObject roundObject = new JSONObject();

            roundObject.put("label", dataManager.getMeta(SEC_ROUND, KEY_ROUND_LABEL));
            roundObject.put("score_p1", dataManager.getMeta(SEC_ROUND, KEY_SCORE_1));
            roundObject.put("score_p2", dataManager.getMeta(SEC_ROUND, KEY_SCORE_2));
            roundObject.put("isGF", dataManager.getMeta(SEC_ROUND, KEY_GF));
            roundObject.put("isReset", dataManager.getMeta(SEC_ROUND, KEY_GF_RESET));
            roundObject.put("isP1Winners", dataManager.getMeta(SEC_ROUND, KEY_GF_W1));

            jsonObject.put("round", roundObject);


            // P1 Data
            JSONObject p1Object = new JSONObject();

            p1Object.put("name", dataManager.getMeta(SEC_P1, KEY_NAME));
            p1Object.put("tag", dataManager.getMeta(SEC_P1, KEY_TAG));
            p1Object.put("nation", dataManager.getMeta(SEC_P1, KEY_NATION));

            jsonObject.put("p1", p1Object);


            // P2 Data
            JSONObject p2Object = new JSONObject();

            p2Object.put("name", dataManager.getMeta(SEC_P2, KEY_NAME));
            p2Object.put("tag", dataManager.getMeta(SEC_P2, KEY_TAG));
            p2Object.put("nation", dataManager.getMeta(SEC_P2, KEY_NATION));

            jsonObject.put("p2", p2Object);

            
            // Commentator Data
            JSONObject commsObject = new JSONObject();

            commsObject.put("host", dataManager.getMeta(SEC_COMMS, KEY_HOST));
            commsObject.put("commentator_1", dataManager.getMeta(SEC_COMMS, KEY_COMM_1));
            commsObject.put("commentator_2", dataManager.getMeta(SEC_COMMS, KEY_COMM_2));

            jsonObject.put("comms", commsObject);
            

            // Set headers
            t.getResponseHeaders().set("content-type", "application/json");
            t.getResponseHeaders().set("access-control-allow-origin", "*");
            t.sendResponseHeaders(200, jsonObject.toString().getBytes().length);

            // Write JSON object to response body
            OutputStream os = t.getResponseBody();
            os.write(jsonObject.toString().getBytes());
            os.close();
        }
    }
}
