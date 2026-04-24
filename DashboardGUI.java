import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.sql.*;

public class DashboardGUI extends JFrame {

    private final studentsDAO st_dao;
    private final int         user_id;
    private final boolean     isAdmin;

    private DefaultTableModel tableModel;
    private JTable            table;
    private JLabel            statusLabel;

    // ── Palette (shared from LoginGUI) ────────────────────────────────────────
    private static final Color BG        = LoginGUI.BG;
    private static final Color CARD      = LoginGUI.CARD;
    private static final Color CARD_BD   = LoginGUI.CARD_BORDER;
    private static final Color TEXT      = LoginGUI.TEXT;
    private static final Color SUBTEXT   = LoginGUI.SUBTEXT;
    private static final Color ACCENT    = LoginGUI.ACCENT;
    private static final Color INPUT_BG  = LoginGUI.INPUT_BG;
    private static final Color INPUT_BD  = LoginGUI.INPUT_BD;
    private static final Color GREEN     = LoginGUI.GREEN;
    private static final Color RED       = LoginGUI.RED;
    private static final Color SIDEBAR   = new Color(19, 22, 34);
    private static final Color SIDE_HVR  = new Color(30, 35, 55);
    private static final Color SIDE_ACT  = new Color(36, 42, 66);
    private static final Color ROW_ALT   = new Color(20, 23, 36);
    private static final Color ROW_SEL   = new Color(40, 70, 110);
    private static final Color AMBER     = new Color(255, 185, 60);

    public DashboardGUI(studentsDAO st_dao, int user_id, String title, boolean isAdmin) {
        this.st_dao  = st_dao;
        this.user_id = user_id;
        this.isAdmin = isAdmin;
        setTitle("EduRecord — " + title);
        setSize(920, 600);
        setMinimumSize(new Dimension(800, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI(title);
        loadStudents();
        setVisible(true);
    }

    // ══ MAIN LAYOUT ═══════════════════════════════════════════════════════════

    private void buildUI(String title) {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);

        root.add(buildSidebar(title), BorderLayout.WEST);
        root.add(buildMain(),         BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar(String title) {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, CARD_BD));

        // Logo / app name
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(SIDEBAR);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(new EmptyBorder(28, 20, 20, 20));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.setMaximumSize(new Dimension(210, 100));

        JLabel icon = new JLabel("◈");
        icon.setFont(new Font("Dialog", Font.PLAIN, 26));
        icon.setForeground(ACCENT);
        logoPanel.add(icon);
        logoPanel.add(Box.createVerticalStrut(4));
        JLabel appName = new JLabel("EduRecord");
        appName.setFont(new Font("Dialog", Font.BOLD, 15));
        appName.setForeground(TEXT);
        logoPanel.add(appName);
        JLabel roleTag = new JLabel(isAdmin ? "Administrator" : "Student User");
        roleTag.setFont(new Font("Dialog", Font.PLAIN, 11));
        roleTag.setForeground(SUBTEXT);
        logoPanel.add(Box.createVerticalStrut(2));
        logoPanel.add(roleTag);

        sidebar.add(logoPanel);
        sidebar.add(separator());

        // Nav items — each calls the matching option
        String[][] navItems = {
            {"＋  Add Student",    "add"},
            {"☰  View All",        "view"},
            {"✎  Update Student",  "update"},
            {"✕  Remove Student",  "remove"},
            {"⌕  Search",          "search"},
        };
        for (String[] item : navItems) {
            sidebar.add(navBtn(item[0], item[1]));
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(separator());

        // Logout at the bottom
        JButton logoutBtn = navBtnRaw("⏻  Log Out");
        logoutBtn.setForeground(RED);
        logoutBtn.addActionListener(e -> handleExit());
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(12));

        return sidebar;
    }

    private JPanel separator() {
        JPanel sep = new JPanel();
        sep.setBackground(CARD_BD);
        sep.setMaximumSize(new Dimension(210, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    private JButton navBtn(String label, String action) {
        JButton b = navBtnRaw(label);
        b.addActionListener(e -> {
            switch (action) {
                case "add":    showAddDialog();    break;
                case "view":   loadStudents();     break;
                case "update": showUpdateDialog(); break;
                case "remove": showRemoveDialog(); break;
                case "search": showSearchDialog(); break;
            }
        });
        return b;
    }

    private JButton navBtnRaw(String label) {
        JButton b = new JButton(label) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()  ? SIDE_ACT  :
                           getModel().isRollover() ? SIDE_HVR  : new Color(0,0,0,0);
                g2.setColor(bg);
                g2.fillRoundRect(6, 2, getWidth()-12, getHeight()-4, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Dialog", Font.PLAIN, 13));
        b.setForeground(new Color(190, 200, 225));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(11, 20, 11, 20));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(210, 44));
        return b;
    }

    // ── Main content area ─────────────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(BG);

        // Header bar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BD),
                new EmptyBorder(14, 24, 14, 24)));

        JLabel titleLbl = new JLabel("Students");
        titleLbl.setFont(new Font("Dialog", Font.BOLD, 18));
        titleLbl.setForeground(TEXT);

        statusLabel = new JLabel("Loading…");
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        statusLabel.setForeground(SUBTEXT);

        header.add(titleLbl,   BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Name", "Age", "Course"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            // Alternating row colors
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? CARD : ROW_ALT);
                } else {
                    c.setBackground(ROW_SEL);
                }
                c.setForeground(TEXT);
                return c;
            }
        };

        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setGridColor(new Color(35, 40, 60));
        table.setRowHeight(38);
        table.setFont(new Font("Dialog", Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(ROW_SEL);
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);

        // Center-align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < cols.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        // ID column narrower
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(80);

        // Header styling
        JTableHeader header2 = table.getTableHeader();
        header2.setBackground(new Color(22, 26, 42));
        header2.setForeground(SUBTEXT);
        header2.setFont(new Font("Dialog", Font.BOLD, 11));
        header2.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BD));
        header2.setReorderingAllowed(false);

        // Center table header text
        ((DefaultTableCellRenderer) header2.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(new EmptyBorder(16, 20, 16, 20));
        scroll.getVerticalScrollBar().setBackground(BG);

        main.add(scroll, BorderLayout.CENTER);
        return main;
    }

    // ══ DATA ══════════════════════════════════════════════════════════════════

    private void loadStudents() {
        tableModel.setRowCount(0);
        try (Connection con = dbconnection.dbcn()) {
            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM students WHERE user_id = ?");
            pst.setInt(1, user_id);
            ResultSet rs = pst.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                tableModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4)
                });
            }
            statusLabel.setText(count + " student" + (count == 1 ? "" : "s"));
        } catch (Exception e) {
            statusLabel.setText("Error loading data");
            err("Failed to load students:\n" + e.getMessage());
        }
    }

    // ══ OPTION DIALOGS ════════════════════════════════════════════════════════

    // Option 1 — Add Student
    private void showAddDialog() {
        JDialog d = makeDialog("Add New Student", 420, 360);
        JPanel  body = dialogBody(d, "Add New Student", "Fill in the student details below");

        JTextField nameField   = LoginGUI.inputField("e.g. Maria Santos");
        JTextField ageField    = LoginGUI.inputField("e.g. 20");
        JTextField courseField = LoginGUI.inputField("e.g. Computer Science");

        body.add(LoginGUI.fieldLabel("Full Name"));
        body.add(Box.createVerticalStrut(6));
        body.add(nameField);
        body.add(Box.createVerticalStrut(14));
        body.add(LoginGUI.fieldLabel("Age"));
        body.add(Box.createVerticalStrut(6));
        body.add(ageField);
        body.add(Box.createVerticalStrut(14));
        body.add(LoginGUI.fieldLabel("Course"));
        body.add(Box.createVerticalStrut(6));
        body.add(courseField);
        body.add(Box.createVerticalStrut(24));

        JPanel btns = dialogBtns();
        JButton save = colorBtn("Add Student", GREEN);
        JButton cancel = LoginGUI.ghostBtn("Cancel");
        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            String name   = nameField.getText().trim();
            String ageStr = ageField.getText().trim();
            String course = courseField.getText().trim();
            if (name.isEmpty() || ageStr.isEmpty() || course.isEmpty()) {
                err("All fields are required."); return;
            }
            try {
                st_dao.addStudent(new student(name, Integer.parseInt(ageStr), course), user_id);
                d.dispose(); loadStudents();
                ok("Student added successfully.");
            } catch (NumberFormatException ex) { err("Age must be a number."); }
        });
        btns.add(cancel); btns.add(Box.createHorizontalStrut(10)); btns.add(save);
        body.add(btns);
        d.setVisible(true);
    }

    // Option 3 — Remove Student
    private void showRemoveDialog() {
        int sel = table.getSelectedRow();
        JDialog d = makeDialog("Remove Student", 420, 280);
        JPanel  body = dialogBody(d, "Remove Student", "Enter the ID of the student to remove");

        JTextField idField = LoginGUI.inputField("Student ID");
        if (sel != -1) idField.setText(String.valueOf(tableModel.getValueAt(sel, 0)));

        JLabel hint = new JLabel(sel != -1
                ? "Selected: " + tableModel.getValueAt(sel, 1) : "Tip: click a row to pre-fill");
        hint.setFont(new Font("Dialog", Font.ITALIC, 12));
        hint.setForeground(SUBTEXT);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(LoginGUI.fieldLabel("Student ID"));
        body.add(Box.createVerticalStrut(6));
        body.add(idField);
        body.add(Box.createVerticalStrut(8));
        body.add(hint);
        body.add(Box.createVerticalStrut(24));

        JPanel btns = dialogBtns();
        JButton remove = colorBtn("Remove", RED);
        JButton cancel = LoginGUI.ghostBtn("Cancel");
        cancel.addActionListener(e -> d.dispose());
        remove.addActionListener(e -> {
            String idStr = idField.getText().trim();
            if (idStr.isEmpty()) { err("Enter a student ID."); return; }
            try {
                int id = Integer.parseInt(idStr);
                int confirm = JOptionPane.showConfirmDialog(d,
                        "Permanently remove student with ID " + id + "?",
                        "Confirm Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    st_dao.remove_student(id); d.dispose(); loadStudents();
                    ok("Student removed.");
                }
            } catch (NumberFormatException ex) { err("ID must be a number."); }
        });
        btns.add(cancel); btns.add(Box.createHorizontalStrut(10)); btns.add(remove);
        body.add(btns);
        d.setVisible(true);
    }

    // Option 4 — Update Student
    private void showUpdateDialog() {
        int sel = table.getSelectedRow();
        JDialog d = makeDialog("Update Student", 420, 420);
        JPanel body = dialogBody(d, "Update Student", "Edit the student's information");

        JTextField idField     = LoginGUI.inputField("Student ID");
        JTextField nameField   = LoginGUI.inputField("Full name");
        JTextField ageField    = LoginGUI.inputField("Age");
        JTextField courseField = LoginGUI.inputField("Course");

        if (sel != -1) {
            idField.setText(String.valueOf(tableModel.getValueAt(sel, 0)));
            nameField.setText((String) tableModel.getValueAt(sel, 1));
            ageField.setText(String.valueOf(tableModel.getValueAt(sel, 2)));
            courseField.setText((String) tableModel.getValueAt(sel, 3));
        }

        body.add(LoginGUI.fieldLabel("Student ID"));
        body.add(Box.createVerticalStrut(6)); body.add(idField);
        body.add(Box.createVerticalStrut(14));
        body.add(LoginGUI.fieldLabel("Full Name"));
        body.add(Box.createVerticalStrut(6)); body.add(nameField);
        body.add(Box.createVerticalStrut(14));
        body.add(LoginGUI.fieldLabel("Age"));
        body.add(Box.createVerticalStrut(6)); body.add(ageField);
        body.add(Box.createVerticalStrut(14));
        body.add(LoginGUI.fieldLabel("Course"));
        body.add(Box.createVerticalStrut(6)); body.add(courseField);
        body.add(Box.createVerticalStrut(24));

        JPanel btns = dialogBtns();
        JButton update = colorBtn("Save Changes", AMBER);
        JButton cancel = LoginGUI.ghostBtn("Cancel");
        cancel.addActionListener(e -> d.dispose());
        update.addActionListener(e -> {
            String idStr  = idField.getText().trim();
            String name   = nameField.getText().trim();
            String ageStr = ageField.getText().trim();
            String course = courseField.getText().trim();
            if (idStr.isEmpty() || name.isEmpty() || ageStr.isEmpty() || course.isEmpty()) {
                err("All fields are required."); return;
            }
            try {
                st_dao.update_student(Integer.parseInt(idStr),
                        new student(name, Integer.parseInt(ageStr), course));
                d.dispose(); loadStudents(); ok("Student updated.");
            } catch (NumberFormatException ex) { err("ID and Age must be numbers."); }
        });
        btns.add(cancel); btns.add(Box.createHorizontalStrut(10)); btns.add(update);
        body.add(btns);
        d.setVisible(true);
    }

    // Option 5 — Search Student
    private void showSearchDialog() {
        JDialog d = makeDialog("Search Students", 420, 260);
        JPanel body = dialogBody(d, "Search Students", "Search by student name");

        JTextField nameField = LoginGUI.inputField("Enter name to search…");

        body.add(LoginGUI.fieldLabel("Student Name"));
        body.add(Box.createVerticalStrut(6));
        body.add(nameField);
        body.add(Box.createVerticalStrut(24));

        JPanel btns = dialogBtns();
        JButton search = colorBtn("Search", ACCENT);
        JButton cancel = LoginGUI.ghostBtn("Cancel");
        cancel.addActionListener(e -> d.dispose());
        search.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { err("Enter a name to search."); return; }
            tableModel.setRowCount(0);
            try (Connection con = dbconnection.dbcn()) {
                PreparedStatement pst = con.prepareStatement(
                        "SELECT * FROM students WHERE name = ?");
                pst.setString(1, name);
                ResultSet rs = pst.executeQuery();
                int count = 0;
                while (rs.next()) {
                    count++;
                    tableModel.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4)
                    });
                }
                d.dispose();
                statusLabel.setText(count + " result" + (count == 1 ? "" : "s") + " for "" + name + """);
                if (count == 0) ok("No student found with the name "" + name + "".");
            } catch (Exception ex) { err("Search failed:\n" + ex.getMessage()); }
        });
        btns.add(cancel); btns.add(Box.createHorizontalStrut(10)); btns.add(search);
        body.add(btns);
        d.setVisible(true);
    }

    // Option 6 — Exit / Logout
    private void handleExit() {
        int c = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?", "Log Out",
                JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) { dispose(); new LoginGUI(); }
    }

    // ══ DIALOG BUILDERS ═══════════════════════════════════════════════════════

    private JDialog makeDialog(String title, int w, int h) {
        JDialog d = new JDialog(this, title, true);
        d.setSize(w, h);
        d.setResizable(false);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(BG);
        return d;
    }

    /** Returns the scrollable body panel already added to the dialog */
    private JPanel dialogBody(JDialog d, String heading, String sub) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.setBorder(new EmptyBorder(28, 32, 20, 32));

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG);

        JLabel h = new JLabel(heading);
        h.setFont(new Font("Dialog", Font.BOLD, 17));
        h.setForeground(TEXT);
        h.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel s = new JLabel(sub);
        s.setFont(new Font("Dialog", Font.PLAIN, 12));
        s.setForeground(SUBTEXT);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(h);
        body.add(Box.createVerticalStrut(4));
        body.add(s);
        body.add(Box.createVerticalStrut(22));

        outer.add(body, BorderLayout.CENTER);
        d.add(outer, BorderLayout.CENTER);
        return body;
    }

    private JPanel dialogBtns() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        p.setBackground(BG);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(9999, 50));
        return p;
    }

    private JButton colorBtn(String text, Color bg) {
        JButton b = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed()  ? bg.darker() :
                          getModel().isRollover() ? bg.brighter() : bg;
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Dialog", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        return b;
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE);
    }
    private void ok(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
