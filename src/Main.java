import java.sql.*;
import java.util.Scanner;

/**
 * <추가사항>
 * 1. admin: 각 관리자는 로그인용 비번을 가진다. 해당 비밀번호로 로그인을 해야 관리자 업무를 볼 수 있음
 * </추가사항>
 */

/**
 * delete this! ->추후 수정, 삭제해야할 사항
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

            String DB_URL = "jdbc:mysql://localhost:3306/bankApp?serverTimezone=Asia/Seoul&useSSL=false";
/*
            //TODO: id, pw 로그인해서 db에 접속
            System.out.print("Enter ID: ");
            String DB_ID = keyboard.next();

            System.out.print("Enter PW: ");
            String DB_PW = keyboard.next();
*/
            String DB_ID = "root"; //delete this!
            String DB_PW = "tksxhflsl12#"; //delete this!
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
                } else if (selectInput == 0) {

                } else {
                    System.out.println("잘못된 입력입니다.");
                }

            }


            //DB 종료
            if(resultSet != null) resultSet.close();
            if(preparedStatement != null) preparedStatement.close();
            if(statement != null) statement.close();
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

    public static void adminMainMenu() throws SQLException {
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
                    adminManageUser();
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

    public static void adminManageUser() throws SQLException {
        int inputOption = -1;
        System.out.println("\n < User 정보 관리 >");
        System.out.println("  0. Return to previous menu");
        System.out.println("  1. User 정보 조회");
        System.out.println("  2. User 추가");
        System.out.println("  3. User 삭제");
        System.out.println("  4. User 정보 수정");
        System.out.print(" Input: ");
        inputOption = keyboard.nextInt();
        switch (inputOption) {
            case 0:
                return;
            case 1:
                System.out.println("\n < User 정보 조회 >");
                System.out.println("  1. User ID로 검색하기");
                System.out.println("  2. 전체 User 조회하기");
                System.out.print(" Input: ");
                int inputOption1 = keyboard.nextInt();

                if(inputOption1 == 1){
                    //단일 user 검색
                    System.out.print(" 검색할 User ID 입력: ");
                    int inputUserID = keyboard.nextInt();
                    findUserByID(inputUserID);
                    if(resultSet.next()){
                        System.out.println("UserID   Name                 phoneNum      Address                        BirthDate");
                        System.out.print(String.format("%08d", resultSet.getInt(1)));
                        String name = resultSet.getString(2) + " " + resultSet.getString(3);
                        String address = resultSet.getString(5) + " " + resultSet.getString(6);
                        System.out.printf(" %-20s %-13s %-30s ", name, resultSet.getString(4), address);
                        System.out.println(resultSet.getDate(7));
                    } else {
                        System.out.println("등록되지 않은 User 입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    }
                } else if(inputOption1 == 2) {
                    //모든 user 검색
                    resultSet = statement.executeQuery("SELECT UserID, Fname, Lname, phoneNum, Ad_state, Ad_details, BirthDate FROM User");
                    System.out.println("\n--------------전체 user--------------");
                    System.out.println("UserID   Name                 phoneNum      Address                        BirthDate");
                    while(resultSet.next()){
                        System.out.print(String.format("%08d", resultSet.getInt(1)));
                        String name = resultSet.getString(2) + " " + resultSet.getString(3);
                        String address = resultSet.getString(5) + " " + resultSet.getString(6);
                        System.out.printf(" %-20s %-13s %-30s ", name, resultSet.getString(4), address);
                        System.out.println(resultSet.getDate(7));
                    }
                } else {
                    System.out.println("유효하지 않은 입력입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                }
                break;
            case 2: //User 추가
                System.out.println("\n < User 추가 등록 >");
                boolean flag = false;
                int inputUserID = 0;
                while(!flag) {
                    System.out.print("  1. User ID(8자리 수) 입력: ");
                    inputUserID = keyboard.nextInt();

                    findUserByID(inputUserID);
                    if (resultSet.next()) {
                        System.out.println("이미 존재하는 ID입니다. 다른 ID를 입력해주십시오.");
                    } else {
                        break;
                    }
                }
                System.out.print("  2. 이름(first name) 입력: ");
                String inputFname = keyboard.next();

                System.out.print("  3. 성(last name) 입력: ");
                String inputLname = keyboard.next();

                System.out.print("  4. 전화번호(000-0000-0000) 입력: ");
                String inputphoneNum = keyboard.next();

                System.out.print("  5. 주소(특별시/광역시/도) 입력: ");
                String inputAd_state = keyboard.next();

                System.out.print("  6. 주소(나머지 주소) 입력: ");
                String inputAd_details = keyboard.next();

                System.out.print("  7. 생년월일(yyyy-MM-dd) 입력: ");
                String inputBirthDate = keyboard.next();

                try {
                    preparedStatement = connection.prepareStatement("INSERT INTO User(UserID, Fname, Lname, phoneNum, Ad_state, Ad_details, BirthDate) values (?,?,?,?,?,?,?)");
                    preparedStatement.setInt(1, inputUserID);
                    preparedStatement.setString(2, inputFname);
                    preparedStatement.setString(3, inputLname);
                    preparedStatement.setString(4, inputphoneNum);
                    preparedStatement.setString(5, inputAd_state);
                    preparedStatement.setString(6, inputAd_details);

                    Date sqlDate = Date.valueOf(inputBirthDate);
                    preparedStatement.setDate(7, sqlDate);

                    preparedStatement.executeUpdate();

                } catch (SQLException e) {
                    System.out.println(" User추가 실패: Invalid input, 이전 메뉴 선택 창으로 돌아갑니다.");
                    break;
                }

                findUserByID(inputUserID);
                if(resultSet.next()){
                    System.out.println(" 다음의 User을 추가하였습니다: ");
                        System.out.println("UserID   Name                 phoneNum      Address                        BirthDate");
                        System.out.print(String.format("%08d", resultSet.getInt(1)));
                        String name = resultSet.getString(2) + " " + resultSet.getString(3);
                        String address = resultSet.getString(5) + " " + resultSet.getString(6);
                        System.out.printf(" %-20s %-13s %-30s ", name, resultSet.getString(4), address);
                        System.out.println(resultSet.getDate(7));

                } else {
                    System.out.println(" User추가 실패: 예기치 못한 에러, 이전 메뉴 선택 창으로 돌아갑니다.");
                }

                break;
            case 3: //User 삭제
                System.out.println("\n < User 삭제 >");
                System.out.print("  삭제할 User ID(8자리 수) 입력: ");
                inputUserID = keyboard.nextInt();
                findUserByID(inputUserID);
                if (resultSet.next()) {
                    try {
                        statement.executeUpdate("DELETE FROM User WHERE UserID = " + inputUserID);
                    } catch (SQLException e) {
                        System.out.println(" User삭제 실패: 이전 메뉴 선택 창으로 돌아갑니다.");
                        break;
                    }
                } else {
                    System.out.println(" 존재하지 않는 User ID 입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                }
                break;
            case 4: //User 수정
                System.out.println("\n < User 정보 수정 >");
                System.out.print("  수정할 User ID(8자리 수) 입력: ");
                inputUserID = keyboard.nextInt();
                findUserByID(inputUserID);

                if (resultSet.next()) {
                    try {
                        System.out.print("  1. 이름(first name) 입력: ");
                        inputFname = keyboard.next();

                        System.out.print("  2. 성(last name) 입력: ");
                        inputLname = keyboard.next();

                        System.out.print("  3. 전화번호(000-0000-0000) 입력: ");
                        inputphoneNum = keyboard.next();

                        System.out.print("  4. 주소(특별시/광역시/도) 입력: ");
                        inputAd_state = keyboard.next();

                        System.out.print("  5. 주소(나머지 주소) 입력: ");
                        inputAd_details = keyboard.next();

                        preparedStatement = connection.prepareStatement("UPDATE User SET Fname = ?, Lname = ?, phoneNum = ?, Ad_state = ?, Ad_details = ? WHERE UserID = ?");
                        preparedStatement.setString(1, inputFname);
                        preparedStatement.setString(2, inputLname);
                        preparedStatement.setString(3, inputphoneNum);
                        preparedStatement.setString(4, inputAd_state);
                        preparedStatement.setString(5, inputAd_details);
                        preparedStatement.setInt(6, inputUserID);

                        preparedStatement.executeUpdate();

                        System.out.println(" User 정보 수정을 완료하였습니다.");
                    } catch (SQLException e) {
                        System.out.println(" User 정보 수정 실패: 이전 메뉴 선택 창으로 돌아갑니다.");
                        break;
                    }
                } else {
                    System.out.println(" 존재하지 않는 User ID 입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                }
                break;
            default:
                System.out.println("유효하지 않은 입력입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                break;
        }

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

    public static void findUserByID(int inputUserID) throws SQLException {
        resultSet = statement.executeQuery("SELECT UserID, Fname, Lname, phoneNum, Ad_state, Ad_details, BirthDate FROM User WHERE UserID = " + inputUserID);
    }

    public static void findAccountByID(String inputAccountID) throws SQLException {
        preparedStatement = connection.prepareStatement("SELECT AccountID, Balance FROM Account WHERE AccountID = ?");
        preparedStatement.setString(1, inputAccountID);
        resultSet = preparedStatement.executeQuery();
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
