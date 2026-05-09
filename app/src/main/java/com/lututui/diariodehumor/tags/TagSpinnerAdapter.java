package com.lututui.diariodehumor.tags;

import static com.lututui.diariodehumor.tags.TagsView.corDoTexto;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lututui.diariodehumor.R;

import java.util.List;

public class TagSpinnerAdapter extends ArrayAdapter<Tag> {

    public TagSpinnerAdapter(@NonNull Context context, @NonNull List<Tag> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getDropDownView(
            int position,
            @Nullable View convertView,
            @NonNull ViewGroup parent
    ) {
        return criarView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return criarView(position, convertView, parent);
    }

    private View criarView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            var inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_spinner_tag, parent, false);
        }

        var tag = getItem(position);
        var nomeTagWidget = (TextView) convertView.findViewById(R.id.text_spinner_tag);

        if (tag == null) {
            nomeTagWidget.setText(R.string.tag_unset);
            convertView.setBackground(null);

            return convertView;
        }

        nomeTagWidget.setText(tag.getNome());
        nomeTagWidget.setTextColor(corDoTexto(tag.getCor()));

        convertView.setBackground(new ColorDrawable(tag.getCor()));

        return convertView;
    }


}
