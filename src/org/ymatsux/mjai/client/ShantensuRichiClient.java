package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ShantensuRichiClient extends BaseMjaiClient{

    public ShantensuRichiClient(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public String getClientName() {
        return "shantensu-richi-java";
    }

    @Override
    protected final void processSelfTsumo(Hai tsumohai) {
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
            readMessage();
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

    @Override
    protected void processOthersDahai(int actorId, Hai sutehai) {
        if (doneRichi) {
            // This client never does furiten-richi. Thus we can assume that this ronho is not
            // furiten.
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
    }
}
