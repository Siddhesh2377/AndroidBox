package com.dark.androidbox;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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
import com.gyso.treeview.model.NodeModel;
import com.gyso.treeview.model.TreeModel;

public class MainActivity extends AppCompatActivity {

    public static GysoTreeView treeViewStatic;
    //BINDING
    private ActivityMainBinding binding;
    //TREEVIEW Variables
    private GysoTreeView treeView;
    private TreeViewAdapter<NodeData> adapter;
    //CODE EDITOR Variables
    private Editor editor;
    //CODE INIT Variables
    private Lexer lexer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SETUP ACTIVITY View
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        treeView = binding.nodeView.treeview;
        treeViewStatic = binding.nodeView.treeview;

        //Init Essentials Objects
        editor = new Editor(binding.code);
        lexer = new Lexer(codeStr());
        adapter = new NodeViewAdapter();

        //LOGIC Begin
        init();
        binding.code.setText(codeStr());

        //HIGHLIGHTING Variables
        for (FieldDeclaration declaration : lexer.getFields()) {
            editor.setTxtColor(declaration.getVariables().get(0).getTypeAsString(), Color.parseColor("#e4c17b"));
        }

        //HIGHLIGHTING Variable's Objects
        for (FieldDeclaration declaration : lexer.getFields()) {
            editor.setTxtColor(declaration.getVariables().get(0).getNameAsString(), Color.parseColor("#ee596e"));
        }

        //Temp Click Fun to Load Nodes
        loadNodes();

        binding.drag.setOnCheckedChangeListener((compoundButton, b) -> treeView.getEditor().setWantEdit(!b));
    }

    //LOADING NODES
    private void loadNodes() {

        //Create NODES
        NodeModel<NodeData> root =
                createNode(new NodeData(
                        lexer.getClasses().get(0).getNameAsString(),
                        lexer.getClasses().get(0).toString(),
                        NodeTypes.CLASSES, new ClassNode(this),
                        lexer.getClasses()));

        NodeModel<NodeData> var =
                createNode(new NodeData(
                        "Var", "",
                        NodeTypes.VARIABLES,
                        new ClassNode(this),
                        lexer.getFields()));

        NodeModel<NodeData> methods =
                createNode(new NodeData(
                        "Methods", "",
                        NodeTypes.METHODS,
                        new ClassNode(this),
                        lexer.getMethods()));

        //LOAD ROOT-NODE in TREE-MODEL
        TreeModel<NodeData> treeModel = new TreeModel<>(root);

        //LOAD CHILD-NODES in TREE-MODEL
        treeModel.addNode(root, var, methods);

        //LOAD Var-NODES in TREE-MODEL
        for (FieldDeclaration declaration : lexer.getFields()) {
            treeModel.addNode(var,
                    createNode(
                            new NodeData(
                                    declaration.getVariables().get(0).getNameAsString(),
                                    declaration.getVariables().get(0).getTypeAsString(),
                                    NodeTypes.VARIABLES, new ClassNode(this),
                                    null)));
        }
        //LOAD Methods-NODES in TREE-MODEL
        for (MethodDeclaration declaration : lexer.getMethods()) {
            treeModel.addNode(methods,
                    createNode(
                            new NodeData(
                                    declaration.getNameAsString(),
                                    declaration.toString(),
                                    NodeTypes.METHODS,
                                    new MethodNode(this),
                                    null)));
        }

        //LOAD TREE-MODEL IN ADAPTER
        adapter.setTreeModel(treeModel);
        //TEMP UPDATE THE UI
        updateUI();

        //Rules For Nodes
        treeView.treeViewContainer.onNodeEvents((targetNode, releaseNode) -> {
            NodeModel<NodeData> target = (NodeModel<NodeData>) targetNode;
            NodeModel<NodeData> release = (NodeModel<NodeData>) releaseNode;
            boolean b = true;

            if (release.value.types == NodeTypes.METHODS && target.value.types == NodeTypes.VARIABLES) b = false;
            else if (release.value.types == NodeTypes.VARIABLES && release.value.types == NodeTypes.METHODS) b = true;

            return b;
        });
    }

    //SETUP VIEWS
    private void init() {
        treeView.setAdapter(adapter);
        treeView.setTreeLayoutManager(new BoxHorizonLeftAndRightLayoutManager(this, 20, 20, new SmoothLine()));
    }

    //CREATING NEW NODES
    private NodeModel<NodeData> createNode(NodeData data) {
        return new NodeModel<>(data);
    }

    //UI UPDATE
    private void updateUI() {
        binding.btn.setVisibility(View.GONE);
        binding.code.setVisibility(View.GONE);
        new Handler(getMainLooper()).postDelayed(() -> binding.nodeView.treeview.getEditor().focusMidLocation(), 2000);
    }

    //Sample CODE
    private StringBuilder codeStr() {
        return new StringBuilder("package com.dark.androidbox;\n" +
                "\n" +
                "import android.graphics.Color;\n" +
                "import android.os.Bundle;\n" +
                "import android.os.Handler;\n" +
                "import android.text.Editable;\n" +
                "import android.text.Spannable;\n" +
                "import android.text.TextWatcher;\n" +
                "import android.text.style.ForegroundColorSpan;\n" +
                "import android.view.View;\n" +
                "\n" +
                "import androidx.appcompat.app.AppCompatActivity;\n" +
                "\n" +
                "import com.dark.androidbox.adapter.NodeViewAdapter;\n" +
                "import com.dark.androidbox.codeView.Editor;\n" +
                "import com.dark.androidbox.databinding.ActivityMainBinding;\n" +
                "import com.dark.androidbox.model.NodeData;\n" +
                "import com.dark.androidbox.types.NodeTypes;\n" +
                "import com.gyso.treeview.GysoTreeView;\n" +
                "import com.gyso.treeview.adapter.TreeViewAdapter;\n" +
                "import com.gyso.treeview.layout.BoxDownTreeLayoutManager;\n" +
                "import com.gyso.treeview.line.SmoothLine;\n" +
                "import com.gyso.treeview.model.NodeModel;\n" +
                "import com.gyso.treeview.model.TreeModel;\n" +
                "\n" +
                "import java.util.regex.Matcher;\n" +
                "import java.util.regex.Pattern;\n" +
                "\n" +
                "public class MainActivity extends AppCompatActivity {\n" +
                "\n" +
                "    private ActivityMainBinding binding;\n" +
                "    private GysoTreeView treeView;\n" +
                "    private TreeViewAdapter<NodeData> adapter;\n" +
                "\n" +
                "    @Override\n" +
                "    protected void onCreate(Bundle savedInstanceState) {\n" +
                "        super.onCreate(savedInstanceState);\n" +
                "        binding = ActivityMainBinding.inflate(getLayoutInflater());\n" +
                "        setContentView(binding.getRoot());\n" +
                "\n" +
                "        Editor editor = new Editor(binding.code);\n" +
                "        \n" +
                "        binding.code.setText();\n" +
                "\n" +
                "        treeView = binding.nodeView.treeview;\n" +
                "\n" +
                "        binding.btn.setOnClickListener(view -> {\n" +
                "            String codeText = binding.code.getText().toString();\n" +
                "            if (!codeText.isEmpty()) {\n" +
                "                loadJava(new StringBuilder(codeText));\n" +
                "            }\n" +
                "        });\n" +
                "    }\n" +
                "\n" +
                "    private void loadJava(StringBuilder code) {\n" +
                "        Lexer lexer = new Lexer(code);\n" +
                "        adapter = new NodeViewAdapter();\n" +
                "        initializeTreeView();\n" +
                "\n" +
                "        NodeModel<NodeData> node0 = createNode(lexer.getClasses().get(0), NodeTypes.CLASSES);\n" +
                "        TreeModel<NodeData> treeModel = new TreeModel<>(node0);\n" +
                "\n" +
                "        treeModel.addNode(\n" +
                "                createNode(lexer.getMethods().get(0), NodeTypes.METHODS),\n" +
                "                createNode(lexer.getFields().get(0).getVariables().get(0), NodeTypes.VARIABLES)\n" +
                "        );\n" +
                "\n" +
                "        adapter.setTreeModel(treeModel);\n" +
                "        updateUI();\n" +
                "    }\n" +
                "\n" +
                "    private void initializeTreeView() {\n" +
                "        treeView.setAdapter(adapter);\n" +
                "        treeView.setTreeLayoutManager(new BoxDownTreeLayoutManager(this, 20, 20, new SmoothLine()));\n" +
                "    }\n" +
                "\n" +
                "    private NodeModel<NodeData> createNode(Object item, NodeTypes type) {\n" +
                "        return new NodeModel<>(new NodeData(item.toString(), item.toString(), type));\n" +
                "    }\n" +
                "\n" +
                "    private void updateUI() {\n" +
                "        binding.btn.setVisibility(View.GONE);\n" +
                "        binding.code.setVisibility(View.GONE);\n" +
                "        new Handler(getMainLooper()).postDelayed(() -> treeView.getEditor().focusMidLocation(), 2000);\n" +
                "    }\n" +
                "\n" +
                "}\n");

    }

}
