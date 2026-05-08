package com.lututui.diariodehumor.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.lututui.diariodehumor.tags.Tag;

import java.util.List;

@Dao
public abstract class TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long inserir(Tag tag);

    @Query("SELECT * FROM Tag WHERE nome = :nome LIMIT 1")
    public abstract Tag buscarPorNome(String nome);

    @Delete
    public abstract void deletar(Tag tag);

    @Query("SELECT * FROM Tag WHERE id NOT IN (:tagIds)")
    public abstract List<Tag> getTagsRestantes(List<Long> tagIds);

    @Query("SELECT * FROM Tag WHERE id = :id")
    public abstract Tag getTag(long id);

    @Update
    public abstract void _update(Tag tag);

    @Query("SELECT COUNT(*) FROM Tag WHERE nome = :nome AND id != :id")
    public abstract int contarComMesmoNome(String nome, long id);

    @Transaction
    public boolean update(Tag tag) {
        if (contarComMesmoNome(tag.getNome(), tag.getId()) > 0) return false;

        _update(tag);
        return true;
    }

    @Query("SELECT * FROM Tag")
    public abstract List<Tag> getTags();
}