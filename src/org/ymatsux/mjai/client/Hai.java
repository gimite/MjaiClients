package org.ymatsux.mjai.client;

public class Hai {

    enum Type {
        MANZU,
        PINZU,
        SOZU,
        JIHAI
    }

    enum Ji {
        TON,
        NAN,
        SHA,
        PE,
        HAKU,
        HATSU,
        CHUN
    }

    private Type type;
    private int kazu;
    private Ji ji;
    private boolean isAkahai;

    // VisibleForTesting.
    Hai(Type type, int kazu, Ji ji, boolean isAkahai) {
        this.type = type;
        this.kazu = kazu;
        this.ji = ji;
        this.isAkahai = isAkahai;
    }

    public static Hai parse(String string) {
        // Handle jihai.
        if (string.length() == 1) {
            Ji ji;
            switch (string.charAt(0)) {
            case 'E':
                ji = Ji.TON;
                break;
            case 'S':
                ji = Ji.NAN;
                break;
            case 'W':
                ji = Ji.SHA;
                break;
            case 'N':
                ji = Ji.PE;
                break;
            case 'P':
                ji = Ji.HAKU;
                break;
            case 'F':
                ji = Ji.HATSU;
                break;
            case 'C':
                ji = Ji.CHUN;
                break;
            default:
                throw new IllegalArgumentException();
            }
            return new Hai(Type.JIHAI, 0, ji, false);
        }

        // Decide whether this is an akahai or not.
        boolean isAkahai = false;
        if (string.length() == 3) {
            if (string.charAt(2) == 'r') {
                isAkahai = true;
                string = string.substring(0, 2);
            } else {
                throw new IllegalArgumentException();
            }
        }

        if (string.length() != 2) {
            throw new IllegalArgumentException();
        }

        // Handle kazuhai.
        Type type;
        switch (string.charAt(1)) {
        case 'm':
            type = Type.MANZU;
            break;
        case 'p':
            type = Type.PINZU;
            break;
        case 's':
            type = Type.SOZU;
            break;
        default:
            throw new IllegalArgumentException();
        }

        int kazu;
        if ('1' <= string.charAt(0) && string.charAt(0) <= '9') {
            kazu = string.charAt(0) - '0';
        } else {
            throw new IllegalArgumentException();
        }

        return new Hai(type, kazu, null, isAkahai);
    }

    @Override
    public String toString() {
        switch(type) {
        case MANZU:
            return kazu + "m" + (isAkahai ? "r" : "");
        case SOZU:
            return kazu + "s" + (isAkahai ? "r" : "");
        case PINZU:
            return kazu + "p" + (isAkahai ? "r" : "");
        case JIHAI:
            switch (ji) {
            case TON:
                return "E";
            case NAN:
                return "S";
            case SHA:
                return "W";
            case PE:
                return "N";
            case HAKU:
                return "P";
            case HATSU:
                return "F";
            case CHUN:
                return "C";
            default:
                throw new IllegalStateException();
            }
        default:
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Hai other = (Hai) obj;
        if (isAkahai != other.isAkahai)
            return false;
        if (ji != other.ji)
            return false;
        if (kazu != other.kazu)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    public boolean equalsIgnoreAkahai(Hai other) {
        if (ji != other.ji)
            return false;
        if (kazu != other.kazu)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    // NOTE: Akahai is ignored in the id.
    public int getId() {
        switch (type) {
        case MANZU:
            return kazu - 1;
        case PINZU:
            return 9 + (kazu - 1);
        case SOZU:
            return 18 + (kazu - 1);
        case JIHAI:
            return 27 + ji.ordinal();
        default:
            throw new IllegalStateException();
        }
    }

    public boolean isAkahai() {
        return isAkahai;
    }

    public static Hai ofId(int id) {
        if (0 <= id && id < 9) {
            return new Hai(Type.MANZU, id + 1, null, false);
        } else if (9 <= id && id < 18) {
            return new Hai(Type.PINZU, id - 8, null, false);
        } else if (18 <= id && id < 27) {
            return new Hai(Type.SOZU, id - 17, null, false);
        } else if (27 <= id && id < 34) {
            return new Hai(Type.JIHAI, 0, Ji.values()[id - 27], false);
        }
        throw new IllegalArgumentException();
    }

    public Hai next() {
        if (type == Type.MANZU || type == Type.PINZU || type == Type.SOZU) {
           return new Hai(type, kazu == 9 ? 1 : kazu + 1, null, false);
        }
        switch (ji) {
        case TON:
            return new Hai(Type.JIHAI, 0, Ji.NAN, false);
        case NAN:
            return new Hai(Type.JIHAI, 0, Ji.SHA, false);
        case SHA:
            return new Hai(Type.JIHAI, 0, Ji.PE, false);
        case PE:
            return new Hai(Type.JIHAI, 0, Ji.TON, false);
        case HAKU:
            return new Hai(Type.JIHAI, 0, Ji.HATSU, false);
        case HATSU:
            return new Hai(Type.JIHAI, 0, Ji.CHUN, false);
        case CHUN:
            return new Hai(Type.JIHAI, 0, Ji.HAKU, false);
        default:
            throw new IllegalStateException();
        }
    }
}
