package com.lututui.diariodehumor;

public enum Sentimento {
    MUITO_RUIM(R.string.sentimento_1),
    RUIM(R.string.sentimento_2),
    NEUTRO(R.string.sentimento_3),
    BEM(R.string.sentimento_4),
    MUITO_BEM(R.string.sentimento_5);

    private final int resourceID;

    Sentimento(int rID) {
        this.resourceID = rID;
    }

    public int getResourceID() {
        return resourceID;
    }
}
