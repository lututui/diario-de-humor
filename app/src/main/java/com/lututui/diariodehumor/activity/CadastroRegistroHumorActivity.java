package com.lututui.diariodehumor.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lututui.diariodehumor.PeriodoDia;
import com.lututui.diariodehumor.R;
import com.lututui.diariodehumor.RegistroDeHumor;
import com.lututui.diariodehumor.Sentimento;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class CadastroRegistroHumorActivity extends AppCompatActivity {
    public static final String MODO_KEY = "MODO_KEY";

    private EditText nomeMomentoWidget;
    private EditText anotacoesWidget;
    private EditText dataWidget;
    private Spinner periodoDiaWidget;
    private RadioGroup sentimentosWidget;
    private CheckBox momentoEspecialWidget;

    private Calendar calendar;
    private SimpleDateFormat formatoData;

    private boolean editando;

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

        formatoData = new SimpleDateFormat(getString(R.string.formato_data), Locale.ROOT);

        editando = getIntent().getBooleanExtra(MODO_KEY, false);

        if (editando) {
            var rgHumor = (RegistroDeHumor) getIntent().getParcelableExtra(RegistroDeHumor.REGISTRO_DE_HUMOR_KEY);

            if (rgHumor != null) {
                var tituloWidget = (TextView) findViewById(R.id.label_titulo);
                tituloWidget.setText(getString(R.string.editando_registro_de_humor));

                var data = Optional.ofNullable(dataTryParse(rgHumor.getData()))
                                   .orElse(Calendar.getInstance().getTime());

                nomeMomentoWidget.setText(rgHumor.getTitulo());
                calendar.setTime(data);
                periodoDiaWidget.setSelection(rgHumor.getPeriodoDia().ordinal() + 1);
                sentimentosWidget.check(sentimentosWidget.getChildAt(rgHumor.getSentimento().ordinal())
                                                         .getId());
                momentoEspecialWidget.setChecked(rgHumor.isEspecial());
                anotacoesWidget.setText(rgHumor.getAnotacoes());
            }
        }

        setDataWidget();
    }

    private void setDataWidget() {
        dataWidget.setText(formatoData.format(calendar.getTime()));
    }

    private Date dataTryParse(String talvezData) {
        try {
            return formatoData.parse(talvezData);
        } catch (ParseException e) {
            return null;
        }
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

        var data = dataTryParse(dataString);

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
                dataString,
                periodo,
                sentimento,
                momentoEspecial,
                anotacoes
        );

        var intent = new Intent();

        intent.putExtra(MODO_KEY, editando);
        intent.putExtra(RegistroDeHumor.REGISTRO_DE_HUMOR_KEY, rgHumor);

        setResult(RESULT_OK, intent);
        finish();
    }

    public void limpar() {
        nomeMomentoWidget.setText(null);
        anotacoesWidget.setText(null);

        periodoDiaWidget.setSelection(0);

        sentimentosWidget.clearCheck();

        momentoEspecialWidget.setChecked(false);

        nomeMomentoWidget.requestFocus();

        calendar = Calendar.getInstance();
        setDataWidget();

        Toast.makeText(this, R.string.cadastro_limpo, Toast.LENGTH_LONG).show();
    }

    public void escolherData(View view) {
        var dPickerDialog = new DatePickerDialog(
                this,
                (dp_view, year, month, dayOfMonth) -> {

                    calendar.set(year, month - 1, dayOfMonth);

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
}
