package com.lututui.diariodehumor.activity;


import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.lututui.diariodehumor.DiarioHumorDB;
import com.lututui.diariodehumor.PeriodoDia;
import com.lututui.diariodehumor.R;
import com.lututui.diariodehumor.RegistroDeHumor;
import com.lututui.diariodehumor.Sentimento;
import com.lututui.diariodehumor.Util;
import com.lututui.diariodehumor.tags.Tag;
import com.lututui.diariodehumor.tags.TagRegistroDeHumor;

import java.util.Arrays;
import java.util.List;

public class ConfiguracoesActivity extends AppCompatActivity {
    private Spinner esquemaCoresWidget;
    private Spinner ordenacaoWidget;
    private Spinner formatoDataWidget;
    private CheckBox checkBoxWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        esquemaCoresWidget = findViewById(R.id.spinner_esquema_cores);
        ordenacaoWidget = findViewById(R.id.spinner_ordenacao);
        formatoDataWidget = findViewById(R.id.spinner_formato_data);
        checkBoxWidget = findViewById(R.id.checkbox_demo);

        var sharedPref = getSharedPreferences(Util.SharedPreferences.FILE, MODE_PRIVATE);

        var esquemaCores = sharedPref.getInt(Util.SharedPreferences.SP_CORES, 0);
        var ordenacao = sharedPref.getInt(Util.SharedPreferences.SP_ORDEM, 0);
        var formatoData = sharedPref.getInt(Util.SharedPreferences.SP_DATA, 0);
        var modoDemo = sharedPref.getBoolean(Util.SharedPreferences.SP_DEMO, false);

        esquemaCoresWidget.setSelection(esquemaCores);
        ordenacaoWidget.setSelection(ordenacao);
        formatoDataWidget.setSelection(formatoData);
        checkBoxWidget.setChecked(modoDemo);

        esquemaCoresWidget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < 0 || position > 2) return;

                if (position == 0) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else if (position == 1) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }

                var sharedPref = getSharedPreferences(Util.SharedPreferences.FILE, MODE_PRIVATE);
                sharedPref.edit().putInt(Util.SharedPreferences.SP_CORES, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ordenacaoWidget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < 0 || position > 4) return;

                var sharedPref = getSharedPreferences(Util.SharedPreferences.FILE, MODE_PRIVATE);
                sharedPref.edit().putInt(Util.SharedPreferences.SP_ORDEM, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        formatoDataWidget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < 0 || position > 4) return;

                var sharedPref = getSharedPreferences(Util.SharedPreferences.FILE, MODE_PRIVATE);
                sharedPref.edit().putInt(Util.SharedPreferences.SP_DATA, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        checkBoxWidget.setOnCheckedChangeListener((buttonView, isChecked) -> {
            var sPref = getSharedPreferences(Util.SharedPreferences.FILE, MODE_PRIVATE);
            sPref.edit().putBoolean(Util.SharedPreferences.SP_DEMO, isChecked).apply();

            if (isChecked) {
                DiarioHumorDB.resetDemo(this);

                popularExemplos();
            }
        });
    }

    private void popularExemplos() {
        var rsc = getResources();

        String[] titulos = rsc.getStringArray(R.array.titulos);
        int[] sentimentos = rsc.getIntArray(R.array.sentimentos);
        int[] periodos = rsc.getIntArray(R.array.periodo);
        int[] momentosEspeciais = rsc.getIntArray(R.array.especial);
        String[] datas = rsc.getStringArray(R.array.datas);
        String[] anotacoes = rsc.getStringArray(R.array.anotacoes);

        var size = titulos.length;
        var db = DiarioHumorDB.getInstance(this);

        Tag[][] tags = new Tag[size][];

        try (var tagsArrays = rsc.obtainTypedArray(R.array.tags)) {
            for (int i = 0; i < size; i++) {
                var subArrayId = tagsArrays.getResourceId(i, 0);

                if (subArrayId == 0) continue;

                var subArray = rsc.getStringArray(subArrayId);

                tags[i] = registarTagsExemplo(subArray);
            }
        } catch (Resources.NotFoundException exc) {
            Arrays.fill(tags, new Tag[0]);
        }


        for (int i = 0; i < size; i++) {
            var periodo = PeriodoDia.values()[periodos[i]];
            var sentimento = Sentimento.values()[sentimentos[i]];
            var especial = momentosEspeciais[i] == 1;

            var rgDao = db.getRegistroDeHumorDao();

            var rg = new RegistroDeHumor(
                    titulos[i],
                    Util.FormatoData.DD_MM_YYYY.toDate(this, datas[i]),
                    periodo,
                    sentimento,
                    especial,
                    anotacoes[i],
                    List.of(tags[i])
            );

            var novoId = rgDao.inserir(rg.getEntity());

            rg.setId(novoId);

            for (var tag : tags[i]) {
                rgDao.inserirCrossRef(new TagRegistroDeHumor(rg.getId(), tag.getId()));
            }
        }
    }

    private Tag[] registarTagsExemplo(String[] tagNames) {
        var tags = new Tag[tagNames.length];
        var tagDao = DiarioHumorDB.getInstance(this).getTagDao();

        for (int i = 0; i < tagNames.length; i++) {
            var tagName = tagNames[i];

            var novaTag = new Tag(tagName);
            var tagId = tagDao.inserir(novaTag);

            if (tagId == -1) {
                tags[i] = tagDao.buscarPorNome(novaTag.getNome());
            } else {
                novaTag.setId(tagId);
                tags[i] = novaTag;
            }
        }

        return tags;
    }
}