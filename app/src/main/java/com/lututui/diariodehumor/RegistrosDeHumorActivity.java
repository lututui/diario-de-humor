package com.lututui.diariodehumor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RegistrosDeHumorActivity extends AppCompatActivity {
    private RecyclerView rvRegistrosDeHumor;
    private RecyclerView.LayoutManager rvLayoutManager;
    private RegistroHumorRecyclerViewAdapter rgRecyclerViewAdapter;
    private RegistroHumorRecyclerViewAdapter.OnItemClickListener onItemClickListener;

    private ActivityResultLauncher<Intent> launcherNovoRegistro;

    private List<RegistroDeHumor> registros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros_de_humor);

        rvRegistrosDeHumor = findViewById(R.id.rv_registros);
        rvLayoutManager = new LinearLayoutManager(this);

        rvRegistrosDeHumor.setLayoutManager(rvLayoutManager);
        rvRegistrosDeHumor.setHasFixedSize(true);
        rvRegistrosDeHumor.addItemDecoration(new DividerItemDecoration(
                this,
                LinearLayout.VERTICAL
        ));

        onItemClickListener = new RegistroHumorRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                var rgHumor = registros.get(position);

                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.titulo_momento) + ": " + rgHumor.getTitulo() + "\n" +
                                getString(R.string.data) + ": " + rgHumor.getData() + "\n" +
                                getString(R.string.periodo_dia) + ": " +
                                getString(rgHumor.getPeriodoDia().getResourceID()) + "\n" +
                                getString(R.string.prompt_sentimento) + " " +
                                getString(rgHumor.getSentimento().getResourceID()) + "\n" +
                                getString(R.string.momento_especial_q) + " " + (
                                rgHumor.isEspecial() ?
                                        getString(R.string.sim) :
                                        getString(R.string.nao)
                        ) + "\n" + getString(R.string.anotacoes) + ": " + rgHumor.getAnotacoes(),
                        Toast.LENGTH_LONG
                ).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                onItemClick(view, position);
            }
        };

        registros = new ArrayList<>();

        // popularExemplos();

        rgRecyclerViewAdapter = new RegistroHumorRecyclerViewAdapter(
                this,
                registros,
                onItemClickListener
        );

        rvRegistrosDeHumor.setAdapter(rgRecyclerViewAdapter);

        launcherNovoRegistro = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() != RegistrosDeHumorActivity.RESULT_OK) return;

                    var intent = result.getData();

                    if (intent == null) return;

                    var bundle = intent.getExtras();

                    if (bundle == null) return;

                    var titulo = bundle.getString(CadastroRegistroHumorActivity.KEY_TITULO);
                    var data = bundle.getString(CadastroRegistroHumorActivity.KEY_DATA);
                    var periodo = PeriodoDia.values()[bundle.getInt(CadastroRegistroHumorActivity.KEY_PERIODO)];
                    var sentimento = Sentimento.values()[bundle.getInt(CadastroRegistroHumorActivity.KEY_SENTIMENTO)];
                    var especial = bundle.getBoolean(CadastroRegistroHumorActivity.KEY_ESPECIAL);
                    var anotacoes = bundle.getString(CadastroRegistroHumorActivity.KEY_ANOTACOES);

                    var rgHumor = new RegistroDeHumor(
                            titulo,
                            data,
                            periodo,
                            sentimento,
                            especial,
                            anotacoes
                    );

                    registros.add(rgHumor);

                    rgRecyclerViewAdapter.notifyDataSetChanged();
                }
        );
    }

    public void abrirSobre(View view) {
        var intent = new Intent(this, AutoriaActivity.class);
        startActivity(intent);
    }

    public void abrirRegistrarHumor(View view) {
        var intent = new Intent(this, CadastroRegistroHumorActivity.class);
        launcherNovoRegistro.launch(intent);
    }

    private void popularExemplos() {
        String[] titulos = getResources().getStringArray(R.array.titulos);
        int[] sentimentos = getResources().getIntArray(R.array.sentimentos);
        int[] periodos = getResources().getIntArray(R.array.periodo);
        int[] momentosEspeciais = getResources().getIntArray(R.array.especial);
        String[] datas = getResources().getStringArray(R.array.datas);
        String[] anotacoes = getResources().getStringArray(R.array.anotacoes);

        for (int i = 0; i < titulos.length; i++) {
            var periodo = PeriodoDia.values()[periodos[i]];
            var sentimento = Sentimento.values()[sentimentos[i]];
            var especial = momentosEspeciais[i] == 1;

            registros.add(new RegistroDeHumor(
                    titulos[i],
                    datas[i],
                    periodo,
                    sentimento,
                    especial,
                    anotacoes[i]
            ));
        }


    }
}