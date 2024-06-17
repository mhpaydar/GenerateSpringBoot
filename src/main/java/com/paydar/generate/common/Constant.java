package com.paydar.generate.common;

import com.paydar.generate.model.TableModel;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author m.h paydar
 * @date 6/14/2024 5:46 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public class Constant {
    public static HashMap<String, TableModel> tableInfo = new HashMap<>();
    public static HashMap<String, String> tableCheck = new HashMap<>();
    public static final String REPLACE_TABLE_PATTERN_START = "^TBL_";
    public static final String REPLACE_FK_PATTERN_START = "^(FK_|Fk_|fK_|fk_)";
    public static final String REPLACE_FK_PATTERN_END = "(_ID|_Id|_iD|_id)$";
    public static final String USER_REG_FIELD = "fk_user_reg_id";
    public static final Set<String> userFields = Stream.of("fk_user_reg_id", "user_reg_date", "fk_user_update_id", "user_update_date").collect(Collectors.toSet());
    public static final String AUDIT_ENTITY="AbstractAuditEntity";

    public static String argDb = "";
    public static String argDir = "d";
    public static String argTableName = "";
    public static String argSchemas = "";
    public static String argPackageName = "";
    public static String argReadOnly = "0";
    public static String argDBType = "oracle";
    public static String argProgramName = "";
    public static String os = System.getProperty("os.name").toLowerCase();
    public static String fileSep = System.getProperty("file.separator");

}
