package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            doTsumoho(tsumohai);
            return;
        }

        DahaiAction tsumoAction = chooseDahaiAction(tsumohai);
        doDahai(tsumohai, tsumoAction.sutehaiIndex, tsumoAction.doRichi);
    }

    private boolean isHora(Hai tsumohai) {
        if (ShantensuUtil.calculateShantensu(tehais) > 0) {
            return false;
        }

        List<Hai> tehaisWithTsumohai = new ArrayList<Hai>(tehais);
        tehaisWithTsumohai.add(tsumohai);

        return HoraUtil.isHora(tehaisWithTsumohai);
    }

    private static class DahaiAction {
        public int sutehaiIndex;
        public boolean doRichi;

        public DahaiAction(int sutehaiIndex, boolean doRichi) {
            this.sutehaiIndex = sutehaiIndex;
            this.doRichi = doRichi;
        }
    }

    private DahaiAction chooseDahaiAction(Hai tsumohai) {
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
            return new DahaiAction(-1, false);
        } else {
            Collections.shuffle(alternatives);
            int sutehaiIndex = alternatives.get(0);
            if (shantensu == 1 && numRemainingPipai >= 4 && !doneRichi && !isFuriten()) {
                // The new shantensu is zero in this case. Then the player can do richi.
                return new DahaiAction(sutehaiIndex, true);
            } else {
                return new DahaiAction(sutehaiIndex, false);
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
                doRonho(actorId, sutehai);
            } else {
                sendNone();
            }
        } else {
            sendNone();
        }
    }
}
