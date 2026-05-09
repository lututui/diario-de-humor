package com.lututui.diariodehumor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lututui.diariodehumor.DiarioHumorDB;
import com.lututui.diariodehumor.R;
import com.lututui.diariodehumor.tags.TagsView;

import java.util.ArrayList;

public class TagsActivity extends AppCompatActivity {
    private TagsView tagsWidget;
    private final ActivityResultLauncher<Intent> launcherCadastroTag = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::onCadastroTagResult
    );
    private int posSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        tagsWidget = findViewById(R.id.tags_todas);

        var tagDao = DiarioHumorDB.getInstance(this).getTagDao();

        var tags = new ArrayList<>(tagDao.getTags());

        tagsWidget.setRegistrarMenu(true);

        tagsWidget.setTags(tags, true, false);

        tagsWidget.setClickListener(position -> {
            if (position == Integer.MIN_VALUE) {
                var intent = new Intent(this, CadastroTagActivity.class);

                launcherCadastroTag.launch(intent);
            }
        });

        tagsWidget.setCreateContextMenuListener((menu, p) -> {
            getMenuInflater().inflate(R.menu.opcoes_tags, menu);
            posSelection = p;
        });
    }

    public void onCadastroTagResult(ActivityResult result) {
        if (result.getResultCode() != RESULT_OK) return;

        var db = DiarioHumorDB.getInstance(this);
        var dao = db.getTagDao();

        tagsWidget.setTags(dao.getTags());
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        var tag = tagsWidget.getTagAt(posSelection);
        var id = item.getItemId();

        if (id == R.id.opcao_tag_editar) {
            var intent = new Intent(this, CadastroTagActivity.class);

            intent.putExtra(CadastroTagActivity.EDIT_KEY, true);
            intent.putExtra(CadastroTagActivity.ID_KEY, tag.getId());

            launcherCadastroTag.launch(intent);
        } else if (id == R.id.opcao_tag_remover) {
            DiarioHumorDB.getInstance(this).getTagDao().deletar(tag);
            tagsWidget.removeTag(posSelection);
        } else {
            return super.onContextItemSelected(item);
        }

        return true;
    }
}