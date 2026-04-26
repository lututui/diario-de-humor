package com.lututui.diariodehumor.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.lututui.diariodehumor.R;
import com.lututui.diariodehumor.Util;

public class ConfiguracoesActivity extends AppCompatActivity {

    private Spinner esquemaCoresWidget;
    private Spinner ordenacaoWidget;
    private Spinner formatoDataWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        esquemaCoresWidget = findViewById(R.id.spinner_esquema_cores);
        ordenacaoWidget = findViewById(R.id.spinner_ordenacao);
        formatoDataWidget = findViewById(R.id.spinner_formato_data);

        var sharedPref = getSharedPreferences(Util.SharedPreferences.FILE, MODE_PRIVATE);

        var esquemaCores = sharedPref.getInt(Util.SharedPreferences.SP_CORES, 0);
        var ordenacao = sharedPref.getInt(Util.SharedPreferences.SP_ORDEM, 0);
        var formatoData = sharedPref.getInt(Util.SharedPreferences.SP_DATA, 0);

        esquemaCoresWidget.setSelection(esquemaCores);
        ordenacaoWidget.setSelection(ordenacao);
        formatoDataWidget.setSelection(formatoData);

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
                if (position < 0 || position > 3) return;

                var sharedPref = getSharedPreferences(Util.SharedPreferences.FILE, MODE_PRIVATE);
                sharedPref.edit().putInt(Util.SharedPreferences.SP_DATA, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}