import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


class Transaction {
    private final Date date;
    private final String type;
    private final double amount;
    private final String description;

    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Transaction(String type, double amount, String description) {
        this.date = new Date();
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    @Override
    public String toString() {
      
        return String.format("[%s] %-10s | Amount: $%.2f | %s",
                dateFormat.format(date), type, amount, description);
    }
}


abstract class BankAccount {
    protected String name;
    protected String phoneNumber;
    protected int accountNumber;
    protected double balance;
  
    protected ArrayList<Transaction> transactionHistory = new ArrayList<>();

    public BankAccount(String name, String phoneNumber, int accountNumber, double balance) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.balance = balance;
       
        transactionHistory.add(new Transaction("OPEN_ACCOUNT", balance, "Initial deposit"));
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactionHistory.add(new Transaction("DEPOSIT", amount, "Customer deposit"));
        }
    }


    public abstract boolean withdraw(double amount);

    public ArrayList<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public String getName() {
        return name;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    // Abstract method to get account-specific details
    public abstract String getAccountDetails();
}


class SavingsAccount extends BankAccount {
    private double interestRate;

    public SavingsAccount(String name, String phoneNumber, int accountNumber, double balance, double interestRate) {
        super(name, phoneNumber, accountNumber, balance);
        this.interestRate = interestRate;
    }

    public void applyInterest() {
        double interestAmount = balance * interestRate / 100.0;
        balance += interestAmount;
        transactionHistory.add(new Transaction("INTEREST", interestAmount, "Interest applied at " + interestRate + "%"));
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactionHistory.add(new Transaction("WITHDRAWAL", amount, "Withdrawal from savings"));
            return true;
        }
        return false;
    }

    @Override
    public String getAccountDetails() {
        // Provides a formatted string of account details
        return String.format("Account Type: Savings\nName: %s\nPhone: %s\nAccount #: %d\nBalance: $%.2f\nInterest Rate: %.2f%%",
                name, phoneNumber, accountNumber, balance, interestRate);
    }
}


class CheckingAccount extends BankAccount {
    private double overdraftLimit;

    public CheckingAccount(String name, String phoneNumber, int accountNumber, double balance, double overdraftLimit) {
        super(name, phoneNumber, accountNumber, balance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public boolean withdraw(double amount) {
        // Allows withdrawing up to balance + overdraft limit
        if (amount > 0 && amount <= (balance + overdraftLimit)) {
            balance -= amount;
            transactionHistory.add(new Transaction("WITHDRAWAL", amount, "Withdrawal from checking"));
            if (balance < 0) {
                transactionHistory.add(new Transaction("OVERDRAFT", 0, "Overdraft used. Current balance: $" + balance));
            }
            return true;
        }
        return false;
    }

    @Override
    public String getAccountDetails() {
        return String.format("Account Type: Checking\nName: %s\nPhone: %s\nAccount #: %d\nBalance: $%.2f\nOverdraft Limit: $%.2f",
                name, phoneNumber, accountNumber, balance, overdraftLimit);
    }
}


class Bank {
    private HashMap<Integer, BankAccount> accounts = new HashMap<>();
    private int nextAccountNumber = 1003;

    public Bank() {
        // Initialize with some dummy data
        accounts.put(1001, new SavingsAccount("Narendra Modi", "145-492-7890", 1001, 1000.0, 2.5));
        accounts.put(1002, new CheckingAccount("Bhupendra Patel", "947-352-3210", 1002, 1500.0, 200.0));
    }

 
    public BankAccount getAccount(int accountNumber) {
        return accounts.get(accountNumber);
    }

  
    public BankAccount createAccount(String name, String phoneNumber, double initialBalance, String accountType) {
        // Check if account already exists
        for (BankAccount acc : accounts.values()) {
            if (acc.getName().equalsIgnoreCase(name) && acc.getPhoneNumber().equals(phoneNumber)) {
                return null; // Account already exists
            }
        }

        BankAccount newAccount;
        int newAccountNumber = nextAccountNumber++;

        if ("Savings".equals(accountType)) {
            // Default interest rate for new savings accounts
            newAccount = new SavingsAccount(name, phoneNumber, newAccountNumber, initialBalance, 2.5);
        } else if ("Checking".equals(accountType)) {
            // Default overdraft limit for new checking accounts
            newAccount = new CheckingAccount(name, phoneNumber, newAccountNumber, initialBalance, 100.0);
        } else {
            return null; // Invalid account type
        }

        accounts.put(newAccountNumber, newAccount);
        return newAccount;
    }
}


public class BankingSystem {
    
    // The Bank (model) which holds all the data and logic
    private Bank bank;

    // GUI Components
    private JFrame frame;
    private JTextField accountNumberField;
    private JTextArea displayTextArea;
    private JLabel resultLabel;

    public BankingSystem() {
        this.bank = new Bank(); // Create the bank instance
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Advanced Banking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Use BorderLayout for the main frame
        frame.setLayout(new BorderLayout(10, 10));

        // --- Top Panel (Input) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("Account Number:"));
        accountNumberField = new JTextField(10);
        topPanel.add(accountNumberField);
        frame.add(topPanel, BorderLayout.NORTH);

        // --- Center Panel (Buttons) ---
        // Use GridLayout for a neat grid of buttons
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // 0 rows = flexible, 2 columns, 10px gaps
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Add side padding

        JButton checkBalanceButton = new JButton("Check Balance");
        buttonPanel.add(checkBalanceButton);

        JButton depositButton = new JButton("Deposit");
        depositButton.setBackground(new Color(220, 255, 220)); // Light green
        buttonPanel.add(depositButton);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setBackground(new Color(255, 220, 220)); // Light red
        buttonPanel.add(withdrawButton);

        JButton createAccountButton = new JButton("Create New Account");
        buttonPanel.add(createAccountButton);
        
        JButton viewTransactionButton = new JButton("View Transactions");
        buttonPanel.add(viewTransactionButton);

        JButton accountDetailsButton = new JButton("View Account Details");
        buttonPanel.add(accountDetailsButton);

        frame.add(buttonPanel, BorderLayout.CENTER);

        // --- Bottom Panel (Output) ---
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding

        resultLabel = new JLabel("Please enter an account number and select an action.");
        resultLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        bottomPanel.add(resultLabel, BorderLayout.NORTH);

        displayTextArea = new JTextArea(10, 40); // 10 rows, 40 columns
        displayTextArea.setEditable(false);
        displayTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(displayTextArea); // Add a scrollbar
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // --- Add Action Listeners (Controller logic) ---
        
        checkBalanceButton.addActionListener(e -> handleCheckBalance());
        depositButton.addActionListener(e -> handleDeposit());
        withdrawButton.addActionListener(e -> handleWithdraw());
        createAccountButton.addActionListener(e -> handleCreateAccount());
        viewTransactionButton.addActionListener(e -> handleViewTransactions());
        accountDetailsButton.addActionListener(e -> handleAccountDetails());

        // --- Finalize Frame ---
        frame.pack(); // Pack the components to their preferred sizes
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }

    // --- Private Helper Methods for Validation ---


    private boolean isNameValid(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return name.matches("^[A-Za-z\\s'-]+$");
    }

    /**
     * Validates a phone number. (Simple validation)
     */
    private boolean isPhoneNumberValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) return false;
        return phoneNumber.matches("^[0-9-]+$");
    }

  
    private BankAccount getAccountFromField() {
        try {
            int accountNumber = Integer.parseInt(accountNumberField.getText());
            BankAccount account = bank.getAccount(accountNumber);
            if (account == null) {
                resultLabel.setText("Error: Account #" + accountNumber + " not found.");
                resultLabel.setForeground(Color.RED);
            }
            return account;
        } catch (NumberFormatException ex) {
            resultLabel.setText("Error: Invalid account number. Please enter digits only.");
            resultLabel.setForeground(Color.RED);
            return null;
        }
    }
    
    

    private void handleCheckBalance() {
        BankAccount account = getAccountFromField();
        if (account != null) {
            resultLabel.setText("Balance for " + account.getName() + ": $" + String.format("%.2f", account.getBalance()));
            resultLabel.setForeground(Color.BLACK);
            displayTextArea.setText(""); // Clear display
        }
    }

    private void handleDeposit() {
        BankAccount account = getAccountFromField();
        if (account == null) return;

        try {
            String amountStr = JOptionPane.showInputDialog(frame, "Enter deposit amount:", "Deposit", JOptionPane.PLAIN_MESSAGE);
            if (amountStr == null) return; // User cancelled
            
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                 JOptionPane.showMessageDialog(frame, "Amount must be positive.", "Input Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            account.deposit(amount);
            resultLabel.setText("Deposit successful. New balance: $" + String.format("%.2f", account.getBalance()));
            resultLabel.setForeground(new Color(0, 100, 0)); // Dark green
            displayTextArea.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid amount. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleWithdraw() {
         BankAccount account = getAccountFromField();
        if (account == null) return;

        try {
            String amountStr = JOptionPane.showInputDialog(frame, "Enter withdrawal amount:", "Withdraw", JOptionPane.PLAIN_MESSAGE);
            if (amountStr == null) return; // User cancelled

            double amount = Double.parseDouble(amountStr);
             if (amount <= 0) {
                 JOptionPane.showMessageDialog(frame, "Amount must be positive.", "Input Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            if (account.withdraw(amount)) {
                resultLabel.setText("Withdrawal successful. New balance: $" + String.format("%.2f", account.getBalance()));
                resultLabel.setForeground(new Color(0, 100, 0)); // Dark green
            } else {
                resultLabel.setText("Withdrawal failed. Insufficient funds.");
                resultLabel.setForeground(Color.RED);
            }
            displayTextArea.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid amount. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateAccount() {
        // Use a more complex dialog for creating an account
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField balanceField = new JTextField("0.0");
        JComboBox<String> accountTypeBox = new JComboBox<>(new String[]{"Savings", "Checking"});

        // Create a panel to hold all the input fields
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Initial Balance:"));
        panel.add(balanceField);
        panel.add(new JLabel("Account Type:"));
        panel.add(accountTypeBox);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Create New Account", 
                                                   JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String phoneNumber = phoneField.getText();
                double initialBalance = Double.parseDouble(balanceField.getText());
                String accountType = (String) accountTypeBox.getSelectedItem();

                // Validation
                if (!isNameValid(name)) {
                    JOptionPane.showMessageDialog(frame, "Invalid name. Please use letters, spaces, hyphens, or apostrophes.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!isPhoneNumberValid(phoneNumber)) {
                    JOptionPane.showMessageDialog(frame, "Invalid phone number. Please use digits and hyphens only.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (initialBalance < 0) {
                    JOptionPane.showMessageDialog(frame, "Initial balance cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                BankAccount newAccount = bank.createAccount(name, phoneNumber, initialBalance, accountType);

                if (newAccount != null) {
                    resultLabel.setText("Account created successfully!");
                    resultLabel.setForeground(new Color(0, 100, 0));
                    displayTextArea.setText("New Account Details:\n" + newAccount.getAccountDetails());
                    accountNumberField.setText(String.valueOf(newAccount.getAccountNumber()));
                } else {
                    resultLabel.setText("Account creation failed. An account may already exist for that name/phone.");
                    resultLabel.setForeground(Color.RED);
                }
            } catch (NumberFormatException ex) {
                 JOptionPane.showMessageDialog(frame, "Invalid balance. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleViewTransactions() {
        BankAccount account = getAccountFromField();
        if (account != null) {
            ArrayList<Transaction> history = account.getTransactionHistory();
            displayTextArea.setText("--- Transaction History for " + account.getName() + " (#" + account.getAccountNumber() + ") ---\n");
            if (history.isEmpty()) {
                displayTextArea.append("No transactions found.");
            } else {
                for (Transaction transaction : history) {
                    displayTextArea.append(transaction.toString() + "\n");
                }
            }
            resultLabel.setText("Transaction history loaded.");
            resultLabel.setForeground(Color.BLACK);
        }
    }

    private void handleAccountDetails() {
        BankAccount account = getAccountFromField();
        if (account != null) {
            displayTextArea.setText("--- Account Details ---\n" + account.getAccountDetails());
            resultLabel.setText("Account details loaded.");
            resultLabel.setForeground(Color.BLACK);
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BankingSystem();
            }
        });
    }
}
