package com.lututui.diariodehumor;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RegistroDeHumor implements Parcelable {
    public static final Creator<RegistroDeHumor> CREATOR = new Creator<>() {
        @Override
        public RegistroDeHumor createFromParcel(Parcel source) {
            var titulo = source.readString();
            var data = source.readLong();
            var periodo = PeriodoDia.values()[source.readInt()];
            var sentimento = Sentimento.values()[source.readInt()];
            var especial = source.readInt() == 1;
            var anotacoes = source.readString();

            return new RegistroDeHumor(titulo, new Date(data), periodo, sentimento, especial, anotacoes);
        }

        @Override
        public RegistroDeHumor[] newArray(int size) {
            return new RegistroDeHumor[size];
        }
    };
    public static final String REGISTRO_DE_HUMOR_KEY = "REGISTRO_DE_HUMOR_KEY";
    private String titulo;
    private Date data;
    private PeriodoDia periodoDia;
    private Sentimento sentimento;
    private boolean especial;
    private String anotacoes;

    public RegistroDeHumor(
            String titulo,
            Date data,
            PeriodoDia periodoDia,
            Sentimento sentimento,
            boolean especial,
            String anotacoes
    ) {
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(titulo);
        dest.writeLong(data.getTime());
        dest.writeInt(periodoDia.ordinal());
        dest.writeInt(sentimento.ordinal());
        dest.writeInt(especial ? 1 : 0);
        dest.writeString(anotacoes);
    }
}
