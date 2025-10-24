import java.sql.*;
import java.util.Scanner;

public class projects {
    static final String DB_URL = "jdbc:mysql://localhost:3306/banking_db";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "Haribaskar@111";
    
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            createTableIfNotExists(conn);
            Scanner scanner = new Scanner(System.in);
            boolean running = true;
            
            while (running) {
                System.out.println("\n=== Banking Application ===");
                System.out.println("1. Create Account");
                System.out.println("2. Deposit Money");
                System.out.println("3. Withdraw Money");
                System.out.println("4. Check Balance");
                System.out.println("5. Exit");
                System.out.println("6. Get Account info of all");
                System.out.println("7. Get info by Account Id");
                System.out.println("8. Get info by Name");
                System.out.print("Choose an option: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1:
                        createAccount(conn, scanner);
                        break;
                    case 2:
                        deposit(conn, scanner);
                        break;
                    case 3:
                        withdraw(conn, scanner);
                        break;
                    case 4:
                        checkBalance(conn, scanner);
                        break;
                    case 5:
                        running = false;
                        System.out.println("Thank you for using our bank!");
                        break;
                    case 6:
                        getAllAccountHolders(conn);
                        break;
                    case 7:
                        getAccountInfo(conn, scanner);
                        break;
                    case 8:
                        searchByName(conn, scanner);
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
            }
            
            scanner.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static void createTableIfNotExists(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS accounts (" +
                     "account_id INT AUTO_INCREMENT PRIMARY KEY," +
                     "account_holder VARCHAR(100)," +
                     "balance DOUBLE," +
                     "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    static void createAccount(Connection conn, Scanner scanner) {
        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine();
        System.out.print("Enter initial balance: ");
        double balance = scanner.nextDouble();
        
        String sql = "INSERT INTO accounts (account_holder, balance) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, balance);
            pstmt.executeUpdate();
            System.out.println("✓ Account created successfully!");
            ResultSet rs=pstmt.getGeneratedKeys();
            if(rs.next()){
                int accid=rs.getInt(1);

                System.out.println("Your account Id is : "+accid);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    static void deposit(Connection conn, Scanner scanner) {
        System.out.print("Enter account ID: ");
        int accountId = scanner.nextInt();
        System.out.print("Enter deposit amount: ");
        double amount = scanner.nextDouble();
        
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("✓ Deposit successful! New balance: " + getBalance(conn, accountId));
            } else {
                System.out.println("✗ Account not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    static void withdraw(Connection conn, Scanner scanner) {
        System.out.print("Enter account ID: ");
        int accountId = scanner.nextInt();
        System.out.print("Enter withdrawal amount: ");
        double amount = scanner.nextDouble();
        
        double currentBalance = getBalance(conn, accountId);
        
        if (currentBalance == -1) {
            System.out.println("✗ Account not found!");
            return;
        }
        
        if (amount > currentBalance) {
            System.out.println("✗ Insufficient balance!");
            return;
        }
        
        String sql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            pstmt.executeUpdate();
            System.out.println("✓ Withdrawal successful! New balance: " + getBalance(conn, accountId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    static void checkBalance(Connection conn, Scanner scanner) {
        System.out.print("Enter account ID: ");
        int accountId = scanner.nextInt();
        
        double balance = getBalance(conn, accountId);
        
        if (balance == -1) {
            System.out.println("✗ Account not found!");
        } else {
            System.out.println("Current Balance: $" + balance);
        }
    }
    
    static double getBalance(Connection conn, int accountId) {
        String sql = "SELECT balance FROM accounts WHERE account_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    // Method to get account info by Account ID
static void getAccountInfo(Connection conn, Scanner scanner) {
    System.out.print("Enter Account ID: ");
    int accountId = scanner.nextInt();
    
    String sql = "SELECT account_id, account_holder, balance, created_date FROM accounts WHERE account_id = ?";
    
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, accountId);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            int id = rs.getInt("account_id");
            String name = rs.getString("account_holder");
            double balance = rs.getDouble("balance");
            String createdDate = rs.getString("created_date");
            
            System.out.println("\n========== Account Information ==========");
            System.out.println("Account ID: " + id);
            System.out.println("Account Holder: " + name);
            System.out.println("Balance: $" + balance);
            System.out.println("Created Date: " + createdDate);
            System.out.println("=========================================\n");
        } else {
            System.out.println("✗ Account not found!");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// Method to get all account holders
static void getAllAccountHolders(Connection conn) {
    String sql = "SELECT account_id, account_holder, balance, created_date FROM accounts";
    
    try (Statement stmt = conn.createStatement()) {
        ResultSet rs = stmt.executeQuery(sql);
        
        System.out.println("\n========== All Account Holders ==========");
        System.out.println("ID | Name | Balance | Created Date");
        System.out.println("----------------------------------------");
        
        boolean found = false;
        while (rs.next()) {
            found = true;
            int id = rs.getInt("account_id");
            String name = rs.getString("account_holder");
            double balance = rs.getDouble("balance");
            String createdDate = rs.getString("created_date");
            
            System.out.println(id + " | " + name + " | $" + balance + " | " + createdDate);
        }
        
        if (!found) {
            System.out.println("No accounts found!");
        }
        System.out.println("========================================\n");
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// Method to search by account holder name
static void searchByName(Connection conn, Scanner scanner) {
    System.out.print("Enter Account Holder Name: ");
    scanner.nextLine(); // Clear buffer
    String name = scanner.nextLine();
    
    String sql = "SELECT account_id, account_holder, balance, created_date FROM accounts WHERE account_holder LIKE ?";
    
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, "%" + name + "%");
        ResultSet rs = pstmt.executeQuery();
        
        System.out.println("\n========== Search Results ==========");
        boolean found = false;
        
        while (rs.next()) {
            found = true;
            int id = rs.getInt("account_id");
            String holderName = rs.getString("account_holder");
            double balance = rs.getDouble("balance");
            String createdDate = rs.getString("created_date");
            
            System.out.println("Account ID: " + id);
            System.out.println("Account Holder: " + holderName);
            System.out.println("Balance: $" + balance);
            System.out.println("Created Date: " + createdDate);
            System.out.println("-----------------------------------");
        }
        
        if (!found) {
            System.out.println("✗ No accounts found matching: " + name);
        }
        System.out.println("====================================\n");
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
