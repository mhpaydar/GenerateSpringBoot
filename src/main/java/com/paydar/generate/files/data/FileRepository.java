package com.paydar.generate.files.data;

import com.paydar.generate.common.Constant;
import com.paydar.generate.common.Utils;
import com.paydar.generate.enums.IndexDbType;
import com.paydar.generate.enums.JavaType;
import com.paydar.generate.model.ColumnData;
import com.paydar.generate.model.IndexData;
import com.paydar.generate.model.TableModel;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author m.h paydar
 * @date 6/22/2024 8:25 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public class FileRepository {
    public static void gen(TableModel tableModel, String __PKGJAVA) throws Exception {
//        if (tableModel.isExtraInfo()) return;
        StringBuilder sbjavaR = new StringBuilder();//define
        StringBuilder sbjavaP = new StringBuilder();//package
        StringBuilder sbjavaH = new StringBuilder();//header
        String ownerPackage = "." + tableModel.getOwnerName().toLowerCase();

        sbjavaP.append("package " + __PKGJAVA + ownerPackage + ";\n");
        sbjavaH.append("import org.springframework.stereotype.Repository;\n" +
                "import com.paydar.commons.spring.boot.data.repository.BaseRevisionRepository;\n" +
                "import java.lang.annotation.Native;\n" +
                "import java.util.List;\nimport java.util.Optional;\n" +
                "import org.springframework.data.domain.Page;\n" +
                "import org.springframework.data.domain.Pageable;\n" +
                "import org.springframework.data.jpa.domain.Specification;\n" +
                "import org.springframework.data.jpa.repository.Query;\n" +
                "import org.springframework.data.jpa.repository.query.Procedure;\n" +
                "import org.springframework.data.repository.query.Param;\n");

        if (tableModel.getLstColData() != null) {
            for (ColumnData d : tableModel.getLstColData()) {
                if (!Constant.userFields.contains(d.getColName().toLowerCase())) {
                    if (d.getParent() == 0) {
                        if (d.getColJavaType().equals(JavaType.DATE) || d.getColJavaType().equals(JavaType.LOCAL_DATETIME) || d.getColJavaType().equals(JavaType.TIME)) {
                            //sbjava.append("\n\t@Column(name = \"" + d.getColName() + "\" " + readOnlyStr + " , columnDefinition = \"" + d.getColType() + "\")\n");
                        } else {
                            if (d.getColUnique()) {
                                sbjavaR.append("\tboolean existsBy" + d.getColNameNet() + "(final " + d.getColJavaType().getValue() + " " + d.getColNameJava() + ");\n");
                                sbjavaR.append("\tlong countBy" + d.getColNameNet() + "(final " + d.getColJavaType().getValue() + " " + d.getColNameJava() + ");\n");
                                sbjavaR.append("\tOptional<" + tableModel.getClazzName() + "> findBy" + d.getColNameNet() + "(final " + d.getColJavaType().getValue() + " " + d.getColNameJava() + ");\n\n");
                            } else {
                                sbjavaR.append("\tList<" + tableModel.getClazzName() + "> findBy" + d.getColNameNet() + "(final " + d.getColJavaType().getValue() + " " + d.getColNameJava() + ");\n");
                                sbjavaR.append("\tPage<" + tableModel.getClazzName() + "> findBy" + d.getColNameNet() + "(final " + d.getColJavaType().getValue() + " " + d.getColNameJava() + " , Pageable pageable);\n\n");
                            }
                        }
                    } else {
                        String mapped = "";
                        TableModel parentTable = Constant.tableInfo.get(d.getParentTableKey());
                        if (d.getIsDuplicate() == 0) {
                            mapped = parentTable.getTableColName();
                        } else {
                            mapped = parentTable.getTableColName() + d.getParentColNameJava();
                        }
                        String cfnnp = "";
                        if (d.getIsDuplicate() == 0) {
                            cfnnp = parentTable.getTableColNameNet();
                        } else {
                            cfnnp = parentTable.getTableColNameNet() + d.getParentColNameJava();
                        }
                        if (d.getColUnique()) {
                            sbjavaR.append("\tboolean existsBy" + cfnnp + "Id(final Long " + mapped + "Id);\n");
                            sbjavaR.append("\tlong countBy" + cfnnp + "Id(final Long " + mapped + "Id);\n");
                            sbjavaR.append("\tOptional<" + tableModel.getClazzName() + "> findBy" + cfnnp + "Id(final Long " + mapped + "Id);\n\n");
                        } else {
                            sbjavaR.append("\tList<" + tableModel.getClazzName() + "> findBy" + cfnnp + "Id(final Long " + mapped + "Id);\n");
                            sbjavaR.append("\tPage<" + tableModel.getClazzName() + "> findBy" + cfnnp + "Id(final Long " + mapped + "Id , Pageable pageable);\n\n");
                        }
                    }
                }
            }
        }
        int inx = 1;
        if (tableModel.getLstIndexData() != null) {
            for (IndexData indexData : tableModel.getLstIndexData()) {
                if (indexData.getType().equals(IndexDbType.NORMAL)) {
                    if (indexData.getData().indexOf(",") > 0) {
                        if (indexData.getNonUnique()) {
                            sbjavaR.append("\tList<" + tableModel.getClazzName() + "> findBy" + indexData.getSearching() + "(" + indexData.getSearchingParam() + ");\n");
                            sbjavaR.append("\tPage<" + tableModel.getClazzName() + "> findBy" + indexData.getSearching() + "(" + indexData.getSearchingParam() + " , Pageable pageable);\n");
                        } else {
                            sbjavaR.append("\tboolean existsBy" + indexData.getSearching() + "(" + indexData.getSearchingParam() + ");\n");
                            sbjavaR.append("\tlong countBy" + indexData.getSearching() + "(" + indexData.getSearchingParam() + ");\n");
                            sbjavaR.append("\tOptional<" + tableModel.getClazzName() + "> findBy" + indexData.getSearching() + "(" + indexData.getSearchingParam() + ");\n");
                        }
                    }
                } else {
                    if (indexData.getData().indexOf(",") > 0) {
                        String[] splitDataTemp = indexData.getData().split(",");
                        List<String> splitData = new ArrayList<>();
                        String dataIndex = "";
                        for (int i = 0; i < splitDataTemp.length; i++) {
                            StringBuilder sbData = new StringBuilder();
                            dataIndex = splitDataTemp[i];
                            sbData.append(dataIndex);
                            int countStart = StringUtils.countMatches(dataIndex, "(");
                            int countEnd = StringUtils.countMatches(dataIndex, ")");
                            while (countStart != countEnd) {
                                i++;
                                dataIndex = splitDataTemp[i];
                                sbData.append("," + splitDataTemp[i]);
                                countStart += StringUtils.countMatches(dataIndex, "(");
                                countEnd += StringUtils.countMatches(dataIndex, ")");
                            }
                            splitData.add(sbData.toString().replaceAll("\"", ""));
                        }
                        int jParam = 1;
                        StringBuilder sbWhere = new StringBuilder();
                        StringBuilder sbParam = new StringBuilder();
                        for (String data : splitData) {
                            sbWhere.append(" and  " + data.replaceAll("\"", "") + "= :P" + jParam);
                            sbParam.append(", @Param(\"P" + jParam + "\") final String param" + jParam);
                            jParam++;
                        }
                        sbjavaR.append("//\t" + indexData.getName() + "\n");
                        sbjavaR.append("\t@Query(value =\"select * from " + tableModel.getOwnerName() + "." + tableModel.getTableName() + " where " + sbWhere.substring(5) + "\", nativeQuery = true)\n");
                        sbjavaR.append("\tList<" + tableModel.getClazzName() + "> findData" + inx + "(" + sbParam.substring(2) + ");\n");
                    } else {
                        sbjavaR.append("//\t" + indexData.getName() + "\n");
                        sbjavaR.append("\t@Query(value =\"select * from " + tableModel.getOwnerName() + "." + tableModel.getTableName() + " where " + indexData.getData() + "= :P1\", nativeQuery = true)\n");
                        sbjavaR.append("\tList<" + tableModel.getClazzName() + "> findData" + inx + "(@Param(\"P1\") final String param1);\n");
                    }
                    inx++;
                }
            }
        }
        List<String> lstjavaRepository = new ArrayList<>();
        lstjavaRepository.add(sbjavaP.toString());
        lstjavaRepository.add(sbjavaH.toString());
        lstjavaRepository.add("@Repository");
        lstjavaRepository.add("public interface " + tableModel.getClazzName() + "Repository extends BaseRevisionRepository<" + tableModel.getClazzName() + ", " + tableModel.getPkColumnType().getValue() + "> {\n");
        lstjavaRepository.add(sbjavaR + "\n}\n");

        String dir = Utils.genPath(Constant.argDir, __PKGJAVA+ownerPackage);
        Files.write(Paths.get(dir + Constant.fileSep + tableModel.getClazzName() + "Repository.java"), lstjavaRepository);
        lstjavaRepository.clear();

    }
}
