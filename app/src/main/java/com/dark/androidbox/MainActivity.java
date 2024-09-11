package com.dark.androidbox;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.dark.androidbox.adapter.NodeSelector;
import com.dark.androidbox.adapter.NodeViewAdapter;
import com.dark.androidbox.codeView.Editor;
import com.dark.androidbox.databinding.ActivityMainBinding;
import com.dark.androidbox.model.NodeData;
import com.dark.androidbox.nodes.ClassNode;
import com.dark.androidbox.nodes.MethodNode;
import com.dark.androidbox.types.NodeTypes;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.gyso.treeview.GysoTreeView;
import com.gyso.treeview.adapter.TreeViewAdapter;
import com.gyso.treeview.layout.BoxHorizonLeftAndRightLayoutManager;
import com.gyso.treeview.line.SmoothLine;
import com.gyso.treeview.listener.TreeViewControlListener;
import com.gyso.treeview.model.NodeModel;
import com.gyso.treeview.model.TreeModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GysoTreeView treeView;
    private TreeViewAdapter<NodeData> adapter;
    private Editor editor;
    private Lexer lexer;
    private NodeModel<NodeData> root, methods, var;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        treeView = binding.nodeView.treeview;
        editor = new Editor(binding.code);
        lexer = new Lexer(codeStr());
        adapter = new NodeViewAdapter();

        init();
        binding.code.setText(codeStr());

        highlightCode();
        loadNodes();

        binding.drag.setOnCheckedChangeListener((compoundButton, isChecked) -> treeView.getEditor().setWantEdit(isChecked));

        binding.center.setOnClickListener(view -> treeView.getEditor().focusMidLocation());
    }

    private void highlightCode() {
        for (FieldDeclaration declaration : lexer.getFields()) {
            String type = declaration.getVariables().get(0).getTypeAsString();
            String name = declaration.getVariables().get(0).getNameAsString();
            editor.setTxtColor(type, Color.parseColor("#e4c17b"));
            editor.setTxtColor(name, Color.parseColor("#ee596e"));
        }
    }

    private void loadNodes() {
        root = createNode(new NodeData(lexer.getClasses().get(0).getNameAsString(), lexer.getClasses().get(0).toString(), NodeTypes.CLASS, new ClassNode(this), lexer.getClasses().get(0)));

        var = createNode(new NodeData("Var", "", NodeTypes.VARIABLE, new ClassNode(this), null));
        methods = createNode(new NodeData("Methods", "", NodeTypes.METHOD, new ClassNode(this), null));

        TreeModel<NodeData> treeModel = new TreeModel<>(root);
        treeModel.addNode(root, var, methods);

        for (FieldDeclaration declaration : lexer.getFields()) {
            treeModel.addNode(var, createNode(new NodeData(declaration.getVariables().get(0).getNameAsString(), declaration.getVariables().get(0).getTypeAsString(), NodeTypes.VARIABLE, new ClassNode(this), declaration)));
        }

        for (MethodDeclaration declaration : lexer.getMethods()) {
            treeModel.addNode(methods, createNode(new NodeData(declaration.getNameAsString(), declaration.toString(), NodeTypes.METHOD, new MethodNode(this), declaration)));
        }

        adapter.setTreeModel(treeModel);
        updateUI();

        treeView.treeViewContainer.onNodeEvents((targetNode, releaseNode) -> {
            NodeModel<NodeData> target = (NodeModel<NodeData>) targetNode;
            NodeModel<NodeData> release = (NodeModel<NodeData>) releaseNode;

            return !(release.value.types == NodeTypes.METHOD && target.value.types == NodeTypes.VARIABLE) && !(release.value.types == NodeTypes.VARIABLE && release.value.types == NodeTypes.METHOD);
        });

        setup(binding.choose);
    }

    private void setup(AppCompatSpinner spinner) {
        NodeSelector adapter = new NodeSelector(this, getMaxNodes(root));
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NodeModel<NodeData> selectedItem = (NodeModel<NodeData>) parent.getItemAtPosition(position);
                treeView.getEditor().focusOn(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });
    }

    private void init() {
        treeView.setAdapter(adapter);
        treeView.setTreeLayoutManager(new BoxHorizonLeftAndRightLayoutManager(this, 20, 20, new SmoothLine()));
    }

    private NodeModel<NodeData> createNode(NodeData data) {
        return new NodeModel<>(data);
    }

    private ArrayList<NodeModel<NodeData>> getMaxNodes(NodeModel<NodeData> node) {
        ArrayList<NodeModel<NodeData>> nodeModels = new ArrayList<>();
        if (!node.getChildNodes().isEmpty()) {
            for (NodeModel<NodeData> dataNodeModel : node.getChildNodes()) {
                nodeModels.add(dataNodeModel);
                nodeModels.addAll(getMaxNodes(dataNodeModel));
            }
        }
        return nodeModels;
    }

    private void updateUI() {
        binding.code.setVisibility(View.GONE);
        treeView.setTreeViewControlListener(new TreeViewControlListener() {
            @Override
            public void onScaling(int state, int percent) {
                // Handle scaling
            }

            @Override
            public void onDragMoveNodesHit(@Nullable NodeModel<?> draggingNode, @Nullable NodeModel<?> hittingNode, @Nullable View draggingView, @Nullable View hittingView) {
                // Handle drag move nodes hit
            }
        });
    }

    private StringBuilder codeStr() {
        return new StringBuilder("package com.dark.androidbox;\n" + "\n" + "import android.graphics.Color;\n" + "import android.os.Bundle;\n" + "import android.os.Handler;\n" + "import android.text.Editable;\n" + "import android.text.Spannable;\n" + "import android.text.TextWatcher;\n" + "import android.text.style.ForegroundColorSpan;\n" + "import android.view.View;\n" + "\n" + "public class MainActivity extends AppCompatActivity implements Node, Data, ClM {\n" + "\n" + "    private ActivityMainBinding binding;\n" + "    private GysoTreeView treeView;\n" + "    private TreeViewAdapter<NodeData> adapter;\n" + "\n" + "    @Override\n" + "    protected void onCreate(Bundle savedInstanceState) {\n" + "        super.onCreate(savedInstanceState);\n" + "        binding = ActivityMainBinding.inflate(getLayoutInflater());\n" + "        setContentView(binding.getRoot());\n" + "\n" + "        Editor editor = new Editor(binding.code);\n" + "        \n" + "        binding.code.setText();\n" + "\n" + "        treeView = binding.nodeView.treeview;\n" + "\n" + "        binding.btn.setOnClickListener(view -> {\n" + "            String codeText = binding.code.getText().toString();\n" + "            if (!codeText.isEmpty()) {\n" + "                loadJava(new StringBuilder(codeText));\n" + "            }\n" + "        });\n" + "    }\n" + "\n" + "    private void loadJava(StringBuilder code) {\n" + "        Lexer lexer = new Lexer(code);\n" + "        adapter = new NodeViewAdapter();\n" + "        initializeTreeView();\n" + "\n" + "        NodeModel<NodeData> node0 = createNode(lexer.getClasses().get(0), NodeTypes.CLASSES);\n" + "        TreeModel<NodeData> treeModel = new TreeModel<>(node0);\n" + "\n" + "        treeModel.addNode(\n" + "                createNode(lexer.getMethods().get(0), NodeTypes.METHODS),\n" + "                createNode(lexer.getFields().get(0).getVariables().get(0), NodeTypes.VARIABLES)\n" + "        );\n" + "\n" + "        adapter.setTreeModel(treeModel);\n" + "        updateUI();\n" + "    }\n" + "\n" + "    private void initializeTreeView() {\n" + "        treeView.setAdapter(adapter);\n" + "        treeView.setTreeLayoutManager(new BoxDownTreeLayoutManager(this, 20, 20, new SmoothLine()));\n" + "    }\n" + "\n" + "    private NodeModel<NodeData> createNode(NodeData data, NodeTypes type) {\n" + "        return new NodeModel<>(new NodeData(data.getName(), data.getDescription(), type, data.getNode(), data.getNode()));\n" + "    }\n" + "\n" + "    private void updateUI() {\n" + "        binding.code.setVisibility(View.GONE);\n" + "        treeView.setTreeViewControlListener(new TreeViewControlListener() {\n" + "            @Override\n" + "            public void onScaling(int state, int percent) {\n" + "                // Handle scaling\n" + "            }\n" + "\n" + "            @Override\n" + "            public void onDragMoveNodesHit(@Nullable NodeModel<?> draggingNode, @Nullable NodeModel<?> hittingNode, @Nullable View draggingView, @Nullable View hittingView) {\n" + "                // Handle drag move nodes hit\n" + "            }\n" + "        });\n" + "    }\n" + "}\n");
    }
}
