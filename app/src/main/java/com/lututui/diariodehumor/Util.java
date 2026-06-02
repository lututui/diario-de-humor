package com.lututui.diariodehumor;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Util {
    public enum FormatoData {
        DD_MM_YYYY("dd/MM/yyyy"), MM_DD_YYYY("MM/dd/yyyy"), YYYY_MM_DD("yyyy/MM/dd"), LOCALE(null);

        private DateTimeFormatter formatter;

        FormatoData(String formatString) {
            if (formatString != null) {
                this.formatter = DateTimeFormatter.ofPattern(formatString, Locale.ROOT);
            }
        }

        private void createFormatter(Context context) {
            if (this.formatter != null) return;

            var pattern = context.getString(R.string.formato_data_locale);
            this.formatter = DateTimeFormatter.ofPattern(pattern, Locale.ROOT);
        }

        public LocalDate toDate(Context context, String dataString) {
            createFormatter(context);

            try {
                return LocalDate.parse(dataString, formatter);
            } catch (DateTimeParseException e) {
                return LocalDate.ofEpochDay(0);
            }
        }

        public String toString(Context context, LocalDate data) {
            createFormatter(context);

            return formatter.format(data);
        }
    }

    public static class SharedPreferences {
        public static final String FILE = "SP_FILE";
        public static final String SP_CORES = "SP_CORES";
        public static final String SP_ORDEM = "SP_ORDEM";
        public static final String SP_DATA = "SP_DATA";
        public static final String SP_DEMO = "SP_DEMO";
    }

    public static class Alert {
        public static void mostrarAviso(Context context, String titulo, String mensagem) {

            var builder = new AlertDialog.Builder(context);

            builder.setTitle(titulo);
            builder.setMessage(mensagem);

            builder.setNeutralButton(R.string.ok, null);

            builder.create().show();
        }

        public static void confirmarExclusao(
                Context context,
                String titulo,
                String mensagem,
                DialogInterface.OnClickListener onConfirm
        ) {
            var builder = new AlertDialog.Builder(context);

            builder.setTitle(titulo);
            builder.setMessage(mensagem);
            builder.setPositiveButton(R.string.excluir, onConfirm);
            builder.setNegativeButton(R.string.cancelar, null);

            builder.create().show();
        }
    }
}
