package com.lututui.diariodehumor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.lututui.diariodehumor.ViewSelecionada;

import java.util.ArrayList;
import java.util.List;

public class RegistrosDeHumorActivity extends AppCompatActivity {
    private final boolean DEBUG_POPULAR_EXEMPLOS = false;

    private RecyclerView recyclerView;
    private RegistroHumorRecyclerViewAdapter recyclerViewAdapter;

    private ActivityResultLauncher<Intent> launcherNovoRegistro;

    private List<RegistroDeHumor> registros;

    private ActionMode actionMode;

    private ViewSelecionada selecionado;

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
            actionMode = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros_de_humor);

        recyclerView = findViewById(R.id.rv_registros);
        var layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        registros = new ArrayList<>();

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

        var onClickListener = new RegistroHumorRecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (actionMode == null) return;
            }
        };

        recyclerViewAdapter.setOnItemClickListener(onClickListener);
        recyclerViewAdapter.setOnItemLongClickListener(onLongClickListener);

        recyclerView.setAdapter(recyclerViewAdapter);

        launcherNovoRegistro = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::onActivityResult
        );

        registerForContextMenu(recyclerView);
    }

    public void onActivityResult(ActivityResult result) {
        // implement mode edit/add
        if (result.getResultCode() != RegistrosDeHumorActivity.RESULT_OK) return;

        var intent = result.getData();

        if (intent == null) return;

        var rgHumor = (RegistroDeHumor) intent.getParcelableExtra(RegistroDeHumor.REGISTRO_DE_HUMOR_KEY);

        if (rgHumor == null) return;

        var editando = intent.getBooleanExtra(CadastroRegistroHumorActivity.MODO_KEY, false);

        if (editando) {
            registros.set(selecionado.getPosicao(), rgHumor);
            recyclerViewAdapter.notifyItemChanged(selecionado.getPosicao());
            actionMode.finish();
        } else {
            registros.add(rgHumor);
            recyclerViewAdapter.notifyItemInserted(registros.size() - 1);
        }

        // recyclerViewAdapter.notifyDataSetChanged();
    }

    public void abrirSobre() {
        var intent = new Intent(this, SobreActivity.class);
        startActivity(intent);
    }

    public void abrirRegistrarHumor() {
        var intent = new Intent(this, CadastroRegistroHumorActivity.class);
        launcherNovoRegistro.launch(intent);
    }

    private void excluirRegistroHumor() {
        registros.remove(selecionado.getRegistro());
        recyclerViewAdapter.notifyItemRemoved(selecionado.getPosicao());
    }

    public void editarRegistroHumor() {
        var intent = new Intent(this, CadastroRegistroHumorActivity.class);

        intent.putExtra(CadastroRegistroHumorActivity.MODO_KEY, true);
        intent.putExtra(
                RegistroDeHumor.REGISTRO_DE_HUMOR_KEY,
                selecionado.getRegistro()
        );

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
        } else {
            return super.onContextItemSelected(item);
        }

        return true;
    }
}