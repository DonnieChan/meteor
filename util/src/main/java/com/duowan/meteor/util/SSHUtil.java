package com.duowan.meteor.util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Created by huangshaoqian on 2015/11/20.
 * remote ssh util
 */
public class SSHUtil {

    public static void exeCmd(String host,String command) throws Exception {
        String privateKey = "~/.ssh/id_rsa";
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;
        try {
            String osUser=System.getProperty("user.name");
            jsch.addIdentity(privateKey);
            session = jsch.getSession(osUser, host,32200);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();
        } catch (JSchException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
    }
}
