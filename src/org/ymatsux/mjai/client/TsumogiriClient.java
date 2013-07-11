package org.ymatsux.mjai.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TsumogiriClient implements MjaiClient {

    private final BufferedReader reader;
    private final PrintWriter writer;
    private final ObjectMapper objectMapper;
    private int id = -1;

    public TsumogiriClient(Socket socket) throws IOException {
        reader = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream())));
        objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try {
            runInternal();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private void runInternal() throws IOException {
        while (true) {
            String line = reader.readLine();
            System.out.println("<-  " + line);
            JsonNode inputJson = objectMapper.readTree(line);
            String type = inputJson.get("type").asText();
            switch (type) {
            case "hello":
                ObjectNode joinMessage = objectMapper.createObjectNode();
                joinMessage.put("type", "join");
                joinMessage.put("name", "tsumogiri-java");
                joinMessage.put("room", Flags.ROOM.getValue());
                sendMessage(joinMessage);
                break;
            case "start_game":
                id = inputJson.get("id").asInt();
                sendNone();
                break;
            case "tsumo":
                int actorId = inputJson.get("actor").asInt();
                if (actorId == id) {
                    ObjectNode dahaiMessage = objectMapper.createObjectNode();
                    dahaiMessage.put("type", "dahai");
                    dahaiMessage.put("actor", id);
                    dahaiMessage.put("pai", inputJson.get("pai").asText());
                    dahaiMessage.put("tsumogiri", true);
                    sendMessage(dahaiMessage);
                } else {
                    sendNone();
                }
                break;
            case "end_game":
                sendNone();
                return;
            case "error":
                sendNone();
                break;
            default:
                sendNone();
            }
        }
    }

    private void sendMessage(JsonNode json) {
        System.out.println("->  " + json.toString());
        writer.println(json.toString());
        writer.flush();
    }

    private void sendNone() {
        ObjectNode message = objectMapper.createObjectNode();
        message.put("type", "none");
        sendMessage(message);
    }
}
