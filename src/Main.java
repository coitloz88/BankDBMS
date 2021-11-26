import java.sql.*;
import java.util.Scanner;

/**
 * <추가사항>
 * 1. admin: 각 관리자는 로그인용 비번을 가진다. 해당 비밀번호로 로그인을 해야 관리자 업무를 볼 수 있음
 * </추가사항>
 */

public class Main {

    static Scanner keyboard = null;

    static Connection connection = null;
    static Statement statement = null;
    static PreparedStatement preparedStatement = null;
    static ResultSet resultSet = null;

    public static void main(String[] args) {

        keyboard = new Scanner(System.in);

        //jdbc driver 연결
        try {
            Class.forName("com.mysql.jdbc.Driver");

            String DB_URL = "jdbc:mysql://localhost/BankDBMS";

            System.out.print("Enter ID: ");
            String DB_ID = keyboard.next();

            System.out.print("Enter PW: ");
            String DB_PW = keyboard.next();

            connection = DriverManager.getConnection(DB_URL, DB_ID, DB_PW);
            if (connection == null) {
                System.err.println(". . . DB 접속 실패");
                return;
            }

        } catch (ClassNotFoundException e) {
            System.err.println("driver loading 실패");
            return;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return;
        }

        System.out.println(". . . DB 접속 성공\n");
        System.out.println("--------Bank Database System--------");

        try {
            statement = connection.createStatement();

            int selectInput = -1;

            //가장 외부 menu 선택 화면에서 종료를 입력할때까지 계속해서 실행됨
            while (selectInput != 0) {
                selectInput = menuSelectPosition();

                if (selectInput == 1) {
                    adminMainMenu();
                } else if (selectInput == 2) {
                    userMainMenu();
                } else {
                    System.out.println("잘못된 입력입니다.");
                }

            }


            //DB 종료
            resultSet.close();
            preparedStatement.close();
            statement.close();
            if (!connection.isClosed()) connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        keyboard.close();

    }

    public static int menuSelectPosition() {
        System.out.println("\n--------Select your position--------");
        System.out.println(" 0. Exit");
        System.out.println(" 1. Administrator mode");
        System.out.println(" 2. User mode");
        System.out.println("------------------------------------");
        System.out.print(" Input: ");

        int inputOption = keyboard.nextInt();            
        System.out.println("------------------------------------");

        return inputOption;
    }


    /*
    adminMainMenu, userMainMenu에서는 해당 함수 내에서 input에 따라 다른 함수 호출등의 action을 취함
     */

    public static void adminMainMenu() {
        int inputOption = -1;
        while (inputOption != 0) {
            System.out.println("\n------------ADMIN MODE-------------");
            System.out.println("-----------Select action-----------");
            System.out.println(" 0. Return to previous menu");
            System.out.println(" 1. User 정보 관리");
            System.out.println(" 2. Administrator 정보 관리");
            System.out.println(" 3. 계좌 정보 조회");
            System.out.println(" 4. 입출금 내역 조회");
            System.out.println(" 6. 은행 관리");
            System.out.println("------------------------------------");
            System.out.print(" Input: ");

            inputOption = keyboard.nextInt();
            System.out.println("------------------------------------");

            switch (inputOption) {
                case 0:
                    return;
                case 1:
                    System.out.println("  1. User 정보 조회");
                    System.out.println("  2. User 추가");
                    System.out.println("  3. User 삭제");
                    System.out.println("  4. User 정보 수정");
                    System.out.print(" Input: ");
                    inputOption = keyboard.nextInt();
                    break;
                case 2:
                    System.out.println("  1. Administrator 정보 조회");
                    System.out.println("  2. Administrator 추가");
                    System.out.println("  3. Administrator 삭제");
                    System.out.println("  4. Administrator 정보 수정");
                    System.out.print(" Input: ");
                    inputOption = keyboard.nextInt();
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    System.out.println("  1. 은행 소유 금액 조회"); //account의 모든 balance의 합
                    System.out.println("  2. 지점 조회");
                    System.out.println("  3. 지점 추가");
                    System.out.println("  4. 지점 삭제");
                    System.out.println("  5. 지점 정보 수정");
                    System.out.print(" Input: ");
                    inputOption = keyboard.nextInt();
                    break;
                default:
                    System.out.println("잘못된 입력입니다.");
                    break;
            }
        }
    }

    public static void adminRegisterNewPerson(int mode) {
        //mode = 0: Admin, mode = 1: User
    }


    public static void userMainMenu() {
        int inputOption = -1;
        while (inputOption != 0) {
            System.out.println("\n-------------USER MODE-------------");
            System.out.println("------Banking System(for user)------");
            System.out.println(" 0. Return to previous menu");
            System.out.println(" 1. 계좌에 입금하기");
            System.out.println(" 2. 계좌로부터 출금하기");
            System.out.println(" 3. 내 계좌 조회");
            System.out.println(" 4. 내 계좌 생성");
            System.out.println(" 5. 내 계좌 삭제");
            System.out.println("------------------------------------");
            System.out.print(" Input: ");
            inputOption = keyboard.nextInt();
            System.out.println("------------------------------------");

            switch (inputOption) {
                case 0:
                    return;
                case 1:
                    userDeposit();
                    break;
                case 2:
                    userWithdraw();
                    break;
                case 3:
                    System.out.println("  1. 내 계좌 정보 조회");
                    System.out.println("  2. 내 계좌 거래 내역 조회");
                    System.out.print(" Input: ");
                    inputOption = keyboard.nextInt();
                    break;
                case 4:
                    userNewAccount();
                    break;
                case 5:
                    userDeleteAccount();
                    break;
                default:
                    System.out.println("잘못된 입력입니다.");
                    break;
            }
        }
    }

    public static int findUserByID() throws SQLException {
        System.out.print("User ID 입력(8자리 수): ");
        int inputUserID = keyboard.nextInt();
        resultSet = statement.executeQuery("SELECT UserID, Fname, Lname FROM User WHERE UserID = " + inputUserID);

        if(!resultSet.next()) return -1;
        else return inputUserID;
    }

    public static int findAccountByID() throws SQLException {
        System.out.print("User ID 입력(8자리 수): ");
        int inputUserID = keyboard.nextInt();


        return inputUserID;
    }

    public static void userDeposit() {
        int inputAccountID, inputBalance;
        System.out.print("입금할 계좌 번호 입력: ");
        inputAccountID = keyboard.nextInt();
        System.out.print("입금할 금액 입력: ");
        inputBalance = keyboard.nextInt();
    }

    public static void userWithdraw() {
        int inputAccountID, inputBalance, inputPassword; 
        System.out.print("출금할 계좌 번호 입력: ");
        inputAccountID = keyboard.nextInt();

        System.out.println("계좌 비밀번호 입력(4자리 숫자): ");
        inputPassword = keyboard.nextInt();

        System.out.print("출금할 금액 입력: ");
        inputBalance = keyboard.nextInt();

    }

    public static int userNewAccount(){
        int accountID = 0;

        return accountID;
    }

    public static void userDeleteAccount(){

    }

}
