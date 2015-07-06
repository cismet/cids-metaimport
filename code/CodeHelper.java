package code;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CodeHelper extends AssignerBase {

    public static String getClassId(String class_name) {
        ResultSet rs = null;

            try {
                Statement stmnt = FLD$TargetConnection.createStatement();
                rs = stmnt.executeQuery("select id from cs_class where name = '" + class_name + "'");

                if (rs.next()) {
                    return rs.getString("id");
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        //nothing to do
                    }
                }
            }

            return null;
    }

    public static String getType(String type_name) {
        ResultSet rs = null;

            try {
                Statement stmnt = FLD$TargetConnection.createStatement();
                rs = stmnt.executeQuery("select id from cs_type where name = '" + type_name + "'");

                if (rs.next()) {
                    return rs.getString("id");
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        //nothing to do
                    }
                }
            }

            return null;
    }
}