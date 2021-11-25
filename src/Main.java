import java.sql.*;
import java.util.Scanner;

/**<추가사항>
 * 1. admin: 각 관리자는 로그인용 비번을 가진다. 해당 비밀번호로 로그인을 해야 관리자 업무를 볼 수 있음
 * </추가사항>
 */

public class Main {

    static Connection connection;
    static Scanner keyboard = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("--------Bank Database System--------\n");

        //jdbc driver 연결
        try{
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //계정 로그인
        try{
            String url, id, pw;
            connection = DriverManager.getConnection(url, id, pw);
        }

    }

    public static int menuSelectPosition(){
        System.out.println("--------Select your position--------");
        System.out.println(" 0. Exit");
        System.out.println(" 1. Administrator mode");
        System.out.println(" 2. User mode");
        System.out.println("------------------------------------");
        System.out.println(" Input >> ");

        int inputOption = keyboard.nextInt();
        return inputOption;
    }

    public static int adminMainMenu(){
        System.out.println("------------ADMIN MODE-------------");
        System.out.println("-----------Select action-----------");
        System.out.println(" 0. Return to previous menu");
        System.out.println(" 1. Login(Existing administrator)");
        System.out.println(" 2. Register new administrator");
        System.out.println("------------------------------------");
        System.out.println(" Input >> ");

        int inputOption = keyboard.nextInt();
        return inputOption;
    }

    public static int UserMainMenu(){
        System.out.println("-------------USER MODE-------------");
        System.out.println("-----------Select action-----------");
        System.out.println(" 0. Return to previous menu");
        System.out.println(" 1. Register new user");
        System.out.println(" 2. ");
        System.out.println("------------------------------------");
        System.out.println(" Input >> ");

        int inputOption = keyboard.nextInt();
        return inputOption;
    }

    public static void registerNew(int mode){
        //mode = 0: Admin, mode = 1: User
    }

}
