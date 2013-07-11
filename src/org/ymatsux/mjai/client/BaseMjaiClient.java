package org.ymatsux.mjai.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class BaseMjaiClient implements MjaiClient {

    protected static final int INITIAL_TEHAI_SIZE = 13;
    protected static final int INITIAL_NUM_REMAINING_PIPAI = 70;

    protected final BufferedReader reader;
    protected final PrintWriter writer;
    protected final ObjectMapper objectMapper;
    protected int id = -1;

    protected List<Hai> tehais;
    protected List<Hai> sutehais;
    protected boolean doneRichi;
    protected int numRemainingPipai;

    public BaseMjaiClient(Socket socket) throws IOException {
        reader = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream())));
        objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        while (true) {
            JsonNode inputJson = readMessage();
            String type = inputJson.get("type").asText();
            switch (type) {
            case "hello":
                processHello();
                break;
            case "start_game":
                processStartGame(inputJson);
                break;
            case "start_kyoku":
                processStartKyoku(inputJson);
                break;
            case "tsumo":
                processTsumo(inputJson);
                break;
            case "dahai":
                processDahai(inputJson);
                break;
            case "end_game":
                sendNone();
                return;
            case "error":
                return;
            default:
                // TODO: Handle other messages in a serious manner.
                sendNone();
            }
        }
    }

    private void processHello() {
        ObjectNode joinMessage = objectMapper.createObjectNode();
        joinMessage.put("type", "join");
        joinMessage.put("name", getClientName());
        joinMessage.put("room", Flags.ROOM.getValue());
        sendMessage(joinMessage);
    }

    private void processStartGame(JsonNode inputJson) {
        id = inputJson.get("id").asInt();
        sendNone();
    }

    private void processTsumo(JsonNode inputJson) {
        int actorId = inputJson.get("actor").asInt();
        if (actorId == id) {
            Hai tsumohai = Hai.parse(inputJson.get("pai").asText());
            processSelfTsumo(tsumohai);
        } else {
            sendNone();
        }
    }

    abstract protected void processSelfTsumo(Hai tsumohai);

    private void processDahai(JsonNode inputJson) {
        int actorId = inputJson.get("actor").asInt();
        if (actorId != id) {
            Hai sutehai = Hai.parse(inputJson.get("pai").asText());
            processOthersDahai(actorId, sutehai);
        } else {
            sendNone();
        }
    }

    abstract protected void processOthersDahai(int actorId, Hai sutehai);

    private void processStartKyoku(JsonNode inputJson) {
        sutehais = new ArrayList<Hai>();
        doneRichi = false;
        numRemainingPipai = INITIAL_NUM_REMAINING_PIPAI;
        JsonNode tehaisJson = inputJson.get("tehais");
        tehais = new ArrayList<Hai>();
        for (int i = 0; i < INITIAL_TEHAI_SIZE; i++) {
            tehais.add(Hai.parse(tehaisJson.get(id).get(i).asText()));
        }
        sendNone();
    }

    abstract protected String getClientName();

    protected final JsonNode readMessage() {
        try {
            String line = reader.readLine();
            if (Boolean.parseBoolean(Flags.DEBUG_OUTPUT_JSON.getValue())) {
                System.out.println("<-  " + line);
            }
            return objectMapper.readTree(line);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected final void sendMessage(JsonNode json) {
        if (Boolean.parseBoolean(Flags.DEBUG_OUTPUT_JSON.getValue())) {
            System.out.println("->  " + json.toString());
        }
        writer.println(json.toString());
        writer.flush();
    }

    protected final void sendNone() {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("type", "none");
        sendMessage(json);
    }
}
