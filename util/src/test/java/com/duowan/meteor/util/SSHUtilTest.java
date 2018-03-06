package com.duowan.meteor.util;

import org.junit.Test;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHUtilTest {

	@Test
	public void test() throws Exception {
        String privateKey = "F:/Downloads/id_rsa";
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;
        String osUser = "root";
        String host = "127.0.0.1";
        String command = "/bin/bash /data/spark/data_chenwu/test.sh";
        try {
            jsch.addIdentity(privateKey);
            session = jsch.getSession(osUser, host, 9022);
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
