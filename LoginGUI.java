import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.sql.*;

public class LoginGUI extends JFrame {

    // ── Palette ───────────────────────────────────────────────────────────────
    static final Color BG         = new Color(15,  17,  26);
    static final Color CARD       = new Color(24,  27,  42);
    static final Color CARD_BORDER= new Color(45,  50,  75);
    static final Color ACCENT     = new Color(99, 179, 255);
    static final Color ACCENT2    = new Color(67, 133, 214);
    static final Color TEXT       = new Color(220, 225, 240);
    static final Color SUBTEXT    = new Color(130, 140, 170);
    static final Color INPUT_BG   = new Color(18,  21,  35);
    static final Color INPUT_BD   = new Color(55,  62,  95);
    static final Color GREEN      = new Color(72, 199, 142);
    static final Color RED        = new Color(240, 80,  80);

    private final studentsDAO st_dao = new studentsDAO();

    public LoginGUI() {
        setTitle("EduRecord — Student Management");
        setSize(480, 560);
        setMinimumSize(new Dimension(420, 480));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        showMainMenu();
        setVisible(true);
    }

    // ══ SCREENS ═══════════════════════════════════════════════════════════════

    private void showMainMenu() {
        JPanel root = bg();
        root.setLayout(new GridBagLayout());

        JPanel card = card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(44, 48, 44, 48));

        card.add(logo());
        card.add(Box.createVerticalStrut(8));
        card.add(centeredLabel("Student Record System", 22, Font.BOLD, TEXT));
        card.add(Box.createVerticalStrut(6));
        card.add(centeredLabel("Manage your academic records", 13, Font.PLAIN, SUBTEXT));
        card.add(Box.createVerticalStrut(36));

        JButton signInBtn = primaryBtn("Sign In");
        JButton signUpBtn = ghostBtn("Create Account");
        card.add(signInBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(signUpBtn);

        signInBtn.addActionListener(e -> showSignIn());
        signUpBtn.addActionListener(e -> showSignUp());

        root.add(card);
        setContentPane(root);
        revalidate(); repaint();
    }

    private void showSignIn() {
        JPanel root = bg();
        root.setLayout(new GridBagLayout());

        JPanel card = card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 48, 40, 48));

        card.add(centeredLabel("Welcome Back", 22, Font.BOLD, TEXT));
        card.add(Box.createVerticalStrut(6));
        card.add(centeredLabel("Sign in to your account", 13, Font.PLAIN, SUBTEXT));
        card.add(Box.createVerticalStrut(32));

        JTextField     emailField = inputField("Email address");
        JPasswordField passField  = passField("Password");

        card.add(fieldLabel("Email"));
        card.add(Box.createVerticalStrut(6));
        card.add(emailField);
        card.add(Box.createVerticalStrut(16));
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(passField);
        card.add(Box.createVerticalStrut(28));

        JButton loginBtn = primaryBtn("Sign In");
        JButton backBtn  = linkBtn("← Back to menu");
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(backBtn);

        loginBtn.addActionListener(e -> {
            String email    = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            if (email.isEmpty() || password.isEmpty()) { err("Please fill in all fields."); return; }
            openDashboard(email, password);
        });
        backBtn.addActionListener(e -> showMainMenu());

        root.add(card);
        setContentPane(root);
        revalidate(); repaint();
    }

    private void showSignUp() {
        JPanel root = bg();
        root.setLayout(new GridBagLayout());

        JPanel card = card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 48, 36, 48));

        card.add(centeredLabel("Create Account", 22, Font.BOLD, TEXT));
        card.add(Box.createVerticalStrut(6));
        card.add(centeredLabel("Register a new account to get started", 13, Font.PLAIN, SUBTEXT));
        card.add(Box.createVerticalStrut(28));

        JTextField     emailField = inputField("Enter your email");
        JPasswordField passField  = passField("Choose a password");

        String[] roleOptions = {"Normal User", "Admin"};
        JComboBox<String> roleBox = styledCombo(roleOptions);

        card.add(fieldLabel("Email"));
        card.add(Box.createVerticalStrut(6));
        card.add(emailField);
        card.add(Box.createVerticalStrut(16));
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(passField);
        card.add(Box.createVerticalStrut(16));
        card.add(fieldLabel("Role"));
        card.add(Box.createVerticalStrut(6));
        card.add(roleBox);
        card.add(Box.createVerticalStrut(28));

        JButton registerBtn = primaryBtn("Create Account");
        JButton backBtn     = linkBtn("← Back to menu");
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(backBtn);

        registerBtn.addActionListener(e -> {
            String email    = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String role     = roleBox.getSelectedIndex() == 1 ? "admin" : "Normal_user";
            if (email.isEmpty() || password.isEmpty()) { err("Please fill in all fields."); return; }
            st_dao.user_storeinf(email, password, role);
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!\nYou can now sign in.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            showMainMenu();
        });
        backBtn.addActionListener(e -> showMainMenu());

        root.add(card);
        setContentPane(root);
        revalidate(); repaint();
    }

    private void openDashboard(String email, String password) {
        try (Connection con = dbconnection.dbcn()) {
            PreparedStatement pst = con.prepareStatement(
                    "SELECT id, roles FROM Admin WHERE email = ? AND password = ?");
            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int    user_id = rs.getInt("id");
                String role    = rs.getString("roles");
                dispose();
                boolean isAdmin = role.equals("admin");
                new DashboardGUI(st_dao, user_id,
                        isAdmin ? "Admin Dashboard" : "Student Dashboard", isAdmin);
            } else {
                err("Invalid email or password.");
            }
        } catch (Exception ex) {
            err("Connection error:\n" + ex.getMessage());
        }
    }

    // ══ COMPONENT BUILDERS ════════════════════════════════════════════════════

    static JPanel bg() {
        JPanel p = new JPanel();
        p.setBackground(BG);
        p.setOpaque(true);
        return p;
    }

    static JPanel card() {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0.6f, 0.6f, getWidth()-1.2f, getHeight()-1.2f, 18, 18));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBackground(CARD);
        p.setPreferredSize(new Dimension(360, 420));
        p.setMaximumSize(new Dimension(360, 9999));
        return p;
    }

    static JLabel centeredLabel(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Dialog", style, size));
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(9999, 40));
        return l;
    }

    static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Dialog", Font.BOLD, 12));
        l.setForeground(SUBTEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    static JLabel logo() {
        JLabel l = new JLabel("◈", SwingConstants.CENTER);
        l.setFont(new Font("Dialog", Font.PLAIN, 38));
        l.setForeground(ACCENT);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(9999, 50));
        return l;
    }

    static JTextField inputField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("Dialog", Font.PLAIN, 13));
        f.setForeground(TEXT);
        f.setBackground(INPUT_BG);
        f.setCaretColor(ACCENT);
        f.setOpaque(true);
        f.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(INPUT_BD, 10, 1),
                new EmptyBorder(10, 14, 10, 14)));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(9999, 42));
        return f;
    }

    static JPasswordField passField(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Dialog", Font.PLAIN, 13));
        f.setForeground(TEXT);
        f.setBackground(INPUT_BG);
        f.setCaretColor(ACCENT);
        f.setOpaque(true);
        f.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(INPUT_BD, 10, 1),
                new EmptyBorder(10, 14, 10, 14)));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(9999, 42));
        return f;
    }

    static JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("Dialog", Font.PLAIN, 13));
        c.setBackground(INPUT_BG);
        c.setForeground(TEXT);
        c.setBorder(new RoundedBorder(INPUT_BD, 10, 1));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(9999, 42));
        return c;
    }

    static JButton primaryBtn(String text) {
        JButton b = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed()  ? ACCENT2.darker() :
                             getModel().isRollover() ? ACCENT2 : ACCENT;
                g2.setColor(base);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Dialog", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(12, 24, 12, 24));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(9999, 46));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        return b;
    }

    static JButton ghostBtn(String text) {
        JButton b = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(99, 179, 255, 25) : new Color(0, 0, 0, 0);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1.4f));
                g2.draw(new RoundRectangle2D.Float(0.7f, 0.7f, getWidth()-1.4f, getHeight()-1.4f, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Dialog", Font.BOLD, 14));
        b.setForeground(ACCENT);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(12, 24, 12, 24));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(9999, 46));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        return b;
    }

    static JButton linkBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Dialog", Font.PLAIN, 13));
        b.setForeground(SUBTEXT);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setMaximumSize(new Dimension(9999, 30));
        return b;
    }

    // ── Rounded border helper ─────────────────────────────────────────────────
    static class RoundedBorder extends AbstractBorder {
        private final Color color; private final int radius, thickness;
        RoundedBorder(Color c, int r, int t) { color = c; radius = r; thickness = t; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x+0.5f, y+0.5f, w-1, h-1, radius, radius));
            g2.dispose();
        }
        public Insets getBorderInsets(Component c) { return new Insets(thickness,thickness,thickness,thickness); }
    }

    void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginGUI::new);
    }
}
