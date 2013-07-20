package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ymatsux.mjai.client.ClientActions.DahaiAction;
import org.ymatsux.mjai.client.ClientActions.NoneAction;
import org.ymatsux.mjai.client.ClientActions.OthersDahaiAction;
import org.ymatsux.mjai.client.ClientActions.RonhoAction;
import org.ymatsux.mjai.client.ClientActions.SelfTsumoAction;
import org.ymatsux.mjai.client.ClientActions.TsumohoAction;

public class ShantensuRichiClient extends BaseMjaiClient {

    public ShantensuRichiClient(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public final String getClientName() {
        return "shantensu-richi-java";
    }

    @Override
    protected final SelfTsumoAction chooseSelfTsumoAction(Hai tsumohai) {
        if (HoraUtil.isHoraIgnoreYaku(tehais(), tsumohai)) {
            return new TsumohoAction();
        }

        return chooseDahaiAction(tsumohai);
    }

    private DahaiAction chooseDahaiAction(Hai tsumohai) {
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
            return new DahaiAction(-1, false);
        } else {
            Collections.shuffle(alternatives);
            int sutehaiIndex = alternatives.get(0);
            if (canRichi(tsumohai, sutehaiIndex)) {
                return new DahaiAction(sutehaiIndex, true);
            } else {
                return new DahaiAction(sutehaiIndex, false);
            }
        }
    }

    @Override
    protected OthersDahaiAction chooseOthersDahaiAction(int actorId, Hai sutehai) {
        if (doneRichi()) {
            if (HoraUtil.isHoraIgnoreYaku(tehais(), sutehai) && !isFuriten()) {
                return new RonhoAction();
            } else {
                return new NoneAction();
            }
        } else {
            return new NoneAction();
        }
    }
}
