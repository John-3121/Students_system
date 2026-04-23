import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class AdminGUI extends JFrame {

    private studentsDAO st_dao;
    private int user_id;

    private DefaultTableModel tableModel;
    private JTable studentTable;

    public AdminGUI(studentsDAO st_dao, int user_id) {
        this.st_dao = st_dao;
        this.user_id = user_id;

        setTitle("Admin Dashboard");
        setSize(750, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
        loadStudents();
        setVisible(true);
    }

    private void buildUI() {
        getContentPane().setBackground(new Color(30, 30, 40));
        setLayout(new BorderLayout(10, 10));

        // ── Title bar ────────────────────────────────────────────────────────
        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(100, 200, 255));
        title.setBorder(BorderFactory.createEmptyBorder(14, 0, 6, 0));
        add(title, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────────
        String[] columns = {"ID", "Name", "Age", "Course"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = new JTable(tableModel);
        studentTable.setBackground(new Color(40, 40, 55));
        studentTable.setForeground(Color.WHITE);
        studentTable.setGridColor(new Color(70, 70, 90));
        studentTable.setRowHeight(26);
        studentTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        studentTable.getTableHeader().setBackground(new Color(50, 100, 180));
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        studentTable.setSelectionBackground(new Color(60, 120, 200));

        JScrollPane scroll = new JScrollPane(studentTable);
        scroll.getViewport().setBackground(new Color(40, 40, 55));
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(scroll, BorderLayout.CENTER);

        // ── Button panel ──────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(new Color(30, 30, 40));

        JButton addBtn    = btn("Add",    new Color(46, 139, 87));
        JButton removeBtn = btn("Remove", new Color(180, 50, 50));
        JButton updateBtn = btn("Update", new Color(180, 130, 0));
        JButton searchBtn = btn("Search", new Color(60, 120, 200));
        JButton refreshBtn= btn("Refresh",new Color(80, 80, 100));
        JButton logoutBtn = btn("Logout", new Color(100, 40, 100));

        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(searchBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(logoutBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // ── Actions ───────────────────────────────────────────────────────────
        addBtn.addActionListener(e -> showAddDialog());
        removeBtn.addActionListener(e -> removeSelected());
        updateBtn.addActionListener(e -> showUpdateDialog());
        searchBtn.addActionListener(e -> showSearchDialog());
        refreshBtn.addActionListener(e -> loadStudents());
        logoutBtn.addActionListener(e -> { dispose(); new LoginGUI(); });
    }

    // ── Load all students into table ──────────────────────────────────────────
    private void loadStudents() {
        tableModel.setRowCount(0);
        try (Connection con = dbconnection.dbcn()) {
            String query = "SELECT * FROM students WHERE user_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, user_id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4)
                });
            }
        } catch (Exception e) {
            error("Failed to load students: " + e.getMessage());
        }
    }

    // ── Add student dialog ────────────────────────────────────────────────────
    private void showAddDialog() {
        JDialog dialog = dialog("Add Student");
        JPanel p = formPanel();

        JTextField nameField   = field(); 
        JTextField ageField    = field(); 
        JTextField courseField = field();

        addRow(p, "Name:",   nameField);
        addRow(p, "Age:",    ageField);
        addRow(p, "Course:", courseField);

        JButton save = btn("Save", new Color(46, 139, 87));
        save.addActionListener(e -> {
            try {
                String name   = nameField.getText().trim();
                int    age    = Integer.parseInt(ageField.getText().trim());
                String course = courseField.getText().trim();
                if (name.isEmpty() || course.isEmpty()) { error("All fields required."); return; }
                st_dao.addStudent(new student(name, age, course), user_id);
                dialog.dispose();
                loadStudents();
                info("Student added successfully.");
            } catch (NumberFormatException ex) {
                error("Age must be a number.");
            }
        });
        finishDialog(dialog, p, save);
    }

    // ── Remove selected row ───────────────────────────────────────────────────
    private void removeSelected() {
        int row = studentTable.getSelectedRow();
        if (row == -1) { error("Select a student to remove."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove student with ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            st_dao.remove_student(id);
            loadStudents();
            info("Student removed.");
        }
    }

    // ── Update dialog (pre-filled with selected row) ──────────────────────────
    private void showUpdateDialog() {
        int row = studentTable.getSelectedRow();
        if (row == -1) { error("Select a student to update."); return; }

        int    id     = (int)    tableModel.getValueAt(row, 0);
        String name   = (String) tableModel.getValueAt(row, 1);
        int    age    = (int)    tableModel.getValueAt(row, 2);
        String course = (String) tableModel.getValueAt(row, 3);

        JDialog dialog = dialog("Update Student — ID " + id);
        JPanel p = formPanel();

        JTextField nameField   = field(); nameField.setText(name);
        JTextField ageField    = field(); ageField.setText(String.valueOf(age));
        JTextField courseField = field(); courseField.setText(course);

        addRow(p, "Name:",   nameField);
        addRow(p, "Age:",    ageField);
        addRow(p, "Course:", courseField);

        JButton save = btn("Update", new Color(180, 130, 0));
        save.addActionListener(e -> {
            try {
                String newName   = nameField.getText().trim();
                int    newAge    = Integer.parseInt(ageField.getText().trim());
                String newCourse = courseField.getText().trim();
                if (newName.isEmpty() || newCourse.isEmpty()) { error("All fields required."); return; }
                st_dao.update_student(id, new student(newName, newAge, newCourse));
                dialog.dispose();
                loadStudents();
                info("Student updated.");
            } catch (NumberFormatException ex) {
                error("Age must be a number.");
            }
        });
        finishDialog(dialog, p, save);
    }

    // ── Search dialog ─────────────────────────────────────────────────────────
    private void showSearchDialog() {
        JDialog dialog = dialog("Search Student");
        JPanel p = formPanel();
        JTextField nameField = field();
        addRow(p, "Name:", nameField);

        JButton searchBtn = btn("Search", new Color(60, 120, 200));
        searchBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { error("Enter a name to search."); return; }
            tableModel.setRowCount(0);
            try (Connection con = dbconnection.dbcn()) {
                String query = "SELECT * FROM students WHERE name = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, name);
                ResultSet rs = pst.executeQuery();
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    tableModel.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4)
                    });
                }
                dialog.dispose();
                if (!found) info("No student found with that name.");
            } catch (Exception ex) {
                error("Search failed: " + ex.getMessage());
            }
        });
        finishDialog(dialog, p, searchBtn);
    }

    // ── Swing helpers ─────────────────────────────────────────────────────────
    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        return b;
    }

    private JDialog dialog(String title) {
        JDialog d = new JDialog(this, title, true);
        d.setSize(360, 260);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(new Color(30, 30, 40));
        return d;
    }

    private JPanel formPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(30, 30, 40));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        return p;
    }

    private JTextField field() {
        JTextField f = new JTextField(15);
        f.setBackground(new Color(50, 50, 65));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 100)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        return f;
    }

    private void addRow(JPanel p, String labelText, JComponent field) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;
        int row  = p.getComponentCount() / 2;
        g.gridx  = 0; g.gridy = row;
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(Color.LIGHT_GRAY);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        p.add(lbl, g);
        g.gridx = 1; p.add(field, g);
    }

    private void finishDialog(JDialog d, JPanel form, JButton action) {
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        south.setBackground(new Color(30, 30, 40));
        JButton cancel = btn("Cancel", new Color(80, 80, 100));
        cancel.addActionListener(e -> d.dispose());
        south.add(cancel);
        south.add(action);
        d.add(form, BorderLayout.CENTER);
        d.add(south, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE);
    }
    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info",    JOptionPane.INFORMATION_MESSAGE);
    }
}
