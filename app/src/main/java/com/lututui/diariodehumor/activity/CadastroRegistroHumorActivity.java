package com.lututui.diariodehumor.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lututui.diariodehumor.DiarioHumorDB;
import com.lututui.diariodehumor.PeriodoDia;
import com.lututui.diariodehumor.R;
import com.lututui.diariodehumor.RegistroDeHumor;
import com.lututui.diariodehumor.Sentimento;
import com.lututui.diariodehumor.Util;
import com.lututui.diariodehumor.tags.Tag;
import com.lututui.diariodehumor.tags.TagRegistroDeHumor;
import com.lututui.diariodehumor.tags.TagSpinnerAdapter;
import com.lututui.diariodehumor.tags.TagsView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import java.util.stream.Collectors;

public class CadastroRegistroHumorActivity extends AppCompatActivity {
    public static final String MODO_KEY = "MODO_KEY";
    public static final String ID_KEY = "ID_KEY";

    private EditText nomeMomentoWidget;
    private EditText anotacoesWidget;
    private EditText dataWidget;
    private Spinner periodoDiaWidget;
    private RadioGroup sentimentosWidget;
    private CheckBox momentoEspecialWidget;
    private TagsView tagsWidget;

    private Calendar calendar;
    private boolean editando;
    private RegistroDeHumor registroDeHumorOriginal;


    private TagsView dialogTagsSelecionadas;
    private Spinner dialogTagsDisponiveis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_registro_humor);

        calendar = Calendar.getInstance();

        nomeMomentoWidget = findViewById(R.id.text_momento);
        anotacoesWidget = findViewById(R.id.text_anotacoes);
        dataWidget = findViewById(R.id.text_data);
        periodoDiaWidget = findViewById(R.id.spinner_periodo);
        sentimentosWidget = findViewById(R.id.rg_sentimento);
        momentoEspecialWidget = findViewById(R.id.checkbox_especial);
        tagsWidget = findViewById(R.id.tags_view_cadastro);

        editando = getIntent().getBooleanExtra(MODO_KEY, false);

        tagsWidget.setClickListener((view, position) -> {
            if (position == Integer.MIN_VALUE) {
                dialogSelecionarTags();
            } else {
                tagsWidget.removeTag(position);
            }
        });

        if (editando) {
            var tituloWidget = (TextView) findViewById(R.id.label_titulo);
            tituloWidget.setText(getString(R.string.editando_registro_de_humor));

            var rgId = getIntent().getLongExtra(ID_KEY, -1);
            var db = DiarioHumorDB.getInstance(this);

            registroDeHumorOriginal = db.getRegistroDeHumorDao().getRegistro(rgId);
        }

        if (registroDeHumorOriginal != null) {
            nomeMomentoWidget.setText(registroDeHumorOriginal.getTitulo());
            calendar.setTime(registroDeHumorOriginal.getData());
            periodoDiaWidget.setSelection(registroDeHumorOriginal.getPeriodoDia().ordinal() + 1);
            sentimentosWidget.check(sentimentosWidget.getChildAt(registroDeHumorOriginal.getSentimento()
                                                                                        .ordinal())
                                                     .getId());
            momentoEspecialWidget.setChecked(registroDeHumorOriginal.isEspecial());
            anotacoesWidget.setText(registroDeHumorOriginal.getAnotacoes());

            tagsWidget.setTags(new ArrayList<>(registroDeHumorOriginal.getTags()), true, true);
        } else {
            tagsWidget.setTags(new ArrayList<>(), true, true);
        }

        setDataWidget();
    }

    private void setDataWidget() {
        var sharedPref = getSharedPreferences(Util.SharedPreferences.FILE, MODE_PRIVATE);
        var modoData = Util.FormatoData.values()[sharedPref.getInt(Util.SharedPreferences.SP_DATA, 0)];

        dataWidget.setText(modoData.toString(this, calendar.getTime()));
    }

    public void salvar() {
        var nomeMomento = Optional.ofNullable(nomeMomentoWidget.getText())
                                  .map(o -> o.toString().trim()).orElse("");

        if (nomeMomento.isBlank()) {
            Toast.makeText(this, R.string.erro_sem_titulo, Toast.LENGTH_LONG).show();
            nomeMomentoWidget.requestFocus();
            return;
        }

        var periodoId = periodoDiaWidget.getSelectedItemPosition();

        if (periodoId == AdapterView.INVALID_POSITION || periodoId == 0) {
            Toast.makeText(this, R.string.erro_sem_periodo, Toast.LENGTH_LONG).show();
            return;
        }

        var rdButtonId = sentimentosWidget.getCheckedRadioButtonId();

        if (rdButtonId == -1) {
            Toast.makeText(this, R.string.erro_sem_sentimento, Toast.LENGTH_LONG).show();
            return;
        }

        var sentimentoId = sentimentosWidget.indexOfChild(sentimentosWidget.findViewById(rdButtonId));

        var momentoEspecial = momentoEspecialWidget.isChecked();
        var anotacoes = Optional.ofNullable(anotacoesWidget.getText()).map(o -> o.toString().trim())
                                .orElse("");

        var dataString = Optional.ofNullable(dataWidget.getText()).map(o -> o.toString().trim())
                                 .orElse("");

        var sharedPref = getSharedPreferences(Util.SharedPreferences.FILE, MODE_PRIVATE);
        var modoData = Util.FormatoData.values()[sharedPref.getInt(Util.SharedPreferences.SP_DATA, 0)];
        var data = modoData.toDate(this, dataString);

        if (data == null) {
            Toast.makeText(this, R.string.erro_data_invalida, Toast.LENGTH_LONG).show();
            calendar = Calendar.getInstance();
            setDataWidget();
            return;
        }

        var periodo = PeriodoDia.values()[periodoId - 1];
        var sentimento = Sentimento.values()[sentimentoId];

        var rgHumor = new RegistroDeHumor(
                nomeMomento,
                data,
                periodo,
                sentimento,
                momentoEspecial,
                anotacoes,
                tagsWidget.getTags()
        );

        if (rgHumor.equals(registroDeHumorOriginal)) {
            setResult(RESULT_CANCELED);
            finish();

            return;
        }

        var db = DiarioHumorDB.getInstance(this);
        var rgDao = db.getRegistroDeHumorDao();

        if (editando) {
            rgHumor.setId(registroDeHumorOriginal.getId());

            rgDao.update(rgHumor.getEntity());
            rgDao.removerCrossRef(rgHumor.getId());
        } else {
            var novoId = rgDao.inserir(rgHumor.getEntity());

            rgHumor.setId(novoId);
        }

        var tagDao = db.getTagDao();

        for (var tag : rgHumor.getTags()) {
            var tagId = tagDao.inserir(tag);

            if (tagId == -1) {
                tagId = tagDao.buscarPorNome(tag.getNome()).getId();
            }

            tag.setId(tagId);

            rgDao.inserirCrossRef(new TagRegistroDeHumor(rgHumor.getId(), tagId));
        }

        var intent = new Intent();

        intent.putExtra(MODO_KEY, editando);
        intent.putExtra(ID_KEY, rgHumor.getId());

        setResult(RESULT_OK, intent);
        finish();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void limpar() {
        nomeMomentoWidget.setText(null);
        anotacoesWidget.setText(null);

        periodoDiaWidget.setSelection(0);

        sentimentosWidget.clearCheck();

        momentoEspecialWidget.setChecked(false);

        nomeMomentoWidget.requestFocus();

        calendar = Calendar.getInstance();
        setDataWidget();

        tagsWidget.setTags(new ArrayList<>(), true, true);

        Toast.makeText(this, R.string.cadastro_limpo, Toast.LENGTH_LONG).show();
    }

    public void escolherData(View view) {
        var dPickerDialog = new DatePickerDialog(
                this,
                (dp_view, year, month, dayOfMonth) -> {

                    calendar.set(year, month, dayOfMonth);

                    setDataWidget();
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        dPickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        dPickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opcoes_cadastro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        var menuId = item.getItemId();

        if (menuId == R.id.menu_cadastro_limpar) {
            limpar();
        } else if (menuId == R.id.menu_cadastro_salvar) {
            salvar();
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void dialogSelecionarTags() {
        var inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") var view = inflater.inflate(
                R.layout.dialog_selecionar_tag,
                null,
                false
        );

        dialogTagsSelecionadas = view.findViewById(R.id.tags_selecionadas);
        dialogTagsDisponiveis = view.findViewById(R.id.spinner_tags_disponiveis);

        var db = DiarioHumorDB.getInstance(this);
        var dao = db.getTagDao();

        var tagsRestantes = new ArrayList<Tag>();
        tagsRestantes.add(null);
        tagsRestantes.addAll(dao.getTagsRestantes(tagsWidget.getTags().stream().map(Tag::getId)
                                                            .collect(Collectors.toList())));


        var spinnerAdapter = new TagSpinnerAdapter(this, tagsRestantes);

        dialogTagsDisponiveis.setAdapter(spinnerAdapter);
        dialogTagsDisponiveis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;

                var selecionada = spinnerAdapter.getItem(position);

                tagsWidget.addTag(selecionada);
                dialogTagsSelecionadas.addTag(selecionada);

                spinnerAdapter.remove(selecionada);
                spinnerAdapter.notifyDataSetChanged();

                dialogTagsDisponiveis.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dialogTagsSelecionadas.setTags(new ArrayList<>(tagsWidget.getTags()), false, true);
        dialogTagsSelecionadas.setClickListener((view1, position) -> {
            var removido = dialogTagsSelecionadas.removeTag(position);
            tagsWidget.removeTag(position);

            spinnerAdapter.add(removido);
            spinnerAdapter.notifyDataSetChanged();
        });

        new AlertDialog.Builder(this).setTitle(R.string.selecionar_tags).setView(view)
                                     .setPositiveButton(R.string.ok, null)
                                     .setOnDismissListener(d -> {
                                         dialogTagsDisponiveis = null;
                                         dialogTagsSelecionadas = null;
                                     }).show();

    }
}
