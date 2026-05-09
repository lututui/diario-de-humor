package com.lututui.diariodehumor.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.lututui.diariodehumor.DiarioHumorDB;
import com.lututui.diariodehumor.R;
import com.lututui.diariodehumor.Util;
import com.lututui.diariodehumor.tags.Tag;

import java.util.Random;

public class CadastroTagActivity extends AppCompatActivity {
    public static final String ID_KEY = "ID_KEY";
    public static final String EDIT_KEY = "EDIT_KEY";

    private SeekBar seekRWidget;
    private SeekBar seekGWidget;
    private SeekBar seekBWidget;
    private View previewWidget;
    private EditText nomeTagWidget;

    private boolean editando;
    private Tag tagOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_tag);

        seekRWidget = findViewById(R.id.seekbar_r);
        seekGWidget = findViewById(R.id.seekbar_g);
        seekBWidget = findViewById(R.id.seekbar_b);
        previewWidget = findViewById(R.id.view_preview_cor);
        nomeTagWidget = findViewById(R.id.text_nome_tag);

        editando = getIntent().getBooleanExtra(EDIT_KEY, false);

        if (editando) {
            var labelTagWidget = (TextView) findViewById(R.id.label_adicionar_tag);
            var adicionarWidget = (Button) findViewById(R.id.button_adicionar);

            labelTagWidget.setText(R.string.editar_tag);
            adicionarWidget.setText(R.string.editar_tag);

            var tagId = getIntent().getLongExtra(ID_KEY, -1);
            tagOriginal = DiarioHumorDB.getInstance(this).getTagDao().getTag(tagId);
        }

        if (tagOriginal != null) {
            var cor = tagOriginal.getCor();

            seekRWidget.setProgress(Color.red(cor));
            seekGWidget.setProgress(Color.green(cor));
            seekBWidget.setProgress(Color.blue(cor));

            nomeTagWidget.setText(tagOriginal.getNome());
        } else {
            var r = new Random();

            seekRWidget.setProgress(r.nextInt(255));
            seekGWidget.setProgress(r.nextInt(255));
            seekBWidget.setProgress(r.nextInt(255));
        }

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
        var drawable = (GradientDrawable) ContextCompat.getDrawable(
                this,
                R.drawable.background_tag
        );

        if (drawable == null) return;

        drawable = (GradientDrawable) drawable.mutate();

        drawable.setColor(Color.rgb(
                seekRWidget.getProgress(),
                seekGWidget.getProgress(),
                seekBWidget.getProgress()
        ));

        previewWidget.setBackground(drawable);
    }

    public void salvar(View view) {
        var nome = nomeTagWidget.getText().toString().trim();

        if (nome.isEmpty()) {
            Util.Alert.mostrarAviso(
                    this,
                    getString(R.string.tag_sem_nome),
                    getString(R.string.tag_sem_nome_erro),
                    null
            );

            return;
        }

        int cor = Color.rgb(
                seekRWidget.getProgress(),
                seekGWidget.getProgress(),
                seekBWidget.getProgress()
        );

        var newTag = new Tag(nome, cor);

        if (newTag.equals(tagOriginal)) {
            setResult(RESULT_CANCELED);
            finish();

            return;
        }

        var db = DiarioHumorDB.getInstance(this);
        var tagDao = db.getTagDao();

        if (editando) {
            newTag.setId(tagOriginal.getId());

            var updateOk = tagDao.update(newTag);

            if (!updateOk) {
                tagJaExiste(newTag.getNome());

                return;
            }

            setResult(RESULT_OK);
        } else {
            var tagId = tagDao.inserir(newTag);

            if (tagId == -1) {
                tagJaExiste(newTag.getNome());

                return;
            }

            newTag.setId(tagId);

            var intent = new Intent();

            intent.putExtra(ID_KEY, newTag.getId());

            setResult(RESULT_OK, intent);
        }

        finish();
    }

    private void tagJaExiste(String nome) {
        Util.Alert.mostrarAviso(
                this,
                getString(R.string.tag_existe_titulo),
                getString(R.string.tag_existe_erro, nome),
                null
        );
    }

    public void cancelar(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}