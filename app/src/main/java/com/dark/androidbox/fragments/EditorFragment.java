package com.dark.androidbox.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.dark.androidbox.Lexer;
import com.dark.androidbox.adapter.NodeAdderAdapter;
import com.dark.androidbox.adapter.NodeSelector;
import com.dark.androidbox.adapter.NodeViewAdapter;
import com.dark.androidbox.codeView.Editor;
import com.dark.androidbox.databinding.FragmentEditorBinding;
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

import java.util.ArrayList;
import java.util.List;

public class EditorFragment extends Fragment {


    Context ctx;
    private FragmentEditorBinding binding;
    private GysoTreeView treeView;
    private TreeViewAdapter<NodeData> adapter;
    private Editor editor;
    private Lexer lexer;
    private NodeModel<NodeData> root, methods, var;
    private TreeModel<NodeData> treeModel;
    private boolean isFirstCode = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditorBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ctx = requireContext();
        init();
    }


    private void init() {
        treeView = binding.nodeView.treeview;
        editor = new Editor(binding.code);
        lexer = new Lexer(new StringBuilder());
        adapter = new NodeViewAdapter();
        treeViewInit();

        binding.drag.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) CodeToNode(binding.code.getText().toString());
            else binding.code.setText(nodeToCode());
        });

        binding.center.setOnClickListener(view -> treeView.getEditor().focusMidLocation());

        treeView.treeViewGestureHandler.setOnDoubleTapListener(() -> treeView.getEditor().focusMidLocation());

        new Handler(ctx.getMainLooper()).postDelayed(() -> treeView.getEditor().focusMidLocation(), 2000);
    }

    private void treeViewInit() {
        treeView.setAdapter(adapter);
        treeView.setTreeLayoutManager(new BoxHorizonLeftAndRightLayoutManager(ctx, 20, 20, new SmoothLine()));
        treeView.treeViewContainer.onNodeEvents((targetNode, releaseNode) -> {
            NodeModel<NodeData> target = (NodeModel<NodeData>) targetNode;
            NodeModel<NodeData> release = (NodeModel<NodeData>) releaseNode;

            return !(release.value.types == NodeTypes.METHOD && target.value.types == NodeTypes.VARIABLE) && !(release.value.types == NodeTypes.VARIABLE && release.value.types == NodeTypes.METHOD);
        });
    }

    private void codeViewInit() {
        for (FieldDeclaration declaration : lexer.getFields()) {
            String type = declaration.getVariables().get(0).getTypeAsString();
            String name = declaration.getVariables().get(0).getNameAsString();
            editor.setTxtColor(type, Color.parseColor("#e4c17b"));
            editor.setTxtColor(name, Color.parseColor("#ee596e"));
        }
    }

    private String nodeToCode() {
        isFirstCode = false;
        binding.drag.setText("Code To Node");
        binding.code.setVisibility(View.VISIBLE);
        treeView.setVisibility(View.INVISIBLE);

        return lexer.unit.toString();
    }

    private void CodeToNode(String code) {
        hideKeyboard();
        isFirstCode = false;
        binding.drag.setText("Node To Code");
        treeView.setVisibility(View.VISIBLE);

        if (code.isEmpty()) {
            loadDefaultNodes();
        } else {
            lexer = new Lexer(new StringBuilder(code));
            root = createNode(new NodeData(lexer.getClasses().get(0).getNameAsString(), lexer.getClasses().get(0).toString(), NodeTypes.CLASS, new ClassNode(ctx), lexer.getClasses().get(0)));

            var = createNode(new NodeData("Var", "", NodeTypes.VARIABLE, new ClassNode(ctx), null));
            methods = createNode(new NodeData("Methods", "", NodeTypes.METHOD, new ClassNode(ctx), null));

            TreeModel<NodeData> treeModel = new TreeModel<>(root);
            treeModel.addNode(root, var, methods);

            for (FieldDeclaration declaration : lexer.getFields()) {
                treeModel.addNode(var, createNode(new NodeData(declaration.getVariables().get(0).getNameAsString(), declaration.getVariables().get(0).getTypeAsString(), NodeTypes.VARIABLE, new ClassNode(ctx), declaration)));
            }

            for (MethodDeclaration declaration : lexer.getMethods()) {
                treeModel.addNode(methods, createNode(new NodeData(declaration.getNameAsString(), declaration.toString(), NodeTypes.METHOD, new ClassNode(ctx), declaration)));
            }

            adapter.setTreeModel(treeModel);
            setUpNodeSelector(binding.choose);
        }
        updateUI();

    }

    private void loadDefaultNodes() {
        lexer = new Lexer(new StringBuilder());
        lexer.addClass("Main", null, null);
        root = createNode(new NodeData(lexer.getClasses().get(0).getNameAsString(), lexer.getClasses().get(0).toString(), NodeTypes.CLASS, new ClassNode(ctx), lexer.getClasses().get(0)));
        var = createNode(new NodeData("Var", "", NodeTypes.VARIABLE, new ClassNode(ctx), null));
        methods = createNode(new NodeData("Methods", "", NodeTypes.METHOD, new ClassNode(ctx), null));

        treeModel = new TreeModel<>(root);
        treeModel.addNode(root, var, methods);
        adapter.setTreeModel(treeModel);
    }

    private void setUpNodeSelector(AppCompatSpinner spinner) {
        NodeSelector adapter = new NodeSelector(ctx, getMaxNodes(root));
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

    private void setUpNodeAdder(AppCompatSpinner spinner) {
        NodeAdderAdapter adapter = new NodeAdderAdapter(ctx, getListAvaNode());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NodeModel<NodeData> selectedItem = (NodeModel<NodeData>) parent.getItemAtPosition(position);

                if (selectedItem != var && selectedItem != methods) {
                    if (selectedItem.value.types == NodeTypes.VARIABLE) {
                        treeView.getEditor().addChildNodes(var, selectedItem);
                    }
                    if (selectedItem.value.types == NodeTypes.METHOD) {
                        treeView.getEditor().addChildNodes(methods, selectedItem);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });
    }

    private List<NodeModel<NodeData>> getListAvaNode() {
        List<NodeModel<NodeData>> nodes = new ArrayList<>();

        if (!lexer.getMethods().isEmpty() || !lexer.getFields().isEmpty()) {
            nodes.add(var);
            for (FieldDeclaration declaration : lexer.getFields()) {
                nodes.add(createNode(new NodeData(declaration.getVariables().get(0).getNameAsString(), declaration.toString(), NodeTypes.VARIABLE, new ClassNode(ctx), declaration)));
            }

            nodes.add(methods);
            for (MethodDeclaration declaration : lexer.getMethods()) {
                nodes.add(createNode(new NodeData(declaration.getNameAsString(), declaration.toString(), NodeTypes.METHOD, new MethodNode(ctx), declaration)));
            }
        }

        return nodes;
    }

    private ArrayList<NodeModel<NodeData>> getMaxNodes(NodeModel<NodeData> node) {
        ArrayList<NodeModel<NodeData>> nodeModels = new ArrayList<>();
        addUniqueNode(node, nodeModels);
        return nodeModels;
    }

    private void addUniqueNode(NodeModel<NodeData> node, ArrayList<NodeModel<NodeData>> nodeModels) {
        if (!nodeModels.contains(node)) nodeModels.add(node);
        for (NodeModel<NodeData> childNode : node.getChildNodes())
            addUniqueNode(childNode, nodeModels);
    }

    private NodeModel<NodeData> createNode(NodeData data) {
        return new NodeModel<>(data);
    }

    private void updateUI() {
        binding.code.setVisibility(View.GONE);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
    }
}
