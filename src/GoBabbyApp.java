import java.sql.* ;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Scanner;
import java.util.Date;
import java.lang.*;

public class GoBabbyApp {

    public static void main ( String [ ] args ) throws SQLException
    {

        // Register the driver.  You must register the driver before you can use it.
        try { DriverManager.registerDriver ( new com.ibm.db2.jcc.DB2Driver() ) ; }
        catch (Exception cnfe){ System.out.println("Class not found"); }

        // This is the url you must use for DB2.
        //Note: This url may not valid now ! Check for the correct year and semester and server name.
        String url = "jdbc:db2://winter2022-comp421.cs.mcgill.ca:50000/cs421";

        //REMEMBER to remove your user id and password before submitting your code!!
        String your_userid = null;
        String your_password = null;
        //AS AN ALTERNATIVE, you can just set your password in the shell environment in the Unix (as shown below) and read it from there.
        //$  export SOCSPASSWD=yoursocspasswd
        if(your_userid == null && (your_userid = System.getenv("SOCSUSER")) == null)
        {
            System.err.println("Error!! do not have a password to connect to the database!");
            System.exit(1);
        }
        if(your_password == null && (your_password = System.getenv("SOCSPASSWD")) == null)
        {
            System.err.println("Error!! do not have a password to connect to the database!");
            System.exit(1);
        }
        Connection con = DriverManager.getConnection (url,your_userid,your_password) ;
        Statement statement = con.createStatement ( ) ;
        inputPid(con,statement);
    }


    public static void inputPid(Connection con,Statement statement) throws SQLException {
        Scanner myObj = new Scanner(System.in);


        // Enter username and press Enter
        System.out.println("Please enter your practitioner id [E] to exit:");
        String id = myObj.nextLine();

        //if user inter E the application will end
        if (id. equals("E")){
            statement.close ( ) ;
            con.close ( ) ;
        }
        try{
            //to search if the input exists in midwife table
            String querySQL = "SELECT pid from Midwife WHERE pid = '" + id + "' ORDER BY pid";
            //execute the sql
            java.sql.ResultSet rs = statement.executeQuery (querySQL) ;
            boolean flag=false;
            while (rs.next()){flag=true;}
            if (!flag){
                System.out.println("Invalid  practitioner id! Please try again!");
                inputPid(con,statement);
            }

            //if the input is valid, process to enter the date of appointment
            GoBabbyApp.inputDate(con,statement,id);
        }
        catch (SQLException e)
        {
            //if not exists, error throw, place message and let the user re enter
            System.out.println("Invalid  practitioner id! Please try again!");
            inputPid(con,statement);
        }
    }
    public static void inputDate(Connection con, Statement statement, String id){
        Scanner myObj1 = new Scanner(System.in);
        String date0;
        System.out.println("Please enter  date for appointment list [E] to exit:");
        date0 = myObj1.nextLine();

        //if user inter E the application will end
        try{
            if (date0.equals("E")){
                statement.close ( ) ;
                con.close ( ) ;
            }
            showAppointment(con,statement,id,date0);


        }
        catch (SQLException e)
        {
            //if this date doesn't exsit, let the user re enter a date
            System.out.println("No record for this date, please try another date.");
            GoBabbyApp.inputDate(con,statement,id);
        }
    }
    public static void menu(Connection con, Statement statement, String id, String pregId, String appoimentId,String date0) {
        Scanner mInput = new Scanner(System.in);
        int choice;
        System.out.println("1. Review notes\n" +
                "2. Review tests\n" +
                "3. Add a note\n" +
                "4. Prescribe a test\n" +
                "5. Go back to the appointments.\n" +
                "Enter your choice:");
        choice=mInput.nextInt();

        switch (choice) {
            case 1:
                reviewNotes(con,statement,id,appoimentId,pregId,date0);
                break;
            case 2:
                reviewTests(con,statement,id,pregId,appoimentId,date0);
                break;
            case 3:
                addNotes(con,statement,id,pregId,appoimentId,date0);
                break;
            case 4:
                prescribeTest(con,statement,id,pregId,appoimentId,date0);
                break;
            case 5:
                showAppointment(con,statement,id,date0);
                break;
        }
    }
    public static void reviewNotes(Connection con, Statement statement, String id,String appointmentId, String pregId,String date0)  {
      try{
          String querySQL3 = "SELECT ndate, ntime, a.aid,ntext from Appointment a, notes n, Pregnancy p where p.pregid = \'"+pregId+"\' AND a.aid = n.aid AND p.pregid = a.pregid order by ndate,ntime DESC";
          java.sql.ResultSet rs4 = statement.executeQuery ( querySQL3 ) ;
          while ( rs4.next ( ) )
          {
              String ndate = rs4.getString ("ndate" ) ;
              String ntime = rs4.getString ("ntime" ) ;
              String appoimentId= rs4.getString("aid");
              String ntext=rs4.getString("ntext");
              String output=ndate+" "+ntime+" "+ntext;
              //only 50 char
              String suboutput=output.substring(0, Math.min(output.length(), 50));
              System.out.println(suboutput);
          }
          //"select ndate, ntime from Appointment a, notes n, Pregnancy p where a.aid=\'"+appointmentId+"\' AND p.pregid=\'"+pregId+"\'a.aid=n.aid AND p.pregid=a.pregid order by ndate,ntime DESC;
          menu(con,statement,id,pregId,appointmentId,date0);
      }catch (SQLException e){
          System.out.println("1 error");
      }

    }
    public static void reviewTests(Connection con, Statement statement, String id,String pregId, String appointmentId,String date0)  {
        //test results only from mothers
        // select presday,mtype,result from medicaltest where pregid = pregid;
        // if result is null display PENDING instead
        try {
            String querySQL5 = " select presday,mtype,result from medicaltest where pregid = \'"+pregId+"\'";
            java.sql.ResultSet rs5 = statement.executeQuery ( querySQL5 ) ;

            while ( rs5.next ( ) )
            {
                String presday = rs5.getString ("presday" ) ;
                String mtype = rs5.getString ("mtype" ) ;
                String result = rs5.getString ("result" ) ;
                if(!(mtype == null)){
                    if(!(result == null )){
                        String output=presday+" ["+mtype+"]  "+result;
                        //only 50 char
                        String suboutput=output.substring(0, Math.min(output.length(), 50));
                        System.out.println(suboutput);
                    } else  {
                        String output1=presday+" ["+mtype+"]  PENDING";
                        //only 50 char
                        String suboutput1=output1.substring(0, Math.min(output1.length(), 50));
                        System.out.println(suboutput1);
                    }
                }


            }
            menu(con,statement,id,pregId,appointmentId,date0);
        }catch (SQLException e){
            System.out.println("2 error");
        }


    }
    public static void addNotes(Connection con, Statement statement, String id,String pregId,String appointmentId,String date0){
        try{
            //('n100005','2022-03-24','10:29:47','a100005','hello4')
            //(nid,cdate,time,appointmentId,text)
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatter1 = new SimpleDateFormat("hh:mm:ss");
            Date date = new Date();


            String nid="n"+getRandomNumberString();
            String cdate=formatter.format(date);
            String time=formatter1.format(date);

            Scanner aInput = new Scanner(System.in);
            String text;
            System.out.println("Please type your observation:");
            text=aInput.nextLine();
            String insertSQL = "INSERT INTO Notes VALUES ( \'"+nid+"\' ,\'"+cdate+"\',\'"+time+"\',\'"+appointmentId+"\',\'"+text+"\')";
            statement.executeUpdate(insertSQL);
            System.out.println("add successful!");
            menu(con,statement,id,pregId,appointmentId,date0);
        }catch (SQLException e)
        {
            System.out.println("3 error");
        }


    }
    public static void prescribeTest(Connection con, Statement statement, String id,String pregId,String appointmentId,String date0){
        try{
            //('t100001','blood',NULL,NULL,'2022-02-09','2022-02-08','p100011','pg100001',NULL,NULL),
            //(tid,ptext,NULL,NULL,pdate,pdate,id,pregId,NULL,NULL)
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();


            String tid="t"+getRandomNumberString();
            String pdate=formatter.format(date);


            Scanner aInput = new Scanner(System.in);
            String ptext;
            System.out.println("Please enter the type of test:");
            ptext=aInput.nextLine();
            String insertSQL1 = "INSERT INTO MedicalTest VALUES ( \'"+tid+"\' ,\'"+ptext+"\',NULL,NULL,\'"+pdate+"\',\'"+pdate+"\',\'"+id+"\',\'"+pregId+"\',NULL,NULL)";

            statement.executeUpdate(insertSQL1);
            System.out.println("add successful!");
            menu(con,statement,id,pregId,appointmentId,date0);
        }catch (SQLException e)
        {
            System.out.println("4 error");
        }

    }
    public static void showAppointment(Connection con, Statement statement, String id,String date0){
        try{
            String querySQL1 = "SELECT atime, ppid,bpid,mname, p.mhid from Appointment a, Pregnancy p,Mother m WHERE a.pid = \'"+ id +"\'AND adate = \'"+date0+"\' AND a. pregid=p. pregid AND m.mhid=p.mhid ORDER BY atime ASC";
            java.sql.ResultSet rs1 = statement.executeQuery ( querySQL1 ) ;
            int count=1;
            while ( rs1.next ( ) )
            {
                String date1 = rs1.getString ("atime" ) ;
                String isP = rs1.getString ("ppid" ) ;
                String isB = rs1.getString ("bpid" ) ;
                String motherName = rs1.getString ("mname" ) ;
                String motherId = rs1.getString ("mhid");
                if(!(isP == null)){
                    System.out.println(count+" "+date1+" P "+motherName+" "+motherId);
                } else if (!(isB == null)) {
                    System.out.println(count+" "+date1+" B "+motherName+" "+motherId);
                }
                count++;
            }
            Scanner myObj2 = new Scanner(System.in);
            String am2;
            System.out.println("Enter the appointment number that you would like to work on.\n" +
                    "[E] to exit [D] to go back to another date :");
            am2 = myObj2.nextLine();


            if (am2.equals("E")){
                statement.close ( ) ;
                con.close ( ) ;
            }

            if (am2.equals("D")){
                inputDate(con,statement,id);
            }

            String querySQL2 ="SELECT * from( SELECT row_number() over (order by atime) as row_num, atime, ppid,bpid,mname, p.mhid,p.pregid,aid from Appointment a, Pregnancy p,Mother m WHERE ppid = \'"+id+"\' AND adate = \'"+date0+"\' AND a.pregid=p.pregid AND m.mhid=p.mhid order by atime asc) t where row_num = \'"+am2+"\'";
            java.sql.ResultSet rs2 = statement.executeQuery ( querySQL2 ) ;
            while ( rs2.next ( ) )
            {
                String date3 = rs2.getString ("atime" ) ;
                String motherName1 = rs2.getString ("mname" ) ;
                String motherId1 = rs2.getString ("mhid");
                String pregId = rs2.getString ("pregid" ) ;
                String appoimentId = rs2.getString ("aid" ) ;
                System.out.println("For "+motherName1+" "+motherId1);
                GoBabbyApp.menu(con, statement, id, pregId, appoimentId,date0);
            }
        }catch (SQLException e){
            System.out.println("");
        }
    }
    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

}
