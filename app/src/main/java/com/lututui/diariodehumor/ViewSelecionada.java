package com.lututui.diariodehumor;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.Objects;

public class ViewSelecionada {
    private final int posicao;
    private final View view;
    private final Drawable background;
    private final RegistroDeHumor registro;

    public ViewSelecionada(int posicao, View view, Drawable background, RegistroDeHumor registro) {
        this.posicao = posicao;
        this.view = view;
        this.background = background;
        this.registro = registro;
    }

    public Drawable getBackground() {
        return background;
    }

    public View getView() {
        return view;
    }

    public int getPosicao() {
        return posicao;
    }

    public RegistroDeHumor getRegistro() {
        return registro;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ViewSelecionada)) return false;
        var that = (ViewSelecionada) o;
        return posicao == that.posicao;
    }

    @Override
    public int hashCode() {
        return Objects.hash(posicao);
    }
}
