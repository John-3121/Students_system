import javax.swing.*;
import java.awt.*;

public class LoginGUI extends JFrame {

    private studentsDAO st_dao = new studentsDAO();

    public LoginGUI() {
        setTitle("Student Record System");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        showMainMenu();
        setVisible(true);
    }

    // ── Main Menu ──────────────────────────────────────────────────────────────
    private void showMainMenu() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Student Record System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(100, 200, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        JButton signInBtn = styledButton("Sign In");
        JButton signUpBtn = styledButton("Sign Up");

        gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridx = 0;
        panel.add(signInBtn, gbc);
        gbc.gridx = 1;
        panel.add(signUpBtn, gbc);

        signInBtn.addActionListener(e -> showSignIn());
        signUpBtn.addActionListener(e -> showSignUp());

        setContentPane(panel);
        revalidate();
        repaint();
    }

    // ── Sign In ────────────────────────────────────────────────────────────────
    private void showSignIn() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Sign In", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(100, 200, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        panel.add(label("Email:"), gbc(gbc, 0, 1));
        JTextField emailField = styledField();
        panel.add(emailField, gbc(gbc, 1, 1));

        panel.add(label("Password:"), gbc(gbc, 0, 2));
        JPasswordField passField = new JPasswordField(15);
        styleField(passField);
        panel.add(passField, gbc(gbc, 1, 2));

        JButton loginBtn  = styledButton("Login");
        JButton backBtn   = styledButton("Back");

        panel.add(backBtn,  gbc(gbc, 0, 3));
        panel.add(loginBtn, gbc(gbc, 1, 3));

        loginBtn.addActionListener(e -> {
            String email    = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            if (email.isEmpty() || password.isEmpty()) {
                error("Please fill in all fields."); return;
            }
            int user_id = st_dao.Check_login_info(email, password);
            if (user_id == 0) {
                error("Invalid email or password.");
            }
            // Check_login_info already launches Admin/Normal_user options via CLI.
            // For the GUI we need to know the role — we re-query it here.
            else {
                openDashboard(email, password, user_id);
            }
        });

        backBtn.addActionListener(e -> showMainMenu());

        setContentPane(panel);
        revalidate(); repaint();
    }

    // ── Open the right dashboard after login ──────────────────────────────────
    private void openDashboard(String email, String password, int user_id) {
        // Determine role by re-checking credentials (same query as DAO)
        try (java.sql.Connection con = dbconnection.dbcn()) {
            String query = "SELECT roles FROM Admin WHERE email = ? AND password = ?";
            java.sql.PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, password);
            java.sql.ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String role = rs.getString("roles");
                dispose();
                if (role.equals("admin")) {
                    new AdminGUI(st_dao, user_id);
                } else {
                    new NormalUserGUI(st_dao, user_id);
                }
            }
        } catch (Exception ex) {
            error("DB error: " + ex.getMessage());
        }
    }

    // ── Sign Up ────────────────────────────────────────────────────────────────
    private void showSignUp() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Sign Up", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(100, 200, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        panel.add(label("Email:"), gbc(gbc, 0, 1));
        JTextField emailField = styledField();
        panel.add(emailField, gbc(gbc, 1, 1));

        panel.add(label("Password:"), gbc(gbc, 0, 2));
        JPasswordField passField = new JPasswordField(15);
        styleField(passField);
        panel.add(passField, gbc(gbc, 1, 2));

        panel.add(label("Role:"), gbc(gbc, 0, 3));
        String[] roles = {"Normal_user", "admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        roleBox.setBackground(new Color(50, 50, 65));
        roleBox.setForeground(Color.WHITE);
        panel.add(roleBox, gbc(gbc, 1, 3));

        JButton registerBtn = styledButton("Register");
        JButton backBtn     = styledButton("Back");

        panel.add(backBtn,     gbc(gbc, 0, 4));
        panel.add(registerBtn, gbc(gbc, 1, 4));

        registerBtn.addActionListener(e -> {
            String email    = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String role     = (String) roleBox.getSelectedItem();
            if (email.isEmpty() || password.isEmpty()) {
                error("Please fill in all fields."); return;
            }
            st_dao.user_storeinf(email, password, role);
            JOptionPane.showMessageDialog(this, "Account created! You can now sign in.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            showMainMenu();
        });

        backBtn.addActionListener(e -> showMainMenu());

        setContentPane(panel);
        revalidate(); repaint();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────
    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(60, 120, 200));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.LIGHT_GRAY);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return lbl;
    }

    private JTextField styledField() {
        JTextField f = new JTextField(15);
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(50, 50, 65));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 100)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
    }

    private GridBagConstraints gbc(GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x; gbc.gridy = y;
        return gbc;
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginGUI::new);
    }
}
