package org.ymatsux.mjai.client;

import java.io.IOException;
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

// Version 0.1.2.2
public class YmatsuxClient extends BaseMjaiClient {

    // Group kyoku-specific data here.
    private static class KyokuData {
        private List<Integer>[] playerToAnzenhais;
        private boolean[] playerToDoneRichi;
    }

    private KyokuData kyokuData;

    private List<Integer>[] playerToAnzenhais() {
        return kyokuData.playerToAnzenhais;
    }

    private boolean[] playerToDoneRichi() {
        return kyokuData.playerToDoneRichi;
    }

    public YmatsuxClient(Socket socket) throws IOException {
        super(socket);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void updateStatusForStartKyoku(JsonNode inputJson) {
        super.updateStatusForStartKyoku(inputJson);

        KyokuData kyokuData = new KyokuData();
        kyokuData.playerToAnzenhais = (List<Integer>[]) new List<?>[4];
        for (int playerId = 0; playerId < 4; playerId++) {
            kyokuData.playerToAnzenhais[playerId] = new ArrayList<Integer>();
        }
        kyokuData.playerToDoneRichi = new boolean[4];
        this.kyokuData = kyokuData;
    }

    @Override
    protected SelfTsumoAction chooseSelfTsumoAction(Hai tsumohai) {
        if (HoraUtil.isHoraIgnoreYaku(tehais(), tsumohai)) {
            return new TsumohoAction();
        }

        return chooseDahaiAction(tsumohai);
    }

    private DahaiAction chooseDahaiAction(Hai tsumohai) {
        if (doneRichi()) {
            return new DahaiAction(-1, false);
        }
        int shantensu = ShantensuUtil.calculateShantensu(tehais());
        if (shantensu >= 4) {
            return chooseDahaiActionByXScorer(tsumohai, shantensu);
        } else {
            return chooseDahaiActionByYScorer(tsumohai, shantensu);
        }
    }

    private double calculateScoreWithXScorer(
            List<Hai> hais, Hai sutehai, int newShantensu, double[] riskVector) {
        XScorer xScorer = new XScorer(hais, isOya(), doras(), bakaze(), jikaze());
        double xScore = xScorer.calculateXScore(newShantensu);
        double riskPenalty = riskVector[sutehai.getId()];
        return xScore - riskPenalty;
    }

    private DahaiAction chooseDahaiActionByXScorer(Hai tsumohai, int shantensu) {
        List<Integer> alternatives = new ArrayList<Integer>();
        for (int i = 0; i < tehais().size(); i++) {
            List<Hai> trialTehais = new ArrayList<Hai>(tehais());
            trialTehais.set(i, tsumohai);
            int trialShantensu = ShantensuUtil.calculateShantensu(trialTehais);
            if (trialShantensu < shantensu) {
                alternatives.add(i);
            }
        }

        int bestSutehaiIndex = -1;
        if (alternatives.isEmpty()) {
            double[] riskVector = calculateRiskVectorForRichiPlayers();
            double maxScore = calculateScoreWithXScorer(
                    tehais(), tsumohai, shantensu, riskVector);
            for (int sutehaiIndex = 0; sutehaiIndex < tehais().size(); sutehaiIndex++) {
                List<Hai> trialTehais = new ArrayList<Hai>(tehais());
                trialTehais.set(sutehaiIndex, tsumohai);
                double score = calculateScoreWithXScorer(
                        trialTehais, tehais().get(sutehaiIndex), shantensu, riskVector);
                if (score > maxScore) {
                    maxScore = score;
                    bestSutehaiIndex = sutehaiIndex;
                }
            }
        } else {
            int newShantensu = shantensu - 1;
            double[] riskVector = calculateRiskVectorForRichiPlayers();
            double maxScore = Double.NEGATIVE_INFINITY;
            for (int alternative : alternatives) {
                List<Hai> trialTehais = new ArrayList<Hai>(tehais());
                trialTehais.set(alternative, tsumohai);
                double score = calculateScoreWithXScorer(
                        trialTehais, tehais().get(alternative), newShantensu, riskVector);
                if (score > maxScore) {
                    maxScore = score;
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

    private static final double RISK_PENALTY_FOR_RICHI_PLAYERS_KIKENHAI = 8000.0;

    private double[] calculateRiskVectorForRichiPlayers() {
        double[] riskVector = new double[34];
        if ("".isEmpty()) {
            return riskVector;
        }
        for (int playerId = 0; playerId < 4; playerId++) {
            if (playerId == id) {
                continue;
            }
            if (!playerToDoneRichi()[playerId]) {
                continue;
            }
            for (int haiId = 0; haiId < 34; haiId++) {
                if (!playerToAnzenhais()[playerId].contains(haiId)) {
                    riskVector[haiId] += RISK_PENALTY_FOR_RICHI_PLAYERS_KIKENHAI;
                }
            }
        }
        return riskVector;
    }

    private double calculateScoreWithYScorer(
            List<Hai> hais, Hai sutehai, int currentShantensu, double[] riskVector) {
        YScorer yScorer = new YScorer(hais, isOya(), doras(), bakaze(), jikaze());
        double yScore = yScorer.calculateYScore(currentShantensu);
        double riskPenalty = currentShantensu >= 2 ? riskVector[sutehai.getId()] : 0;
        return yScore - riskPenalty;
    }

    private DahaiAction chooseDahaiActionByYScorer(Hai tsumohai, int shantensu) {
        double[] riskVector = calculateRiskVectorForRichiPlayers();

        int sutehaiIndex = -1;
        double maxScore = calculateScoreWithYScorer(tehais(), tsumohai, shantensu, riskVector);
        for (int i = 0; i < tehais().size(); i++) {
            List<Hai> trialTehais = new ArrayList<Hai>(tehais());
            trialTehais.set(i, tsumohai);
            double trialScore = calculateScoreWithYScorer(
                    trialTehais, tsumohai, shantensu, riskVector);
            if (trialScore > maxScore) {
                sutehaiIndex = i;
                maxScore = trialScore;
            }
        }

        if (sutehaiIndex == -1) {
            return new DahaiAction(-1, false);
        } else {
            if (canRichi(tsumohai, sutehaiIndex)) {
                return new DahaiAction(sutehaiIndex, true);
            } else {
                return new DahaiAction(sutehaiIndex, false);
            }
        }
    }

    @Override
    protected void updateStateForSelfDahai(Hai sutehai) {
        for (int playerId = 0; playerId < 4; playerId++) {
            if (playerToDoneRichi()[playerId]) {
                playerToAnzenhais()[playerId].add(sutehai.getId());
            }
        }
    }

    @Override
    protected void updateStateForOthersDahai(int actorId, Hai sutehai) {
        playerToAnzenhais()[actorId].add(sutehai.getId());
        for (int playerId = 0; playerId < 4; playerId++) {
            if (playerToDoneRichi()[playerId]) {
                playerToAnzenhais()[playerId].add(sutehai.getId());
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

    @Override
    protected void processRichi(JsonNode inputJson) {
        super.processRichi(inputJson);
        int actorId = inputJson.get("actor").asInt();
        playerToDoneRichi()[actorId] = true;
    }

    @Override
    protected String getClientName() {
        return "ymatsux-0.1.2.2";
    }
}
