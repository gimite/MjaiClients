package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// Version 0.1.0
public class YmatsuxClient extends BaseMjaiClient {

    public YmatsuxClient(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    protected void processSelfTsumo(Hai tsumohai) {
        if (HoraUtil.isHoraIgnoreYaku(tehais, tsumohai)) {
            doTsumoho(tsumohai);
            return;
        }

        DahaiAction tsumoAction = chooseDahaiAction(tsumohai);
        doDahai(tsumohai, tsumoAction.sutehaiIndex, tsumoAction.doRichi);
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
        YScorer yScorer = new YScorer(tehais, isOya, doras, bakaze, jikaze);
        int sutehaiIndex = -1;
        double maxYScore = yScorer.calculateYScore(shantensu);
        int maxYScoreShantensu = shantensu;
        for (int i = 0; i < tehais.size(); i++) {
            List<Hai> trialTehais = new ArrayList<Hai>(tehais);
            trialTehais.set(i, tsumohai);
            YScorer trialYScorer = new YScorer(trialTehais, isOya, doras, bakaze, jikaze);
            double trialYScore = trialYScorer.calculateYScore(shantensu);
            if (trialYScore > maxYScore) {
                sutehaiIndex = i;
                maxYScore = trialYScore;
                maxYScoreShantensu = ShantensuUtil.calculateShantensu(trialTehais);
            }
        }

        if (sutehaiIndex == -1) {
            return new DahaiAction(-1, false);
        } else {
            if (maxYScoreShantensu == 0 && numRemainingPipai >= 4 && !doneRichi && !isFuriten()) {
                // The new shantensu is zero in this case. Then the player can do richi.
                return new DahaiAction(sutehaiIndex, true);
            } else {
                return new DahaiAction(sutehaiIndex, false);
            }
        }
    }

    @Override
    protected void processOthersDahai(int actorId, Hai sutehai) {
        if (doneRichi) {
            if (HoraUtil.isHoraIgnoreYaku(tehais, sutehai) && !isFuriten()) {
                doRonho(actorId, sutehai);
            } else {
                sendNone();
            }
        } else {
            sendNone();
        }
    }

    @Override
    protected String getClientName() {
        return "ymatsux";
    }
}
