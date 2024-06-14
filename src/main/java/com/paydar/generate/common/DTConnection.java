package com.paydar.generate.common;

import com.paydar.generate.enums.DbType;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author m.h paydar
 * @date 6/14/2024 9:40 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public class DTConnection {
    private final DbType dbType;

    public DTConnection(String dbType) {
        this.dbType = DbType.of(dbType);
    }

    public Connection getConn(String data) {
        String[] arrData = data.split("\\|");
        if (dbType.equals(DbType.ORACLE)) {
            return getConnOracle(arrData);
        } else if (dbType.equals(DbType.MYSQL)) {
            return getConnMySql("", arrData);
        } else {
            return null;
        }
    }

    public Connection getConn(String schemaName, String data) {
        String[] arrData = data.split("\\|");
        if (dbType.equals(DbType.ORACLE)) {
            return getConnOracle(arrData);
        } else if (dbType.equals(DbType.MYSQL)) {
            return getConnMySql(schemaName, arrData);
        } else {
            return null;
        }
    }

    private Connection getConnOracle(String... data) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:" + data[2], data[0], data[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Connection getConnMySql(String schemaName, String... data) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + schemaName, data[0], data[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String getTableNames(String schemaName, String tableName) {
        if (dbType.equals(DbType.ORACLE)) {
            return "select t.* from all_all_tables t where t.owner = upper('" + schemaName.toUpperCase() + "') and t.table_name like '" + tableName.toUpperCase() + "%' order by t.table_name";
        } else if (dbType.equals(DbType.MYSQL)) {
            return "select upper(t.table_schema) as owner,upper(t.table_name) as table_name from INFORMATION_SCHEMA.tables t where upper(t.table_schema) = '" + schemaName.toUpperCase() + "' and upper(t.table_name) like '" + tableName.toUpperCase() + "%'  order by t.table_name";
        } else {
            return null;
        }
    }

    public String getPkName(String schemaName, String tableName) {
        if (dbType.equals(DbType.ORACLE)) {
            return    "select tc.COLUMN_ID,tc.COLUMN_NAME,tc.DATA_TYPE,nvl(tc.DATA_PRECISION, tc.DATA_LENGTH) as len,tc.NULLABLE,tc.DATA_DEFAULT,tc.TABLE_NAME,"
                    + "       (select cc.COMMENTS from all_col_comments cc where cc.OWNER = tc.owner and cc.TABLE_NAME = tc.TABLE_NAME and cc.COLUMN_NAME = tc.COLUMN_NAME) as comm,"
                    + "       'yy' as java_type,decode(tc.nullable, 'N', 0, 1) as java_null,nvl(tc.DATA_SCALE, 0) as scale,'' as extra "
                    + "from all_tab_cols tc "
                    + "where   tc.owner = '" + schemaName.toUpperCase() + "' and tc.table_name = '" + tableName.toUpperCase() + "' and tc.COLUMN_NAME in "
                    + "        (select ic.COLUMN_NAME from all_constraints c,all_indexes i,all_ind_columns ic where c.OWNER = '" + schemaName.toUpperCase() + "' and i.TABLE_NAME = '" + tableName.toUpperCase() + "' and c.CONSTRAINT_TYPE = 'P' and c.INDEX_OWNER = i.OWNER and c.INDEX_NAME = i.INDEX_NAME and i.OWNER = ic.INDEX_OWNER and i.INDEX_NAME = ic.INDEX_NAME)"
                    +"         and tc.HIDDEN_COLUMN='NO' and tc.VIRTUAL_COLUMN='NO'";
        } else if (dbType.equals(DbType.MYSQL)) {
            return "select 0 as COLUMN_ID,COLUMN_NAME,DATA_TYPE,coalesce(NUMERIC_PRECISION,character_maximum_length) as len,is_nullable as NULLABLE,column_DEFAULT as DATA_DEFAULT,TABLE_NAME,column_comment as comm,'yy' as java_type , if(is_nullable='NO','0','1') as java_null,coalesce(t.numeric_scale, 0) as scale,t.extra "
                    + "from INFORMATION_SCHEMA.COLUMNS t where upper(table_schema)='" + schemaName.toUpperCase() + "' and upper(table_name)='" + tableName.toUpperCase() + "' and column_key='PRI'";
        } else {
            return null;
        }
    }
}
