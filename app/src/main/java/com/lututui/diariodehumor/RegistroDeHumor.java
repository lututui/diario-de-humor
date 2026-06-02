package com.lututui.diariodehumor;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.lututui.diariodehumor.tags.Tag;
import com.lututui.diariodehumor.tags.TagRegistroDeHumor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class RegistroDeHumor {
    @Embedded
    private final RegistroDeHumorEntity registroDeHumor;
    @Relation(parentColumn = "id", entityColumn = "id", associateBy = @Junction(value = TagRegistroDeHumor.class, parentColumn = "registroId", entityColumn = "tagId"))
    private final List<Tag> tags;

    public RegistroDeHumor(
            @NonNull String titulo,
            @NonNull LocalDate data,
            @NonNull PeriodoDia periodoDia,
            @NonNull Sentimento sentimento,
            boolean especial,
            @NonNull String anotacoes,
            List<Tag> tags
    ) {
        this.registroDeHumor = new RegistroDeHumorEntity(
                titulo,
                data,
                periodoDia,
                sentimento,
                especial,
                anotacoes
        );
        this.tags = tags;
    }

    public RegistroDeHumor(RegistroDeHumorEntity registroDeHumor, List<Tag> tags) {
        this.registroDeHumor = registroDeHumor;
        this.tags = tags;
    }

    private static boolean tagsIguais(List<Tag> a, List<Tag> b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.size() != b.size()) return false;

        return new HashSet<>(a).equals(new HashSet<>(b));
    }

    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RegistroDeHumor)) return false;

        var that = (RegistroDeHumor) o;
        return Objects.equals(registroDeHumor, that.registroDeHumor) &&
                tagsIguais(this.tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registroDeHumor, new HashSet<>(tags));
    }

    public String getTitulo() {
        return registroDeHumor.getTitulo();
    }

    public LocalDate getData() {
        return registroDeHumor.getData();
    }

    public PeriodoDia getPeriodoDia() {
        return registroDeHumor.getPeriodoDia();
    }

    public Sentimento getSentimento() {
        return registroDeHumor.getSentimento();
    }

    public String getAnotacoes() {
        return registroDeHumor.getAnotacoes();
    }

    public boolean isEspecial() {
        return registroDeHumor.isEspecial();
    }

    public long getId() {
        return registroDeHumor.getId();
    }

    public void setId(long id) {
        registroDeHumor.setId(id);
    }

    public RegistroDeHumorEntity getEntity() {
        return registroDeHumor;
    }
}