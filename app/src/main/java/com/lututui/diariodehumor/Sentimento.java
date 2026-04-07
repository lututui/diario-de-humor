package com.lututui.diariodehumor;

public enum Sentimento {
    MUITO_BEM(R.string.sentimento_5),
    BEM(R.string.sentimento_4),
    NEUTRO(R.string.sentimento_3),
    RUIM(R.string.sentimento_2),
    MUITO_RUIM(R.string.sentimento_1);


    private final int resourceID;

    Sentimento(int rID) {
        this.resourceID = rID;
    }

    public int getResourceID() {
        return resourceID;
    }
}
