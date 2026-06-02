package com.lututui.diariodehumor;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Objects;

@Entity(tableName = "registro_de_humor")
public class RegistroDeHumorEntity {
    @NonNull
    @ColumnInfo(index = true)
    private final String titulo;
    @NonNull
    @ColumnInfo
    private final LocalDate data;
    @NonNull
    @ColumnInfo
    private final PeriodoDia periodoDia;
    @NonNull
    @ColumnInfo
    private final Sentimento sentimento;
    @ColumnInfo
    private final boolean especial;
    @NonNull
    @ColumnInfo
    private final String anotacoes;
    @PrimaryKey(autoGenerate = true)
    private long id;

    public RegistroDeHumorEntity(
            @NonNull String titulo,
            @NonNull LocalDate data,
            @NonNull PeriodoDia periodoDia,
            @NonNull Sentimento sentimento,
            boolean especial,
            @NonNull String anotacoes
    ) {
        this.titulo = titulo;
        this.data = data;
        this.periodoDia = periodoDia;
        this.sentimento = sentimento;
        this.especial = especial;
        this.anotacoes = anotacoes;
    }

    @NonNull
    public String getTitulo() {
        return titulo;
    }

    @NonNull
    public LocalDate getData() {
        return data;
    }

    @NonNull
    public PeriodoDia getPeriodoDia() {
        return periodoDia;
    }

    @NonNull
    public Sentimento getSentimento() {
        return sentimento;
    }

    public boolean isEspecial() {
        return especial;
    }

    @NonNull
    public String getAnotacoes() {
        return anotacoes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RegistroDeHumorEntity)) return false;

        var that = (RegistroDeHumorEntity) o;
        return especial == that.especial && Objects.equals(titulo, that.titulo) &&
                Objects.equals(data, that.data) && periodoDia == that.periodoDia &&
                sentimento == that.sentimento && Objects.equals(anotacoes, that.anotacoes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, data, periodoDia, sentimento, especial, anotacoes);
    }
}



