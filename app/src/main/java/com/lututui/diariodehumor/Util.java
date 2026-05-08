package com.lututui.diariodehumor;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    public enum FormatoData {
        DD_MM_YYYY("dd/MM/yyyy"), MM_DD_YYYY("MM/dd/yyyy"), YYYY_MM_DD("yyyy/MM/dd");


        private final String formatString;
        private final SimpleDateFormat formatter;

        FormatoData(String formatString) {
            this.formatString = formatString;
            this.formatter = new SimpleDateFormat(this.formatString, Locale.ROOT);
        }

        public Date toDate(String maybeDate) {
            try {
                return formatter.parse(maybeDate);
            } catch (ParseException e) {
                return new Date(0);
            }
        }

        public String toString(Date data) {
            return formatter.format(data);
        }

        public String getFormatString() {
            return formatString;
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
        public static void mostrarAviso(Context context, String titulo, String mensagem,
                                        DialogInterface.OnClickListener listener){

            var builder = new AlertDialog.Builder(context);

            builder.setTitle(titulo);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setMessage(mensagem);

            builder.setNeutralButton(R.string.ok, listener);

            builder.create().show();
        }
    }
}
