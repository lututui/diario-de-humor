package com.lututui.diariodehumor.tags;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.lututui.diariodehumor.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TagsView extends ViewGroup implements View.OnClickListener {
    private final int espacamentoHorizontal;
    private final int espacamentoVertical;

    private List<Tag> tags = new ArrayList<>();
    private boolean remover = false;
    private boolean adicionar = false;

    public void setClickListener(OnTagClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private OnTagClickListener clickListener;


    public TagsView(Context context) {
        this(context, null);
    }

    public TagsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagsView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TagsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        float density = context.getResources().getDisplayMetrics().density;
        this.espacamentoHorizontal = (int) (8 * density);
        this.espacamentoVertical = (int) (8 * density);
    }

    @Override
    public void onClick(View v) {
        var p = (int) v.getTag();

        if (clickListener == null || p == -1) return;

        clickListener.onTagClickListener(v, p);
    }

    public void setTags(List<Tag> tags) {
        setTags(tags, adicionar, remover);
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);

        refresh();
    }

    public void refresh() {
        removeAllViews();
        inflarTags();
        requestLayout();
    }

    public Tag removeTag(int pos) {
        var removed = this.tags.remove(pos);

        refresh();

        return removed;
    }

    public void setTags(List<Tag> tags, boolean adicionar, boolean remover) {
        this.tags = tags;

        this.adicionar = adicionar;
        this.remover = remover;

        refresh();
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    private void inflarTags() {
        var inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < tags.size(); i++) {
            var tag = tags.get(i);
            var tagView = inflater.inflate(R.layout.item_tag, this, false);

            var nome = (TextView) tagView.findViewById(R.id.text_tag_nome);
            var fechar = (TextView) tagView.findViewById(R.id.text_tag_fechar);

            nome.setText(tag.getNome());
            fechar.setVisibility(remover ? View.VISIBLE : View.GONE);

            Optional.ofNullable(ContextCompat.getDrawable(getContext(), R.drawable.background_tag))
                    .map(Drawable::mutate).ifPresent(d -> {
                        d.setColorFilter(tag.getCor(), PorterDuff.Mode.SRC_IN);
                        tagView.setBackground(d);
                    });

            tagView.setTag(i);
            tagView.setOnClickListener(this);

            var corTexto = corDoTexto(tag.getCor());
            nome.setTextColor(corTexto);
            fechar.setTextColor(corTexto);

            addView(tagView);
        }

        if (this.adicionar) {
            var adicionar = inflater.inflate(R.layout.item_tag_adicionar, this, false);

            adicionar.setTag(Integer.MIN_VALUE);
            adicionar.setOnClickListener(this);

            addView(adicionar);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int larguraDisponivel =
                MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();

        int offsetX = 0;
        int offsetY = 0;
        int alturaLinhaAtual = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (offsetX + childWidth > larguraDisponivel) {
                offsetX = 0;
                offsetY += alturaLinhaAtual + espacamentoVertical;
                alturaLinhaAtual = 0;
            }

            offsetX += childWidth + espacamentoHorizontal;
            alturaLinhaAtual = Math.max(alturaLinhaAtual, childHeight);
        }

        int alturaTotal = offsetY + alturaLinhaAtual + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), alturaTotal);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int larguraDisponivel = (r - l) - getPaddingLeft() - getPaddingRight();

        int offsetX = getPaddingLeft();
        int offsetY = getPaddingTop();
        int alturaLinhaAtual = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (offsetX - getPaddingLeft() + childWidth > larguraDisponivel) {
                offsetX = getPaddingLeft();
                offsetY += alturaLinhaAtual + espacamentoVertical;
                alturaLinhaAtual = 0;
            }

            child.layout(offsetX, offsetY, offsetX + childWidth, offsetY + childHeight);

            offsetX += childWidth + espacamentoHorizontal;
            alturaLinhaAtual = Math.max(alturaLinhaAtual, childHeight);
        }
    }

    private static int corDoTexto(int corFundo) {
        int r = Color.red(corFundo);
        int g = Color.green(corFundo);
        int b = Color.blue(corFundo);

        double luminancia = 0.299 * r + 0.587 * g + 0.114 * b;

        return luminancia > 128 ? Color.BLACK : Color.WHITE;
    }

    public interface OnTagClickListener {
        void onTagClickListener(View view, int position);
    }
}
