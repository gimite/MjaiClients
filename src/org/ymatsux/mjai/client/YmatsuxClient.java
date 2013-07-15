package org.ymatsux.mjai.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// Version 0.1.1
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
        if (doneRichi) {
            return new DahaiAction(-1, false);
        }
        int shantensu = ShantensuUtil.calculateShantensu(tehais);
        if (shantensu >= 4) {
            return chooseDahaiActionByXScorer(tsumohai, shantensu);
        } else {
            return chooseDahaiActionByYScorer(tsumohai, shantensu);
        }
    }

    private DahaiAction chooseDahaiActionByXScorer(Hai tsumohai, int shantensu) {
        List<Integer> alternatives = new ArrayList<Integer>();
        for (int i = 0; i < tehais.size(); i++) {
            List<Hai> trialTehais = new ArrayList<Hai>(tehais);
            trialTehais.set(i, tsumohai);
            int trialShantensu = ShantensuUtil.calculateShantensu(trialTehais);
            if (trialShantensu < shantensu) {
                alternatives.add(i);
            }
        }

        int bestSutehaiIndex = -1;
        if (alternatives.isEmpty()) {
            XScorer xScorer = new XScorer(tehais, isOya, doras, bakaze, jikaze);
            double maxXScore = xScorer.calculateXScore(shantensu);
            for (int sutehaiIndex = 0; sutehaiIndex < tehais.size(); sutehaiIndex++) {
                List<Hai> trialTehais = new ArrayList<Hai>(tehais);
                trialTehais.set(sutehaiIndex, tsumohai);
                XScorer trialXScorer = new XScorer(trialTehais, isOya, doras, bakaze, jikaze);
                double xScore = trialXScorer.calculateXScore(shantensu);
                if (xScore > maxXScore) {
                    maxXScore = xScore;
                    bestSutehaiIndex = sutehaiIndex;
                }
            }
        } else {
            int newShantensu = shantensu - 1;
            double maxXScore = 0.0;
            for (int alternative : alternatives) {
                List<Hai> trialTehais = new ArrayList<Hai>(tehais);
                trialTehais.set(alternative, tsumohai);
                XScorer trialXScorer = new XScorer(trialTehais, isOya, doras, bakaze, jikaze);
                double xScore = trialXScorer.calculateXScore(newShantensu);
                if (xScore > maxXScore) {
                    maxXScore = xScore;
                    bestSutehaiIndex = alternative;
                }
            }
        }

        if (bestSutehaiIndex == -1) {
            return new DahaiAction(-1, false);
        } else {
            // We don't do consider richi here because the shantensu is larger when we use XScorer.
            return new DahaiAction(bestSutehaiIndex, false);
        }
    }

    private DahaiAction chooseDahaiActionByYScorer(Hai tsumohai, int shantensu) {
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
            if (maxYScoreShantensu == 0 && numRemainingPipai >= 4 && !doneRichi && !isFuriten() &&
                    score > 1000) {
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
        return "ymatsux-0.1.1";
    }
}
