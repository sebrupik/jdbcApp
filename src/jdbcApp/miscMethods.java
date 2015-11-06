package jdbcApp;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.HashMap;
import java.sql.*;

import java.util.ArrayList;

public class miscMethods {
    static SimpleDateFormat timeForm = new SimpleDateFormat("HHmm");

    public static String convertDateToText(java.sql.Date date, String format, String nullDate) {
        System.out.println("convertDateToText date is : "+date);
        if(convertDateToText(date, format).equals(nullDate)) {
            return format;
        } else {
            return convertDateToText(date, format);
        }
    }

    public static String convertDateToText(java.sql.Date date, String format) {
        System.out.println("miscMethods/convertDate - converting: "+date+" using format: "+format);
        if(date==null) {
            return format;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format( new java.util.Date(date.getTime()) );
        }
    }

    public static java.sql.Date convertTextToDate(String date, String dateFormat, String dateNull) throws java.lang.NumberFormatException, java.text.ParseException {
        //System.out.println("miscMethods/convertTextToDate - trying to convert: "+date+" using this format: "+dateFormat);
        SimpleDateFormat pattern = new SimpleDateFormat(dateFormat);
        if (!date.toUpperCase().equals(pattern.toPattern().toUpperCase()) & !date.equals("")) {
            //java.util.Date d = pattern.parse(date);
            return new java.sql.Date(pattern.parse(date).getTime());
        } else {
            //System.out.println("miscMethods/convertTextToDate - trying to return a null value");
            //return new java.sql.Date( pattern.parse(dateNull).getTime() );
            return null;
        }
    }

    public static java.sql.Time convertStringToTime(String strTime) throws java.text.ParseException {
        if (!strTime.toUpperCase().equals(timeForm.toPattern().toUpperCase()) & !strTime.equals("")) {
            java.util.Date d = timeForm.parse(strTime);
            return new java.sql.Time(d.getTime());
        } else {
            return null;
        }
    }

    public static String convertTimeToString(Time time) {
        //String t = "";
        String[] segs =  time.toString().split(":");
        //t = segs[0]+segs[1];

        return segs[0]+segs[1];
    }

    public static int convertYNtoBool(String str) {
        System.out.println("convertYNtoBool - val is : "+str);
        if (str.toUpperCase().equals("YES"))
            return 1;
        else if(str.toUpperCase().equals("NO"))
            return 0;
        else
            return -1;
    }

    public static String convertBooltoYN(boolean val) {
        System.out.println("convertBooltoYN - val is : "+val);

        if (val ==true)
            return "Yes";
        else if(val == false)
            return "No";
        else
            return "Please Select";
    }

    public static int convertTFtoBool(boolean b) {
        if(b==true)
            return 1;
        else
            return 0;
    }

    public static boolean convertBooltoTF(int val) {
        return val==1;
    }

    /**
     *
     */
    public static int timeMinutesDifference(Time time1, Time time2, boolean workWeek) {
        int minutes = 0;

        try {
            java.util.Date d = timeForm.parse("0900");
            Time earliest = new java.sql.Time(d.getTime());
            d = timeForm.parse("1700");
            Time latest = new java.sql.Time(d.getTime());

            if(time1 != null & time2 != null) {
                long full1 = time1.getTime();
                long full2 = time2.getTime();
                if(workWeek) {
                    if(full1 < earliest.getTime())
                        full1 = earliest.getTime();
                    if(full2 > latest.getTime())
                        full2 = latest.getTime();
                }
                minutes = (int) ((full2-full1)/1000)/60;
                if(minutes < 0)
                    minutes += 1440;
            } else { System.out.println("miscMethods/timeMinutesDifference - null values have been passed as parameters, unable to calculate the time difference.");
            }
        } catch(java.text.ParseException pe) { System.out.println("miscMethods/timeMinutesDifference - "+pe); }
        return minutes;
    }

    /**
     *
     */
    public static int dateMinutesDifference(Date date1, Date date2, boolean workWeek) {
        GregorianCalendar sDate = new GregorianCalendar();  sDate.setTime(date1);
        GregorianCalendar eDate = new GregorianCalendar();  eDate.setTime(date2);

        int minutes = 0;
        int days = 0;
        long full1 = sDate.getTimeInMillis();
        long full2 = eDate.getTimeInMillis();

        if(workWeek) {
            while(full1 != full2) {
                sDate.add(GregorianCalendar.DAY_OF_YEAR, 1);
                full1 = sDate.getTimeInMillis();
                days++;
            }
            System.out.println("miscMethods/dateMinutesDifference - work days : "+days);
            minutes = days * (8*60);
        } else {
            minutes = (int) ((full2-full1)/1000)/60;
        }

        System.out.println("miscMethods/dateMinutesDifference - "+minutes);
        return minutes;
    }

    /**
     *
     * If workWeek true then the only time between 0900 and 1700 mon-fri is counted
     * @param startDate
     * @param startTime
     * @param endDate
     * @param endTime
     * @param workWeek
     * @return 
     */
    public static int calcDuration(java.sql.Date startDate, java.sql.Time startTime, java.sql.Date endDate, java.sql.Time endTime, boolean workWeek) {
        int minutes = 0;

        int dateMinutes = dateMinutesDifference(startDate, endDate, workWeek);
        int timeMinutes = timeMinutesDifference(startTime, endTime, workWeek);

        if(dateMinutes < 0) {
            //the end date must be before the start date
            minutes = timeMinutes;
        } else {
            minutes = dateMinutes + timeMinutes;
        }

        return minutes;
    }

    /**
     * Given a date the date for the beginning of the week for the supplied
     * date is returned
     *
     * @param       date to be calculated
     * @return      first day of the week (sunday) of the supplied date
     */
    public static GregorianCalendar calcWeekStart(String strDate, String dFormat, String nDate) throws java.text.ParseException {
        GregorianCalendar actual = stringToGregCal(strDate, dFormat, nDate);
        actual.add(java.util.Calendar.DAY_OF_YEAR, 1-(actual.get(java.util.Calendar.DAY_OF_WEEK)));
        return actual;
    }

    /**
     * Given a valid date, the date for the begining of the week within which
     * it is, is returned.
     *
     * @param       Date to be used for calculation
     * @return      Date for the beginning of the week
     */
    public static int calcDayOfWeek(String strDate, String dFormat, String nDate) throws java.text.ParseException {
        GregorianCalendar actual = stringToGregCal(strDate, dFormat, nDate);
        return actual.get(java.util.Calendar.DAY_OF_WEEK);
    }

    /**
     * Converts a String date into a GregorianCalendar object of the same value
     *
     * @param       Date to be converted
     * @return      The GregorianCalendar representation of the supplied date
     */
    public static GregorianCalendar stringToGregCal(String strDate, String dFormat, String nDate) throws java.text.ParseException {
        GregorianCalendar actual = new GregorianCalendar(new java.util.Locale("ENGLISH", "UK"));
        actual.setTime(convertTextToDate(strDate, dFormat, nDate));

        return actual;
    }

    /**
     * Given a date (YYYY-MM-DD)
     *
     * @param       A date of birth
     * @return      The number of elapsed years since the specified date
     */
    public static int calcAge(String dob, GregorianCalendar currentDate, String pat) {
        //GregorianCalendar currentDate = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(pat);

        java.util.Date date = null;
        int approximateAge = -1;
        int actualAge = -1;

        try {
            date = sdf.parse(dob);

            GregorianCalendar birthDate = new GregorianCalendar();
            birthDate.setTime(date);
            GregorianCalendar testDate = new GregorianCalendar();
            testDate.setTime(date);
            approximateAge = currentDate.get(java.util.Calendar.YEAR) - birthDate.get(java.util.Calendar.YEAR);
            testDate.add(java.util.Calendar.YEAR, approximateAge);
            actualAge = (testDate.after(currentDate)) ? approximateAge - 1 : approximateAge;
            if(actualAge < 0)
                actualAge += 100;
            System.out.println("Current date "+sdf.format(currentDate.getTime()));
            System.out.println("Birth date "+sdf.format(birthDate.getTime()));
            System.out.println("Age = "+actualAge);
        } catch (Exception pe) {
            System.out.println("miscMethods/calcAge - Bad date "+pe);
        }

        return actualAge;
    }
  
    

    /**
     * @param       String array to be searched
     * @param       String to be searched for
     * @return      whether the specified String has been found in the supplied array
     */
     public static boolean arrayContains(String[] arr, String item) {
        //System.out.println("arr length is "+arr.length+" & item is ."+item+".");
        for (int i=0; i<arr.length;i++) {
            //System.out.println(arr[i]+" : "+item);
            if (arr[i].equals(item)) {
                //System.out.println(arr[i]+" : "+item);
                return true;
            }
        }
        return false;
    }

    /**
     * Add the first column in the supplied ResultSet to the supplied JCombobox
     * This method was created to replace the bulky 3 lines of code previously used
     * for each JCombobox, with a now more streamlined one line call to this method
     *
     * @return     JCombobox filled with items from the ResultSet
     * @date       2005-05-05
     */
    public static javax.swing.JComboBox<String> addColumnToComboBox(javax.swing.JComboBox<String> target, ResultSet res) {
        return addColumnToComboBox(target, res, 1);
    }

    public static javax.swing.JComboBox<String> addColumnToComboBox(javax.swing.JComboBox<String> target, ResultSet res, int column) {
        try {
            while(res.next()) {
                target.addItem(res.getObject(column).toString());
            }
        } catch(SQLException sqle) { System.out.println("miscMethods/addColumnToComboBox - "+sqle); }
        return target;
    }

    public static Integer[] colToArrayInt(ResultSet r, int col) {
        Integer[] ar;
        int rows;
        try {
            r.last();
            rows = r.getRow();
            r.beforeFirst();
            ar = new Integer[rows];
            while(r.next()) {
                ar[r.getRow()-1] = (Integer)r.getObject(col);
            }
            return ar;
        } catch (SQLException sqle) {
            System.out.println("miscMethods/colToArrayInt - ");
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("VendorError: " + sqle.getErrorCode());
        }
        return null;
    }

    public static String[] colToArray(ResultSet r, int col) {
        String[] ar;
        int rows;
        try {
            r.last();
            rows = r.getRow();
            r.beforeFirst();
            ar = new String[rows];
            while(r.next()) {
                ar[r.getRow()-1] = (String)r.getObject(col);
            }
            return ar;
        } catch (SQLException sqle) {
            System.out.println("miscMethods/colToArray - ");
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("VendorError: " + sqle.getErrorCode());
        }

        return null;
    }
    public static java.util.ArrayList colToArrayList(ResultSet r, int col) {
        ArrayList<Object> ar = new ArrayList<Object>();
        try {
            while(r.next())
                ar.add(r.getObject(col));
        } catch (SQLException sqle) {
            System.out.println("miscMethods/colToArray - ");
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("VendorError: " + sqle.getErrorCode());
        }
        return ar;
    }

    public static java.sql.Date[] colToArrayDate(ResultSet r, int col) {
        java.sql.Date[] dates;
        int rows;
        try {
            r.last();
            rows = r.getRow();
            r.beforeFirst();
            dates = new java.sql.Date[rows];
            while(r.next()) {
                dates[r.getRow()-1] = (java.sql.Date)r.getDate(col);
            }
            return dates;
        } catch (SQLException sqle) {
            System.out.println("miscMethods/colToArrayDate - ");
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("VendorError: " + sqle.getErrorCode());
        }

        return null;
    }
    
    public static Object[] rsToArray(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        rs.last();
        int rc = rs.getRow();
        Object[][] obj = new Object[rsmd.getColumnCount()][rc];
        rs.beforeFirst();
        
        int curRow = 1;
        while(rs.next()) {
            for(int i=0; i<rc; i++) {
                obj[curRow][i] = rs.getObject(i);
                curRow++;
            }
        }
        
        return obj;
    }
    
    public static HashMap rsToHashmap(ResultSet rs) throws SQLException {
        HashMap<String, Object[]> hm = new HashMap<String, Object[]>();
        ResultSetMetaData rsmd = rs.getMetaData();
        rs.last();
        int rc = rs.getRow();
        rs.beforeFirst();
        
        String colName;
        Object[] obj;
        int curRow = 1;
        while(rs.next()) {
            for(int i=0; i<rsmd.getColumnCount(); i++) {
                colName = rsmd.getColumnName(i+1);
                System.out.println(colName);
                if(hm.containsKey(colName)) {
                    System.out.println("ADDING TO: "+colName);
                    obj = (Object[])hm.get(colName);
                    obj[curRow] = rs.getObject(i+1);
                    hm.put(colName, obj);
                } else {
                    System.out.println("NEW: "+colName);
                    obj = new Object[rc+1];
                    obj[0] = colName;
                    obj[1] = rs.getObject(i+1);
                    hm.put(colName, obj);
                }
                curRow++;
            }
        }
       
        return hm;
    }  
}