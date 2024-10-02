package pl.cheily.filegen.LocalData.FileManagement.Output;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;
import static pl.cheily.filegen.LocalData.MetaKey.*;

/**
 * A WebSocket from which data, fetched from the DataManager, can be recieved upon saving.
 * WebSocket will send a JSON object with a type parameter as well as a data payload. 
 * Current data types:
 * <ul>
 *      <li>flagsdir: contains a data payload that is simply a string which describing the current flagsDir.</li>
 *      <li>metadata: contains a data payload that contains the current metadata. </li>
 * </ul>
 */
public class DataWebSocket extends WebSocketServer {
    
    public DataWebSocket(InetSocketAddress address){
        super(address);
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake shake) {

        // Inform the connection of the flagdir, as indicated by the "flagsDir" type parameter.
        JSONObject flagJSONObject = new JSONObject();
        flagJSONObject.put("type", "flagsdir");
        flagJSONObject.put("data", dataManager.flagsDir.toString());

        conn.send(flagJSONObject.toString());

        // Then give it the relevant information
        conn.send(getMetadataJSON().toString());

        System.out.println(conn + " connected.");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println(conn + " disconnected:" + reason + ".");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(conn + " sent: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("Error on " + conn + ": " + ex);
    }

    @Override
    public void onStart() {
        System.out.println("Started WebSocket Server!");
    }

    /**
     * Updates all WebSocket connections with the latest metadata from the DataManager.
     */
    public void updateMetadata() {
        broadcast(getMetadataJSON().toString());
    }


    private static JSONObject getMetadataJSON() {

        // Create JSON object
        JSONObject jsonObject = new JSONObject();

        // And add all the data needed from the DataManager.

        //Round data
        JSONObject roundObject = new JSONObject();

//        roundObject.put("label", dataManager.getMeta(SEC_ROUND, KEY_ROUND_LABEL));
//        roundObject.put("score_p1", dataManager.getMeta(SEC_ROUND, KEY_SCORE_1));
//        roundObject.put("score_p2", dataManager.getMeta(SEC_ROUND, KEY_SCORE_2));
//        roundObject.put("isGF", dataManager.getMeta(SEC_ROUND, KEY_GF));
//        roundObject.put("isReset", dataManager.getMeta(SEC_ROUND, KEY_GF_RESET));
//        roundObject.put("isP1Winners", dataManager.getMeta(SEC_ROUND, KEY_GF_W1));

        jsonObject.put("round", roundObject);


        // P1 Data
        JSONObject p1Object = new JSONObject();

//        p1Object.put("name", dataManager.getMeta(SEC_P1, KEY_NAME));
//        p1Object.put("tag", dataManager.getMeta(SEC_P1, KEY_TAG));
        // This is so cursed...
//        try {
//            p1Object.put("nation", "data:image/png;base64, " + dataManager.getFlagBase64String(dataManager.getMeta(SEC_P1, KEY_NATION)));
//        } catch (IOException e) { p1Object.put("nation", ""); }

        jsonObject.put("p1", p1Object);


        // P2 Data
        JSONObject p2Object = new JSONObject();

//        p2Object.put("name", dataManager.getMeta(SEC_P2, KEY_NAME));
//        p2Object.put("tag", dataManager.getMeta(SEC_P2, KEY_TAG));
        // Return of the accursed Base64...
//        try {
//            p2Object.put("nation", "data:image/png;base64, " + dataManager.getFlagBase64String(dataManager.getMeta(SEC_P2, KEY_NATION)));
//        } catch (IOException e) { p2Object.put("nation", ""); }

        jsonObject.put("p2", p2Object);

        
        // Commentator Data
        JSONObject commsObject = new JSONObject();

//        commsObject.put("host", dataManager.getMeta(SEC_COMMS, KEY_HOST));
//        commsObject.put("commentator_1", dataManager.getMeta(SEC_COMMS, KEY_COMM_1));
//        commsObject.put("commentator_2", dataManager.getMeta(SEC_COMMS, KEY_COMM_2));

        jsonObject.put("comms", commsObject);

        return new JSONObject().put("type", "metadata").put("data", jsonObject);
    }

}
