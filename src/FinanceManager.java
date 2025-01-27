import java.io.*;
import java.util.*;

public class FinanceManager {

    // Data structures to store user info, transactions, and budgets
    static Map<String, String> users = new HashMap<>();
    static List<Transaction> transactions = new ArrayList<>();
    static Map<String, Double> budgets = new HashMap<>();
    static Map<String, Double> spending = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);

    // Transaction class to represent income/expenses
    static class Transaction {
        String category;
        double amount;
        boolean isIncome;  // true if income, false if expense
        String date;

        public Transaction(String category, double amount, boolean isIncome, String date) {
            this.category = category;
            this.amount = amount;
            this.isIncome = isIncome;
            this.date = date;
        }
    }

    public static void main(String[] args) {
        // Load users and transactions if files exist, but suppress errors if they don't
        loadUsers();  // Load users from file
        loadTransactions();  // Load transactions from file

        System.out.println("Welcome to the Personal Finance Manager!");
        while (true) {
            System.out.println("\n1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            int choice = getValidChoice(1, 3);

            if (choice == 1) {
                login();
            } else if (choice == 2) {
                register();
            } else if (choice == 3) {
                System.out.println("Goodbye!");
                break;
            }
        }
    }

    // Method to get valid user input for choice options
    private static int getValidChoice(int min, int max) {
        int choice;
        while (true) {
            choice = scanner.nextInt();
            if (choice >= min && choice <= max) {
                break;
            } else {
                System.out.println("Please select a valid option");
            }
        }
        return choice;
    }

    // Load users from file, but don't show error if file doesn't exist
    private static void loadUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userData = line.split(",");
                users.put(userData[0], userData[1]);
            }
        } catch (FileNotFoundException e) {
            // File not found is okay, just means no users yet
        } catch (IOException e) {
            System.out.println("Error loading user data.");
        }
    }

    // Save users to file
    private static void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt"))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                bw.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving user data.");
        }
    }

    // Load transactions from file, but don't show error if file doesn't exist
    private static void loadTransactions() {
        try (BufferedReader br = new BufferedReader(new FileReader("transactions.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                boolean isIncome = data[2].equals("Income");
                transactions.add(new Transaction(data[0], Double.parseDouble(data[1]), isIncome, data[3]));
            }
        } catch (FileNotFoundException e) {
            // File not found is okay, just means no transactions yet
        } catch (IOException e) {
            System.out.println("Error loading transaction data.");
        }
    }

    // Save transactions to file
    private static void saveTransactions() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("transactions.csv"))) {
            for (Transaction t : transactions) {
                String type = t.isIncome ? "Income" : "Expense";
                bw.write(t.category + "," + t.amount + "," + type + "," + t.date + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions.");
        }
    }

    // Login process
    private static void login() {
        System.out.println("Enter username: ");
        String username = scanner.next();
        System.out.println("Enter password: ");
        String password = scanner.next();

        if (users.containsKey(username) && users.get(username).equals(password)) {
            System.out.println("Login successful!");
            userMenu(username);
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    // Register process
    private static void register() {
        System.out.println("Enter username: ");
        String username = scanner.next();
        System.out.println("Enter password: ");
        String password = scanner.next();

        if (!users.containsKey(username)) {
            users.put(username, password);
            saveUsers();
            System.out.println("Registration successful!");
        } else {
            System.out.println("Username already exists.");
        }
    }

    // User menu
    private static void userMenu(String username) {
        while (true) {
            System.out.println("\nWelcome, " + username + "!");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. Set Monthly Budget");
            System.out.println("4. Show Budget Status");
            System.out.println("5. Show Financial Summary");
            System.out.println("6. Generate Report (CSV)");
            System.out.println("7. Logout");
            int choice = getValidChoice(1, 7);

            switch (choice) {
                case 1:
                    addIncome(username);
                    break;
                case 2:
                    addExpense(username);
                    break;
                case 3:
                    setBudget(username);
                    break;
                case 4:
                    showBudgetStatus();
                    break;
                case 5:
                    showFinancialSummary();
                    break;
                case 6:
                    generateReport();
                    break;
                case 7:
                    System.out.println("Logging out...");
                    return;
                default:
                    // This is unnecessary because we validate inputs earlier, but left here for safety
                    System.out.println("Please select a valid option");
            }
        }
    }

    // Add income to the system
    private static void addIncome(String username) {
        System.out.println("Enter income category: ");
        String category = scanner.next();
        System.out.println("Enter income amount: ");
        double amount = scanner.nextDouble();
        System.out.println("Enter date (YYYY-MM-DD): ");
        String date = scanner.next();
        transactions.add(new Transaction(category, amount, true, date));
        saveTransactions();
        System.out.println("Income added successfully!");
    }

    // Add expense to the system
    private static void addExpense(String username) {
        System.out.println("Enter expense category: ");
        String category = scanner.next();
        System.out.println("Enter expense amount: ");
        double amount = scanner.nextDouble();
        System.out.println("Enter date (YYYY-MM-DD): ");
        String date = scanner.next();
        transactions.add(new Transaction(category, amount, false, date));
        addExpenseToBudget(category, amount);
        saveTransactions();
        System.out.println("Expense added successfully!");
    }

    // Set a monthly budget for a category
    private static void setBudget(String username) {
        System.out.println("Enter budget category: ");
        String category = scanner.next();
        System.out.println("Enter budget amount: ");
        double amount = scanner.nextDouble();
        budgets.put(category, amount);
        System.out.println("Budget set successfully!");
    }

    // Add expense to budget
    private static void addExpenseToBudget(String category, double amount) {
        spending.put(category, spending.getOrDefault(category, 0.0) + amount);
    }

    // Show the status of the budget for each category
    private static void showBudgetStatus() {
        for (String category : budgets.keySet()) {
            double budget = budgets.get(category);
            double spent = spending.getOrDefault(category, 0.0);
            System.out.println("Category: " + category);
            System.out.println("Budget: " + budget);
            System.out.println("Spent: " + spent);
            System.out.println("Remaining: " + (budget - spent));
            System.out.println();
        }
    }

    // Show a summary of the user's finances
    private static void showFinancialSummary() {
        double totalIncome = 0, totalExpense = 0;
        for (Transaction t : transactions) {
            if (t.isIncome) {
                totalIncome += t.amount;
            } else {
                totalExpense += t.amount;
            }
        }
        System.out.println("Total Income: " + totalIncome);
        System.out.println("Total Expense: " + totalExpense);
        System.out.println("Net Savings: " + (totalIncome - totalExpense));
    }

    // Generate a financial report in CSV format
    private static void generateReport() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("financial_report.csv"))) {
            bw.write("Category,Amount,Type,Date\n");
            for (Transaction t : transactions) {
                String type = t.isIncome ? "Income" : "Expense";
                bw.write(t.category + "," + t.amount + "," + type + "," + t.date + "\n");
            }
            System.out.println("Report generated.");
        } catch (IOException e) {
            System.out.println("Error generating report.");
       }
    }
}