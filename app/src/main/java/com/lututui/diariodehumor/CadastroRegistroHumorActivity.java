package com.lututui.diariodehumor;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    private EditText nomeMomentoWidget;
    private EditText anotacoesWidget;
    private EditText dataWidget;
    private Spinner periodoDiaWidget;
    private RadioGroup sentimentosWidget;
    private CheckBox momentoEspecialWidget;

    private Calendar calendar;
    private SimpleDateFormat formatoData;

    public static final String KEY_TITULO = "KEY_TITULO";
    public static final String KEY_DATA = "KEY_DATA";
    public static final String KEY_PERIODO = "KEY_PERIODO";
    public static final String KEY_SENTIMENTO = "KEY_SENTIMENTO";
    public static final String KEY_ESPECIAL = "KEY_ESPECIAL";
    public static final String KEY_ANOTACOES = "KEY_ANOTACOES";

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
        var nomeMomento = Optional.ofNullable(nomeMomentoWidget.getText())
                                  .map(o -> o.toString().trim())
                                  .orElse("");

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
        var anotacoes = Optional.ofNullable(anotacoesWidget.getText())
                                .map(o -> o.toString().trim())
                                .orElse("");

        var data = Optional.ofNullable(dataWidget.getText())
                           .map(o -> o.toString().trim())
                           .orElse("");

        if (!dataValida(data)) {
            Toast.makeText(this, R.string.erro_data_invalida, Toast.LENGTH_LONG).show();
            calendar = Calendar.getInstance();
            setDataWidget();
            return;
        }

        var intent = new Intent();

        intent.putExtra(KEY_TITULO, nomeMomento);
        intent.putExtra(KEY_DATA, data);
        intent.putExtra(KEY_PERIODO, periodoId - 1);
        intent.putExtra(KEY_SENTIMENTO, sentimentoId);
        intent.putExtra(KEY_ESPECIAL, momentoEspecial);
        intent.putExtra(KEY_ANOTACOES, anotacoes);

        setResult(RESULT_OK, intent);
        finish();
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
