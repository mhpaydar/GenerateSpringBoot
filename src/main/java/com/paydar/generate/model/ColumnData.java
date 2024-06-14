package com.paydar.generate.model;

import com.paydar.generate.common.Utils;
import com.paydar.generate.enums.ColumnDBType;
import com.paydar.generate.enums.DbType;

import java.io.Serializable;

/**
 * @author m.h paydar
 * @date 6/14/2024 5:48 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public class ColumnData implements Serializable {
    private String colName;
    private String colNameJava;
    private String colNameNet;
    private ColumnDBType colType;
    private int colLen;
    private int colScale;
    private boolean colNullable;
    private String colTitle;
    private String colJavaType;
    private boolean colUnique;
    private int parent;
    private String parentOwner;
    private String parentTable;
    private String parentColName;
    private String parentFKName;
    private int isDuplicate;

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
        this.colNameJava = Utils.getColName(colName);
        this.colNameNet = Utils.getColName_NET(colName);
    }

    public ColumnDBType getColType() {
        return colType;
    }

//    public void setColType(String colType) {
//        this.colType = colType;
//    }

    public void setColType(DbType dbType, ColumnDBType colType) {
        this.colType = colType;
        try {
            int l = this.colLen;
            int s = this.colScale;
            int n = dbType.equals(DbType.ORACLE) ? 18 : 20;
            if (colType.equals(ColumnDBType.NUMBER) || colType.equals(ColumnDBType.DECIMAL)) {
                if (l == 1) {
                    if (s == 0) {
                        this.colJavaType = "Boolean";
                    } else {
                        this.colJavaType = "Double";
                    }
                } else if (l > 1 && l <= 9) {
                    if (s == 0) {
                        this.colJavaType = "Integer";
                    } else {
                        this.colJavaType = "Double";
                    }
                } else if (l > 9 && l <= n) {
                    if (s == 0) {
                        this.colJavaType = "Long";
                    } else {
                        this.colJavaType = "Double";
                    }
                } else {
                    if (s == 0) {
                        this.colJavaType = "BigDecimal";
                    } else {
                        this.colJavaType = "Double";
                    }
                }
            } else if (colType.equals(ColumnDBType.INTEGER)) {
                this.colJavaType = "Integer";
            } else if (colType.equals(ColumnDBType.INT) || colType.equals(ColumnDBType.BIGINT)) {
                this.colJavaType = "Long";
            } else if (colType.equals(ColumnDBType.SMALLINT)) {
                if (l == 1) {
                    this.colJavaType = "Boolean";
                } else {
                    this.colJavaType = "Integer";
                }
            } else if (colType.equals(ColumnDBType.VARCHAR2) || colType.equals(ColumnDBType.NVARCHAR2) || colType.equals(ColumnDBType.CHAR) || colType.equals(ColumnDBType.VARCHAR) || colType.equals(ColumnDBType.NVARCHAR)) {
                this.colJavaType = "String";
            } else if (colType.equals(ColumnDBType.DATE) || colType.equals(ColumnDBType.TIMESTAMP) || colType.equals(ColumnDBType.DATETIME)) {
                this.colJavaType = "LocalDateTime";
            } else if (colType.equals(ColumnDBType.FLOAT)) {
                this.colJavaType = "Double";
            } else {
                this.colJavaType = "yy";
            }
        } catch (Exception ex) {
            this.colJavaType = "zz";
        }
    }
//
//    public String getColLen() {
//        return colLen;
//    }
//
//    public void setColLen(String colLen) {
//        this.colLen = colLen;
//    }
//
//    public String getColNullable() {
//        return colNullable;
//    }
//
//    public void setColNullable(String colNullable) {
//        this.colNullable = colNullable;
//    }
//
//    public String getColTitle() {
//        return colTitle;
//    }
//
//    public void setColTitle(String colTitle) {
//        this.colTitle = colTitle;
//    }

    public int getParent() {
        return parent;
    }
//
//    public void setParent(int parent) {
//        this.parent = parent;
//    }
//
//    public String getParentOwner() {
//        return parentOwner;
//    }
//
//    public void setParentOwner(String parentOwner) {
//        this.parentOwner = parentOwner;
//    }
//
//    public String getParentTable() {
//        return parentTable;
//    }
//
//    public void setParentTable(String parentTable) {
//        this.parentTable = parentTable;
//    }
//
//    public String getParentColName() {
//        return parentColName;
//    }
//
//    public void setParentColName(String parentColName) {
//        this.parentColName = parentColName;
//    }
//
//    public int getIsDuplicate() {
//        return isDuplicate;
//    }
//
//    public void setIsDuplicate(int isDuplicate) {
//        this.isDuplicate = isDuplicate;
//    }
//
//    public String getColJavaType() {
//        return colJavaType;
//    }
//
//    public void setColJavaType(String colJavaType) {
//        this.colJavaType = colJavaType;
//    }
//
//    public String getColScale() {
//        return colScale;
//    }
//
//    public void setColScale(String colScale) {
//        this.colScale = colScale;
//    }
//
//    public String getParentFKName() {
//        return parentFKName;
//    }
//
//    public void setParentFKName(String parentFKName) {
//        this.parentFKName = parentFKName;
//    }
//
//    public String getColUnique() {
//        return colUnique;
//    }
//
//    public void setColUnique(String colUnique) {
//        this.colUnique = colUnique;
//    }
//
//    public String getColNameJava() {
//        return colNameJava;
//    }
//
//    public void setColNameJava(String colNameJava) {
//        this.colNameJava = colNameJava;
//    }
//
//    public String getColNameNet() {
//        return colNameNet;
//    }
//
//    public void setColNameNet(String colNameNet) {
//        this.colNameNet = colNameNet;
//    }

    @Override
    public String toString() {
        return "ColData{" +
                "colName='" + colName + '\'' +
                ", colNameJava='" + colNameJava + '\'' +
                ", colNameNet='" + colNameNet + '\'' +
                ", colType='" + colType + '\'' +
                ", colLen='" + colLen + '\'' +
                ", colScale='" + colScale + '\'' +
                ", colNullable='" + colNullable + '\'' +
                ", colTitle='" + colTitle + '\'' +
                ", colJavaType='" + colJavaType + '\'' +
                ", colUnique='" + colUnique + '\'' +
                ", parent=" + parent +
                ", parentOwner='" + parentOwner + '\'' +
                ", parentTable='" + parentTable + '\'' +
                ", parentColName='" + parentColName + '\'' +
                ", parentFKName='" + parentFKName + '\'' +
                ", isDuplicate=" + isDuplicate +
                '}';
    }
}
