package com.lututui.diariodehumor.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lututui.diariodehumor.PeriodoDia;
import com.lututui.diariodehumor.R;
import com.lututui.diariodehumor.RegistroDeHumor;
import com.lututui.diariodehumor.RegistroHumorRecyclerViewAdapter;
import com.lututui.diariodehumor.Sentimento;
import com.lututui.diariodehumor.SortedArrayList;
import com.lututui.diariodehumor.Util;
import com.lututui.diariodehumor.ViewSelecionada;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class RegistrosDeHumorActivity extends AppCompatActivity {
    private final boolean DEBUG_POPULAR_EXEMPLOS = false;
    private final List<Comparator<RegistroDeHumor>> comparators = Arrays.asList(
            Comparator.comparing(RegistroDeHumor::getData).reversed(),
            Comparator.comparing(RegistroDeHumor::getData),
            Comparator.comparing(RegistroDeHumor::getSentimento),
            Comparator.comparing(RegistroDeHumor::getSentimento).reversed()
    );
    private RecyclerView recyclerView;
    private RegistroHumorRecyclerViewAdapter recyclerViewAdapter;
    private SortedArrayList<RegistroDeHumor> registros;
    private final ActivityResultLauncher<Intent> launcherConfiguracoes = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            this::onConfiguracoesResult
    );
    private ActionMode actionMode;
    private ViewSelecionada selecionado;
    private final ActivityResultLauncher<Intent> launcherNovoRegistro = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            this::onCadastroResult
    );
    private final ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.opcoes_listagem_item, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            var id = item.getItemId();

            if (id == R.id.menu_listagem_item_editar) {
                editarRegistroHumor();
            } else if (id == R.id.menu_listagem_item_excluir) {
                excluirRegistroHumor();
                mode.finish();
            } else {
                return false;
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            selecionado.getView().setBackground(selecionado.getBackground());
            recyclerView.setEnabled(true);
            selecionado = null;
            actionMode = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros_de_humor);

        var sharedPref = getSharedPreferences(Util.SharedPreferences.FILE, MODE_PRIVATE);

        var esquemaCores = sharedPref.getInt(Util.SharedPreferences.SP_CORES, 0);

        if (esquemaCores == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (esquemaCores == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }


        recyclerView = findViewById(R.id.rv_registros);
        var layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        var modoOrdenacao = comparators.get(sharedPref.getInt(Util.SharedPreferences.SP_ORDEM, 0));

        registros = new SortedArrayList<>(modoOrdenacao);

        if (DEBUG_POPULAR_EXEMPLOS) popularExemplos();

        recyclerViewAdapter = new RegistroHumorRecyclerViewAdapter(this, registros);

        var onLongClickListener = new RegistroHumorRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                if (actionMode != null) return false;

                selecionado = new ViewSelecionada(
                        position,
                        view,
                        view.getBackground(),
                        registros.get(position)
                );

                view.setBackground(ContextCompat.getDrawable(
                        view.getContext(),
                        R.drawable.background_registro_selecionado
                ));

                recyclerView.setEnabled(false);
                actionMode = startSupportActionMode(actionCallback);

                return true;
            }
        };

        recyclerViewAdapter.setOnItemLongClickListener(onLongClickListener);

        recyclerView.setAdapter(recyclerViewAdapter);

        registerForContextMenu(recyclerView);
    }

    public void onCadastroResult(ActivityResult result) {
        if (result.getResultCode() != RegistrosDeHumorActivity.RESULT_OK) return;

        var intent = result.getData();

        if (intent == null) return;

        var rgHumor = (RegistroDeHumor) intent.getParcelableExtra(RegistroDeHumor.REGISTRO_DE_HUMOR_KEY);

        if (rgHumor == null) return;

        var editando = intent.getBooleanExtra(CadastroRegistroHumorActivity.MODO_KEY, false);

        if (editando) {
            registros.remove(selecionado.getPosicao());
        }

        var newPos = registros.addSorted(rgHumor);

        if (editando) {
            recyclerViewAdapter.notifyItemMoved(selecionado.getPosicao(), newPos);
            recyclerViewAdapter.notifyItemChanged(newPos);

            actionMode.finish();
        } else {
            recyclerViewAdapter.notifyItemInserted(newPos);
        }

        recyclerView.smoothScrollToPosition(newPos);

        // recyclerViewAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onConfiguracoesResult(ActivityResult result) {
        var sharedPref = getSharedPreferences(Util.SharedPreferences.FILE, MODE_PRIVATE);
        var sortComp = comparators.get(sharedPref.getInt(Util.SharedPreferences.SP_ORDEM, 0));

        registros.setComparator(sortComp);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    public void abrirSobre() {
        var intent = new Intent(this, SobreActivity.class);
        startActivity(intent);
    }

    public void abrirRegistrarHumor() {
        var intent = new Intent(this, CadastroRegistroHumorActivity.class);
        launcherNovoRegistro.launch(intent);
    }

    public void abrirConfiguracoes() {
        var intent = new Intent(this, ConfiguracoesActivity.class);
        launcherConfiguracoes.launch(intent);
    }

    private void excluirRegistroHumor() {
        registros.remove(selecionado.getRegistro());
        recyclerViewAdapter.notifyItemRemoved(selecionado.getPosicao());
    }

    public void editarRegistroHumor() {
        var intent = new Intent(this, CadastroRegistroHumorActivity.class);

        intent.putExtra(CadastroRegistroHumorActivity.MODO_KEY, true);
        intent.putExtra(RegistroDeHumor.REGISTRO_DE_HUMOR_KEY, selecionado.getRegistro());

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

            registros.addSorted(new RegistroDeHumor(
                    titulos[i],
                    Util.FormatoData.DD_MM_YYYY.toDate(datas[i]),
                    periodo,
                    sentimento,
                    especial,
                    anotacoes[i]
            ));
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opcoes_listagem, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        var menuItemId = item.getItemId();

        if (menuItemId == R.id.menu_listagem_adicionar) {
            abrirRegistrarHumor();
        } else if (menuItemId == R.id.menu_listagem_sobre) {
            abrirSobre();
        } else if (menuItemId == R.id.menu_listagem_configuracao) {
            abrirConfiguracoes();
        } else {
            return super.onContextItemSelected(item);
        }

        return true;
    }
}