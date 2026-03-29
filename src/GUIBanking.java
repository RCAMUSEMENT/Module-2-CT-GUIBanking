/** Ryley's Wealth Manager
 * Student Name: Ryley Carlson
 * CSC372 Module 2 Critical Thinking Assignment
 * Date: 2026-03-29
 * Program: GUIBanking.java
 * Description: This GUI-based Java application simulates a simple banking interface,
 * where it allows users to manage their account balance through deposits and withdrawals.
 * The program features a visually appealing dashboard that displays the current balance, an input field for transaction amounts,
 * and buttons for executing transactions. A transaction log keeps track of all activities,
 * and the application ensures user-friendly interactions with error handling and confirmation dialogs. Upon exiting,
 * users receive a final receipt showing their remaining balance.
 */

import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;

/**
 * CORE LOGIC (BankAccount)
 */
class BankAccount {
    private double balance;
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

    public BankAccount(double initialBalance) {
        this.balance = Math.max(0, initialBalance);
    }

    public void deposit(double amount) { balance += amount; }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public String getDisplayBalance() { return currency.format(balance); }
}

/**
 * GUI APPLICATION (GUIBanking)
 */
public class GUIBanking  extends JFrame {
    private final BankAccount account;
    private final JLabel balanceLabel;
    private final JTextField amountInput;
    private final DefaultListModel<String> activityLog;

    // UI Colors
    private final Color PRIMARY_DARK = new Color(33, 37, 41);
    private final Color ACCENT_BLUE = new Color(0, 123, 255);
    private final Color SUCCESS_GREEN = new Color(40, 167, 69);
    private final Color DANGER_RED = new Color(220, 53, 69);

    public GUIBanking() {
        // Prompt the user for an initial balance before setting up the account and UI
        double initial = promptForBalance();
        this.account = new BankAccount(initial);

        // WINDOW SETUP
        setTitle("Ryley's Wealth Manager");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(PRIMARY_DARK);
        setLocationRelativeTo(null);

        // Layout and Components
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(PRIMARY_DARK);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // --- DASHBOARD SECTION ---
        balanceLabel = new JLabel(account.getDisplayBalance());
        balanceLabel.setFont(new Font("Times New Roman", Font.BOLD, 42));
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(balanceLabel, gbc);

        JLabel subText = new JLabel("Your Current Available Balance");
        subText.setForeground(Color.LIGHT_GRAY);
        subText.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        mainPanel.add(subText, gbc);

        // --- INPUT SECTION ---
        amountInput = new JTextField();
        amountInput.setBackground(new Color(52, 58, 64));
        amountInput.setForeground(Color.WHITE);
        amountInput.setCaretColor(Color.WHITE);
        amountInput.setFont(new Font("Monospaced", Font.BOLD, 24));
        amountInput.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_BLUE), "Transaction Amount ($)",
            TitledBorder.LEFT, TitledBorder.TOP, null, Color.WHITE));
        gbc.gridy = 2;
        mainPanel.add(amountInput, gbc);

        // --- ACTION BUTTONS ---
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonRow.setOpaque(false);
        
        JButton btnDeposit = createStyledButton("DEPOSIT", SUCCESS_GREEN);
        JButton btnWithdraw = createStyledButton("WITHDRAW", DANGER_RED);
        
        buttonRow.add(btnDeposit);
        buttonRow.add(btnWithdraw);
        gbc.gridy = 3;
        mainPanel.add(buttonRow, gbc);

        // --- TRANSACTION LOG ---
        activityLog = new DefaultListModel<>();
        activityLog.addElement("Secure Session Established... ");
        JList<String> logDisplay = new JList<>(activityLog);
        logDisplay.setBackground(new Color(40, 44, 52));
        logDisplay.setForeground(new Color(171, 178, 191));
        JScrollPane scrollPane = new JScrollPane(logDisplay);
        scrollPane.setPreferredSize(new Dimension(100, 150));
        gbc.gridy = 4;
        mainPanel.add(scrollPane, gbc);

        // --- EXIT BUTTON ---
        JButton btnExit = createStyledButton("CLOSE SESSION", Color.GRAY);
        gbc.gridy = 5;
        mainPanel.add(btnExit, gbc);

        // --- ACTION LISTENERS ---
        btnDeposit.addActionListener(e -> executeTransaction(true));
        btnWithdraw.addActionListener(e -> executeTransaction(false));
        btnExit.addActionListener(e -> finishAndExit());

        add(mainPanel);
        setVisible(true);
    }

    private void executeTransaction(boolean isDeposit) {
        try {
            double amt = Double.parseDouble(amountInput.getText());
            if (amt <= 0) throw new NumberFormatException();

            if (isDeposit) {
                account.deposit(amt);
                activityLog.add(0, "[+] Deposit: " + amt);
                askToContinue(" Congratulations! Your deposit is confirmed.");
            } else {
                if (account.withdraw(amt)) {
                    activityLog.add(0, "[-] Withdrawal: " + amt);
                    askToContinue("Congratulations! Your withdrawal is confirmed.");
                } else {
                    JOptionPane.showMessageDialog(this, " Sorry, you have insufficient funds for this transaction. Please verify the balance in your account and try again.", "Declined", JOptionPane.ERROR_MESSAGE);
                }
            }
            updateUI();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number. e.g., 85.50", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void askToContinue(String message) {
        int choice = JOptionPane.showConfirmDialog(this,
            message + "\nWould you like to make another transaction today?",
            "Success", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.NO_OPTION) {
            finishAndExit();
        }
    }

    private void updateUI() {
        balanceLabel.setText(account.getDisplayBalance());
        amountInput.setText("");
    }

    private void finishAndExit() {
        // Show final balance before exiting
        JOptionPane.showMessageDialog(this,
            "--- YOUR RECEIPT ---\nFinal Balance: " + account.getDisplayBalance() + "\nThank you. For using Ryley's Wealth Manager. Have a blessed day!",
            "Session Ended", JOptionPane.PLAIN_MESSAGE);
        System.exit(0);
    }

    private double promptForBalance() {
        String input = JOptionPane.showInputDialog(null, "Enter Your Opening Balance:", "Welcome to Ryley's Wealth Manager", JOptionPane.INFORMATION_MESSAGE);
        try { return (input == null) ? 0 : Double.parseDouble(input); }
        catch (NumberFormatException e) { return 0; }
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Times New Roman", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUIBanking::new);
    }
}