package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ShantensuClient extends BaseMjaiClient {

    public ShantensuClient(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public String getClientName() {
        return "shantensu-java";
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

        int sutehaiIndex = chooseSutehai(tsumohai);

        ObjectNode dahaiMessage = objectMapper.createObjectNode();
        dahaiMessage.put("type", "dahai");
        dahaiMessage.put("actor", id);
        if (sutehaiIndex < 0) {
            dahaiMessage.put("pai", tsumohai.toString());
            dahaiMessage.put("tsumogiri", true);
        } else {
            dahaiMessage.put("pai", tehais.get(sutehaiIndex).toString());
            dahaiMessage.put("tsumogiri", false);
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

    private int chooseSutehai(Hai tsumohai) {
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
            return -1;
        } else {
            Collections.shuffle(alternatives);
            return alternatives.get(0);
        }
    }

    @Override
    protected final void processOthersDahai(int actorId, Hai sutehai) {
        sendNone();
    }
}
