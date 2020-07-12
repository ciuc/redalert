package ro.antiprotv.sugar.db;

public enum MeasureUnit {
    KGS(1), BOXES(2), BOTTLES(3), PIECES(4), PACKS(5), OTHER(99);

    int code;

    MeasureUnit(int code) {
        this.code = code;
    }

    public static int getCodeFromString(String measureUnit) {
        int code = 99;
        for (MeasureUnit m :
                MeasureUnit.values()) {
            if (m.toString().equals(measureUnit)) {
                code = m.code;
                break;
            }
        }
        return code;
    }

    public static String fromCode(int code){
        MeasureUnit mu = MeasureUnit.OTHER;
        for (MeasureUnit m :
                MeasureUnit.values()) {
            if (m.code == code) {
                mu = m;
                break;
            }
        }
        return mu.toString();
    }
}
