package com.lututui.diariodehumor.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.lututui.diariodehumor.tags.Tag;

import java.util.List;

@Dao
public interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long inserir(Tag tag);

    @Query("SELECT * FROM Tag WHERE nome = :nome LIMIT 1")
    Tag buscarPorNome(String nome);

    @Delete
    void deletar(Tag tag);

    @Query("SELECT * FROM Tag WHERE id NOT IN (:tagIds)")
    List<Tag> getTagsRestantes(List<Long> tagIds);

    @Query("SELECT * FROM Tag WHERE id = :id")
    Tag getTag(long id);
}