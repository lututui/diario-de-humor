package com.lututui.diariodehumor;

public enum PeriodoDia {
    MANHA(R.string.periodo_manha),
    TARDE(R.string.periodo_tarde),
    NOITE(R.string.periodo_noite),
    MADRUGADA(R.string.periodo_madrugada),
    ;

    private final int resourceID;

    PeriodoDia(int rID) {
        this.resourceID = rID;
    }

    public int getResourceID() {
        return resourceID;
    }
}
