package org.ymatsux.mjai.client;

public class ClientActions {
    interface SelfTsumoAction {}
    interface OthersDahaiAction {}

    public static class DahaiAction implements SelfTsumoAction {
        public final int sutehaiIndex;
        public final boolean doRichi;

        public DahaiAction(int sutehaiIndex, boolean doRichi) {
            this.sutehaiIndex = sutehaiIndex;
            this.doRichi = doRichi;
        }
    }

    public static class TsumohoAction implements SelfTsumoAction {}

    public static class NoneAction implements OthersDahaiAction {}

    public static class RonhoAction implements OthersDahaiAction {}
}
