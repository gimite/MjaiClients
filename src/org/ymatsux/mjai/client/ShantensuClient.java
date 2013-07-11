package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        if (HoraUtil.isHora(tehais, tsumohai)) {
            doTsumoho(tsumohai);
            return;
        }

        int sutehaiIndex = chooseSutehai(tsumohai);
        doDahai(tsumohai, sutehaiIndex, false);
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
