import java.sql.*;
import java.util.Scanner;

/**<추가사항>
 * 1. admin: 각 관리자는 로그인용 비번을 가진다. 해당 비밀번호로 로그인을 해야 관리자 업무를 볼 수 있음
 * </추가사항>
 */

public class Main {

    static Scanner keyboard = new Scanner(System.in);

    static Connection connection = null;
    static Statement statement = null;
    static ResultSet resultSet = null;

    public static void main(String[] args) {

        //jdbc driver 연결
        try{
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://localhost/BankDBMS";

            System.out.print("Enter ID: ");
            String ID = keyboard.next();

            System.out.print("Enter PW: ");
            String PW = keyboard.next();

            connection = DriverManager.getConnection(url, ID, PW);
            if(connection == null) {
                System.err.println(". . . DB 접속 실패");
                return;
            }

        } catch (ClassNotFoundException e) {
            System.err.println("driver loading 실패"); return;
        } catch (SQLException throwables) {
            throwables.printStackTrace(); return;
        }

        System.out.println(". . . DB 접속 성공\n");
        System.out.println("--------Bank Database System--------");

        try {
            statement = connection.createStatement();

            int selectInput = -1;

            //가장 외부 menu 선택 화면에서 종료를 입력할때까지 계속해서 실행됨
            while(selectInput != 0){
                selectInput = menuSelectPosition();

                if(selectInput == 1){
                    adminMainMenu();
                } else if(selectInput == 2){
                    userMainMenu();
                } else {
                    System.out.println("잘못된 입력입니다.");
                }

                continue;
            }


            //DB 종료
            if(connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static int menuSelectPosition(){
        System.out.println("\n--------Select your position--------");
        System.out.println(" 0. Exit");
        System.out.println(" 1. Administrator mode");
        System.out.println(" 2. User mode");
        System.out.println("------------------------------------");
        System.out.println(" Input: ");

        int inputOption = keyboard.nextInt();
        return inputOption;
    }


    /*
    adminMainMenu, userMainMenu에서는 해당 함수 내에서 input에 따라 다른 함수 호출등의 action을 취함
     */

    public static void adminMainMenu(){
        System.out.println("\n------------ADMIN MODE-------------");
        System.out.println("-----------Select action-----------");
        System.out.println(" 0. Return to previous menu");
        System.out.println(" 1. ");
        System.out.println(" 2. Administrator 정보 관리");
        System.out.println("------------------------------------");
        System.out.println(" Input: ");

        int inputOption = keyboard.nextInt();

        switch (inputOption){
            case 0: return;
            case 1:
                adminBankingSystem();
                break;
            case 2:
                registerNew(1);
                break;
            default:
                System.out.println("잘못된 입력입니다.");
                break;
        }
    }

    public static void userMainMenu(){
        System.out.println("\n-------------USER MODE-------------");
        System.out.println("------Banking System(for user)------");
        System.out.println(" 0. Return to previous menu");
        System.out.println(" 1. 새 계좌 생성");
        System.out.println(" 2. 내 계좌 삭제하기");
        System.out.println(" 3. 계좌에 입금하기");
        System.out.println(" 4. 계좌로부터 출금하기");
        System.out.println(" 5. 내 정보 수정");
        System.out.println("------------------------------------");
        System.out.println(" Input: ");
        int inputOption = keyboard.nextInt();

        switch (inputOption){
            case 0: return;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            default:
                System.out.println("잘못된 입력입니다.");
                break;
        }
    }

    public static void registerNew(int mode){
        //mode = 0: Admin, mode = 1: User
    }

    public static void adminBankingSystem(){
        System.out.println("------Banking System(for Admin)------");
        System.out.println(" 0. Return to previous menu");
        System.out.println(" 1. User 정보 확인");
        System.out.println(" 2. Account 정보 확인");
        System.out.println(" 3. 입출금 정보 확인");
        System.out.println("------------------------------------");
        System.out.println(" Input: ");
        int inputOption = keyboard.nextInt();

        switch (inputOption){
            case 0: return;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            default:
                System.out.println("잘못된 입력입니다.");
                break;
        }
    }


}
