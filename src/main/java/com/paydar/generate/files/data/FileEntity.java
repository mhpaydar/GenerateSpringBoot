package com.paydar.generate.files.data;

import com.paydar.generate.common.Constant;
import com.paydar.generate.common.Utils;
import com.paydar.generate.enums.IndexDbType;
import com.paydar.generate.enums.JavaType;
import com.paydar.generate.enums.SequenceType;
import com.paydar.generate.model.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * @author m.h paydar
 * @date 6/16/2024 12:20 AM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public class FileEntity {
    public static void gen(TableModel tableModel, String __PKGJAVA) throws Exception {
        if(tableModel.isExtraInfo()) return;
        StringBuilder sbjava = new StringBuilder();//define
        StringBuilder sbjavaIndex = new StringBuilder();//define index
//        StringBuilder sbjavaB = new StringBuilder();//builder
        StringBuilder sbjavaH = new StringBuilder();//header
        StringBuilder sbjavaP = new StringBuilder();//package
        StringBuilder sbjavaParentH = new StringBuilder();//parent,children header
        StringBuilder sbjavaParent = new StringBuilder();//define parent,children


        sbjavaP.append("package " + __PKGJAVA + ";\n");
        sbjavaH.append("import javax.persistence.*;\n" +
                "import java.time.LocalDateTime;\n" +
                "import com.paydar.commons.spring.boot.data.entity.*;\n" +
                "import lombok.*;\n");
        if (tableModel.getLstIndexData() != null) {
            sbjavaIndex.append(", indexes = {");
            for (IndexData indexData : tableModel.getLstIndexData()) {
                sbjavaIndex.append("\n\t\t" + indexData.getType().getComment() + "@Index(name = \"" + indexData.getName() + "\", columnList = \"" + indexData.getData() + "\", unique = " + (indexData.getNonUnique() ? "false" : "true") + "),");
            }
            sbjavaIndex.append("\n}");
        }
        sbjava.append("@Entity\n@Table(name=\"" + tableModel.getTableName() + "\", schema=\"" + tableModel.getOwnerName() + "\"" + sbjavaIndex + ")\n@Getter\n@Setter\n@Builder(toBuilder = true)\n@NoArgsConstructor\n@AllArgsConstructor\n");
        sbjava.append("public class " + tableModel.getClazzName() + " extends " + tableModel.getExtendsClass() + "<" + tableModel.getPkColumnType().getValue() + "> {\n");
        sbjava.append("\n\t@Id\n\t@Column(name = \"" + tableModel.getPkName() + "\")\n");
        String comment = "";
        if (tableModel.isIsReadOnly()) {
            comment = "//";
        }
        if(tableModel.getSequenceType().equals(SequenceType.SEQUENCE)){
            sbjava.append(comment + "\t@GeneratedValue(strategy = GenerationType." + tableModel.getSequenceType().getValue() + ", generator = \"" + tableModel.getSequenceName() + "\")\n");
            sbjava.append(comment + "\t@SequenceGenerator(schema=\"" + tableModel.getOwnerName() + "\",sequenceName = \"" + tableModel.getSequenceName() + "\", allocationSize = 1, name = \"" + tableModel.getSequenceName() + "\")\n");
        }else if(tableModel.getSequenceType().equals(SequenceType.IDENTITY)){
            sbjava.append(comment + "\t@GeneratedValue(strategy = GenerationType." + tableModel.getSequenceType() + " )\n");
        }
        sbjava.append("\tprivate " + tableModel.getPkColumnType().getValue() + " " + "id" + ";\n");
//        sbjavaB.append("\t\tpublic Builder id(" + tableModel.getPkColumnType() + " id) {" + tableModel.getTableColName() + ".setId(id); return this;}\n");
        String columnStr = "";
        if (tableModel.getLstColData() != null) {
            for (ColumnData d : tableModel.getLstColData()) {
                columnStr = "";
                if (!Constant.userFields.contains(d.getColName().toLowerCase())) {
                    if (tableModel.isIsReadOnly()) {
                        columnStr += " ,insertable = false, updatable = false";
                    }
                    if (d.getColJavaType() != null) {
                        if (d.getColJavaType().equals(JavaType.STRING)) {
                            columnStr += " ,length = " + d.getColLen();
                        }
                    }
                    if (!d.getColNullable()) {
                        columnStr += " ,nullable = false";
                    }
                    if (d.getColUnique()) {
                        columnStr += " ,unique = true";
                    }
                    if (d.getParent() == 0) {
                        if (d.getColJavaType().equals(JavaType.DATE) || d.getColJavaType().equals(JavaType.LOCAL_DATETIME) || d.getColJavaType().equals(JavaType.TIME)) {
                            sbjava.append("\n\t@Column(name = \"" + d.getColName() + "\" " + columnStr + " , columnDefinition = \"" + d.getColType() + "\")\n");
                        } else {
                            sbjava.append("\n\t@Column(name = \"" + d.getColName() + "\" " + columnStr + " )\n");
                        }
                        sbjava.append("\tprivate " + d.getColJavaType().getValue() + " " + d.getColNameJava() + ";\n");
//                        sbjavaB.append("\t\tpublic Builder " + d.getColNameJava() + "(" + d.getColJavaType() + " " + d.getColNameJava() + ") {" + tableModel.getTableColName() + ".set" + d.getColNameNet() + "(" + d.getColNameJava() + "); return this;}\n");
                    } else {
                        String mapped = "";
                        TableModel parentTable= Constant.tableInfo.get(d.getParentTableKey());
                        if (d.getIsDuplicate() == 0) {
                            mapped = parentTable.getTableColName();;// Utils.getColName(d.getParentTable().replaceAll("TBL_", ""));
                        } else {
                            mapped = parentTable.getTableColName()+d.getParentColNameJava(); //Utils.getColNameParent(d.getParentTable().replaceAll("TBL_", ""), d.getColName());
                        }
//                        String cftp = Utils.getClassName(d.getParentTable());
                        if (d.getColNullable()) {
                            sbjavaParent.append("\n\t@ManyToOne(fetch = FetchType.LAZY)\n\t");
                        } else {
                            sbjavaParent.append("\n\t@ManyToOne(fetch = FetchType.LAZY, optional = false)\n\t");
                        }
                        sbjavaParent.append("@JoinColumn(name =\"" + d.getColName() + "\" " + columnStr + " , foreignKey = @ForeignKey(name = \"" + d.getFKDbName() + "\"))\n");
                        sbjavaParent.append("\tprivate " + parentTable.getClazzName() + " " + mapped + ";\n");
//                        sbjavaB.append("\t\tpublic Builder " + mapped + "(" + cftp + " " + mapped + ") {" + tableModel.getTableColName() + ".set" + cfnnp + "(" + mapped + "); return this;}\n");
                    }
                }
            }
        }
        List<String> lstjava = new ArrayList();
        lstjava.add(sbjavaP.toString());
        lstjava.add(sbjavaH.toString());
        lstjava.add(sbjavaParentH.toString());
        lstjava.add(sbjava.toString());
        lstjava.add(sbjavaParent.toString());
//        lstjava.add("\n\tpublic static Builder builder() { return new Builder(); }\n");
//        lstjava.add("\tpublic static final class Builder {\n");
//        lstjava.add("\t\tprivate final " + tableModel.getClazzName() + " " + tableModel.getTableColName() + ";\n");
//        lstjava.add("\t\tprivate Builder() { " + tableModel.getTableColName() + " = new " + tableModel.getClazzName() + "(); }\n");
//        lstjava.add(sbjavaB.toString());
//        lstjava.add("\t\tpublic " + tableModel.getClazzName() + " build() { return this." + tableModel.getTableColName() + ";}\n");
//        lstjava.add("\t}\n");
        lstjava.add("\n}\n");

        String dir = Utils.genPath(Constant.argDir,__PKGJAVA);
        Files.write(Paths.get(dir + Constant.fileSep + tableModel.getClazzName() + ".java"), lstjava);

    }
}
