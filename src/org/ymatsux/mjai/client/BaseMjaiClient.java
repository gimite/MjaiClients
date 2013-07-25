package org.ymatsux.mjai.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.ymatsux.mjai.client.ClientActions.DahaiAction;
import org.ymatsux.mjai.client.ClientActions.NoneAction;
import org.ymatsux.mjai.client.ClientActions.OthersDahaiAction;
import org.ymatsux.mjai.client.ClientActions.RonhoAction;
import org.ymatsux.mjai.client.ClientActions.SelfTsumoAction;
import org.ymatsux.mjai.client.ClientActions.TsumohoAction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class BaseMjaiClient implements MjaiClient {

    protected static final int INITIAL_TEHAI_SIZE = 13;
    protected static final int INITIAL_NUM_REMAINING_PIPAI = 70;

    private final BufferedReader reader;
    private final PrintWriter writer;
    private final PrintStream logStream;
    private final ObjectMapper objectMapper;

    protected int id = -1;
    protected int score = 0;

    // Group kyoku-specific data here.
    private static class KyokuData {
        private int oyaId = -1;
        private boolean isOya;
        private List<Hai> doras;
        private Hai bakaze;
        private Hai jikaze;
        private List<Hai> tehais;
        private List<Hai> sutehais;
        private boolean doneRichi;
        private int numRemainingPipai;
    }

    private KyokuData kyokuData;

    protected final int oyaId() {
        return kyokuData.oyaId;
    }

    protected final boolean isOya() {
        return kyokuData.isOya;
    }

    protected final List<Hai> doras() {
        return kyokuData.doras;
    }

    protected final Hai bakaze() {
        return kyokuData.bakaze;
    }

    protected final Hai jikaze() {
        return kyokuData.jikaze;
    }

    protected final List<Hai> tehais() {
        return kyokuData.tehais;
    }

    protected final List<Hai> sutehais() {
        return kyokuData.sutehais;
    }

    protected final boolean doneRichi() {
        return kyokuData.doneRichi;
    }

    protected final int numRemainingPipai() {
        return kyokuData.numRemainingPipai;
    }

    public BaseMjaiClient(Socket socket) throws IOException {
        reader = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream())));
        if (!Flags.LOG_FILE.getValue().isEmpty()) {
            logStream = new PrintStream(new File(Flags.LOG_FILE.getValue()));
        } else {
            logStream = System.err;
        }
        objectMapper = new ObjectMapper();
    }

    protected void logPrintln(String x) {
        logStream.println(x);
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
            case "reach":
                processRichi(inputJson);
                break;
            case "hora":
                processHora(inputJson);
                break;
            case "ryukyoku":
                processRyukyoku(inputJson);
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
        score = 25000;
        sendNone();
    }

    private void processTsumo(JsonNode inputJson) {
        kyokuData.numRemainingPipai--;
        int actorId = inputJson.get("actor").asInt();
        if (actorId == id) {
            Hai tsumohai = Hai.parse(inputJson.get("pai").asText());
            updateStateForSelfTsumo(tsumohai);
            processSelfTsumo(tsumohai);
        } else {
            sendNone();
        }
    }

    protected void updateStateForSelfTsumo(Hai tsumohai) {
    }

    private void processSelfTsumo(Hai tsumohai) {
        SelfTsumoAction action = chooseSelfTsumoAction(tsumohai);
        if (action instanceof DahaiAction) {
            DahaiAction dahaiAction = (DahaiAction) action;
            doDahai(tsumohai, dahaiAction.sutehaiIndex, dahaiAction.doRichi);
        } else if (action instanceof TsumohoAction) {
            doTsumoho(tsumohai);
        } else {
            throw new IllegalStateException();
        }
    }

    abstract protected SelfTsumoAction chooseSelfTsumoAction(Hai tsumohai);

    private void processDahai(JsonNode inputJson) {
        int actorId = inputJson.get("actor").asInt();
        Hai sutehai = Hai.parse(inputJson.get("pai").asText());
        if (actorId != id) {
            processOthersDahai(actorId, sutehai);
        } else {
            processSelfDahai(sutehai);
        }
    }

    private void processSelfDahai(Hai sutehai) {
        updateStateForSelfDahai(sutehai);
        sendNone();
    }

    protected void updateStateForSelfDahai(Hai sutehai) {
    }

    private void processOthersDahai(int actorId, Hai sutehai) {
        updateStateForOthersDahai(actorId, sutehai);
        OthersDahaiAction action = chooseOthersDahaiAction(actorId, sutehai);
        if (action instanceof NoneAction) {
            sendNone();
        } else if (action instanceof RonhoAction) {
            doRonho(actorId, sutehai);
        } else {
            throw new IllegalStateException();
        }
    }

    protected void updateStateForOthersDahai(int actorId, Hai sutehai) {
    }

    abstract protected OthersDahaiAction chooseOthersDahaiAction(int actorId, Hai sutehai);

    private void processStartKyoku(JsonNode inputJson) {
        updateStatusForStartKyoku(inputJson);
        sendNone();
    }

    protected void updateStatusForStartKyoku(JsonNode inputJson) {
        KyokuData kyokuData = new KyokuData();
        kyokuData.oyaId = inputJson.get("oya").asInt();
        kyokuData.isOya = id == kyokuData.oyaId;

        kyokuData.doras = new ArrayList<Hai>();
        Hai doraMarker = Hai.parse(inputJson.get("dora_marker").asText());
        kyokuData.doras.add(doraMarker.next());

        kyokuData.bakaze = Hai.parse(inputJson.get("bakaze").asText());
        kyokuData.jikaze = Hai.parse(
                new String[] { "E", "S", "W", "N" }[(id - kyokuData.oyaId + 4) % 4]);

        kyokuData.tehais = new ArrayList<Hai>();
        JsonNode tehaisJson = inputJson.get("tehais");
        for (int i = 0; i < INITIAL_TEHAI_SIZE; i++) {
            kyokuData.tehais.add(Hai.parse(tehaisJson.get(id).get(i).asText()));
        }

        kyokuData.sutehais = new ArrayList<Hai>();
        kyokuData.doneRichi = false;
        kyokuData.numRemainingPipai = INITIAL_NUM_REMAINING_PIPAI;

        this.kyokuData = kyokuData;
    }

    private void processRichi(JsonNode inputJson) {
        updateStatusForRichi(inputJson);
        sendNone();
    }

    protected void updateStatusForRichi(JsonNode inputJson) {
    }

    private void processHora(JsonNode inputJson) {
        score = inputJson.get("scores").get(id).asInt();
        sendNone();
    }

    private void processRyukyoku(JsonNode inputJson) {
        score = inputJson.get("scores").get(id).asInt();
        sendNone();
    }

    abstract protected String getClientName();

    private JsonNode readMessage() {
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

    private void sendMessage(JsonNode json) {
        if (Boolean.parseBoolean(Flags.DEBUG_OUTPUT_JSON.getValue())) {
            System.out.println("->  " + json.toString());
        }
        writer.println(json.toString());
        writer.flush();
    }

    private void sendNone() {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("type", "none");
        sendMessage(json);
    }

    private void doTsumoho(Hai tsumohai) {
        ObjectNode horaMessage = objectMapper.createObjectNode();
        horaMessage.put("type", "hora");
        horaMessage.put("actor", id);
        horaMessage.put("target", id);
        horaMessage.put("pai", tsumohai.toString());
        sendMessage(horaMessage);
    }

    private void doDahai(Hai tsumohai, int sutehaiIndex, boolean doRichi) {
        if (doRichi) {
            ObjectNode richiMessage = objectMapper.createObjectNode();
            richiMessage.put("type", "reach");
            richiMessage.put("actor", id);
            sendMessage(richiMessage);

            // Update the internal state.
            kyokuData.doneRichi = true;

            // Read the richi message.
            readMessage();
        }

        ObjectNode dahaiMessage = objectMapper.createObjectNode();
        dahaiMessage.put("type", "dahai");
        dahaiMessage.put("actor", id);
        if (sutehaiIndex < 0) {
            dahaiMessage.put("pai", tsumohai.toString());
            dahaiMessage.put("tsumogiri", true);
        } else {
            dahaiMessage.put("pai", tehais().get(sutehaiIndex).toString());
            dahaiMessage.put("tsumogiri", false);
        }
        sendMessage(dahaiMessage);

        // Update the internal state.
        if (sutehaiIndex >= 0) {
            tehais().set(sutehaiIndex, tsumohai);
        }
    }

    protected boolean canRichi(Hai tsumohai, int sutehaiIndex) {
        List<Hai> newTehais = new ArrayList<Hai>(tehais());
        if (sutehaiIndex >= 0) {
            newTehais.set(sutehaiIndex, tsumohai);
        }
        boolean isTenpai = ShantensuUtil.calculateShantensu(newTehais) == 0;
        return isTenpai && !doneRichi() && !isFuriten() && numRemainingPipai() >= 4 && score > 1000;
    }

    private void doRonho(int targetId, Hai sutehai) {
        ObjectNode horaMessage = objectMapper.createObjectNode();
        horaMessage.put("type", "hora");
        horaMessage.put("actor", id);
        horaMessage.put("target", targetId);
        horaMessage.put("pai", sutehai.toString());
        sendMessage(horaMessage);
    }

    protected final boolean isFuriten() {
        for (Hai sutehai : sutehais()) {
            if (HoraUtil.isHoraIgnoreYaku(tehais(), sutehai)) {
                return true;
            }
        }
        return false;
    }
}
