import java.sql.*;
import java.util.Scanner;

/**
 * //1. admin: 각 관리자는 로그인용 비번을 가진다. 해당 비밀번호로 로그인을 해야 관리자 업무를 볼 수 있음
 * admin, branch는 최소 하나이상 존재해야함. ex) admin이 하나만 남은 경우 삭제 불가 / branch가 하나만 남은 경우 삭제 불가
 * 입금 = 1, 출금 = -1
 * isMinus == 1이면 마이너스 뚫기 가능
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

    static int currentBranchID = 0;

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

            //TODO: 시작할때 현재 유효한 branch 목록을 출력하고, branch ID를 입력하도록함. 온라인 branch는 기본이 되는 branch로 지우지 못하게 해놔야함!!(추후 처리)

            System.out.println("Bank Application에 오신 것을 환영합니다..");
            System.out.println("Bank의 전체 Branch 목록: ");
            showBranches(-1);

            do {
                System.out.print("현재 BranchID(4자리수) 입력: ");
                currentBranchID = keyboard.nextInt();
                findBranchByID(currentBranchID);
            } while(!resultSet.next());

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
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (statement != null) statement.close();
            if (!connection.isClosed()) connection.close();
        } catch (SQLException throwables) {
            System.out.println("DB 오류 발생: 프로그램 강제 종료");
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

    //find 함수
    public static void findUserByID(int inputUserID) throws SQLException {
        resultSet = statement.executeQuery("SELECT UserID, Fname, Lname, phoneNum, Ad_state, Ad_details, BirthDate FROM user WHERE UserID = " + inputUserID);
    }

    public static void findAdminByID(int inputAdminID) throws SQLException {
        resultSet = statement.executeQuery("SELECT AdminID, Fname, Lname, phoneNum, Ad_state, Ad_details, BirthDate, AdBranchID FROM administrator WHERE AdminID = " + inputAdminID);
        //resultSet = statement.executeQuery("SELECT * FROM administarator WHERE AdminID = " + inputAdminID);
    }

    public static void findAccountByAccountID(String inputAccountID) throws SQLException {
        preparedStatement = connection.prepareStatement("SELECT AccountID, Balance, isMinus, AcAdminID, AcUserID, StartDate FROM account WHERE AccountID = ?");
        preparedStatement.setString(1, inputAccountID);
        resultSet = preparedStatement.executeQuery();
    }

    public static void findAccountByUserID(int inputUserID) throws SQLException {
        resultSet = statement.executeQuery("SELECT AccountID, Balance, isMinus, AcAdminID, AcUserID, StartDate FROM account WHERE AcUserID = " + inputUserID);
    }

    public static void findTransactionByAccountID(String inputAccountID) throws SQLException{
        preparedStatement = connection.prepareStatement("SELECT acTimeStamp, acType, amount, TBranchID, TAccountID FROM actransaction WHERE TAccountID = ? ORDER BY acTimeStamp");
        preparedStatement.setString(1, inputAccountID);
        resultSet = preparedStatement.executeQuery();
    }

    public static void findBranchByID(int inputBranchID) throws SQLException{
        resultSet = statement.executeQuery("SELECT BranchID, Lo_state, Lo_details, ManagerID FROM bankbranch WHERE BranchID = " + inputBranchID);
    }

    public static void showBranches(int inputBranchID){
        System.out.println("BranchID Location                 ManagerID");
        try {
            if (inputBranchID == -1) {
                resultSet = statement.executeQuery("SELECT BranchID, Lo_state, Lo_details, ManagerID FROM bankbranch");
                while(resultSet.next()){
                    String address = resultSet.getString(2) + " " + resultSet.getString(3);
                    System.out.print(String.format("%04d", resultSet.getInt(1)) + "     ");
                    System.out.printf("%-24s %d\n", address, resultSet.getInt(4));
                }
            } else {
                findBranchByID(inputBranchID);
                if(resultSet.next()){
                    String address = resultSet.getString(2) + " " + resultSet.getString(3);
                    System.out.print(String.format("%04d", resultSet.getInt(1)) + "     ");
                    System.out.printf("%-24s %d\n", address, resultSet.getInt(4));
                } else {
                    System.out.println(" Branch가 존재하지 않습니다.");
                }
            }
        } catch (SQLException throwables) {
            System.out.println("DB 에러 발생");
        }
    }

    public static void adminMainMenu() throws SQLException { //TODO: 4, 5
        int inputOption = -1;
        while (inputOption != 0) {
            System.out.println("\n------------ADMIN MODE-------------");
            System.out.println("-----------Select action-----------");
            System.out.println(" 0. Return to previous menu");
            System.out.println(" 1. User 정보 관리");
            System.out.println(" 2. Administrator 정보 관리");
            System.out.println(" 3. 계좌 정보 조회");
            System.out.println(" 4. 입출금 내역 조회");
            System.out.println(" 5. 은행 관리");
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
                    adminManageAdmin();
                    break;
                case 3:
                    adminShowAccounts();
                    break;
                case 4:
                    adminShowTransaction(); //TODO
                    break;
                case 5:
                    System.out.println("  1. 은행 소유 금액 조회"); //account의 모든 balance의 합
                    System.out.println("  2. 지점 조회"); //전체 지점만 조회가능
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

    //complete
    public static void adminManageUser(){
        try {
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

                    if (inputOption1 == 1) {
                        //단일 user 검색
                        System.out.print(" 검색할 User ID 입력: ");
                        int inputUserID = keyboard.nextInt();
                        findUserByID(inputUserID);
                        if (resultSet.next()) {
                            System.out.println("UserID   Name                 phoneNum      Address                        BirthDate");
                            System.out.print(String.format("%08d", resultSet.getInt(1)));
                            String name = resultSet.getString(2) + " " + resultSet.getString(3);
                            String address = resultSet.getString(5) + " " + resultSet.getString(6);
                            System.out.printf(" %-20s %-13s %-30s ", name, resultSet.getString(4), address);
                            System.out.println(resultSet.getDate(7));
                        } else {
                            System.out.println("등록되지 않은 User 입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                        }
                    } else if (inputOption1 == 2) {
                        //모든 user 검색
                        resultSet = statement.executeQuery("SELECT UserID, Fname, Lname, phoneNum, Ad_state, Ad_details, BirthDate FROM User");
                        System.out.println("\n--------------전체 user--------------");
                        System.out.println("UserID   Name                 phoneNum      Address                        BirthDate");
                        while (resultSet.next()) {
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
                    int inputUserID = 0;
                    while (true) {
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
                    if (resultSet.next()) {
                        System.out.println(" 다음의 User을 추가하였습니다: ");
                        System.out.println("UserID   Name                 phoneNum      Address                        BirthDate");
                        System.out.print(String.format("%08d", resultSet.getInt(1)));
                        String name = resultSet.getString(2) + " " + resultSet.getString(3);
                        String address = resultSet.getString(5) + " " + resultSet.getString(6);
                        System.out.printf(" %-20s %-13s %-30s ", name, resultSet.getString(4), address);
                        System.out.println(resultSet.getDate(7));

                    } else {
                        System.out.println(" User 추가 실패: 예기치 못한 에러, 이전 메뉴 선택 창으로 돌아갑니다.");
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
                            System.out.println(" User 삭제 성공: 이전 메뉴 선택 창으로 돌아갑니다.");

                        } catch (SQLException e) {
                            System.out.println(" User 삭제 실패: 이전 메뉴 선택 창으로 돌아갑니다.");
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
        } catch (SQLException e){
            System.out.println("DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
        }

    }

    //complete
    public static void adminManageAdmin() {
        try {
            int inputOption = -1;
            System.out.println("\n < Administrator 정보 관리 >");
            System.out.println("  0. Return to previous menu");
            System.out.println("  1. Administrator 정보 조회");
            System.out.println("  2. Administrator 추가");
            System.out.println("  3. Administrator 삭제");
            System.out.println("  4. Administrator 정보 수정");
            System.out.print(" Input: ");
            inputOption = keyboard.nextInt();
            switch (inputOption) {
                case 0:
                    return;
                case 1:
                    System.out.println("\n < Administrator 정보 조회 >");
                    System.out.println("  1. Administrator ID로 검색하기");
                    System.out.println("  2. 전체 Administrator 조회하기");
                    System.out.print(" Input: ");
                    int inputOption1 = keyboard.nextInt();

                    if (inputOption1 == 1) {
                        //단일 user 검색
                        System.out.print(" 검색할 Administrator ID 입력: ");
                        int inputAdminID = keyboard.nextInt();
                        findAdminByID(inputAdminID);
                        if (resultSet.next()) {
                            System.out.println("AdminID  Name                 phoneNum      Address                        BirthDate  BranchID");
                            System.out.print(String.format("%08d", resultSet.getInt(1)));
                            String name = resultSet.getString(2) + " " + resultSet.getString(3);
                            String address = resultSet.getString(5) + " " + resultSet.getString(6);
                            System.out.printf(" %-20s %-13s %-30s ", name, resultSet.getString(4), address);
                            System.out.print(resultSet.getDate(7));
                            System.out.println(" " + resultSet.getInt(8));
                        } else {
                            System.out.println("등록되지 않은 Administrator 입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                        }
                    } else if (inputOption1 == 2) {
                        //모든 admin 검색
                        resultSet = statement.executeQuery("SELECT * FROM administrator");
                        System.out.println("\n----------전체 administrator----------");
                        System.out.println("AdminID  Name                 phoneNum      Address                        BirthDate  BranchID");
                        while (resultSet.next()) {
                            System.out.print(String.format("%08d", resultSet.getInt(1)));
                            String name = resultSet.getString(2) + " " + resultSet.getString(3);
                            String address = resultSet.getString(5) + " " + resultSet.getString(6);
                            System.out.printf(" %-20s %-13s %-30s ", name, resultSet.getString(4), address);
                            System.out.print(resultSet.getDate(7));
                            System.out.println(" " + resultSet.getInt(8));
                        }
                    } else {
                        System.out.println("유효하지 않은 입력입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    }
                    break;
                case 2: //User 추가
                    System.out.println("\n < Administrator 추가 등록 >");
                    int inputAdminID = 0;
                    while (true) {
                        System.out.print("  1. Administrator ID(8자리 수) 입력: ");
                        inputAdminID = keyboard.nextInt();

                        findAdminByID(inputAdminID);
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

                    System.out.print("  8. 근무 branch ID(4자리 수) 입력: ");
                    int inputAdminBranchID = keyboard.nextInt();

                    try {
                        preparedStatement = connection.prepareStatement("INSERT INTO administrator(AdminID, Fname, Lname, phoneNum, Ad_state, Ad_details, BirthDate, AdBranchID) values (?,?,?,?,?,?,?,?)");
                        preparedStatement.setInt(1, inputAdminID);
                        preparedStatement.setString(2, inputFname);
                        preparedStatement.setString(3, inputLname);
                        preparedStatement.setString(4, inputphoneNum);
                        preparedStatement.setString(5, inputAd_state);
                        preparedStatement.setString(6, inputAd_details);

                        Date sqlDate = Date.valueOf(inputBirthDate);
                        preparedStatement.setDate(7, sqlDate);
                        preparedStatement.setInt(8, inputAdminBranchID);

                        preparedStatement.executeUpdate();

                    } catch (SQLException e) {
                        System.out.println(" Administrator 추가 실패: Invalid input, 이전 메뉴 선택 창으로 돌아갑니다.");
                        break;
                    }

                    findAdminByID(inputAdminID);
                    if (resultSet.next()) {
                        System.out.println(" 다음의 Administrator을 추가하였습니다: ");
                        System.out.println("AdminID  Name                 phoneNum      Address                        BirthDate  BranchID");
                        System.out.print(String.format("%08d", resultSet.getInt(1)));
                        String name = resultSet.getString(2) + " " + resultSet.getString(3);
                        String address = resultSet.getString(5) + " " + resultSet.getString(6);
                        System.out.printf(" %-20s %-13s %-30s ", name, resultSet.getString(4), address);
                        System.out.print(resultSet.getDate(7));
                        System.out.println(" " + resultSet.getInt(8));

                    } else {
                        System.out.println(" Administrator 추가 실패: 예기치 못한 에러, 이전 메뉴 선택 창으로 돌아갑니다.");
                    }

                    break;
                case 3: //admin 삭제
                    System.out.println("\n < Administrator 삭제 >");
                    System.out.print("  삭제할 Administrator ID(8자리 수) 입력: ");
                    inputAdminID = keyboard.nextInt();
                    findAdminByID(inputAdminID);
                    if (resultSet.next()) {
                        try {
                            statement.executeUpdate("DELETE FROM administrator WHERE AdminID = " + inputAdminID);
                            System.out.println(" Administrator 삭제 성공: 이전 메뉴 선택 창으로 돌아갑니다.");
                        } catch (SQLException e) {
                            System.out.println(" Administrator 삭제 실패: 이전 메뉴 선택 창으로 돌아갑니다.");
                            break;
                        }
                    } else {
                        System.out.println(" 존재하지 않는 Admin ID 입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    }
                    break;
                case 4: //Admin 수정
                    System.out.println("\n < Administrator 정보 수정 >");
                    System.out.print("  수정할 Administrator ID(8자리 수) 입력: ");
                    inputAdminID = keyboard.nextInt();
                    findAdminByID(inputAdminID);

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

                            System.out.print("  6. 근무 branch ID(4자리 수) 입력: ");
                            inputAdminBranchID = keyboard.nextInt();

                            if (!isManagerUpdatePossible(inputAdminID, inputAdminBranchID)) {
                                System.out.println(" Administrator 정보 수정 실패(Manager의 근무 branch는 변경 불가): 이전 메뉴 선택 창으로 돌아갑니다.");
                                break;
                            }

                            preparedStatement = connection.prepareStatement("UPDATE administrator SET Fname = ?, Lname = ?, phoneNum = ?, Ad_state = ?, Ad_details = ?, AdBranchID = ? WHERE AdminID = ?");
                            preparedStatement.setString(1, inputFname);
                            preparedStatement.setString(2, inputLname);
                            preparedStatement.setString(3, inputphoneNum);
                            preparedStatement.setString(4, inputAd_state);
                            preparedStatement.setString(5, inputAd_details);
                            preparedStatement.setInt(6, inputAdminBranchID);
                            preparedStatement.setInt(7, inputAdminID);

                            preparedStatement.executeUpdate();

                            System.out.println(" Administrator 정보 수정을 완료하였습니다.");
                        } catch (SQLException e) {
                            //e.printStackTrace();
                            System.out.println(" Administrator 정보 수정 실패: 이전 메뉴 선택 창으로 돌아갑니다.");
                            break;
                        }
                    } else {
                        System.out.println(" 존재하지 않는 Administrator ID 입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    }
                    break;
                default:
                    System.out.println("유효하지 않은 입력입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    break;
            }
        } catch (SQLException e){
            System.out.println("DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
        }
    }

    //adminManageAdmin()의 보조 함수
    public static boolean isManagerUpdatePossible(int AdminID, int newBranchID) throws SQLException {
        resultSet = statement.executeQuery("SELECT BranchID FROM bankbranch WHERE ManagerID = " + AdminID);

        if(resultSet.next() && resultSet.getInt(1) != newBranchID){ //해당 admin은 manager && 새로운 branch에 파견됨(불가능)
            return false;
        } else return true;
    }

    //complete
    public static void adminShowAccounts() {
        try {
            System.out.println("\n < 계좌 정보 조회 >");
            System.out.println("  0. Return to previous menu");
            System.out.println("  1. 계좌 번호(Account ID)로 검색하기");
            System.out.println("  2. User ID로 검색하기");
            System.out.println("  3. 전체 계좌 조회하기");
            System.out.print(" Input: ");
            int inputOption = keyboard.nextInt();

            switch (inputOption) {
                case 0:
                    return;
                case 1: //단일 Account 검색
                    System.out.print(" 검색할 Account ID(000-0000-0000) 입력: ");
                    String inputAccountID = keyboard.next();
                    findAccountByAccountID(inputAccountID);

                    if (resultSet.next()) { //account 존재
                        System.out.println("AccountID     Balance      Overdraft AdminID  OwnerID  OpeningDate"); //띄어쓰기 6
                        System.out.printf("%-13s %-12d %-9d ", resultSet.getString(1), resultSet.getInt(2), resultSet.getInt(3));
                        System.out.println(String.format("%08d", resultSet.getInt(4)) + " " + String.format("%08d", resultSet.getInt(5)) + " " + resultSet.getDate(6));
                    } else {
                        System.out.println("Account가 존재하지 않습니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    }
                    break;
                case 2: //user ID로 검색
                    System.out.print(" 검색할 User ID(8자리 수) 입력: ");
                    int inputUSerID = keyboard.nextInt();
                    findAccountByUserID(inputUSerID);

                    if (resultSet.next()) { //account 존재
                        System.out.println("AccountID     Balance      Overdraft AdminID  OwnerID  OpeningDate");
                        do {
                            System.out.printf("%-13s %-12d %-9d ", resultSet.getString(1), resultSet.getInt(2), resultSet.getInt(3));
                            System.out.println(String.format("%08d", resultSet.getInt(4)) + " " + String.format("%08d", resultSet.getInt(5)) + " " + resultSet.getDate(6));
                        } while (resultSet.next());
                    } else {
                        System.out.println("Account가 존재하지 않습니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    }
                    break;
                case 3: //전체 조회
                    resultSet = statement.executeQuery("SELECT AccountID, Balance, isMinus, AcAdminID, AcUserID, StartDate FROM Account");
                    System.out.println("AccountID     Balance      Overdraft AdminID  OwnerID  OpeningDate");
                    while (resultSet.next()) {
                        System.out.printf("%-13s %-12d %-9d ", resultSet.getString(1), resultSet.getInt(2), resultSet.getInt(3));
                        System.out.println(String.format("%08d", resultSet.getInt(4)) + " " + String.format("%08d", resultSet.getInt(5)) + " " + resultSet.getDate(6));
                    }
                    break;
                default:
                    System.out.println("유효하지 않은 입력입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    break;

            }
        } catch (SQLException e) {
            System.out.println("DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
        }
    }

    //TODO: 계좌 입출금 내역 확인
    public static void adminShowTransaction() {
/**
 * 1. 특정 계좌번호의 계좌 입출금 내역
 *  1-1. 입금만
 *  1-2. 출금만
 *  1-3. (optional) 기간 별?
 */
try{
        System.out.println("\n < 거래 내역 조회 >");
        System.out.println("  0. Return to previous menu");
        System.out.println("  1. 계좌 번호(Account ID)로 검색하기");
        System.out.println("  2. User ID로 검색하기");
        System.out.print(" Input: ");
        int inputOption = keyboard.nextInt();

        switch (inputOption){
            case 0:
                return;
            case 1: //단일 Account의 거래 내역 검색
                System.out.print(" 거래 내역을 검색할 Account ID(000-0000-0000) 입력: ");
                String inputAccountID = keyboard.next();
                findTransactionByAccountID(inputAccountID);

                if(resultSet.next()){ //account 존재
                    System.out.println("TimeStamp           Type Amount      BranchID");
                    do{
                        String type = resultSet.getInt(2) == 1 ? "입금" : "출금";
                        System.out.print(resultSet.getDate(1) + " " + type);
                        System.out.printf("  %-10d", resultSet.getInt(3));
                        System.out.println(String.format("%08d", resultSet.getInt(4)));
                    }while(resultSet.next());

                } else {
                    System.out.println("Account가 존재하지 않습니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                }
                break;
            case 2: //user ID로 검색
                System.out.print(" 거래 내역을 검색할 User ID(8자리 수) 입력: ");
                int inputUserID = keyboard.nextInt();


                try {
                        /*
                        유저가 가진 AccountID를 모두 찾아서
                            AccountID: ~~~
                            (기록 출력)

                            AccountID: ~~~
                            (기록 출력)
                        근데 생각해보니까 이렇게 하면 resultSet이 중간중간에 바뀌네..
                         */
                    /*
                    resultSet = statement.executeQuery("SELECT acTimeStamp, acType, amount, TBranchID, TAccountID FROM actransaction WHERE TAccountID = (SELECT AccountID FROM account WHERE AcUserID = "+ inputUserID + ") GROUP BY TAccountID ORDER BY acTimeStamp");

                    if(resultSet.next()){
                        String currentAccountID = resultSet.getString(5);
                        System.out.println("AccountID: " + currentAccountID);
                        System.out.println("TimeStamp           Type Amount      BranchID");
                        do{
                            String type = resultSet.getInt(2) == 1 ? "입금" : "출금";
                            System.out.print(resultSet.getDate(1) + " " + type);
                            System.out.printf("  %-10d", resultSet.getInt(3));
                            System.out.println(String.format("%08d", resultSet.getInt(4)));
                        }while (resultSet.next() && resultSet.getString(5).equals(currentAccountID));
                    }
                    */
                    resultSet = statement.executeQuery("SELECT acTimeStamp, acType, amount, TBranchID, TAccountID FROM actransaction WHERE TAccountID = (SELECT AccountID FROM account WHERE AcUserID = "+ inputUserID + ") GROUP BY TAccountID ORDER BY acTimeStamp");

                    for(String currentAccountID = "";resultSet.next();){
                        String loopAccountID = resultSet.getString(5);
                        if (!currentAccountID.equals(loopAccountID)) {
                            currentAccountID = new String(loopAccountID);
                            System.out.println("\n AccountID: " + currentAccountID);
                            System.out.println("TimeStamp           Type Amount      BranchID");
                        }
                        String type = resultSet.getInt(2) == 1 ? "입금" : "출금";
                        System.out.print(resultSet.getDate(1) + " " + type);
                        System.out.printf("  %-10d", resultSet.getInt(3));
                        System.out.println(String.format("%08d", resultSet.getInt(4)));
                    }
                    //TODO: 계좌 입출금 user로 찾는거 test해보기
                } catch (SQLException e) {
                    System.out.println("해당 User 혹은 User의 Account 및 기록이 존재하지 않습니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                }
                break;
            default:
                System.out.println("유효하지 않은 입력입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                break;

        }
    } catch (SQLException e){
        System.out.println("DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
    }
    }

    public static void userMainMenu(){
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


    public static void userDeposit(){
        try {
            System.out.print("입금할 계좌 번호 입력(000-0000-0000): ");
            String inputAccountID = keyboard.next();

            findAccountByAccountID(inputAccountID);

            if (resultSet.next()) {
                System.out.print("입금할 금액 입력: ");
                int inputBalance = keyboard.nextInt();

                //계좌 입금 기록 기록하기
                preparedStatement = connection.prepareStatement("INSERT INTO actransaction(acTimeStamp, acType, amount, TBranchID, TACcountID) values(now(), 1, ?, ?, ?)");
                preparedStatement.setInt(1, inputBalance);
                preparedStatement.setInt(2, currentBranchID);
                preparedStatement.setString(3, inputAccountID);
                preparedStatement.executeUpdate();

                //실제로 반영하기
                preparedStatement = connection.prepareStatement("UPDATE account SET Balance = Balance + ? WHERE AccountID = ?");
                preparedStatement.setInt(1, inputBalance);
                preparedStatement.setString(2, inputAccountID);
                preparedStatement.executeUpdate();

                findAccountByAccountID(inputAccountID);
                System.out.println(" 현재 계좌 잔액: " + resultSet.getInt(2));

            } else {
                System.out.println(" 존재하지 않는 계좌 번호 입니다.");
            }
        } catch (SQLException e) {
            System.out.println("DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
        }
    }

    public static void userWithdraw() {
        try {
            System.out.print("출금할 계좌 번호 입력(000-0000-0000): ");
            String inputAccountID = keyboard.next();

            //findAccountByAccountID(inputAccountID); //TODO: 비번도 받아와야함

            preparedStatement = connection.prepareStatement("SELECT AccountID, Balance, Password, isMinus FROM account WHERE AccountID = ?");
            preparedStatement.setString(1, inputAccountID);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.print("계좌 비밀번호 입력(4자리 수): ");
                int inputPassword = keyboard.nextInt();

                if(inputPassword == resultSet.getInt(3)){
                    System.out.print("출금할 금액 입력: ");
                    int inputBalance = keyboard.nextInt();

                    if(resultSet.getInt(4) == 0 && resultSet.getInt(2) - inputBalance < 0){
                        //마이너스 통장 불가
                        System.out.println(" 해당 Account에서는 " + inputBalance + "만큼의 금액을 출금할 수 없습니다.");
                        return;
                    }

                    //계좌 입금 기록 기록하기
                    preparedStatement = connection.prepareStatement("INSERT INTO actransaction(acTimeStamp, acType, amount, TBranchID, TACcountID) values(now(), -1, ?, ?, ?)");
                    preparedStatement.setInt(1, inputBalance);
                    preparedStatement.setInt(2, currentBranchID);
                    preparedStatement.setString(3, inputAccountID);
                    preparedStatement.executeUpdate();

                    //실제로 반영하기
                    preparedStatement = connection.prepareStatement("UPDATE account SET Balance = Balance - ? WHERE AccountID = ?");
                    preparedStatement.setInt(1, inputBalance);
                    preparedStatement.setString(2, inputAccountID);
                    preparedStatement.executeUpdate();

                    findAccountByAccountID(inputAccountID);
                    System.out.println(" 현재 계좌 잔액: " + resultSet.getInt(2));
                }
            } else {
                System.out.println(" 존재하지 않는 계좌 번호 입니다.");
            }
        } catch (SQLException e) {
            System.out.println("DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
        }
    }

    public static void userNewAccount(){

    }

    public static void userDeleteAccount(){

    }

}
