import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

class Candidate {
    String name;
    int votes;

    Candidate(String name) {
        this.name = name;
        votes = 0;
    }
}

class Voter {
    String id;
    boolean voted;

    Voter(String id) {
        this.id = id;
        voted = false;
    }
}

public class VotingGUI {

    JFrame frame;
    CardLayout cardLayout;
    JPanel mainPanel;

    ArrayList<Candidate> candidates = new ArrayList<>();
    ArrayList<Voter> voters = new ArrayList<>();

    Voter currentVoter;

    JPanel votePanel = new JPanel(null);
    JPanel resultPanel = new JPanel(null);
    JTextArea resultArea = new JTextArea();

    // ----------- ADMIN DETAILS -----------
    String ADMIN_USER = "superadmin";
    String ADMIN_PASS = "admin2025";

    public VotingGUI() {

        frame = new JFrame("Online Voting System (Swing)");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add default voters
        voters.add(new Voter("ARJ123"));
        voters.add(new Voter("PRI456"));
        voters.add(new Voter("KAV789"));
        voters.add(new Voter("SAN321"));
        voters.add(new Voter("MEE654"));

        // Auto-add fixed candidates
        candidates.add(new Candidate("VVS"));
        candidates.add(new Candidate("DMK"));
        candidates.add(new Candidate("TVK"));

        mainPanel.add(loginPanel(), "login");
        mainPanel.add(adminPanel(), "admin");
        mainPanel.add(voterLoginPanel(), "voterLogin");
        mainPanel.add(votePanel, "vote");
        mainPanel.add(resultPanel, "result");

        frame.add(mainPanel);
        frame.setSize(600, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        setupResultPanel();
    }

    // ---------------- LOGIN PANEL ----------------
    JPanel loginPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("ONLINE VOTING SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(130, 30, 400, 30);
        panel.add(title);

        JButton adminBtn = new JButton("Admin Login");
        adminBtn.setBounds(200, 120, 200, 40);
        adminBtn.setBackground(new Color(120, 170, 255));
        adminBtn.addActionListener(e -> showAdminLogin());
        panel.add(adminBtn);

        JButton voterBtn = new JButton("Voter Login");
        voterBtn.setBounds(200, 180, 200, 40);
        voterBtn.setBackground(new Color(180, 255, 180));
        voterBtn.addActionListener(e -> cardLayout.show(mainPanel, "voterLogin"));
        panel.add(voterBtn);

        return panel;
    }

    // ---------------- ADMIN LOGIN ----------------
    void showAdminLogin() {

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();

        Object[] message = {
                "Username:", user,
                "Password:", pass
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Admin Login", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            if (user.getText().equals(ADMIN_USER) && new String(pass.getPassword()).equals(ADMIN_PASS)) {
                cardLayout.show(mainPanel, "admin");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Login!");
            }
        }
    }

    // ---------------- ADMIN DASHBOARD ----------------
    JPanel adminPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(255, 253, 208));

        JLabel title = new JLabel("ADMIN DASHBOARD");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(180, 20, 300, 30);
        panel.add(title);

        JButton viewResult = new JButton("View Results");
        viewResult.setBounds(200, 150, 200, 40);
        viewResult.setBackground(new Color(255, 200, 140));
        viewResult.addActionListener(e -> {
            updateResults();
            cardLayout.show(mainPanel, "result");
        });
        panel.add(viewResult);

        JButton logout = new JButton("Logout");
        logout.setBounds(200, 220, 200, 40);
        logout.setBackground(new Color(255, 150, 150));
        logout.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        panel.add(logout);

        return panel;
    }

    // ---------------- VOTER LOGIN PANEL ----------------
    JTextField voterIdField;

    JPanel voterLoginPanel() {

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(230, 255, 250));

        JLabel title = new JLabel("VOTER LOGIN");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(200, 40, 300, 30);
        panel.add(title);

        JLabel idLabel = new JLabel("Enter Voter ID:");
        idLabel.setBounds(150, 140, 120, 25);
        panel.add(idLabel);

        voterIdField = new JTextField();
        voterIdField.setBounds(280, 140, 150, 25);
        panel.add(voterIdField);

        JButton login = new JButton("Login");
        login.setBounds(240, 190, 120, 35);
        login.setBackground(new Color(170, 220, 250));
        login.addActionListener(e -> checkVoterLogin());
        panel.add(login);

        JButton back = new JButton("Back");
        back.setBounds(240, 240, 120, 35);
        back.setBackground(new Color(255, 180, 180));
        back.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        panel.add(back);

        return panel;
    }

    void checkVoterLogin() {
        String id = voterIdField.getText();

        for (Voter v : voters) {
            if (v.id.equals(id)) {
                if (v.voted) {
                    JOptionPane.showMessageDialog(frame, "You already voted!");
                } else {
                    currentVoter = v;
                    loadVotingButtons();
                    cardLayout.show(mainPanel, "vote");
                }
                return;
            }
        }

        JOptionPane.showMessageDialog(frame, "Invalid Voter ID!");
    }

    // ---------------- VOTE PANEL ----------------
    void loadVotingButtons() {
        votePanel.removeAll();
        votePanel.setBackground(new Color(255, 240, 245));

        JLabel title = new JLabel("CAST YOUR VOTE");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(200, 20, 300, 30);
        votePanel.add(title);

        // VOTE OPTIONS (Three Parties)
        JButton btnVVS = new JButton("VVS");
        btnVVS.setBounds(200, 100, 200, 40);
        btnVVS.setBackground(Color.BLUE);
        btnVVS.setForeground(Color.WHITE);
        btnVVS.addActionListener(e -> recordVote("VVS"));
        votePanel.add(btnVVS);

        JButton btnDMK = new JButton("DMK");
        btnDMK.setBounds(200, 170, 200, 40);
        btnDMK.setBackground(Color.RED);
        btnDMK.setForeground(Color.WHITE);
        btnDMK.addActionListener(e -> recordVote("DMK"));
        votePanel.add(btnDMK);

        JButton btnTVK = new JButton("TVK");
        btnTVK.setBounds(200, 240, 200, 40);
        btnTVK.setBackground(Color.YELLOW);
        btnTVK.setForeground(Color.BLACK);
        btnTVK.addActionListener(e -> recordVote("TVK"));
        votePanel.add(btnTVK);
    }

    void recordVote(String party) {
        for (Candidate c : candidates) {
            if (c.name.equals(party)) {
                c.votes++;
            }
        }
        currentVoter.voted = true;
        JOptionPane.showMessageDialog(frame, "Vote Recorded Successfully!");
        cardLayout.show(mainPanel, "login");
    }

    // ---------------- RESULT PANEL ----------------
    void setupResultPanel() {
        resultPanel.setLayout(null);
        resultPanel.setBackground(new Color(240, 255, 240));

        JLabel title = new JLabel("ELECTION RESULTS");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(180, 20, 300, 30);
        resultPanel.add(title);

        resultArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(resultArea);
        scroll.setBounds(120, 80, 350, 220);
        resultPanel.add(scroll);

        JButton back = new JButton("Back");
        back.setBounds(240, 320, 120, 35);
        back.setBackground(new Color(255, 200, 200));
        back.addActionListener(e -> cardLayout.show(mainPanel, "admin"));
        resultPanel.add(back);
    }

    void updateResults() {
        resultArea.setText("");

        int totalVotes = 
                candidates.get(0).votes +
                candidates.get(1).votes +
                candidates.get(2).votes;

        for (Candidate c : candidates) {
            double percent = (totalVotes == 0) ? 0 : (c.votes * 100.0 / totalVotes);
            resultArea.append(c.name + " : " + c.votes + " votes (" + String.format("%.2f", percent) + "%)\n");
        }
    }

    public static void main(String[] args) {
        new VotingGUI();
    }
}
