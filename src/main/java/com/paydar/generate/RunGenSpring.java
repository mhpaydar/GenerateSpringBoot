package com.paydar.generate;

import com.paydar.generate.common.*;
import com.paydar.generate.files.data.FileEntity;
import com.paydar.generate.model.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


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
     * 3 => schema name separate with comma
     * 4 => package name
     * 5 => is readonly?
     * 6=> databaseType
     * 7=> program Name
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("coming soon...");
        try {
            String[] lines = null;
            if (args.length == 0) {
                String rootPath = new File(RunGenSpring.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
                String cfg = new String(Files.readAllBytes(Paths.get(rootPath + Constant.fileSep + "load.txt")), StandardCharsets.UTF_8);
                lines = cfg.split("\\n");

            } else if (args.length == 1) {
                String cfg = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);
                lines = cfg.split("\\n");

            } else {
                lines = new String[args.length];
                lines = args;
            }
            Constant.argDb = lines[0].replace("\r", "");
            Constant.argDir = lines[1].replace("\r", "");
            Constant.argTableName = lines[2].replace("\r", "");
            Constant.argSchemas = lines[3].replace("\r", "");
            Constant.argPackageName = lines[4].replace("\r", "");
            Constant.argReadOnly = lines[5].replace("\r", "");
            Constant.argDBType = lines[6].replace("\r", "");
            Constant.argProgramName = lines[7].replace("\r", "");

            List<String> tableInfoKey = new ArrayList<>();
            List<TableModel> arrTableModels = new ArrayList<>();
            String[] arrSchema = Constant.argSchemas.split(",");
            DTConnection cnn = new DTConnection(Constant.argDBType);

            for (String schema : arrSchema) {
                Connection conn = cnn.getConn(schema, Constant.argDb);
                Statement stmt = conn.createStatement();
                ResultSet res = stmt.executeQuery(cnn.getTableNames(schema, Constant.argTableName));
                while (res.next()) {
                    TableModel tableModel = new TableModel();
                    tableModel.setOwnerName(res.getString("owner"));
                    tableModel.setTableName(res.getString("table_name"));
                    arrTableModels.add(tableModel);
                }
                res.close();
                stmt.close();
                conn.close();
            }
            fetch(cnn, tableInfoKey, arrTableModels);
            List<TableModel> check = check(tableInfoKey);
            while (!check.isEmpty()) {
                fetch(cnn, tableInfoKey, check);
                check.clear();
                check = check(tableInfoKey);
            }
            calc(tableInfoKey);

            /*
             * generate
             */
            String __PKGJAVA = "com." + Constant.argProgramName.toLowerCase() + ".spring.boot.data." + Constant.argPackageName.toLowerCase();// + ";";
            String __PKGJAVAAPI = "com." + Constant.argProgramName.toLowerCase() + ".spring.boot.api." + Constant.argPackageName.toLowerCase();// + ";";
            String __PKGJAVACTL = "com." + Constant.argProgramName.toLowerCase() + ".service.app.controller." + Constant.argPackageName.toLowerCase();// + ";";
            String __PKGJAVASRV = "com." + Constant.argProgramName.toLowerCase() + ".service.services." + Constant.argPackageName.toLowerCase();// + ";";
            for (String key : tableInfoKey) {
                TableModel tableModel = Constant.tableInfo.get(key);
                try {
                    FileEntity.gen(tableModel, __PKGJAVA);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
//                try {
//                    FileRepository.gen(tableModel, __PKGJAVA, argDir);
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//            createProject(argDir, argProgramName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void calc(List<String> tableInfoKey) {
        for (String key : tableInfoKey) {
            TableModel tableModel = Constant.tableInfo.get(key);
            tableModel.calc();
        }
    }

    private static List<TableModel> check(List<String> tableInfoKey) {
        for (String key : tableInfoKey) {
            TableModel tableModel = Constant.tableInfo.get(key);
            if (Constant.tableCheck.containsKey(tableModel.getKey()))
                Constant.tableCheck.replace(tableModel.getKey(), "");
        }
        List<TableModel> arrTableModels = new ArrayList<>();
        Constant.tableCheck.forEach((K, V) -> {
            if (!Constant.tableInfo.containsKey(K)) {
                String[] data = V.split("#");
                TableModel t = new TableModel();
                t.setOwnerName(data[0]);
                t.setTableName(data[1]);
                t.setExtraInfo(true);
                arrTableModels.add(t);
            }
        });
        return arrTableModels;
    }

    private static void fetch(DTConnection cnn, List<String> tableInfoKey, List<TableModel> arrTableModels) throws Exception {
        boolean readOnly = false;
        if (Constant.argReadOnly.equals("1")) {
            readOnly = true;
        }
        for (TableModel t : arrTableModels) {
            Connection conn = cnn.getConn(t.getOwnerName(), Constant.argDb);
            TableModel tableModel = new TableModel();
            tableModel.setOwnerName(t.getOwnerName());
            tableModel.setTableName(t.getTableName());
            tableModel.setExtraInfo(t.isExtraInfo());
            tableModel.setIsReadOnly(readOnly);
            System.out.print("t=" + tableModel.getTableName());
            /*
             * ******* PK *********
             */
            Statement stmt1 = conn.createStatement();
            ResultSet res1 = stmt1.executeQuery(cnn.getPkName(tableModel.getOwnerName(), tableModel.getTableName()));
            ColumnData pkData = new ColumnData();
            if (res1.next()) {
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
             * INDEX
             */
            Statement stmtInx = conn.createStatement();
            ResultSet resInx = stmtInx.executeQuery(cnn.getIndexName(tableModel.getOwnerName(), tableModel.getTableName()));
            while (resInx.next()) {
                IndexData inxD = new IndexData();
                inxD.setName(resInx.getString("index_name").toUpperCase());
                inxD.setNonUnique(resInx.getString("non_unique").equals("1"));
                inxD.setType(resInx.getString("index_type"));
                Statement stmtInxCol = conn.createStatement();
                ResultSet resInxCol = stmtInxCol.executeQuery(cnn.getIndexColName(tableModel.getOwnerName(), tableModel.getTableName(), inxD.getName()));
                StringBuilder inx_col = new StringBuilder();
                while (resInxCol.next()) {
                    inx_col.append(",").append(resInxCol.getString("column_name"));
                }
                resInxCol.close();
                stmtInxCol.close();
                inxD.setData(inx_col.substring(1));
                tableModel.add(inxD);
            }
            resInx.close();
            stmtInx.close();
            if (tableModel.getLstIndexData() != null)
                System.out.print(" index size " + tableModel.getLstIndexData().size());
            else
                System.out.print(" index size 0 ");
            /*
             * ******* FIELDS *********
             */
            Statement stmt2 = conn.createStatement();
            ResultSet res2 = stmt2.executeQuery(cnn.getFieldNames(tableModel.getOwnerName(), tableModel.getTableName(), tableModel.getPkName()));
            while (res2.next()) {
                ColumnData data = new ColumnData();
                data.setColName(res2.getString("column_name"));
                data.setColLen(res2.getInt("len"));
                data.setColScale(res2.getInt("scale"));
                data.setColType(cnn.getDbType(), res2.getString("data_type").toUpperCase());
                data.setColNullable(res2.getString("java_null").equals("1"));
                data.setColTitle(res2.getString("comm"));
                data.setParent(0);
                data.setIsDuplicate(0);
                /*
                 * ***** PARENT FK *************
                 */
                Statement stmt3 = conn.createStatement();
                ResultSet res3 = stmt3.executeQuery(cnn.getFieldForeignNames(tableModel.getOwnerName(), tableModel.getTableName(), data.getColName()));
                String ref = "";
                if (res3.next()) {
                    ref = res3.getString("rowner");
                    data.setParentDbOwner(ref);
                    data.setParentDbTable(res3.getString("rtable"));
                    data.setParentDbColName(res3.getString("rcolname"));
                    data.setFKDbName(res3.getString("fkname"));
                    Constant.tableCheck.putIfAbsent(data.getParentTableKey(), data.getParentDbOwner() + "#" + data.getParentDbTable());
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
            System.out.println(" calc ");
            Constant.tableInfo.put(tableModel.getKey(), tableModel);
            tableInfoKey.add(tableModel.getKey());
            conn.close();
        }

    }
}
