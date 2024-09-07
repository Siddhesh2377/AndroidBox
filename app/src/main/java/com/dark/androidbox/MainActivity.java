package com.dark.androidbox;

import android.os.Bundle;

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

import org.w3c.dom.Node;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    GysoTreeView treeView;

    TreeViewAdapter<NodeData> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        treeView = binding.nodeView.treeview;

        Lexer lexer = new Lexer(code());
        adapter = new NodeViewAdapter();

        //binding.txt.setText(lexer.getFields().get(0).getVariables().get(0).getNameAsString() + lexer.getFields().get(0).getVariables().get(0).getType().asString());


        treeView.setAdapter(adapter);
        treeView.setTreeLayoutManager(new BoxDownTreeLayoutManager(this, 20, 20, new SmoothLine()));

        
        NodeModel<NodeData> node0 = new NodeModel<>(new NodeData(lexer.getClasses().get(0).getNameAsString(), lexer.getClasses().get(0).toString(), NodeTypes.CLASSES));
        TreeModel<NodeData> treeModel = new TreeModel<>(node0);
        NodeModel<NodeData> node1 = new NodeModel<>(new NodeData(lexer.getMethods().get(0).getNameAsString(), lexer.getMethods().get(0).toString(), NodeTypes.METHODS));
        NodeModel<NodeData> node2 = new NodeModel<>(new NodeData(lexer.getFields().get(0).getVariables().get(0).getNameAsString(), lexer.getFields().get(0).getVariables().get(0).toString(), NodeTypes.VARIABLES));


        treeModel.addNode(node0, node1, node2);
        adapter.setTreeModel(treeModel);
    }


    private StringBuilder code() {
        return new StringBuilder("package com.dark.androidbox.Adpaters;\n" +
                "\n" +
                "import android.annotation.SuppressLint;\n" +
                "import android.app.Activity;\n" +
                "import android.graphics.Color;\n" +
                "import android.text.Spannable;\n" +
                "import android.text.SpannableStringBuilder;\n" +
                "import android.text.style.ForegroundColorSpan;\n" +
                "import android.util.Log;\n" +
                "import android.view.LayoutInflater;\n" +
                "import android.view.View;\n" +
                "import android.view.ViewGroup;\n" +
                "import android.widget.LinearLayout;\n" +
                "import android.widget.RelativeLayout;\n" +
                "import android.widget.TextView;\n" +
                "\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.fragment.app.FragmentManager;\n" +
                "\n" +
                "import com.dark.androidbox.Editor.Codes;\n" +
                "import com.dark.androidbox.Fragments.EditorFragment;\n" +
                "import com.dark.androidbox.R;\n" +
                "import com.dark.androidbox.System.NodeEvents;\n" +
                "import com.dark.androidbox.builder.LogicBuilder;\n" +
                "import com.dark.androidbox.databinding.CodeNodesBinding;\n" +
                "import com.google.android.material.button.MaterialButton;\n" +
                "import com.google.android.material.textview.MaterialTextView;\n" +
                "import com.gyso.treeview.TreeViewEditor;\n" +
                "import com.gyso.treeview.adapter.DrawInfo;\n" +
                "import com.gyso.treeview.adapter.TreeViewAdapter;\n" +
                "import com.gyso.treeview.adapter.TreeViewHolder;\n" +
                "import com.gyso.treeview.line.BaseLine;\n" +
                "import com.gyso.treeview.model.NodeModel;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "\n" +
                "public class CodeAdapter extends TreeViewAdapter<Codes> {\n" +
                "\n" +
                "\n" +
                "    public Activity ctx;\n" +
                "    NodeEvents events;\n" +
                "\n" +
                "    FragmentManager manager;\n" +
                "\n" +
                "    TreeViewEditor editor;\n" +
                "\n" +
                "    public CodeAdapter(FragmentManager manager, Activity activity, NodeEvents events, TreeViewEditor editor) {\n" +
                "        this.ctx = activity;\n" +
                "        this.events = events;\n" +
                "        this.manager = manager;\n" +
                "        this.editor = editor;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public TreeViewHolder<Codes> onCreateViewHolder(@NonNull ViewGroup viewGroup, NodeModel<Codes> model) {\n" +
                "        CodeNodesBinding nodeBinding = CodeNodesBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);\n" +
                "        return new TreeViewHolder<>(nodeBinding.getRoot(), model);\n" +
                "    }\n" +
                "\n" +
                "    @SuppressLint(\"SetTextI18n\")\n" +
                "    @Override\n" +
                "    public void onBindViewHolder(@NonNull TreeViewHolder<Codes> holder) {\n" +
                "        View items = holder.getView();\n" +
                "\n" +
                "        LinearLayout head_node = items.findViewById(R.id.head_node);\n" +
                "\n" +
                "        RelativeLayout cardInfo = items.findViewById(R.id.card_info);\n" +
                "\n" +
                "        TextView label = items.findViewById(R.id.label_codeBlock);\n" +
                "\n" +
                "        TextView txt_info = items.findViewById(R.id.txt_info);\n" +
                "\n" +
                "        MaterialTextView node_id = items.findViewById(R.id.node_id);\n" +
                "\n" +
                "        MaterialButton delNode = items.findViewById(R.id.delNode);\n" +
                "\n" +
                "        MaterialButton codeNode = items.findViewById(R.id.btn_code);\n" +
                "\n" +
                "        NodeModel<Codes> nodeObj = holder.getNode();\n" +
                "\n" +
                "        final Codes blockData = nodeObj.value;\n" +
                "\n" +
                "        cardInfo.setVisibility(View.GONE);\n" +
                "\n" +
                "        items.setOnClickListener(view -> events.NodeOnClick(blockData.itemId));\n" +
                "\n" +
                "        head_node.setOnLongClickListener(v -> {\n" +
                "            cardInfo.setVisibility(!(cardInfo.getVisibility() == View.VISIBLE) ? View.VISIBLE : View.GONE);\n" +
                "\n" +
                "            return false;\n" +
                "        });\n" +
                "\n" +
                "        delNode.setOnClickListener(view -> editor.removeNode(holder.getNode()));\n" +
                "\n" +
                "        codeNode.setOnClickListener(view -> {\n" +
                "\n" +
                "        });\n" +
                "\n" +
                "        label.setText(blockData.label);\n" +
                "\n" +
                "        if (blockData.itemId == 0) {\n" +
                "            String data = String.valueOf(setUpClassInfo(new LogicBuilder(EditorFragment.sampleCode())));\n" +
                "\n" +
                "            SpannableStringBuilder colorant = new SpannableStringBuilder(data);\n" +
                "\n" +
                "            SetUpColor(colorant, data, \"Type\", \"#8EBBFF\");\n" +
                "            SetUpColor(colorant, data, \"Returns\", \"#8EBBFF\");\n" +
                "            SetUpColor(colorant, data, \"Extends\", \"#8EBBFF\");\n" +
                "            SetUpColor(colorant, data, \"Interface\", \"#8EBBFF\");\n" +
                "            SetUpColor(colorant, data, \"Null\", \"#FF8E8E\");\n" +
                "            SetUpColor(colorant, data, \"Class\", \"#BBB0FF\");\n" +
                "\n" +
                "            txt_info.setText(colorant);\n" +
                "\n" +
                "        } else {\n" +
                "            if (blockData.itemId == 1) {\n" +
                "                txt_info.setText(\"Click Here To Add Var\");\n" +
                "                txt_info.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);\n" +
                "            } else {\n" +
                "                if (blockData.itemId == 2) {\n" +
                "                    txt_info.setText(\"Click Here To Add Methods\");\n" +
                "                    txt_info.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        node_id.setText(\"\" + blockData.type);\n" +
                "\n" +
                "        Log.d(\"System Info\", String.valueOf(new LogicBuilder(EditorFragment.sampleCode())));\n" +
                "    }\n" +
                "\n" +
                "    public StringBuilder setUpClassInfo(LogicBuilder builder) {\n" +
                "\n" +
                "        StringBuilder Type, Returns, Inputs = new StringBuilder(\"\"), data = new StringBuilder(\"\");\n" +
                "\n" +
                "\n" +
                "        ArrayList<StringBuilder> Interfaces = builder.getClassImplementation();\n" +
                "        StringBuilder Extends = builder.getClassExtends().get(0);\n" +
                "\n" +
                "        if (Extends.length() != 0) {\n" +
                "            Inputs = new StringBuilder(\"Extends : \".concat(Extends.toString()));\n" +
                "            if (Interfaces.size() != 0) {\n" +
                "                Inputs.append(\"\\nInterface : \".concat(String.join(\", \", Interfaces)));\n" +
                "            }\n" +
                "        }\n" +
                "        Type = new StringBuilder(\"Type : Class \\n\");\n" +
                "        Returns = new StringBuilder(\"Returns : Null \\n\");\n" +
                "\n" +
                "        data.append(Type).append(Returns).append(Inputs);\n" +
                "\n" +
                "        return data;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public BaseLine onDrawLine(DrawInfo drawInfo) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    public void SetUpColor(SpannableStringBuilder colorant, String data, String word, String color) {\n" +
                "        ForegroundColorSpan txtColor1 = new ForegroundColorSpan(Color.parseColor(color));\n" +
                "\n" +
                "        int start1 = data.indexOf(word);\n" +
                "        if (start1 != -1) {\n" +
                "            int end1 = start1 + word.length();\n" +
                "            colorant.setSpan(txtColor1, start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);\n" +
                "        }\n" +
                "    }\n" +
                "}");
    }
}