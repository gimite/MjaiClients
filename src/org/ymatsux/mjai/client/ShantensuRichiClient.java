package org.ymatsux.mjai.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ShantensuRichiClient implements MjaiClient{

    private static final int INITIAL_TEHAI_SIZE = 13;

    private final BufferedReader reader;
    private final PrintWriter writer;
    private final ObjectMapper objectMapper;
    private int id = -1;
    private List<Hai> tehais;
    private List<Hai> sutehais;
    private boolean doneRichi;
    private int numRemainingPipai;

    public ShantensuRichiClient(Socket socket) throws IOException {
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
                joinMessage.put("name", "shantensu-richi-java");
                joinMessage.put("room", Flags.ROOM.getValue());
                sendMessage(joinMessage);
                break;
            case "start_game":
                id = inputJson.get("id").asInt();
                sendNone();
                break;
            case "start_kyoku":
                sutehais = new ArrayList<Hai>();
                doneRichi = false;
                numRemainingPipai = 70;
                JsonNode tehaisJson = inputJson.get("tehais");
                tehais = new ArrayList<Hai>();
                for (int i = 0; i < INITIAL_TEHAI_SIZE; i++) {
                    tehais.add(Hai.parse(tehaisJson.get(id).get(i).asText()));
                }
                sendNone();
                break;
            case "tsumo":
            {
                numRemainingPipai--;
                int actorId = inputJson.get("actor").asInt();
                if (actorId == id) {
                    Hai tsumohai = Hai.parse(inputJson.get("pai").asText());
                    processTsumo(tsumohai);
                } else {
                    sendNone();
                }
                break;
            }
            case "dahai":
            {
                int actorId = inputJson.get("actor").asInt();
                if (actorId != id) {
                    if (doneRichi) {
                        Hai sutehai = Hai.parse(inputJson.get("pai").asText());
                        // TODO: Add furiten check.
                        if (isHora(sutehai)) {
                            ObjectNode horaMessage = objectMapper.createObjectNode();
                            horaMessage.put("type", "hora");
                            horaMessage.put("actor", id);
                            horaMessage.put("target", actorId);
                            horaMessage.put("pai", sutehai.toString());
                            sendMessage(horaMessage);
                        } else {
                            sendNone();
                        }
                    } else {
                        sendNone();
                    }
                } else {
                    sendNone();
                }
                break;
            }
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

    private void processTsumo(Hai tsumohai) throws IOException {
        if (isHora(tsumohai)) {
            ObjectNode horaMessage = objectMapper.createObjectNode();
            horaMessage.put("type", "hora");
            horaMessage.put("actor", id);
            horaMessage.put("target", id);
            horaMessage.put("pai", tsumohai.toString());
            sendMessage(horaMessage);
            return;
        }

        TsumoAction tsumoAction = chooseTsumoAction(tsumohai);

        if (tsumoAction.doRichi) {
            // Do richi
            ObjectNode richiMessage = objectMapper.createObjectNode();
            richiMessage.put("type", "reach");
            richiMessage.put("actor", id);
            sendMessage(richiMessage);
            doneRichi = true;
            // Read the richi message.
            String richiLine = reader.readLine();
            System.out.println("<-  " + richiLine);
        }

        int sutehaiIndex = tsumoAction.sutehaiIndex;

        ObjectNode dahaiMessage = objectMapper.createObjectNode();
        dahaiMessage.put("type", "dahai");
        dahaiMessage.put("actor", id);
        if (sutehaiIndex < 0) {
            dahaiMessage.put("pai", tsumohai.toString());
            dahaiMessage.put("tsumogiri", true);
            sutehais.add(tsumohai);
        } else {
            dahaiMessage.put("pai", tehais.get(sutehaiIndex).toString());
            dahaiMessage.put("tsumogiri", false);
            sutehais.add(tehais.get(sutehaiIndex));
        }

        sendMessage(dahaiMessage);

        if (sutehaiIndex >= 0) {
            tehais.set(sutehaiIndex, tsumohai);
        }
    }

    private boolean isHora(Hai tsumohai) {
        if (ShantensuUtil.calculateShantensu(tehais) > 0) {
            return false;
        }

        List<Hai> tehaisWithTsumohai = new ArrayList<Hai>(tehais);
        tehaisWithTsumohai.add(tsumohai);

        return HoraUtil.isHora(tehaisWithTsumohai);
    }

    private static class TsumoAction {
        public int sutehaiIndex;
        public boolean doRichi;

        public TsumoAction(int sutehaiIndex, boolean doRichi) {
            this.sutehaiIndex = sutehaiIndex;
            this.doRichi = doRichi;
        }
    }

    private TsumoAction chooseTsumoAction(Hai tsumohai) {
        int shantensu = ShantensuUtil.calculateShantensu(tehais);
        List<Integer> alternatives = new ArrayList<Integer>();
        for (int i = 0; i < tehais.size(); i++) {
            List<Hai> trialTehais = new ArrayList<Hai>(tehais);
            trialTehais.set(i, tsumohai);
            int trialShantensu = ShantensuUtil.calculateShantensu(trialTehais);
            if (trialShantensu < shantensu) {
                alternatives.add(i);
            }
        }

        if (alternatives.isEmpty()) {
            return new TsumoAction(-1, false);
        } else {
            Collections.shuffle(alternatives);
            int sutehaiIndex = alternatives.get(0);
            if (shantensu == 1 && numRemainingPipai >= 4 && !doneRichi && !isFuriten()) {
                // The new shantensu is zero in this case. Then the player can do richi.
                return new TsumoAction(sutehaiIndex, true);
            } else {
                return new TsumoAction(sutehaiIndex, false);
            }
        }
    }

    private boolean isFuriten() {
        for (Hai sutehai : sutehais) {
            if (isHora(sutehai)) {
                return true;
            }
        }
        return false;
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
