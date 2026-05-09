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
        DD_MM_YYYY("dd/MM/yyyy"), MM_DD_YYYY("MM/dd/yyyy"), YYYY_MM_DD("yyyy/MM/dd"), LOCALE(null);


        private SimpleDateFormat formatter;

        FormatoData(String formatString) {
            if (formatString != null) {
                this.formatter = new SimpleDateFormat(formatString, Locale.ROOT);
            }
        }

        private void createFormatter(Context context) {
            if (this.formatter != null) return;

            var pattern = context.getString(R.string.formato_data_locale);
            this.formatter = new SimpleDateFormat(pattern, Locale.ROOT);
        }

        public Date toDate(Context context, String maybeDate) {
            createFormatter(context);

            try {
                return formatter.parse(maybeDate);
            } catch (ParseException e) {
                return new Date(0);
            }
        }

        public String toString(Context context, Date data) {
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
        public static void mostrarAviso(
                Context context,
                String titulo,
                String mensagem,
                DialogInterface.OnClickListener listener
        ) {

            var builder = new AlertDialog.Builder(context);

            builder.setTitle(titulo);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setMessage(mensagem);

            builder.setNeutralButton(R.string.ok, listener);

            builder.create().show();
        }
    }
}
