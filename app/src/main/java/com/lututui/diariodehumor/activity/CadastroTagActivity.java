package com.lututui.diariodehumor.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.lututui.diariodehumor.DiarioHumorDB;
import com.lututui.diariodehumor.R;
import com.lututui.diariodehumor.Util;
import com.lututui.diariodehumor.tags.Tag;

import java.util.Optional;
import java.util.Random;

public class CadastroTagActivity extends AppCompatActivity {
    public static final String ID_KEY = "ID_KEY";

    private SeekBar seekRWidget;
    private SeekBar seekGWidget;
    private SeekBar seekBWidget;
    private View previewWidget;
    private EditText nomeTagWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_tag);

        seekRWidget = findViewById(R.id.seekbar_r);
        seekGWidget = findViewById(R.id.seekbar_g);
        seekBWidget = findViewById(R.id.seekbar_b);
        previewWidget = findViewById(R.id.view_preview_cor);
        nomeTagWidget = findViewById(R.id.text_nome_tag);

        var r = new Random();

        seekRWidget.setProgress(r.nextInt(255));
        seekGWidget.setProgress(r.nextInt(255));
        seekBWidget.setProgress(r.nextInt(255));

        var listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                atualizarPreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        seekRWidget.setOnSeekBarChangeListener(listener);
        seekGWidget.setOnSeekBarChangeListener(listener);
        seekBWidget.setOnSeekBarChangeListener(listener);

        atualizarPreview();
    }

    private void atualizarPreview() {
        Optional.ofNullable((GradientDrawable) ContextCompat.getDrawable(
                this,
                R.drawable.background_tag
        )).map(GradientDrawable::mutate).ifPresent(d -> {
            ((GradientDrawable) d).setColor(Color.rgb(
                    seekRWidget.getProgress(),
                    seekGWidget.getProgress(),
                    seekBWidget.getProgress()
            ));
            previewWidget.setBackground(d);
        });
    }

    public void salvar(View view) {
        var nome = nomeTagWidget.getText().toString().trim();
        if (nome.isEmpty()) return;

        int cor = Color.rgb(
                seekRWidget.getProgress(),
                seekGWidget.getProgress(),
                seekBWidget.getProgress()
        );

        var newTag = new Tag(nome, cor);

        var db = DiarioHumorDB.getInstance(this);
        var tagDao = db.getTagDao();

        var tagId = tagDao.inserir(newTag);

        if (tagId == -1) {
            Util.Alert.mostrarAviso(
                    this,
                    getString(R.string.tag_existe_titulo),
                    getString(R.string.tag_existe_erro, newTag.getNome()),
                    null
            );

            return;
        }

        newTag.setId(tagId);

        var intent = new Intent();

        intent.putExtra(ID_KEY, tagId);

        setResult(RESULT_OK);
        finish();
    }

    public void cancelar(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}