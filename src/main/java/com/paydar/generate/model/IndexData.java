package com.paydar.generate.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author m.h paydar
 * @date 6/14/2024 6:22 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public class IndexData implements Serializable {
    private String name;
    private String type;
    private String data;
    private boolean nonUnique;
    private List<ColumnData> cols;
    private String searching;
    private String searchingParam;
    private String searchingVar;
    private String searchingEqual;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean getNonUnique() {
        return nonUnique;
    }

    public void setNonUnique(boolean nonUnique) {
        this.nonUnique = nonUnique;
    }

    public List<ColumnData> getCols() {
        return cols;
    }

    public void setCols(List<ColumnData> col) {
        this.cols = col;
    }

    public void addCols(ColumnData col) {
        if (this.cols == null)
            this.cols = new ArrayList<>();
        this.cols.add(col);
    }

    public String getSearching() {
        return searching;
    }

    public void setSearching(String searching) {
        this.searching = searching;
    }

    public String getSearchingParam() {
        return searchingParam;
    }

    public void setSearchingParam(String searchingParam) {
        this.searchingParam = searchingParam;
    }

    public String getSearchingVar() {
        return searchingVar;
    }

    public void setSearchingVar(String searchingVar) {
        this.searchingVar = searchingVar;
    }

    public String getSearchingEqual() {
        return searchingEqual;
    }

    public void setSearchingEqual(String searchingEqual) {
        this.searchingEqual = searchingEqual;
    }

    public void calcSearching() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbp = new StringBuilder();
        StringBuilder sbv = new StringBuilder();
        StringBuilder sbe = new StringBuilder();
        if (this.cols != null)
            if (!this.cols.isEmpty()) {
                for (ColumnData d : this.cols) {
                    if (d.getParent() == 0) {
                        if (d.getColJavaType().contains("Date")) {
                            //sbjava.append("\n\t@Column(name = \"" + d.getColName() + "\" " + readOnlyStr + " , columnDefinition = \"" + d.getColType() + "\")\n");
                        } else {
                            sb.append("And" + d.getColNameNet());
                            sbp.append(",final " + d.getColJavaType() + " " + d.getColNameJava());
                            sbv.append(",#.get" + d.getColNameNet()+"()");
                            sbe.append("&& @.get"+d.getColNameNet()+"().equals(#.get" + d.getColNameNet()+"())");
                        }
                    } else {
                        String ref1 = "";
                        String mapped = "";
                        if (d.getIsDuplicate() == 0) {
                            mapped = Utils.getColName(d.getParentTable().replaceAll("TBL_", ""));
                        } else {
                            mapped = Utils.getColNameParent(d.getParentTable().replaceAll("TBL_", ""), d.getColName());
                        }
                        String cftp = Utils.getClassName(d.getParentTable());
                        String cfnnp = "";
                        if (d.getIsDuplicate() == 0) {
                            cfnnp = Utils.getColName_NET(d.getParentTable().replaceAll("TBL_", ""));
                        } else {
                            cfnnp = Utils.getColNameParent_NET(d.getParentTable().replaceAll("TBL_", ""), d.getColName());
                        }
                        sb.append("And" + cfnnp + "Id");
                        sbp.append(",final Long " + mapped + "Id");
                        sbv.append(",#.get" + cfnnp + "().getId()");
                        sbe.append("&& @.get"+cfnnp+"().getId().equals(#.get" + cfnnp + "().getId())");
                    }
                }
                this.searching = sb.toString().substring(3);
                this.searchingParam = sbp.toString().substring(1);
                this.searchingVar = sbv.toString().substring(1);
                this.searchingEqual = sbe.toString().substring(3);
            }
    }

    @Override
    public String toString() {
        return "IndexData{" + "name=" + name + ", type=" + type + ", data=" + data + ", nonUnique=" + nonUnique + '}';
    }
}
