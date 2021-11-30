import java.sql.*;
import java.util.Scanner;

/**
 * //1. admin: 각 관리자는 로그인용 비번을 가진다. 해당 비밀번호로 로그인을 해야 관리자 업무를 볼 수 있음
 * admin, branch는 최소 하나이상 존재해야함. ex) admin이 하나만 남은 경우 삭제 불가 / branch가 하나만 남은 경우 삭제 불가
 * 입금 = 1, 출금 = -1
 * isMinus == 1이면 마이너스 뚫기 가능
 *
 * branch는 제거할 수 없음(정보 수정만 가능)
 *
 * 통장에 발행해준 adminID는 찍히지 않음. 발행 점포만 찍힘
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
            Class.forName("com.mysql.cj.jdbc.Driver");

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
            System.out.println("\nBank의 전체 Branch 목록: ");
            showBranches(-1);


            System.out.print("현재 BranchID(4자리수) 입력: ");
            currentBranchID = keyboard.nextInt();
            findBranchByID(currentBranchID);

            while (!resultSet.next()) {
                System.out.print("존재하지 않는 BranchID입니다. 다시 입력해주세요(4자리 수): ");
                currentBranchID = keyboard.nextInt();
                findBranchByID(currentBranchID);
            }

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
        resultSet = statement.executeQuery("SELECT * FROM user WHERE UserID = " + inputUserID);
        //resultSet = statement.executeQuery("SELECT UserID, Fname, Lname, phoneNum, Ad_state, Ad_details, BirthDate FROM user WHERE UserID = " + inputUserID);
    }

    public static void findAdminByID(int inputAdminID) throws SQLException {
        //resultSet = statement.executeQuery("SELECT AdminID, Fname, Lname, phoneNum, Ad_state, Ad_details, BirthDate, AdBranchID FROM administrator WHERE AdminID = " + inputAdminID);
        resultSet = statement.executeQuery("SELECT * FROM administrator WHERE AdminID = " + inputAdminID);
    }

    public static void findAccountByAccountID(String inputAccountID) throws SQLException {
        preparedStatement = connection.prepareStatement("SELECT AccountID, Balance, isMinus, AcBranchID, AcUserID, StartDate FROM account WHERE AccountID = ?");
        preparedStatement.setString(1, inputAccountID);
        resultSet = preparedStatement.executeQuery();
    }

    public static void findAccountByUserID(int inputUserID) throws SQLException {
        resultSet = statement.executeQuery("SELECT AccountID, Balance, isMinus, AcBranchID, AcUserID, StartDate FROM account WHERE AcUserID = " + inputUserID);
    }

    public static void findTransactionByAccountID(String inputAccountID) throws SQLException {
        preparedStatement = connection.prepareStatement("SELECT acTimeStamp, acType, amount, TBranchID, TAccountID FROM actransaction WHERE TAccountID = ? ORDER BY acTimeStamp");
        preparedStatement.setString(1, inputAccountID);
        resultSet = preparedStatement.executeQuery();
    }


    public static void findBranchByID(int inputBranchID) throws SQLException {
        resultSet = statement.executeQuery("SELECT BranchID, Lo_state, Lo_details, ManagerID FROM bankbranch WHERE BranchID = " + inputBranchID);
    }

    public static void showBranches(int inputBranchID) {
        System.out.println("BranchID Location                 ManagerID");
        try {
            if (inputBranchID == -1) {
                resultSet = statement.executeQuery("SELECT BranchID, Lo_state, Lo_details, ManagerID FROM bankbranch");
                while (resultSet.next()) {
                    String address = resultSet.getString(2) + " " + resultSet.getString(3);
                    System.out.print(String.format("%04d", resultSet.getInt(1)) + "     ");
                    System.out.printf("%-24s %d\n", address, resultSet.getInt(4));
                }
            } else {
                findBranchByID(inputBranchID);
                if (resultSet.next()) {
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

    public static void showAccountsByUserID(int inputUserID) throws SQLException {
        resultSet = statement.executeQuery("SELECT AccountID, Balance, isMinus, Lo_state, Lo_details, StartDate FROM account, bankbranch WHERE AcUserID = " + inputUserID +  " AND AcBranchID = BranchID ORDER BY StartDate");
            if (resultSet.next()) { //account 존재
                System.out.println("AccountID     Balance      Overdraft     Branch                            OpeningDate"); //띄어쓰기 6
                do {
                    String printOverdraft = resultSet.getInt(3) == 1 ? "possible" : "impossible";
                    System.out.printf("%-13s %-12d %-12s ", resultSet.getString(1), resultSet.getInt(2), printOverdraft);
                    String address = resultSet.getString(4) + " " + resultSet.getString(5) + "지점";
                    System.out.printf(" %-32s ", address); System.out.println(resultSet.getDate(6));
                } while (resultSet.next());
            } else {
                System.out.println(" 해당되는 항목이 존재하지 않습니다. 이전 메뉴 선택 창으로 돌아갑니다.");
            }
    }

    //TODO
    public static void showTransactionByUserIDForEachAccount(int inputUserID) throws SQLException{

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
                case 5: //TODO
                    adminManageBank();
                    break;
                default:
                    System.out.println("잘못된 입력입니다.");
                    break;
            }
        }
    }

    //complete
    public static void adminManageUser() {
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
                        System.out.print("\n 정보를 검색할 User ID 입력(8자리 수): ");
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
                            System.out.println(" 등록되지 않은 User 입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                        }
                    } else if (inputOption1 == 2) {
                        //모든 user 검색
                        resultSet = statement.executeQuery("SELECT UserID, Fname, Lname, phoneNum, Ad_state, Ad_details, BirthDate FROM User ORDER BY UserID");
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
                        System.out.print("  1. 등록할 User ID(8자리 수) 입력: ");
                        inputUserID = keyboard.nextInt();

                        findUserByID(inputUserID);
                        if (resultSet.next()) {
                            System.out.println("  이미 존재하는 User ID입니다. 다른 ID를 입력해주십시오.");
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
                        System.out.println(" User 추가 실패: Invalid input, 이전 메뉴 선택 창으로 돌아갑니다.");
                        break;
                    }

                    findUserByID(inputUserID);
                    if (resultSet.next()) {
                        System.out.println("\n 다음의 User을 추가하였습니다: ");
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
                            /*
                            findAccountByUserID(inputUserID);
                            if(resultSet.next()) {
                                do{
                                    statement.executeUpdate("DELETE FROM account WHERE AcUserID = " + inputUserID);
                                    
                                    //TODO: transaction도 함께 지움
                                    
                                } while(resultSet.next());

                            }*/
                            statement.executeUpdate("DELETE FROM User WHERE UserID = " + inputUserID);
                            System.out.println(" User 삭제 성공: 이전 메뉴 선택 창으로 돌아갑니다.");
                        } catch (SQLException e) {
                            e.printStackTrace();
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
        } catch (SQLException e) {
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
                        System.out.println("\n 다음의 Administrator을 추가하였습니다: ");
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

                    resultSet = statement.executeQuery("SELECT COUNT(*) FROM administrator");
                    if(resultSet.next()){
                        if(resultSet.getInt(1) <= 1){
                            System.out.println(" Administrator 삭제 실패: 최소 한 명 이상의 Administrator가 존재해야 합니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                            return;
                        }
                    }

                    System.out.print("  삭제할 Administrator ID(8자리 수) 입력: ");
                    inputAdminID = keyboard.nextInt();
                    findAdminByID(inputAdminID);
                    if (resultSet.next()) {
                        try {
                            statement.executeUpdate("DELETE FROM administrator WHERE AdminID = " + inputAdminID);
                            System.out.println(" Administrator 삭제 성공: 이전 메뉴 선택 창으로 돌아갑니다.");
                        } catch (SQLException e) {
                            System.out.println(" Administrator 삭제 실패: 잘못된 접근입니다.\n Branch의 Manager 수정을 원하신다면 '은행 관리'창에서 Manager를 변경해주세요.\n 이전 메뉴 선택 창으로 돌아갑니다.");
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
                                System.out.println(" Administrator 정보 수정 실패: Manager의 근무 branch는 변경할 수 없습니다.\n Branch의 Manager 수정을 원하신다면 '은행 관리'창에서 Manager를 변경해주세요.\n 이전 메뉴 선택 창으로 돌아갑니다.");
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
        } catch (SQLException e) {
            System.out.println("DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
        }
    }

    //adminManageAdmin()의 보조 함수
    public static boolean isManagerUpdatePossible(int AdminID, int newBranchID) throws SQLException {
        resultSet = statement.executeQuery("SELECT BranchID FROM bankbranch WHERE ManagerID = " + AdminID);

        if (resultSet.next() && resultSet.getInt(1) != newBranchID) { //해당 admin은 manager && 새로운 branch에 파견됨(불가능)
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

                    if (resultSet.next()) {
                        System.out.println("\nAccountID     Balance      Overdraft    BranchID OwnerID  OpeningDate"); //띄어쓰기 6

                        String printOverdraft = resultSet.getInt(3) == 1 ? "possible" : "impossible";
                        String tmpPrintBranchID = String.format("%04d", resultSet.getInt(4));
                        System.out.printf("%-13s %-12d %-12s %-4s", resultSet.getString(1), resultSet.getInt(2), printOverdraft, tmpPrintBranchID);
                        System.out.println("     " + String.format("%08d", resultSet.getInt(5)) + " " + resultSet.getDate(6));
                    } else {
                        System.out.println("Account가 존재하지 않습니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    }
                    break;
                case 2: //user ID로 검색
                    //TODO: 여기 수정
                    System.out.print(" 검색할 User ID(8자리 수) 입력: ");
                    int inputUserID = keyboard.nextInt();
                    showAccountsByUserID(inputUserID);
                    break;
                case 3: //전체 조회(OwnerID, Branch ID 보여줌)
                    resultSet = statement.executeQuery("SELECT AccountID, Balance, isMinus, AcBranchID, AcUserID, StartDate FROM Account");

                    if(resultSet.next()) {
                        System.out.println("\nAccountID     Balance      Overdraft    BranchID OwnerID  OpeningDate"); //띄어쓰기 6

                        do {
                            String printOverdraft = resultSet.getInt(3) == 1 ? "possible" : "impossible";
                            String tmpPrintBranchID = String.format("%04d", resultSet.getInt(4));
                            System.out.printf("%-13s %-12d %-12s %-4s", resultSet.getString(1), resultSet.getInt(2), printOverdraft, tmpPrintBranchID);
                            System.out.println("     " + String.format("%08d", resultSet.getInt(5)) + " " + resultSet.getDate(6));
                        } while(resultSet.next());
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

    //TODO: 계좌 입출금 내역 확인(userID로 각 계좌 찾아서 ..)
    public static void adminShowTransaction() {
/**
 * 1. 특정 계좌번호의 계좌 입출금 내역
 *  1-1. 입금만
 *  1-2. 출금만
 *  1-3. (optional) 기간 별?
 */
        try {
            System.out.println("\n < 거래 내역 조회 >");
            System.out.println("  0. Return to previous menu");
            System.out.println("  1. 계좌 번호(Account ID)로 검색하기");
            System.out.println("  2. User ID로 검색하기");
            System.out.print(" Input: ");
            int inputOption = keyboard.nextInt();

            switch (inputOption) {
                case 0:
                    return;
                case 1: //단일 Account의 거래 내역 검색
                    System.out.print(" 거래 내역을 검색할 Account ID(000-0000-0000) 입력: ");
                    String inputAccountID = keyboard.next();
                    findTransactionByAccountID(inputAccountID);

                    if (resultSet.next()) { //account 존재
                        System.out.println("TimeStamp               Type Amount            BranchID");
                        do {
                            String type = resultSet.getInt(2) == 1 ? "입금" : "출금";
                            System.out.print(resultSet.getTimestamp(1) + "   " + type);
                            System.out.printf("  %-18d", resultSet.getInt(3));
                            System.out.println(String.format("%04d", resultSet.getInt(4)));
                        } while (resultSet.next());

                    } else {
                        System.out.println("Account가 존재하지 않습니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    }
                    break;
                case 2: //user ID로 검색
                    System.out.print(" 거래 내역을 검색할 User ID(8자리 수) 입력: ");
                    int inputUserID = keyboard.nextInt();
                    //TODO: 계좌 입출금 user로 찾는거 test해보기 -> 안됨요..ㅜ


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
                        resultSet = statement.executeQuery("SELECT acTimeStamp, acType, amount, TBranchID, TAccountID FROM actransaction WHERE TAccountID = (SELECT AccountID FROM account WHERE AcUserID = " + inputUserID + ") GROUP BY TAccountID ORDER BY acTimeStamp");

                        for (String currentAccountID = ""; resultSet.next(); ) {
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
                    } catch (SQLException e) {
                        System.out.println("해당 User 혹은 User의 Account 및 기록이 존재하지 않습니다. 이전 메뉴 선택 창으로 돌아갑니다.");
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

    public static void adminManageBank() {
        try {
            System.out.println("\n <은행 관리>");
            System.out.println("  1. 은행 소유 금액 조회"); //account의 모든 balance의 합
            System.out.println("  2. 전체 지점 조회"); //전체 지점만 조회가능
            System.out.println("  3. 지점 추가");
            //System.out.println("  4. 지점 삭제");
            System.out.println("  4. 지점 정보 수정");
            System.out.print(" Input: ");
            int inputOption = keyboard.nextInt();

            switch (inputOption) {
                case 0:
                    return;
                case 1: //은행이 가진 총 돈
                    System.out.println("\n <은행 소유 금액 조회>");
                    resultSet = statement.executeQuery("SELECT SUM(Balance) FROM account");
                    if (resultSet.next()) {
                        System.out.print("  전체 금액: ");
                        System.out.println(resultSet.getLong(1));
                    } else {
                        System.out.println("  현재 은행에 유효한 계좌가 존재하지 않습니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    }
                    break;
                case 2: //전체 지점 조회
                    System.out.println("\n <은행 지점 전체 조회> ");
                    showBranches(-1);
                    break;
                case 3: //지점 추가
                /*
                새로운 branch정보를 받고, manager의 경우 기존 직원 중 선택하게 함.
                만약 이미 매니저인 직원이 선택되었다면 다른 manager을 선택하게 함.(굳이? 한명이 여러 지점을 맡는 건?) -> 근데 이미 모든 직원이 manager로 참가하고 있을 경우(total branch수 = 직원 수) 추가가 불가능하므로 quit 옵션
                 */
                    System.out.println("\n <은행 지점 추가>");

                    resultSet = statement.executeQuery("SELECT BranchID FROM bankbranch ORDER BY BranchID DESC");

                    int inputBankBranchID;

                    if(!resultSet.next() || resultSet.getInt(1) >= 9999) {
                        System.out.println(" DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
                        return;
                    }

                    inputBankBranchID = resultSet.getInt(1) + 1;

                    System.out.print("  1. 주소(특별시/광역시/도) 입력: ");
                    String inputLo_state = keyboard.next();

                    System.out.print("  2. 주소(나머지 주소) 입력: ");
                    String inputLo_details = keyboard.next();

                    int inputManagerID;
                    while (true) {
                        System.out.print("  3. Manager ID(8자리 수) 입력: ");
                        inputManagerID = keyboard.nextInt();
                        resultSet = statement.executeQuery("SELECT BranchID FROM bankbranch WHERE ManagerID = " + inputManagerID);
                        if (resultSet.next()) {
                            System.out.print(" 해당 Administrator는 이미 Manager을 맡고 있습니다. Branch 등록을 계속 하시려면 1, 등록을 취소하고 이전 메뉴로 돌아가시려면 0을 입력해주세요: ");
                            int tmpOption = keyboard.nextInt();
                            if (tmpOption == 0) {
                                return;
                            }
                        } else {
                            findAdminByID(inputManagerID);
                            if (resultSet.next()) {
                                break;
                            } else {
                                System.out.println(" 존재하지 않는 Administrator ID입니다.");
                            }
                        }
                    }
                    try {
                        preparedStatement = connection.prepareStatement("INSERT INTO bankBranch(BranchID, Lo_State, Lo_details, ManagerID) values (?,?,?,?)");
                        preparedStatement.setInt(1, inputBankBranchID);
                        preparedStatement.setString(2, inputLo_state);
                        preparedStatement.setString(3, inputLo_details);
                        preparedStatement.setInt(4, inputManagerID);
                        preparedStatement.executeUpdate();

                        preparedStatement = connection.prepareStatement("UPDATE administrator SET AdBranchID = ? WHERE AdminID = ?");
                        preparedStatement.setInt(1, inputBankBranchID);
                        preparedStatement.setInt(2, inputManagerID);
                        preparedStatement.executeUpdate();

                        System.out.println(" Bank Branch 추가 완료, ID: " + String.format("%04d", inputBankBranchID));
                    } catch (SQLException e) {
                        System.out.println(" Bank Branch 추가 실패: Invalid input, 이전 메뉴 선택 창으로 돌아갑니다..");
                    }

                    break;
                    /*
                case 4: //지점 삭제
                    System.out.println("\n <은행 지점 삭제>");

                    resultSet = statement.executeQuery("SELECT COUNT(*) FROM bankbranch");
                    if(resultSet.next()){
                        if(resultSet.getInt(1) <= 1){
                            System.out.println(" Bank Branch 삭제 실패: 최소 하나 이상의 Branch가 존재해야 합니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                            return;
                        }
                    }


                    System.out.print("  삭제할 Bank Branch ID(4자리 수) 입력: ");
                    inputBankBranchID = keyboard.nextInt();

                    findBranchByID(inputBankBranchID);

                    if (resultSet.next()) {

                        System.out.print("  Branch를 삭제하기 위해서는 Branch에 근무하는 직원을 다른 Branch로 ");

                        try {
                            statement.executeUpdate("DELETE FROM bankBranch WHERE BranchID = " + inputBankBranchID);
                            System.out.println(" Bank Branch 삭제 성공: 이전 메뉴 선택 창으로 돌아갑니다.");

                        } catch (SQLException e) {
                            System.out.println(" Bank Branch 삭제 실패: 이전 메뉴 선택 창으로 돌아갑니다.");
                        }
                    } else {
                        System.out.println(" 존재하지 않는 Bank Branch ID 입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    }
                    break;
                    */
                case 4: //지점 정보 수정
                    System.out.println("\n <은행 지점 정보 수정>");

                    System.out.print("  수정할 Bank Branch ID(4자리 수) 입력: ");
                    inputBankBranchID = keyboard.nextInt();

                    findBranchByID(inputBankBranchID);


                    if (resultSet.next()) {
                        System.out.print("  1. 주소(특별시/광역시/도) 입력: ");
                        inputLo_state = keyboard.next();

                        System.out.print("  2. 주소(나머지 주소) 입력: ");
                        inputLo_details = keyboard.next();

                        while (true) {
                            System.out.print("  3. Manager ID(8자리 수) 입력: ");
                            inputManagerID = keyboard.nextInt();
                            resultSet = statement.executeQuery("SELECT BranchID FROM bankbranch WHERE ManagerID = " + inputManagerID);
                            if (resultSet.next()) {
                                System.out.print(" 해당 Administrator는 이미 다른 지점의 Manager을 맡고 있습니다. Branch 등록을 계속 하시려면 1, 수정을 취소하고 이전 메뉴로 돌아가시려면 0을 입력해주세요: ");
                                int tmpOption = keyboard.nextInt();
                                if (tmpOption == 0) {
                                    return;
                                }
                            } else {
                                findAdminByID(inputManagerID);
                                if (resultSet.next()) {
                                    break;
                                } else {
                                    System.out.println(" 존재하지 않는 Administrator ID입니다.");
                                }
                            }
                        }
                        try {
                            preparedStatement = connection.prepareStatement("UPDATE bankBranch SET Lo_state = ?, Lo_details = ?, ManagerID = ? WHERE BranchID = " + inputBankBranchID);
                            preparedStatement.setString(1, inputLo_state);
                            preparedStatement.setString(2, inputLo_details);
                            preparedStatement.setInt(3, inputManagerID);
                            preparedStatement.executeUpdate();
                            preparedStatement = connection.prepareStatement("UPDATE administrator SET AdBranchID = ? WHERE AdminID = ?");
                            preparedStatement.setInt(1, inputBankBranchID);
                            preparedStatement.setInt(2, inputManagerID);
                            preparedStatement.executeUpdate();
                            System.out.println(" Bank Branch 정보 수정 완료");
                        } catch (SQLException e) {
                            System.out.println(" Bank Branch 정보 수정 실패: Invalid input, 이전 메뉴 선택 창으로 돌아갑니다.");
                        }
                    } else {
                        System.out.println(" 존재하지 않는 Bank Branch ID 입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    }
                    break;
                default:
                    System.out.println("  유효하지 않은 입력입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    break;
            }
        } catch (SQLException e) {
            System.out.println("  DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
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
            System.out.println(" 4. 내 계좌 개설");
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
                    userShowAccounts();
                    break;
                case 4:
                    userMakeNewAccount();
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

    //complete
    public static void userDeposit() {
        try {
            System.out.print(" 입금할 계좌 번호 입력(000-0000-0000): ");
            String inputAccountID = keyboard.next();

            findAccountByAccountID(inputAccountID);

            if (resultSet.next()) {
                System.out.print(" 입금할 금액 입력: ");
                int inputBalance = keyboard.nextInt();

                //계좌 입금 기록 기록하기
                preparedStatement = connection.prepareStatement("INSERT INTO actransaction(acTimeStamp, acType, amount, TBranchID, TAccountID) values(CURRENT_TIMESTAMP, 1, ?, ?, ?)");
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

                if (resultSet.next()) {
                    System.out.println(" 현재 계좌 잔액: " + resultSet.getInt(2));
                }
            } else {
                System.out.println(" 존재하지 않는 계좌 번호 입니다.");
            }
        } catch (SQLException e) {
            System.out.println("DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
        }
    }

    //complete
    public static void userWithdraw() {
        try {
            System.out.print(" 출금할 계좌 번호 입력(000-0000-0000): ");
            String inputAccountID = keyboard.next();

            //findAccountByAccountID(inputAccountID); //TODO: 비번도 받아와야함

            preparedStatement = connection.prepareStatement("SELECT AccountID, Balance, Password, isMinus FROM account WHERE AccountID = ?");
            preparedStatement.setString(1, inputAccountID);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.print(" 계좌 비밀번호 입력(4자리 수): ");
                int inputPassword = keyboard.nextInt();

                if (inputPassword == resultSet.getInt(3)) {
                    System.out.print(" 출금할 금액 입력: ");
                    int inputBalance = keyboard.nextInt();

                    if (resultSet.getInt(4) != 1 && resultSet.getInt(2) - inputBalance < 0) {
                        //마이너스 통장 불가
                        System.out.println(" 해당 Account에서는 " + inputBalance + "원 만큼의 금액을 출금할 수 없습니다.");
                        return;
                    }

                    //계좌 입금 기록 기록하기
                    preparedStatement = connection.prepareStatement("INSERT INTO actransaction(acTimeStamp, acType, amount, TBranchID, TAccountID) values(CURRENT_TIMESTAMP , -1, ?, ?, ?)");
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
                    if (resultSet.next()) {
                        System.out.println(" 현재 계좌 잔액: " + resultSet.getInt(2));
                    }
                } else {
                    System.out.println(" 비밀번호가 일치하지 않습니다.");
                }
            } else {
                System.out.println(" 존재하지 않는 계좌 번호 입니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
        }
    }
    
    //보조 함수
    public static int userLoginByID() throws SQLException{
        System.out.print("\n <User Login>\n  고객님의 User ID(8자리 수)를 입력해주세요: ");
        int inputUserID = keyboard.nextInt();

        findUserByID(inputUserID);

        if(resultSet.next()){
            System.out.println("  안녕하세요, " + resultSet.getString(3) + " " + resultSet.getString(2) + "님.");
            return inputUserID;
        } else {
            System.out.println("  존재하지 않는 User ID 입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
            return -1;
        }
    }

    //TODO
    public static void userShowAccounts() {
        try {
            int currentUserID;
            if ((currentUserID = userLoginByID()) == -1) {
                return;
            }

            System.out.println("\n <내 계좌 조회>");
            System.out.println("  1. 내 계좌 정보 조회");
            System.out.println("  2. 내 계좌 거래 내역 조회");
            System.out.print(" Input: ");
            int inputOption = keyboard.nextInt();


            switch (inputOption) {
                case 1:
                    System.out.println();
                    showAccountsByUserID(currentUserID);
                    break;
                case 2:
                    //TODO(admin에서 userID로 계좌거래내역 조회하는 것과 동일)
                    break;
                default:
                    System.out.println("유효하지 않은 입력입니다. 이전 메뉴 선택 창으로 돌아갑니다.");
                    break;
            }
        } catch (SQLException e) {
            System.out.println("DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
        }
    }

    public static void userMakeNewAccount() {

        try {
            resultSet = statement.executeQuery("SELECT count(*) FROM account");

            if(resultSet.next() && resultSet.getLong(1) >= ((long) Math.pow(10, 11)) - 1){
                System.out.println("DB 용량 초과: 이전 메뉴 선택 창으로 돌아갑니다.");
                return;
            }

            int currentUserID = -1;
            if ((currentUserID = userLoginByID()) == -1) {
                return;
            }

            System.out.println("\n <내 계좌 개설>");

            System.out.println("  새로운 계좌 개설을 위해 다음의 정보를 입력해주십시오.");


                System.out.print("  1. 개설할 통장의 비밀 번호 입력(4자리 수): ");
                int inputPassword = keyboard.nextInt();
                System.out.print("  2. 마이너스 통장으로 개설하시겠습니까? (Y/N): ");
                int inputIsMinus = keyboard.next().equals("Y") ? 1 : -1;

                
                
                //새로운 계좌 번호 랜덤 생성
                String newAccountID = "";

                resultSet = statement.executeQuery("SELECT AccountID FROM account ORDER BY AccountID DESC");
                
                if(resultSet.next()) {
                    //TODO: 중복되지 않는 계좌 번호 랜덤 생성 - 알고리즘 개선 방법?
                    /*
                    현재 랜덤으로 새 계좌 번호 생성 -> DB에 존재하는지 확인 -> 없으면 적용 순으로 하고 있으나
                    AccountID를 정렬해서 받아오는 경우 최솟값으로부터 시작해서 중간에 빈 곳을 확인하고 빈 곳에 새로운 계좌 번호 삽입하는 건.. -> 근데 이러는 경우 계좌가 많아지면 쓸데없이 앞에서부터 검색해야함
                        어차피 계좌번호를 오름차순으로 생성하니까 COUNT로 총 개수 받아와서 이후의 것을 만드는 것도 방법이겠으나! 중간중간에 계좌를 삭제할 수도 있다는 점... 그러면 count 이상 nunber의 계좌 번호가 존재할 수도 있음

                    계좌 번호를 내림차순으로 정렬해서 최댓값 + 1을 하는 건? 최댓값이 999-9999-9999인 경우 DB가 꽉 찬 경우는 이미 제외됐기 때문에 랜덤으로 돌리기...
                     */
                    String tmpLoopAccountID = resultSet.getString(1);

                    if(tmpLoopAccountID.equals("999-9999-9999")){
                        while(true) { //랜덤 돌리기...
                            int[] partOfAccountID = new int[3];

                            partOfAccountID[0] = (int) (Math.random() * 100);
                            partOfAccountID[1] = (int) (Math.random() * 1000);
                            partOfAccountID[2] = (int) (Math.random() * 1000);

                            newAccountID = String.format("%03d", partOfAccountID[0]) + "-" + String.format("%04d", partOfAccountID[1]) + "-" + String.format("%04d", partOfAccountID[2]);
                            preparedStatement = connection.prepareStatement("SELECT AccountID FROM account WHERE accountID = ?");
                            preparedStatement.setString(1, newAccountID);
                            preparedStatement.executeQuery();

                            if(!resultSet.next()) break;
                        }
                    } else {

                        long[] partOfAccountID = new long[3];

                        partOfAccountID[0] = Long.parseLong(tmpLoopAccountID.substring(0, 3));
                        partOfAccountID[1] = Long.parseLong(tmpLoopAccountID.substring(4, 8));
                        partOfAccountID[2] = Long.parseLong(tmpLoopAccountID.substring(9, 13));

                        long newAccountID_long = partOfAccountID[0] * 100000000 + partOfAccountID[1] * 10000 + partOfAccountID[2] + 1;

                        partOfAccountID[0] = newAccountID_long % 10000;
                        newAccountID_long = (newAccountID_long - newAccountID_long % 10000) / 10000;
                        partOfAccountID[1] = newAccountID_long % 10000;
                        newAccountID_long = (newAccountID_long - newAccountID_long % 10000) / 10000;
                        partOfAccountID[2] = newAccountID_long;

                        newAccountID = String.format("%03d", partOfAccountID[2]) + "-" + String.format("%04d", partOfAccountID[1]) + "-" + String.format("%04d", partOfAccountID[0]);

                    }
                } else { //아직 개설된 account가 하나도 없는 경우
                    newAccountID = "000-0000-0000";
                }

                //계좌 생성 SQL 실행
                preparedStatement = connection.prepareStatement("INSERT INTO account(AccountID, Password, iSMinus, AcBranchID, AcUserID, StartDate) values (?, ?, ?, ?, ?, CURRENT_DATE )");

                preparedStatement.setString(1, newAccountID);
                preparedStatement.setInt(2, inputPassword);
                preparedStatement.setInt(3, inputIsMinus);
                preparedStatement.setInt(4, currentBranchID);
                preparedStatement.setInt(5, currentUserID);
                preparedStatement.executeUpdate();

            System.out.println(" 고객님의 Account가 새롭게 개설 되었습니다. 계좌 번호: " + newAccountID);

        } catch (SQLException e) {
            System.out.println("DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
        }
    }

    public static void userDeleteAccount() {
        //TODO
        try {
            int currentUserID;
            if ((currentUserID = userLoginByID()) == -1) {
                return;
            }

            resultSet = statement.executeQuery("SELECT * FROM account WHERE AcUserID = " + currentUserID);

            if(resultSet.next()) {

                System.out.println("\n <내 계좌 삭제>");
                System.out.println("  현재 고객님께서 소유한 계좌는 다음과 같습니다.\n");

                showAccountsByUserID(currentUserID);

                System.out.print("\n  삭제할 Account ID(000-0000-0000) 입력: ");
                String inputDeleteAccountID = keyboard.next();

                preparedStatement = connection.prepareStatement("SELECT AccountID FROM account WHERE AcUserID = " + currentUserID + " AND AccountID = ?");
                preparedStatement.setString(1, inputDeleteAccountID);

                resultSet = preparedStatement.executeQuery();

                if(resultSet.next()) {
                    preparedStatement = connection.prepareStatement("DELETE FROM account WHERE AccountID = ?");
                    preparedStatement.setString(1, inputDeleteAccountID);
                    preparedStatement.executeUpdate();
                    System.out.println(" 고객님의 Account가 삭제되었습니다.");
                } else {
                    System.out.println("  고객님께서 소유한 계좌가 아닙니다. 이전 이전 메뉴 선택 창으로 돌아갑니다.");
                }
            } else {
                System.out.println("  현재 소유한 계좌가 없습니다. 이전 이전 메뉴 선택 창으로 돌아갑니다.");
            }

        } catch (SQLException e) {
            System.out.println("DB Error: 이전 이전 메뉴 선택 창으로 돌아갑니다.");
        }
    }

}
