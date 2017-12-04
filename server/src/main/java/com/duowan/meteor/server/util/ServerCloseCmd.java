package com.duowan.meteor.server.util;

import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/13.
 */
public class ServerCloseCmd {
    public static void main(String[] args) throws Exception {
        // 解析命令行工具
        Options opts = new Options();
        opts.addOption("app", "appName", true,
                "关闭应用必须指定的参数！");
        opts.addOption("zk", "zookeeper", true,
                "关闭应用必须指定的参数！");
        opts.addOption("cmd", "command", true,
                "执行的命令,只能是reset和close");
        opts.addOption("of", "offset_file", true,
                "执行的命令是reset的时候，需要指定offset_file文件");
        CommandLineParser parser = new PosixParser();
        CommandLine line = null;
        try {
            line = parser.parse(opts, args);
        } catch (Exception exp) {
            exp.printStackTrace();
            System.exit(1);
        }

        if (!line.hasOption("app")||!line.hasOption("zk")||!line.hasOption("cmd")) {
            printHelp(opts, null, true);
            return ;
        }

        String zkCon=line.getOptionValue("zk");
        String appName=line.getOptionValue("app");
        String cmd=line.getOptionValue("cmd");
        String offsetFilePath=line.getOptionValue("of");
        if("close".equals(cmd)){
            CuratorUtil.init(zkCon, 60 * 1000, "appCloseListener");
            CuratorUtil.setData("/"+appName+"/state", "close");
        }else if("reset".equals(cmd)&&offsetFilePath!=null&&offsetFilePath.length()>0){
            CuratorUtil.init(zkCon, 60 * 1000, "appCloseListener");
            File offsetFile=new File(offsetFilePath);
            InputStream inputStream=new BufferedInputStream(new FileInputStream(offsetFile));
            List<String> lines=IOUtils.readLines(inputStream);
            String offsetMeta=StringUtils.join(lines, "");
            ObjectMapper objectMapper=new ObjectMapper();
            objectMapper.readValue(offsetMeta, HashMap.class);
            CuratorUtil.setData("/"+appName+"/checkpoint",offsetMeta);
        }else{
            printHelp(opts, null, true);
            throw new Exception("参数错误");
        }


    }

    /**
     *
     * @param options
     * @param errInfo
     * @param userExpect
     *            是否是用户显示要求显示帮助信息
     */
    private static void printHelp(Options options, String errInfo,
                                  boolean userExpect) {

        if (errInfo != null && errInfo.isEmpty()) {
            System.out.println(errInfo);
        }
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("commandName", options);
        if (userExpect) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
