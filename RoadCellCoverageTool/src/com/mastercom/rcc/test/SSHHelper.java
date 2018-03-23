package com.mastercom.rcc.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHHelper {
    /**
     * 远程 执行命令并返回结果调用过程 是同步的（执行完才会返回）
     * @param host  主机名
     * @param user  用户名
     * @param psw   密码
     * @param port  端口
     * @param command   命令
     * @return
     */
    public static String exec(String host,String user,String psw,int port,String command){
        StringBuffer sb= new StringBuffer();
        Session session =null;
        ChannelExec openChannel =null;
        try {
            JSch jsch=new JSch();
            session = jsch.getSession(user, host, port);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");//跳过公钥的询问
            session.setConfig(config);
            session.setPassword(psw);
            session.connect(5000);//设置连接的超时时间
            openChannel = (ChannelExec) session.openChannel("exec");
            openChannel.setCommand(command); //执行命令
            int exitStatus = openChannel.getExitStatus(); //退出状态为-1，直到通道关闭
            System.out.println(exitStatus);

            // 下面是得到输出的内容
            openChannel.connect();
            InputStream in = openChannel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String buf = null;
            while ((buf = reader.readLine()) != null) {
                sb.append(buf+"\n");
            }
        } catch (JSchException | IOException e) {
            sb.append(e.getMessage()+"\n");
        }finally{
            if(openChannel!=null&&!openChannel.isClosed()){
                openChannel.disconnect();
            }
            if(session!=null&&session.isConnected()){
                session.disconnect();
            }
        }
        return sb.toString();
    }


    public static void main(String args[]){
        String exec = exec("192.168.1.31", "hmaster", "mastercom168", 22, "ls");
        System.out.println(exec);
    }
}
