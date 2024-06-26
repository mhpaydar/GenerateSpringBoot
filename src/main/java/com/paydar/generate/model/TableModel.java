package com.paydar.generate.model;

import com.paydar.generate.common.Constant;
import com.paydar.generate.common.Utils;
import com.paydar.generate.enums.ColumnDBType;
import com.paydar.generate.enums.DbType;
import com.paydar.generate.enums.JavaType;
import com.paydar.generate.enums.SequenceType;

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
public class TableModel implements Serializable {
    //    private DbType dbType;
    private String ownerName;
    private String tableName;
    private String clazzName;
    private String tableColName;
    private String tableColNameNet;
    private ColumnData pkColumn;
    //    private String pkName;
//    private JavaType pkType;
//    private String pkExtra;
//    private String pkComment;
//    private String pkColumnName;
//    private ColumnDBType pkColumnType;
//    private String pkColumnNameNet;
    private SequenceType sequenceType;
    private String sequenceName;
    private boolean isExtraInfo;
    private boolean isUserLog;
    private boolean isReadOnly;
    private String extendsClass;
    private List<ColumnData> lstColumnData;
    private List<IndexData> lstIndexData;

    public String getKey() {
        return getOwnerName() + getTableColName();
    }

    public void calc() {
        this.isUserLog = false;
        if (lstColumnData != null) {
            /*
             * find user log table
             */
            this.extendsClass = "AbstractNonEntity";
            for (ColumnData colData : lstColumnData) {
                if (colData.getColName().equalsIgnoreCase(Constant.USER_REG_FIELD)) {
                    this.isUserLog = true;
                    this.extendsClass = Constant.AUDIT_ENTITY;
                    break;
                }
            }
            /*
             * find tree table
             */
            for (int m = 0; m < lstColumnData.size(); m++) {
                if (lstColumnData.get(m).getParent() > 0) {
                    if (!Constant.userFields.contains(lstColumnData.get(m).getColName().toLowerCase())) {
                        String a = Utils.getColName(lstColumnData.get(m).getParentDbTable().replaceAll(Constant.REPLACE_TABLE_PATTERN_START, ""));
                        for (int n = m + 1; n < lstColumnData.size(); n++) {
                            if (lstColumnData.get(n).getParent() > 0) {
                                if (!Constant.userFields.contains(lstColumnData.get(n).getColName().toLowerCase()))
                                    if (Utils.getColName(lstColumnData.get(n).getParentDbTable().replaceAll(Constant.REPLACE_TABLE_PATTERN_START, "")).equals(a)) {
                                        lstColumnData.get(n).setIsDuplicate(1);
                                        lstColumnData.get(m).setIsDuplicate(1);
                                    }
                            }
                        }
                    }
                }
            }
            if (this.lstIndexData != null) {
                /*
                 * find unique column constraint
                 */
                for (ColumnData colData : lstColumnData) {
                    for (IndexData inxd : this.lstIndexData) {
                        if (!inxd.getNonUnique()) {
                            if (inxd.getData().equalsIgnoreCase(colData.getColName())) {
                                colData.setColUnique(true);
                                inxd.addCols(colData);
                            }
                        }
                    }
                }
                /*
                 * find index simple columns
                 */
                for (IndexData inxd : this.lstIndexData) {
//                    if (inxd.getNonUnique()) {
                    if (inxd.getData().indexOf(",") > 0) {
                        String[] split = inxd.getData().split(",");
                        for (String s : split) {
                            for (ColumnData colData : lstColumnData) {
                                if (s.equalsIgnoreCase(colData.getColName())) {
                                    inxd.addCols(colData);
                                }
                            }
                        }
                    }
//                    }
                    inxd.calcSearching();
                }
            }
        }
        if (this.pkColumn != null) {
            if (this.pkColumn.getExtraInfo() != null) {
                if (this.pkColumn.getExtraInfo().toLowerCase().indexOf("increment") > 0) {
                    this.sequenceType = SequenceType.IDENTITY;
                    this.sequenceName = "";
                } else {
                    this.sequenceType = SequenceType.SEQUENCE;
                    this.sequenceName = getTableName().replaceAll("TBL", "SEQ");
                }
            } else {
                this.sequenceType = SequenceType.SEQUENCE;
                this.sequenceName = getTableName().replaceAll("TBL", "SEQ");
            }
        } else {
            ColumnData temp = null;
            if (lstColumnData != null)
                for (ColumnData colData : lstColumnData) {
                    if (colData.getColUnique()) {
                        if (colData.getColJavaType().equals(JavaType.LONG) || colData.getColJavaType().equals(JavaType.INTEGER) || colData.getColJavaType().equals(JavaType.BIG_DECIMAL)) {
                            temp = colData;
                            break;
                        } else {
                            if (temp != null)
                                temp = colData;
                        }
                    }
                }
            if (temp != null) {
                this.sequenceType = SequenceType.OTHERS;
                this.sequenceName = "";
                temp.setVirtualPk(true);
                this.pkColumn = temp;
            }
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
        this.isExtraInfo=false;
        this.tableName = tableName;
        this.clazzName = Utils.getClassName(tableName);
        this.tableColName = Utils.getColName(tableName.replaceAll(Constant.REPLACE_TABLE_PATTERN_START, ""));
        this.tableColNameNet = Utils.getColName_NET(tableName.replaceAll(Constant.REPLACE_TABLE_PATTERN_START, ""));
    }

    public String getClazzName() {
        return clazzName;
    }

//    public void setClazzName(String clazzName) {
//        this.clazzName = clazzName;
//    }

    public String getPkName() {
        return this.pkColumn != null ? this.pkColumn.getColName() : "*";
    }
//
//    public void setPkName(String pkName) {
//        this.pkName = pkName;
//        this.pkColumnName = Utils.getColName(pkName);
//        this.pkColumnNameNet = Utils.getColName_NET(pkName);
//    }

//    public String getPkComment() {
//        return pkComment;
//    }
//
//    public void setPkComment(String pkComment) {
//        this.pkComment = pkComment;
//    }

    public String getPkColumnName() {
        return this.pkColumn != null ? this.pkColumn.getColName() : "*";
    }

//    public void setPkColumnName(String pkColumnName) {
//        this.pkColumnName = pkColumnName;
//    }

    public JavaType getPkColumnType() {
        return this.pkColumn != null ? this.pkColumn.getColJavaType() : JavaType.LONG;
    }
//
//    public void setPkColumnType(String pkColumnType) {
//        this.pkColumnType = ColumnDBType.of(pkColumnType);
//    }
//
//    public String getPkColumnNameNet() {
//        return pkColumnNameNet;
//    }

//    public void setPkColumnNameNet(String pkColumnNameNet) {
//        this.pkColumnNameNet = pkColumnNameNet;
//    }

    public SequenceType getSequenceType() {
        return sequenceType;
    }

//    public void setSequenceType(String pkSequenceType) {
//        this.sequenceType = SequenceType.of(sequenceName);
//    }

    public String getSequenceName() {
        return sequenceName;
    }

//    public void setSequenceName(String pkSequenceName) {
//        this.sequenceName = pkSequenceName;
//    }

    public List<ColumnData> getLstColData() {
        return lstColumnData;
    }

    public void setLstColData(List<ColumnData> lstColDatas) {
        this.lstColumnData = lstColDatas;
    }

//    public DbType getDbType() {
//        return dbType;
//    }

//    public void setDbType(DbType dbType) {
//        this.dbType = dbType;
//    }

//    public JavaType getPkType() {
//        return pkType;
//    }
//
//    public void setPkType(String pkType, String len, String scale) {
//        this.pkType = pkType.toUpperCase();
//        int l = Integer.parseInt(len);
//        int s = Integer.parseInt(scale);
//        if (this.dbType.equals(DbType.ORACLE)) {
//            if (pkType.equals("NUMBER")) {
//                if (l == 1) {
//                    this.pkColumnType = "Boolean";
//                } else if (l > 1 && l <= 9) {
//                    if (s == 0) {
//                        this.pkColumnType = "Integer";
//                    } else {
//                        this.pkColumnType = "Double";
//                    }
//                } else if (l > 9 && l <= 18) {
//                    if (s == 0) {
//                        this.pkColumnType = "Long";
//                    } else {
//                        this.pkColumnType = "Double";
//                    }
//                } else if (l > 19) {
//                    if (s == 0) {
//                        this.pkColumnType = "Long";
//                    } else {
//                        this.pkColumnType = "Double";
//                    }
//                }
//            } else if (pkType.equals("INTEGER")) {
//                this.pkColumnType = "Integer";
//            } else if (pkType.equals("VARCHAR2")) {
//                this.pkColumnType = "String";
//            } else if (pkType.equals("NVARCHAR2")) {
//                this.pkColumnType = "String";
//            } else if (pkType.equals("DATE")) {
//                this.pkColumnType = "LocalDateTime";
//            } else if (pkType.equals("CHAR")) {
//                this.pkColumnType = "String";
//            } else if (pkType.equals("FLOAT")) {
//                this.pkColumnType = "xx";
//            } else if (pkType.equals("TIMESTAMP")) {
//                this.pkColumnType = "LocalDateTime";
//            } else {
//                this.pkColumnType = "yy";
//            }
//        } else if (this.dbType.equals("mysql")) {
//            if (pkType.equals("DECIMAL")) {
//                if (l == 1) {
//                    this.pkColumnType = "Boolean";
//                } else if (l > 1 && l <= 9) {
//                    if (s == 0) {
//                        this.pkColumnType = "Integer";
//                    } else {
//                        this.pkColumnType = "Double";
//                    }
//                } else if (l > 9 && l <= 20) {
//                    if (s == 0) {
//                        this.pkColumnType = "Long";
//                    } else {
//                        this.pkColumnType = "Double";
//                    }
//                } else if (l > 20) {
//                    if (s == 0) {
//                        this.pkColumnType = "Long";
//                    } else {
//                        this.pkColumnType = "Double";
//                    }
//                }
//            } else if (pkType.equals("INT")) {
//                this.pkColumnType = "Long";
//            } else if (pkType.equals("BIGINT")) {
//                this.pkColumnType = "Long";
//            } else if (pkType.equals("VARCHAR")) {
//                this.pkColumnType = "String";
//            } else if (pkType.equals("SMALLINT")) {
//                if (l == 1) {
//                    this.pkColumnType = "Boolean";
//                } else {
//                    this.pkColumnType = "Integer";
//                }
//            } else if (pkType.equals("DATE")) {
//                this.pkColumnType = "LocalDateTime";
//            } else if (pkType.equals("DATETIME")) {
//                this.pkColumnType = "LocalDateTime";
//            } else {
//                this.pkColumnType = "yy";
//            }
//        }
//    }

    public boolean isIsUserLog() {
        return isUserLog;
    }

    public void setIsUserLog(boolean isUserLog) {
        this.isUserLog = isUserLog;
    }

//    public String getPkExtra() {
//        return pkExtra;
//    }
//
//    public void setPkExtra(String pkExtra) {
//        this.pkExtra = pkExtra;
//    }

    public boolean isIsReadOnly() {
        return isReadOnly;
    }

    public void setIsReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public String getTableColName() {
        return tableColName;
    }

    public String getTableColNameNet() {
        return tableColNameNet;
    }
//    public void setTableColName(String tableColName) {
//        this.tableColName = tableColName;
//    }

    public List<IndexData> getLstIndexData() {
        return lstIndexData;
    }

    public void setLstIndexData(List<IndexData> lstIndexDatas) {
        this.lstIndexData = lstIndexDatas;
    }

    public ColumnData getPkColumn() {
        return pkColumn;
    }

    public void setPkColumn(ColumnData pkColumn) {
        this.pkColumn = pkColumn;
    }

    public String getExtendsClass() {
        return extendsClass;
    }

    public void setExtraInfo(boolean extraInfo) {
        isExtraInfo = extraInfo;
    }
    public boolean isExtraInfo() {
        return isExtraInfo;
    }
}
