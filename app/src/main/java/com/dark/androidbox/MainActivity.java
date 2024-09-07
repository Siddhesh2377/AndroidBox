package com.dark.androidbox;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dark.androidbox.adapter.NodeViewAdapter;
import com.dark.androidbox.databinding.ActivityMainBinding;
import com.dark.androidbox.model.NodeData;
import com.dark.androidbox.types.NodeTypes;
import com.gyso.treeview.GysoTreeView;
import com.gyso.treeview.adapter.TreeViewAdapter;
import com.gyso.treeview.layout.BoxDownTreeLayoutManager;
import com.gyso.treeview.line.SmoothLine;
import com.gyso.treeview.model.NodeModel;
import com.gyso.treeview.model.TreeModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private GysoTreeView treeView;
    private TreeViewAdapter<NodeData> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        treeView = binding.nodeView.treeview;
        setupSyntaxHighlighter();

        binding.btn.setOnClickListener(view -> {
            String codeText = binding.code.getText().toString();
            if (!codeText.isEmpty()) {
                loadJava(new StringBuilder(codeText));
            }
        });
    }

    private void loadJava(StringBuilder code) {
        Lexer lexer = new Lexer(code);
        adapter = new NodeViewAdapter();
        initializeTreeView();

        NodeModel<NodeData> node0 = createNode(lexer.getClasses().get(0), NodeTypes.CLASSES);
        TreeModel<NodeData> treeModel = new TreeModel<>(node0);

        treeModel.addNode(
                createNode(lexer.getMethods().get(0), NodeTypes.METHODS),
                createNode(lexer.getFields().get(0).getVariables().get(0), NodeTypes.VARIABLES)
        );

        adapter.setTreeModel(treeModel);
        updateUI();
    }

    private void initializeTreeView() {
        treeView.setAdapter(adapter);
        treeView.setTreeLayoutManager(new BoxDownTreeLayoutManager(this, 20, 20, new SmoothLine()));
    }

    private NodeModel<NodeData> createNode(Object item, NodeTypes type) {
        return new NodeModel<>(new NodeData(item.toString(), item.toString(), type));
    }

    private void updateUI() {
        binding.btn.setVisibility(View.GONE);
        binding.code.setVisibility(View.GONE);
        new Handler(getMainLooper()).postDelayed(() -> treeView.getEditor().focusMidLocation(), 2000);
    }

    private void setupSyntaxHighlighter() {
        binding.code.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                removeExistingSpans(s);
                highlightSyntax(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void removeExistingSpans(Editable s) {
        ForegroundColorSpan[] spans = s.getSpans(0, s.length(), ForegroundColorSpan.class);
        for (ForegroundColorSpan span : spans) {
            s.removeSpan(span);
        }
    }

    private void highlightSyntax(Editable s) {
        applyPattern(s, "\\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|null|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while)\\b", Color.parseColor("#186fa1"));
        applyPattern(s, "//.*|/\\*(?:.|[\\n\\r])*?\\*/", Color.GRAY);
        applyPattern(s, "\"(.*?)\"", Color.parseColor("#32872f"));
        applyPattern(s, "\\b\\d+\\b", Color.parseColor("#d35400"));
        applyPattern(s, "@\\w+", Color.parseColor("#9b59b6"));
        applyPattern(s, "\\b(int|char|float|double|boolean|long|short|byte)\\b", Color.parseColor("#2c3e50"));
    }

    private void applyPattern(Editable s, String pattern, int color) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s);
        while (m.find()) {
            s.setSpan(new ForegroundColorSpan(color), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
