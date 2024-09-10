import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

class BankAccount {
    protected String name;
    protected String phoneNumber;
    protected int accountNumber;
    protected double balance;
    protected ArrayList<String> transactionHistory = new ArrayList<>();

    public BankAccount(String name, String phoneNumber, int accountNumber, double balance) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactionHistory.add("Deposited $" + amount);
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactionHistory.add("Withdrawn $" + amount);
            return true;
        }
        return false;
    }

    public ArrayList<String> getTransactionHistory() {
        return transactionHistory;
    }
}

class SavingsAccount extends BankAccount {
    private double interestRate;

    public SavingsAccount(String name, String phoneNumber, int accountNumber, double balance, double interestRate) {
        super(name, phoneNumber, accountNumber, balance);
        this.interestRate = interestRate;
    }

    public void applyInterest() {
        balance += balance * interestRate / 100.0;
        transactionHistory.add("Interest applied: $" + (balance * interestRate / 100.0));
    }
}

public class BankingSystem {
    private HashMap<Integer, BankAccount> accounts = new HashMap<>();
    private int nextAccountNumber = 1003; // Starting from 1003
    private JTextField accountNumberField;
    private JTextArea accountDetailsTextArea;

    public BankingSystem() {
        accounts.put(1001, new SavingsAccount("Modi", "145-492-7890", 1001, 1000.0, 2.5));
        accounts.put(1002, new SavingsAccount("Bhupendra", "947-352-3210", 1002, 1500.0, 3.0));

        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Banking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(null);

        JLabel accountNumberLabel = new JLabel("Account Number:");
        accountNumberLabel.setBounds(10, 10, 150, 20);
        frame.add(accountNumberLabel);

        accountNumberField = new JTextField();
        accountNumberField.setBounds(160, 10, 150, 20);
        frame.add(accountNumberField);

        JButton checkBalanceButton = new JButton("Check Balance");
        checkBalanceButton.setBounds(10, 40, 150, 30);
        frame.add(checkBalanceButton);

        JButton depositButton = new JButton("Deposit");
        depositButton.setBounds(180, 40, 150, 30);
        depositButton.setBackground(new Color(0, 128, 0)); // Set button color to green
        frame.add(depositButton);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setBounds(10, 120, 150, 30);
        withdrawButton.setBackground(Color.RED);
        frame.add(withdrawButton);

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBounds(180, 120, 150, 30);
        frame.add(createAccountButton);

        JButton viewTransactionButton = new JButton("View Transaction History");
        viewTransactionButton.setBounds(10, 160, 320, 30);
        frame.add(viewTransactionButton);

        JButton accountDetailsButton = new JButton("Account Details");
        accountDetailsButton.setBounds(180, 160, 150, 30);
        frame.add(accountDetailsButton);

        JTextArea transactionHistoryTextArea = new JTextArea();
        transactionHistoryTextArea.setBounds(10, 200, 370, 100);
        frame.add(transactionHistoryTextArea);

        JLabel resultLabel = new JLabel();
        resultLabel.setBounds(10, 80, 350, 20);
        frame.add(resultLabel);

        accountDetailsTextArea = new JTextArea();
        accountDetailsTextArea.setBounds(10, 240, 370, 100);
        frame.add(accountDetailsTextArea);

        checkBalanceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int accountNumber = Integer.parseInt(accountNumberField.getText());
                    if (accounts.containsKey(accountNumber)) {
                        BankAccount account = accounts.get(accountNumber);
                        resultLabel.setText("Balance for " + account.name + ": $" + account.getBalance());
                    } else {
                        resultLabel.setText("Account not found.");
                    }
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Invalid account number.");
                }
            }
        });

        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int accountNumber = Integer.parseInt(accountNumberField.getText());
                    double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter deposit amount:"));
                    if (accounts.containsKey(accountNumber)) {
                        BankAccount account = accounts.get(accountNumber);
                        account.deposit(amount);
                        JOptionPane.showMessageDialog(null, "Deposited $" + amount + " to " + account.name);
                    } else {
                        JOptionPane.showMessageDialog(null, "Account not found.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers.");
                }
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int accountNumber = Integer.parseInt(accountNumberField.getText());
                    double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter withdrawal amount:"));
                    if (accounts.containsKey(accountNumber)) {
                        BankAccount account = accounts.get(accountNumber);
                        if (account.withdraw(amount)) {
                            JOptionPane.showMessageDialog(null, "Withdrawn $" + amount + " from " + account.name);
                        } else {
                            JOptionPane.showMessageDialog(null, "Insufficient balance.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Account not found.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers.");
                }
            }
        });

        createAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = JOptionPane.showInputDialog("Enter name:");
                    String phoneNumber = JOptionPane.showInputDialog("Enter phone number:");
                    double initialBalance = Double.parseDouble(JOptionPane.showInputDialog("Enter initial balance:"));

                    if (!isNameValid(name) || !isPhoneNumberValid(phoneNumber)) {
                        JOptionPane.showMessageDialog(null, "Invalid name or phone number.");
                        return;
                    }

                    int accountNumber = createAccount(name, phoneNumber, initialBalance);

                    if (accountNumber != -1) {
                        JOptionPane.showMessageDialog(null, "Account created with account number: " + accountNumber);
                    } else {
                        JOptionPane.showMessageDialog(null, "This account already exists.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers.");
                }
            }
        });

        viewTransactionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int accountNumber = Integer.parseInt(accountNumberField.getText());
                    if (accounts.containsKey(accountNumber)) {
                        BankAccount account = accounts.get(accountNumber);
                        ArrayList<String> transactionHistory = account.getTransactionHistory();
                        transactionHistoryTextArea.setText("Transaction History for " + account.name + " (Account #" + accountNumber + "):\n");
                        for (String transaction : transactionHistory) {
                            transactionHistoryTextArea.append(transaction + "\n");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Account not found.");
                    }
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Invalid account number.");
                }
            }
        });

        accountDetailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int accountNumber = Integer.parseInt(accountNumberField.getText());
                    if (accounts.containsKey(accountNumber)) {
                        BankAccount account = accounts.get(accountNumber);
                        String accountDetails = "Account Details\n" +
                                "Name: " + account.name + "\n" +
                                "Phone Number: " + account.phoneNumber + "\n" +
                                "Account Number: " + account.accountNumber + "\n" +
                                "Balance: $" + account.getBalance();

                        accountDetailsTextArea.setText(accountDetails);
                        
                        JOptionPane.showMessageDialog(null, accountDetails, "Account Details", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid account number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

    private boolean isNameValid(String name) {
        return name.matches("^[A-Za-z]+$");
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.matches("^[0-9-]+$");
    }

    private int createAccount(String name, String phoneNumber, double initialBalance) {
        for (int accountNumber : accounts.keySet()) {
            BankAccount account = accounts.get(accountNumber);
            if (account.name.equals(name) && account.phoneNumber.equals(phoneNumber)) {
                return -1;
            }
        }

        SavingsAccount newAccount = new SavingsAccount(name, phoneNumber, nextAccountNumber, initialBalance, 0.0);
        accounts.put(nextAccountNumber, newAccount);
        nextAccountNumber++;
        return nextAccountNumber - 1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BankingSystem();
            }
        });
    }
}
