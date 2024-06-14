package com.paydar.generate.model;

import com.paydar.generate.common.Constant;
import com.paydar.generate.common.Utils;
import com.paydar.generate.enums.ColumnDBType;
import com.paydar.generate.enums.DbType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author m.h paydar
 * @date 6/14/2024 4:47 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public class TableModel  implements Serializable {
    private DbType dbType;
    private String ownerName;
    private String tableName;
    private String clazzName;
    private String tableColName;
    private String pkName;
    private String pkType;
    private String pkExtra;
    private String pkComment;
    private String pkColumnName;
    private String pkColumnType;
    private String pkColumnNameNet;
    private String sequenceType;
    private String sequenceName;
    private List<ColumnData> lstColumnData;
    private boolean isUserLog;
    private boolean isReadOnly;

    private List<IndexData> lstIndexData;

    public void calc() {
        this.isUserLog = false;
        if (lstColumnData != null) {
            for (ColumnData colData : lstColumnData) {
                if (colData.getColName().equalsIgnoreCase(Constant.USER_REG_FIELD)) {
                    this.isUserLog = true;
                    //this.isReadOnly=false;
                    break;
                }
            }
            for (int m = 0; m < lstColumnData.size(); m++) {
                if (lstColumnData.get(m).getParent() > 0) {
                    String a = Utils.getColName(lstColumnData.get(m).getParentTable().replaceAll("TBL_", ""));
                    for (int n = m + 1; n < lstColumnData.size(); n++) {
                        if (lstColumnData.get(n).getParent() > 0) {
                            if (Utils.getColName(lstColumnData.get(n).getParentTable().replaceAll("TBL_", "")).equals(a)) {
                                lstColumnData.get(n).setIsDuplicate(1);
                                lstColumnData.get(m).setIsDuplicate(1);
                            }
                        }
                    }
                }
            }
            if (this.lstIndexData != null) {
                for (ColumnData colData : lstColumnData) {
                    for (IndexData inxd : this.lstIndexData) {
                        if (!inxd.getNonUnique()) {
                            if (inxd.getData().toUpperCase().equals(colData.getColName().toUpperCase())) {
                                colData.setColUnique("1");
                                inxd.addCols(colData);
                            }
                        }
                    }
                }
                for (IndexData inxd : this.lstIndexData) {
                    if (inxd.getNonUnique().equals("0")) {
                        if (inxd.getData().indexOf(",")>0){
                            String[] split = inxd.getData().split(",");
                            for (String s:split) {
                                for (ColumnDBType colData : lstColumnData) {
                                    if (s.toUpperCase().equals(colData.getColName().toUpperCase())) {
                                        inxd.addCols(colData);
                                    }
                                }
                            }
                        }
                    }
                    inxd.calcSearching();
                }
            }
        }

        if (this.pkExtra != null) {
            if (this.pkExtra.toLowerCase().indexOf("increment") > 0) {
                this.sequenceType = "IDENTITY";
                this.sequenceName = "";
            } else {
                this.sequenceType = "SEQUENCE";
                this.sequenceName = getTableName().replaceAll("TBL", "SEQ");
            }
        } else {
            this.sequenceType = "SEQUENCE";
            this.sequenceName = getTableName().replaceAll("TBL", "SEQ");
        }

    }

    public void add(ColumnData c) {
        if (this.lstColumnData == null) {
            this.lstColumnData = new ArrayList<>();
        }
        this.lstColumnData.add(c);
    }

    public void add(IndexData i) {
        if (this.lstIndexData == null) {
            this.lstIndexData = new ArrayList<>();
        }
        this.lstIndexData.add(i);
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
        this.clazzName = Utils.getClassName(tableName);
        this.tableColName = Utils.getColName(tableName.replaceAll("TBL_", ""));
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public String getPkName() {
        return pkName;
    }

    public void setPkName(String pkName) {
        this.pkName = pkName;
        this.pkColumnName = Utils.getColName(pkName);
        this.pkColumnNameNet = Utils.getColName_NET(pkName);
    }

    public String getPkComment() {
        return pkComment;
    }

    public void setPkComment(String pkComment) {
        this.pkComment = pkComment;
    }

    public String getPkColumnName() {
        return pkColumnName;
    }

    public void setPkColumnName(String pkColumnName) {
        this.pkColumnName = pkColumnName;
    }

    public String getPkColumnType() {
        return pkColumnType;
    }

    public void setPkColumnType(String pkColumnType) {
        this.pkColumnType = pkColumnType;
    }

    public String getPkColumnNameNet() {
        return pkColumnNameNet;
    }

    public void setPkColumnNameNet(String pkColumnNameNet) {
        this.pkColumnNameNet = pkColumnNameNet;
    }

    public String getSequenceType() {
        return sequenceType;
    }

    public void setSequenceType(String pkSequenceType) {
        this.sequenceType = pkSequenceType;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String pkSequenceName) {
        this.sequenceName = pkSequenceName;
    }

    public List<ColumnData> getLstColDatas() {
        return lstColumnData;
    }

    public void setLstColDatas(List<ColumnData> lstColDatas) {
        this.lstColumnData = lstColDatas;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public String getPkType() {
        return pkType;
    }

    public void setPkType(String pkType, String len, String scale) {
        this.pkType = pkType.toUpperCase();
        int l = Integer.parseInt(len);
        int s = Integer.parseInt(scale);
        if (this.dbType.equals(DbType.ORACLE)) {
            if (pkType.equals("NUMBER")) {
                if (l == 1) {
                    this.pkColumnType = "Boolean";
                } else if (l > 1 && l <= 9) {
                    if (s == 0) {
                        this.pkColumnType = "Integer";
                    } else {
                        this.pkColumnType = "Double";
                    }
                } else if (l > 9 && l <= 18) {
                    if (s == 0) {
                        this.pkColumnType = "Long";
                    } else {
                        this.pkColumnType = "Double";
                    }
                } else if (l > 19) {
                    if (s == 0) {
                        this.pkColumnType = "Long";
                    } else {
                        this.pkColumnType = "Double";
                    }
                }
            } else if (pkType.equals("INTEGER")) {
                this.pkColumnType = "Integer";
            } else if (pkType.equals("VARCHAR2")) {
                this.pkColumnType = "String";
            } else if (pkType.equals("NVARCHAR2")) {
                this.pkColumnType = "String";
            } else if (pkType.equals("DATE")) {
                this.pkColumnType = "LocalDateTime";
            } else if (pkType.equals("CHAR")) {
                this.pkColumnType = "String";
            } else if (pkType.equals("FLOAT")) {
                this.pkColumnType = "xx";
            } else if (pkType.equals("TIMESTAMP")) {
                this.pkColumnType = "LocalDateTime";
            } else {
                this.pkColumnType = "yy";
            }
        } else if (this.dbType.equals("mysql")) {
            if (pkType.equals("DECIMAL")) {
                if (l == 1) {
                    this.pkColumnType = "Boolean";
                } else if (l > 1 && l <= 9) {
                    if (s == 0) {
                        this.pkColumnType = "Integer";
                    } else {
                        this.pkColumnType = "Double";
                    }
                } else if (l > 9 && l <= 20) {
                    if (s == 0) {
                        this.pkColumnType = "Long";
                    } else {
                        this.pkColumnType = "Double";
                    }
                } else if (l > 20) {
                    if (s == 0) {
                        this.pkColumnType = "Long";
                    } else {
                        this.pkColumnType = "Double";
                    }
                }
            } else if (pkType.equals("INT")) {
                this.pkColumnType = "Long";
            } else if (pkType.equals("BIGINT")) {
                this.pkColumnType = "Long";
            } else if (pkType.equals("VARCHAR")) {
                this.pkColumnType = "String";
            } else if (pkType.equals("SMALLINT")) {
                if (l == 1) {
                    this.pkColumnType = "Boolean";
                } else {
                    this.pkColumnType = "Integer";
                }
            } else if (pkType.equals("DATE")) {
                this.pkColumnType = "LocalDateTime";
            } else if (pkType.equals("DATETIME")) {
                this.pkColumnType = "LocalDateTime";
            } else {
                this.pkColumnType = "yy";
            }
        }
    }

    public boolean isIsUserLog() {
        return isUserLog;
    }

    public void setIsUserLog(boolean isUserLog) {
        this.isUserLog = isUserLog;
    }

    public String getPkExtra() {
        return pkExtra;
    }

    public void setPkExtra(String pkExtra) {
        this.pkExtra = pkExtra;
    }

    public boolean isIsReadOnly() {
        return isReadOnly;
    }

    public void setIsReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public String getTableColName() {
        return tableColName;
    }

    public void setTableColName(String tableColName) {
        this.tableColName = tableColName;
    }

    public List<IndexData> getLstIndexData() {
        return lstIndexData;
    }

    public void setLstIndexData(List<IndexData> lstIndexDatas) {
        this.lstIndexData = lstIndexDatas;
    }
}
