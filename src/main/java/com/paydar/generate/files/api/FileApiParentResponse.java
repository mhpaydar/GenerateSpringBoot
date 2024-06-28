package com.paydar.generate.files.api;

import com.paydar.generate.common.Constant;
import com.paydar.generate.common.Utils;
import com.paydar.generate.model.ColumnData;
import com.paydar.generate.model.TableModel;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author m.h paydar
 * @date 6/23/2024 11:53 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public class FileApiParentResponse {
    public static void gen(TableModel tableModel, String __PKGJAVAAPI) throws Exception {
        StringBuilder sbjavaResponseHeader = new StringBuilder();
        StringBuilder sbjavaResponse = new StringBuilder();
        String ownerPackage ="."+tableModel.getOwnerName().toLowerCase();

        sbjavaResponseHeader.append("package " + __PKGJAVAAPI + "."+tableModel.getClazzName().toLowerCase()+";\n");
        sbjavaResponseHeader.append("\nimport com.paydar.commons.spring.boot.api.portable.AbstractBasePortable;\n" +
                "import io.swagger.v3.oas.annotations.extensions.*;\n" +
                "import io.swagger.v3.oas.annotations.media.*;\n" +
                "import lombok.*;\n" +
                "import lombok.experimental.*;\n" +
                "import java.time.LocalDateTime;\n");
        sbjavaResponse.append("\n\n@Getter\n@Setter\n@NoArgsConstructor\n@SuperBuilder\n");
        sbjavaResponse.append("public class " + tableModel.getClazzName() + "ParentResponse extends AbstractBasePortable<Long> {\n\n");

        if (tableModel.getLstColData() != null) {
            int inx=1;
            for (ColumnData d : tableModel.getLstColData()) {
                if (!Constant.userFields.contains(d.getColName().toLowerCase())) {
                    if (d.getParent() == 0) {
                        if (d.getColLen() <= Constant.MAX_LENGTH_RESPONSE_LIST) {
                            sbjavaResponse.append("\t@Schema(description = \"\", extensions = {@Extension(name = \"x-uix\", properties = {\n" +
                                    "\t\t  @ExtensionProperty(name = \"order\", value = \"" + inx + "\")\n" +
                                    "\t\t, @ExtensionProperty(name = \"dependant\", value = \"\")\n" +
                                    "\t\t})})\n");
                            sbjavaResponse.append("\tprivate " + d.getColJavaType().getValue() + " " + d.getColNameJava() + ";\n");
                        }
                    } else {
                        String mapped = "";
                        TableModel parentTable = Constant.tableInfo.get(d.getParentTableKey());
                        if (d.getIsDuplicate() == 0) {
                            mapped = parentTable.getTableColName();
                        } else {
                            mapped = parentTable.getTableColName() + d.getParentColNameJava();
                        }
                        //level 2 and more
                        sbjavaResponse.append("\t@Schema(description = \"\", extensions = {@Extension(name = \"x-uix\", properties = {\n" +
                                "\t\t  @ExtensionProperty(name = \"order\", value = \""+inx+"\")\n" +
                                "\t\t, @ExtensionProperty(name = \"dependant\", value = \"\")\n" +
                                "\t\t})})\n");
                        sbjavaResponse.append("//\tprivate " + parentTable.getClazzName() + "ParentResponse " + mapped + ";\n");
                        sbjavaResponse.append("\tprivate String " + mapped + ";\n");
                        sbjavaResponseHeader.append("//import " + __PKGJAVAAPI +"."+parentTable.getOwnerName().toLowerCase()+ "."+parentTable.getClazzName().toLowerCase()+".*;\n");
                    }
                    inx++;
                }
            }
        }
        List<String> lstjavaResponse = new ArrayList();
        lstjavaResponse.add(sbjavaResponseHeader.toString());
        lstjavaResponse.add(sbjavaResponse.toString());
        lstjavaResponse.add("}");

        String dir = Utils.genPath(Constant.argDir, __PKGJAVAAPI+ownerPackage+"."+tableModel.getClazzName().toLowerCase());
        Files.write(Paths.get( dir+"\\"+ tableModel.getClazzName() + "ParentResponse.java"), lstjavaResponse);
        lstjavaResponse.clear();

    }
}
