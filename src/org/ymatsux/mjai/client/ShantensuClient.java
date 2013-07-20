package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ymatsux.mjai.client.ClientActions.DahaiAction;
import org.ymatsux.mjai.client.ClientActions.NoneAction;
import org.ymatsux.mjai.client.ClientActions.OthersDahaiAction;
import org.ymatsux.mjai.client.ClientActions.SelfTsumoAction;
import org.ymatsux.mjai.client.ClientActions.TsumohoAction;

public final class ShantensuClient extends BaseMjaiClient {

    public ShantensuClient(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public String getClientName() {
        return "shantensu-java";
    }

    @Override
    protected SelfTsumoAction chooseSelfTsumoAction(Hai tsumohai) {
        if (HoraUtil.isHoraIgnoreYaku(tehais(), tsumohai)) {
            return new TsumohoAction();
        }

        int sutehaiIndex = chooseSutehai(tsumohai);
        return new DahaiAction(sutehaiIndex, false);
    }

    private int chooseSutehai(Hai tsumohai) {
        int shantensu = ShantensuUtil.calculateShantensu(tehais());
        List<Integer> alternatives = new ArrayList<Integer>();
        for (int i = 0; i < tehais().size(); i++) {
            List<Hai> trialTehais = new ArrayList<Hai>(tehais());
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
    protected OthersDahaiAction chooseOthersDahaiAction(int actorId, Hai sutehai) {
        return new NoneAction();
    }
}
