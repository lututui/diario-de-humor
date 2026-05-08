package com.lututui.diariodehumor.tags;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;
import java.util.Random;

@Entity(indices = @Index(value = "nome", unique = true))
public class Tag {
    @NonNull
    private final String nome;
    @ColumnInfo(index = true)
    private final int cor;
    @PrimaryKey(autoGenerate = true)
    private long id;

    public Tag(@NonNull String nome, int cor) {
        this.nome = nome;
        this.cor = cor;
    }

    @Ignore
    public Tag(@NonNull String nome) {
        var r = new Random();

        this.nome = nome;
        this.cor = Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }

    @NonNull
    public String getNome() {
        return nome;
    }

    public int getCor() {
        return cor;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tag)) return false;

        var tag = (Tag) o;
        return Objects.equals(nome, tag.nome) && cor == tag.cor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cor);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
