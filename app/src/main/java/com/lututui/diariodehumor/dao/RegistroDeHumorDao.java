package com.lututui.diariodehumor.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.lututui.diariodehumor.RegistroDeHumor;
import com.lututui.diariodehumor.RegistroDeHumorEntity;
import com.lututui.diariodehumor.tags.TagCrossRefRegistroDeHumor;

import java.util.List;

@Dao
public interface RegistroDeHumorDao {
    @Transaction
    @Query("SELECT * FROM registro_de_humor")
    List<RegistroDeHumor> getRegistros();

    @Insert
    void inserirCrossRef(TagCrossRefRegistroDeHumor crossRef);

    @Query("DELETE FROM TagCrossRefRegistroDeHumor WHERE registroId = :id")
    void removerCrossRef(long id);

    @Transaction
    @Query("SELECT * FROM registro_de_humor WHERE id = :id")
    RegistroDeHumor getRegistro(long id);

    @Update
    int update(RegistroDeHumorEntity pessoa);

    @Insert
    long inserir(RegistroDeHumorEntity registro);
}
