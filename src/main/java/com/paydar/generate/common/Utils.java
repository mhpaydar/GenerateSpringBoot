package com.paydar.generate.common;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author m.h paydar
 * @date 6/14/2024 4:51 PM
 * @linkedin https://www.linkedin.com/in/m-hossein-paydar
 * @github https://github.com/mhpaydar
 * @copyright
 */
public class Utils {

    /***
     * Create path to save output
     * @param dir path
     * @throws Exception failure to make path
     */
    public static void genPath(String dir) throws Exception {
        Path releaseFolder = Paths.get(dir);
        if (!Files.exists(releaseFolder))
            Files.createDirectory(releaseFolder);
    }
    public static String genPath(String dir,String packages) throws Exception {
        Path releaseFolder = Paths.get(dir);
        if (!Files.exists(releaseFolder))
            Files.createDirectory(releaseFolder);
        String[] splitPackage = packages.split("\\.");
        String path=dir;
        for (String s:splitPackage) {
            path+=Constant.fileSep+s;
            genPath(path);
        }
        return path;
    }
    /**
     * Get camelcase template Class name
     * @param param parameter
     * @return
     */
    public static String getClassName(String param) {
        StringBuilder ret = new StringBuilder();
        String[] pa = param.replaceAll(Constant.REPLACE_TABLE_PATTERN_START,"").split("_");
        for (int i = 0; i < pa.length; i++) {
            ret.append(pa[i].substring(0, 1).toUpperCase());
            ret.append(pa[i].substring(1).toLowerCase());
        }
        return ret.toString();
    }

    /**
     * Get camelcase template column name
     * @param param parameter
     * @return
     */
    public static String getColName(String param) {
        StringBuilder ret = new StringBuilder();
        String[] pa = param.split("_");
        ret.append(pa[0].toLowerCase());
        for (int i = 1; i < pa.length; i++) {
            ret.append(pa[i].substring(0, 1).toUpperCase());
            ret.append(pa[i].substring(1).toLowerCase());
        }
        return ret.toString();
    }

    /**
     * Get camelcase template column name for .net
     * @param param paramter
     * @return
     */
    public static String getColName_NET(String param) {
        StringBuilder ret = new StringBuilder();
        String[] pa = param.split("_");
        for (int i = 0; i < pa.length; i++) {
            ret.append(pa[i].substring(0, 1).toUpperCase());
            ret.append(pa[i].substring(1).toLowerCase());
        }
        return ret.toString();
    }
    /**
     * Get camelcase template foregin key column name
     * @param param parameter
     * @return
     */
    public static String getParentColName(String param) {
        StringBuilder ret = new StringBuilder();
        String param1=param.replaceAll(Constant.REPLACE_FK_PATTERN_START,"").replaceAll(Constant.REPLACE_FK_PATTERN_END,"");
        String[] pa = param1.split("_");
        ret.append(pa[0].toLowerCase());
        for (int i = 1; i < pa.length; i++) {
            ret.append(pa[i].substring(0, 1).toUpperCase());
            ret.append(pa[i].substring(1).toLowerCase());
        }
        return ret.toString();
    }
//    public static String getColNameParent(String p, String f) {
//        String ret = "";
//        String[] pa = p.split("_");
//        ret += pa[0].toLowerCase();
//        String[] fa = f.split("_");
//        for (int i = 1; i < pa.length; i++) {
//            ret += pa[i].substring(0, 1).toUpperCase() + pa[i].substring(1).toLowerCase();
//        }
//        String ff = fa[fa.length - 2];
//        ret += ff.substring(0, 1).toUpperCase() + ff.substring(1).toLowerCase();
//        return ret;
//    }

//    public static String getColNameParent_NET(String p, String f) {
//        String ret = "";
//        String[] pa = p.split("_");
//        String[] fa = f.split("_");
//        for (int i = 0; i < pa.length; i++) {
//            ret += pa[i].substring(0, 1).toUpperCase() + pa[i].substring(1).toLowerCase();
//        }
//        String ff = fa[fa.length - 2];
//        ret += ff.substring(0, 1).toUpperCase() + ff.substring(1).toLowerCase();
//        return ret;
//    }
//
//    public static String getColNameChildren_NET(String p) {
//        String ret = getColNameChildren(p);
//        return ret.substring(0, 1).toUpperCase() + ret.substring(1);
//    }

    public static String getColNameChildren(String p) {
        String ret = "";
        String[] pa = p.split("_");
        ret += pa[0].toLowerCase();
        for (int i = 1; i < pa.length; i++) {
            ret += pa[i].substring(0, 1).toUpperCase() + pa[i].substring(1).toLowerCase();
        }
        int l = ret.length();
        String c = ret.substring(l - 1, l);
        String c1 = ret.substring(l - 2, l - 1);
        String cc = ret.substring(l - 2, l);
        if (c.equals("x") || c.equals("z") || c.equals("s")) {
            ret = ret + "es";
        } else if (c.equals("y")) {
            if (c1.equals("a") || c1.equals("e") || c1.equals("i") || c1.equals("o") || c1.equals("u")) {
                ret = ret + "s";
            } else {
                ret = ret.substring(0, l - 1) + "ies";
            }
        } else if (c.equals("o")) {
            if (c1.equals("a") || c1.equals("e") || c1.equals("i") || c1.equals("o") || c1.equals("u")) {
                ret = ret + "s";
            } else {
                ret = ret + "es";
            }
        } else if (c.equals("f")) {
            ret = ret.substring(0, l - 1) + "ves";//handkerchief,roof,proof,chief,gulf,cliff,cuff,grief,hoof,plaintiff,reef,serf
        } else {
            if (cc.equals("sh") || cc.equals("ch")) {
                ret = ret + "es";
            }
            if (cc.equals("fe")) {
                ret = ret.substring(0, l - 2) + "ves"; // safe,strife,fife
            } else {
                ret = ret + "s";
            }
        }
        return ret;
    }
}
