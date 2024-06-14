package com.paydar.generate;

import com.paydar.generate.common.*;
import com.paydar.generate.model.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author m.h paydar
 * @date 6/8/2024 9:12 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public class RunGenSpring {
    /**
     * 0 => database data user|pass|sid
     * 1 => path to save files
     * 2 => table name find like operand
     * 3 => schema name
     * 4 => package name
     * 5 => is readonly?
     * 6=> databaseType
     * 7=> program Name
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("coming soon...");
        try{
            String argDb = "";
            String argDir = "d";
            String argTableName = "";
            String argSchema = "";
            String argPackageName = "";
            String argReadOnly = "0";
            String argDBType = "oracle";
            String argProgramName = "";
            String os = System.getProperty("os.name").toLowerCase();
            String fileSep = System.getProperty("file.separator");
            if (args.length == 0) {
                String rootPath = new File(RunGenSpring.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
                String cfg = new String(Files.readAllBytes(Paths.get(rootPath + fileSep + "load.txt")), StandardCharsets.UTF_8);
                String[] lines = cfg.split("\\n");
                argDb = lines[0].replace("\r", "");
                argDir = lines[1].replace("\r", "");
                argTableName = lines[2].replace("\r", "");
                argSchema = lines[3].replace("\r", "");
                argPackageName = lines[4].replace("\r", "");
                argReadOnly = lines[5].replace("\r", "");
                argDBType = lines[6].replace("\r", "");
                argProgramName = lines[7].replace("\r", "");
            } else if (args.length == 1) {
                String cfg = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);
                String[] lines = cfg.split("\\n");
                argDb = lines[0];
                argDir = lines[1];
                argTableName = lines[2];
                argSchema = lines[3];
                argPackageName = lines[4];
                argReadOnly = lines[5];
                argDBType = lines[6];
                argProgramName = lines[7];
            } else {
                argDb = args[0];
                argDir = args[1];
                argTableName = args[2];
                argSchema = args[3];
                argPackageName = args[4];
                argReadOnly = args[5];
                argDBType = args[6];
                argProgramName = args[7];
            }
            DTConnection cnn = new DTConnection(argDBType);
            Connection conn = cnn.getConn(argSchema, argDb);
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(cnn.getTableNames(argSchema, argTableName));
            boolean readOnly = false;
            if (argReadOnly.equals("1")) {
                readOnly = true;
            }
            while (res.next()) {
                TableModel tableModel = new TableModel();
                tableModel.setOwnerName(res.getString("owner"));
                tableModel.setTableName(res.getString("table_name"));
                tableModel.setIsReadOnly(readOnly);
                System.out.print("t=" + tableModel.getTableName());
                /*
                 * ******* PK *********
                 */
                Statement stmt1 = conn.createStatement();
                ResultSet res1 = stmt1.executeQuery(cnn.getPkName(tableModel.getOwnerName(), tableModel.getTableName()));
                ColumnData pkData = new ColumnData();
                if (res1.next()) {
//                    tableModel.setPkName(res1.getString("column_name"));
//                    tableModel.setPkType(res1.getString("data_type").toUpperCase(), res1.getString("len"), res1.getString("scale"));
//                    tableModel.setPkExtra(res1.getString("extra"));
                    pkData.setColName(res1.getString("column_name"));
                    pkData.setColLen(res1.getInt("len"));
                    pkData.setColScale(res1.getInt("scale"));
                    pkData.setColType(cnn.getDbType(), res1.getString("data_type").toUpperCase());
                    pkData.setColNullable(res1.getString("java_null").equals("1"));
                    pkData.setColTitle(res1.getString("comm"));
                    pkData.setExtraInfo(res1.getString("extra"));
                    pkData.setParent(0);
                    tableModel.setPkColumn(pkData);
                }
                res1.close();
                stmt1.close();
                System.out.print(" pk " + tableModel.getPkColumnName());
                /*
                 * ******* FIELDS *********
                 */
                Statement stmt2 = conn.createStatement();
                ResultSet res2 = stmt2.executeQuery(cnn.getFieldNames(argDBType, tableModel.getOwnerName(), tableModel.getTableName(), tableModel.getPkName()));
                while (res2.next()) {
                    ColumnData data = new ColumnData();
                    data.setColName(res2.getString("column_name"));
                    data.setColLen(res2.getInt("len"));
                    data.setColScale(res2.getInt("scale"));
                    data.setColType(cnn.getDbType(), res2.getString("data_type").toUpperCase());
                    data.setColNullable(res2.getString("java_null").equals("1"));
                    data.setColTitle(res2.getString("comm"));
                    data.setParent(0);
                    /*
                     * ***** PARENT FK *************
                     */
                    Statement stmt3 = conn.createStatement();
                    ResultSet res3 = stmt3.executeQuery(cnn.getFieldForigenNames(tableModel.getOwnerName(), tableModel.getTableName(), data.getColName()));
                    String ref = "";
                    if (res3.next()) {
                        ref = res3.getString("rowner");
                        data.setParentOwner(ref);
                        data.setParentTable(res3.getString("rtable"));
                        data.setParentColName(res3.getString("rcolname"));
                        data.setParentFKName(res3.getString("fkname"));
                        data.setIsDuplicate(0);
                    }
                    res3.close();
                    stmt3.close();
                    /*
                     * *********************************************
                     */
                    if (ref.isEmpty()) {
                        //
                    } else {
                        data.setParent(1);
                        //
                    }
                    tableModel.add(data);
                }
                res2.close();
                stmt2.close();
                tableModel.calc();
                System.out.print(" calc ");
            }
            res.close();
            stmt.close();
            conn.close();

            createProject(argDir, argProgramName);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
