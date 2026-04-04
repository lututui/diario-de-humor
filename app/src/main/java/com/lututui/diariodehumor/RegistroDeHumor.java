package com.lututui.diariodehumor;

import androidx.annotation.NonNull;

public class RegistroDeHumor {
    private String titulo;
    private String data;
    private PeriodoDia periodoDia;
    private Sentimento sentimento;
    private boolean especial;
    private String anotacoes;

    public RegistroDeHumor(String titulo, String data, PeriodoDia periodoDia, Sentimento sentimento,
                           boolean especial, String anotacoes) {
        this.titulo = titulo;
        this.data = data;
        this.periodoDia = periodoDia;
        this.sentimento = sentimento;
        this.especial = especial;
        this.anotacoes = anotacoes;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public PeriodoDia getPeriodoDia() {
        return periodoDia;
    }

    public void setPeriodoDia(PeriodoDia periodoDia) {
        this.periodoDia = periodoDia;
    }

    public Sentimento getSentimento() {
        return sentimento;
    }

    public void setSentimento(Sentimento sentimento) {
        this.sentimento = sentimento;
    }

    public boolean isEspecial() {
        return especial;
    }

    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }
}
