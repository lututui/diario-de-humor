package com.lututui.diariodehumor;

import static com.google.android.material.R.attr.colorPrimaryVariant;

import android.content.Context;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RegistroHumorRecyclerViewAdapter
        extends RecyclerView.Adapter<RegistroHumorRecyclerViewAdapter.RegistroHumorHolder> {
    private final Context context;
    private final List<RegistroDeHumor> registros;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnCreateContextMenu onCreateContextMenuListener;
    private OnContextMenuClickListener onContextMenuClickListener;

    public RegistroHumorRecyclerViewAdapter(Context context, List<RegistroDeHumor> registros) {
        this.context = context;
        this.registros = registros;
    }

    public void setOnContextMenuClickListener(OnContextMenuClickListener onContextMenuClickListener) {
        this.onContextMenuClickListener = onContextMenuClickListener;
    }

    public void setOnCreateContextMenuListener(OnCreateContextMenu onCreateContextMenuListener) {
        this.onCreateContextMenuListener = onCreateContextMenuListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public RegistroHumorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        var view = inflater.inflate(R.layout.item_lista_registros, parent, false);

        return new RegistroHumorHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroHumorHolder holder, int position) {
        var rg_humor = registros.get(position);

        if (rg_humor.isEspecial()) {
            holder.estrelaEsquerda.setVisibility(View.VISIBLE);
            holder.estrelaDireita.setVisibility(View.VISIBLE);

            var typedVal = new TypedValue();
            context.getTheme().resolveAttribute(colorPrimaryVariant, typedVal, true);

            holder.itemView.setBackgroundColor(typedVal.data);
        } else {
            holder.estrelaEsquerda.setVisibility(View.INVISIBLE);
            holder.estrelaDireita.setVisibility(View.INVISIBLE);

            var typedVal = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorBackground, typedVal, true);

            holder.itemView.setBackgroundColor(typedVal.data);
        }

        holder.titulo.setText(rg_humor.getTitulo());
        holder.data.setText(rg_humor.getData());
        holder.periodo.setText(context.getString(rg_humor.getPeriodoDia().getResourceID()));
        holder.sentimento.setText(context.getString(rg_humor.getSentimento().getResourceID()));
        holder.anotacao.setText(rg_humor.getAnotacoes());
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

    public interface OnCreateContextMenu {
        void onCreateContextMenu(
                View view,
                ContextMenu menu,
                ContextMenu.ContextMenuInfo menuInfo,
                MenuItem.OnMenuItemClickListener onMenuItemClickListener,
                int position
        );
    }

    public interface OnContextMenuClickListener {
        boolean onContextMenuClick(MenuItem menuItem, int position);
    }

    public class RegistroHumorHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private final TextView estrelaEsquerda;
        private final TextView estrelaDireita;
        private final TextView titulo;
        private final TextView data;
        private final TextView periodo;
        private final TextView sentimento;
        private final TextView anotacao;

        public RegistroHumorHolder(@NonNull View itemView) {
            super(itemView);

            estrelaEsquerda = itemView.findViewById(R.id.label_item_especial_esquerda);
            estrelaDireita = itemView.findViewById(R.id.label_item_especial_direita);

            titulo = itemView.findViewById(R.id.text_item_titulo);
            data = itemView.findViewById(R.id.text_item_data);
            periodo = itemView.findViewById(R.id.text_item_periodo);
            sentimento = itemView.findViewById(R.id.text_item_sentimento);
            anotacao = itemView.findViewById(R.id.text_item_anotacao);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            var p = getBindingAdapterPosition();

            if (onItemClickListener == null || p == RecyclerView.NO_POSITION) return;

            onItemClickListener.onItemClick(v, p);

        }

        @Override
        public boolean onLongClick(View v) {
            var p = getBindingAdapterPosition();

            if (onItemLongClickListener == null || p == RecyclerView.NO_POSITION) return false;

            return onItemLongClickListener.onItemLongClick(v, p);

        }

        @Override
        public void onCreateContextMenu(
                ContextMenu menu,
                View v,
                ContextMenu.ContextMenuInfo menuInfo
        ) {
            var p = getBindingAdapterPosition();

            if (onCreateContextMenuListener == null || p == RecyclerView.NO_POSITION) return;

            onCreateContextMenuListener.onCreateContextMenu(v, menu, menuInfo, this, p);
        }

        @Override
        public boolean onMenuItemClick(@NonNull MenuItem item) {
            var p = getBindingAdapterPosition();

            if (onContextMenuClickListener == null || p == RecyclerView.NO_POSITION) {
                return false;
            }

            return onContextMenuClickListener.onContextMenuClick(item, p);

        }
    }

}
