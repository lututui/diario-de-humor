package com.lututui.diariodehumor.tags;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.lututui.diariodehumor.RegistroDeHumorEntity;

@Entity(primaryKeys = {"registroId", "tagId"}, foreignKeys = {
        @ForeignKey(entity = RegistroDeHumorEntity.class, parentColumns = "id", childColumns = "registroId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Tag.class, parentColumns = "id", childColumns = "tagId", onDelete = ForeignKey.CASCADE)
})
public class TagRegistroDeHumor {
    @ColumnInfo(index = true)
    private final long registroId;

    @ColumnInfo(index = true)
    private final long tagId;

    public TagRegistroDeHumor(long registroId, long tagId) {
        this.registroId = registroId;
        this.tagId = tagId;
    }

    public long getRegistroId() {
        return registroId;
    }

    public long getTagId() {
        return tagId;
    }
}
