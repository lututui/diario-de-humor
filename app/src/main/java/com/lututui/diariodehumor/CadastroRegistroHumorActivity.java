package com.lututui.diariodehumor;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

public class CadastroRegistroHumorActivity extends AppCompatActivity {
    private EditText nomeMomentoWidget, anotacoesWidget, dataWidget;
    private Spinner periodoDiaWidget;
    private RadioGroup sentimentosWidget;
    private CheckBox momentoEspecialWidget;

    private Calendar calendar;
    private SimpleDateFormat formatoData;

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


        setDataWidget();
    }

    private void setDataWidget() {
        dataWidget.setText(formatoData.format(calendar.getTime()));
    }

    private boolean dataValida(String talvezData) {
        try {
            var d = formatoData.parse(talvezData);

            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public void salvar(View view) {
        var nomeMomento = Optional
                .ofNullable(nomeMomentoWidget.getText())
                .map(o -> o.toString().trim())
                .orElse("");

        if (nomeMomento.isBlank()) {
            Toast.makeText(this, R.string.erro_sem_titulo, Toast.LENGTH_LONG).show();
            nomeMomentoWidget.requestFocus();
            return;
        }

        var periodo = (String) periodoDiaWidget.getSelectedItem();

        if (periodo == null || periodo.equals(getString(R.string.periodo_unset))) {
            Toast.makeText(this, R.string.erro_sem_periodo, Toast.LENGTH_LONG).show();
            return;
        }

        var sentimentoId = sentimentosWidget.getCheckedRadioButtonId();

        if (sentimentoId == -1) {
            Toast.makeText(this, R.string.erro_sem_sentimento, Toast.LENGTH_LONG).show();
            return;
        }

        var sentimentoText = ((RadioButton) findViewById(sentimentoId)).getText();

        var momentoEspecial = momentoEspecialWidget.isChecked();
        var anotacoes = Optional
                .ofNullable(anotacoesWidget.getText())
                .map(o -> o.toString().trim())
                .orElse("");

        var data = Optional
                .ofNullable(dataWidget.getText())
                .map(o -> o.toString().trim()).orElse("");

        if (!dataValida(data)) {
            Toast.makeText(this, R.string.erro_data_invalida, Toast.LENGTH_LONG).show();
            calendar = Calendar.getInstance();
            setDataWidget();
            return;
        }


        Toast.makeText(this,
                getString(R.string.titulo_momento) + ": " + nomeMomento + "\n" +
                        getString(R.string.data) + ": " + data + "\n" +
                        getString(R.string.periodo_dia) + ": " + periodo + "\n" +
                        getString(R.string.prompt_sentimento) + " " + sentimentoText + "\n" +
                        getString(R.string.momento_especial_q) + " " +
                        (momentoEspecial ? getString(R.string.sim) : getString(R.string.nao)) + "\n" +
                        getString(R.string.anotacoes) + ": " + anotacoes,
                Toast.LENGTH_LONG).show();
    }

    public void limpar(View view) {
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


}
