package launcher;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import javax.swing.*;
import javax.swing.tree.*;

public class Launcher extends JFrame {
    private JTree taskTree;

    public Launcher() {
        setTitle("Java課題ランチャー");
        setSize(500, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ツリーノードの構築
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Java課題");
        buildTaskTree(root);

        // JTree の作成
        taskTree = new JTree(root);
        taskTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = taskTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) taskTree.getModel().getRoot();
                        if (node.isLeaf() && node != rootNode) {
                            launchTask(node);
                        }
                    }
                }
            }
        });

        // スクロールペインに追加
        JScrollPane scrollPane = new JScrollPane(taskTree);
        add(scrollPane, BorderLayout.CENTER);

        // 全ノードを展開
        expandAllNodes(taskTree, 0, taskTree.getRowCount());

        setVisible(true);
    }

    private void buildTaskTree(DefaultMutableTreeNode root) {
        Task[] tasks = Task.values();
        java.util.Map<String, DefaultMutableTreeNode> categoryMap = new java.util.LinkedHashMap<>();

        for (Task task : tasks) {
            String taskName = task.getName();
            String categoryName = extractCategoryName(taskName);

            DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(new TaskNode(task));

            // categoryNameが存在する場合（①～⑨で始まる場合）
            if (categoryName != null) {
                DefaultMutableTreeNode categoryNode = categoryMap.get(categoryName);
                if (categoryNode == null) {
                    // 新しいカテゴリを作成
                    categoryNode = new DefaultMutableTreeNode(categoryName);
                    categoryMap.put(categoryName, categoryNode);
                    root.add(categoryNode);
                }
                // タスクをカテゴリに追加
                categoryNode.add(taskNode);
            } else {
                // categoryNameがない場合（例："はじめに"）はルートに直接追加
                root.add(taskNode);
            }
        }
    }

    private String extractCategoryName(String name) {
        // "①", "②", "③" などの1文字目のみを抽出
        if (name == null || name.isEmpty()) {
            return null;
        }
        // 例："③-1 if..." → "③", "③ 条件分岐" → "③"
        // 最初の1文字が①～⑨の場合のみカテゴリとして使用
        char firstChar = name.charAt(0);
        if (firstChar >= '①' && firstChar <= '⑨') {
            return String.valueOf(firstChar);
        }
        return null;
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    private void launchTask(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof TaskNode taskNode) {
            Task task = taskNode.task;

            if (task.isReadme()) {
                ReadmeDialog.showReadme(this);
                return;
            }

            try {
                Class<?> clazz = Class.forName(task.getMainClassName());
                Method mainMethod = clazz.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) new String[0]);
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
                JOptionPane.showMessageDialog(this, "起動に失敗しました: " + ex.getMessage());
            } catch (IllegalAccessException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "メソッド実行に失敗しました: " + ex.getMessage());
            } catch (java.lang.reflect.InvocationTargetException ex) {
                JOptionPane.showMessageDialog(this, "実行中にエラーが発生しました: " + ex.getMessage());
            }
        }
    }

    private static class TaskNode {
        Task task;

        TaskNode(Task task) {
            this.task = task;
        }

        @Override
        public String toString() {
            return task.getName();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Launcher::new);
    }
}
