package com.gtastart.common.util;


import android.util.Log;

import com.billflx.csgo.bean.SampQueryInfoBean;
import com.billflx.csgo.bean.SampQueryPlayerBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

public class CsMosQuery {

    private static final String TAG = "CsMosQuery";

    private String rconPassword = "";
    private DatagramSocket socket = null;
    private InetAddress serverip = null;
    private String serverhost = "";
    private String serveraddress = "";
    private int serverport = 0;
    private Random f = new Random();
    private boolean isValidAddr = true;
    private String charset = "UTF-8";
    private String payload = CsPayload.CSMOS.getPayload();

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public CsMosQuery(String srvip, int srvport) {
        try {
            serverhost = srvip;
            serverip = InetAddress.getByName(srvip);
            serveraddress = serverip.getHostAddress();
        } catch (UnknownHostException e) {
            this.isValidAddr = false;
        }

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(2000);
        } catch (SocketException e) {
            this.isValidAddr = false;
        }

        this.serverport = srvport;
    }

    public CsMosQuery(String srvip, int srvport, String rconPassword) {
        try {
            serverhost = srvip;
            this.rconPassword = rconPassword;
            serverip = InetAddress.getByName(srvip);
            serveraddress = serverip.getHostAddress();
        } catch (UnknownHostException e) {
            this.isValidAddr = false;
        }

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(2000);
        } catch (SocketException e) {
            this.isValidAddr = false;
        }

        this.serverport = srvport;
    }

    private DatagramPacket initPacket(String type) {
        DatagramPacket pkt;
        try {
//            String payload = "Source Engine Query\0";
            String payload = this.payload;
//            String payload = "3163736d6f73";

            int bufferLength = payload.getBytes(StandardCharsets.UTF_8).length;
            if (type.equals("i")) {
                bufferLength = 9;
            }
            ByteBuffer buffer = ByteBuffer.allocate(bufferLength);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            if (type.equals("i")) {
                buffer.put((byte)0xFF);
                buffer.put((byte)0xFF);
                buffer.put((byte)0xFF);
                buffer.put((byte)0xFF);
                buffer.put((byte)0x76);
                buffer.put((byte)0x10);
                buffer.put((byte)0x00);
                buffer.put((byte)0x00);
                buffer.put((byte)0x00);
            } else if (type.equals("s")) {
                buffer.put(payload.getBytes(StandardCharsets.UTF_8));
            }

            pkt = new DatagramPacket(buffer.array(), bufferLength, serverip, serverport);
        } catch (Exception e) {
            pkt = null;
            return pkt;
        }

        return pkt;
    }

    private byte[] receiveData() {
        if (socket == null) {
            return new byte[4096];
        } else {
            byte[] data = new byte[4096];
            DatagramPacket getpacket = null;
            try {
                getpacket = new DatagramPacket(data, 4096);
                socket.receive(getpacket);
            } catch (IOException e) {
            }

            return getpacket.getData();
        }
    }

    private void sendPacket(DatagramPacket d) {
        try {
            if (socket != null) {
                socket.send(d);
            }
        } catch (IOException e) {
        }

    }

    public void socketClose() {
        if (socket != null) {
            socket.close();
        }
    }

    public static int getStringCountFromBuffer(ByteBuffer buffer){
        StringBuilder sb = new StringBuilder();
        byte tmp;
        int count = 0;
        while((tmp = buffer.get()) != (byte)0){
            count++;
            sb.append((char)tmp);
        }
        return count;
    }

    public static String getStringFromBuffer(ByteBuffer buffer){
        byte tmp;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        while ((tmp = buffer.get()) != (byte) 0) {
            byteStream.write(tmp);
        }
        return new String(byteStream.toByteArray(), StandardCharsets.UTF_8);
    }


    public SampQueryInfoBean getInfos() {
        SampQueryInfoBean infos = new SampQueryInfoBean();
        try {
            long sendTime = System.currentTimeMillis(); // 记录发送数据包的时间
            sendPacket(initPacket("i"));
            ByteBuffer buff = ByteBuffer.wrap(receiveData());
            long receiveTime = System.currentTimeMillis(); // 记录接收数据包的时间
            long ping = receiveTime - sendTime; // 计算 ping 值
            infos.setPing(ping);
            infos.setServerIP(serveraddress + ":" + serverport);

            buff.order(ByteOrder.LITTLE_ENDIAN);

            int posTmp = 10;
            /**
             * 服务器名称
             */
            buff.position(posTmp);
            String serverName = getStringFromBuffer(buff);
            infos.setServerName(serverName);
            Log.d(TAG, "serverName: " +serverName);

            /**
             * 服务器地图 cnsr_cache_v1
             */
            String serverMap = getStringFromBuffer(buff);
            infos.setServerMap(serverMap);
            Log.d(TAG, "serverMap: " + serverMap);

            /**
             * 文件夹名称 csmos
             */
            String s1 = getStringFromBuffer(buff);
            Log.d(TAG, "getInfos: " + s1);

            /**
             * 完整游戏名 Counter-Strike: Source Offensive
             */
            String gameName = getStringFromBuffer(buff);
            Log.d(TAG, "gameName: " + gameName);

            /**
             * 人数
             */
            int players = buff.get() & 0xFF;
            int maxPlayers = buff.get() & 0xFF;
            Log.d(TAG, "players: " + players + " / " + maxPlayers);
            infos.setPlayers(players);
            infos.setMaxPlayers(maxPlayers);
            infos.setPlayerCountInfo(players + " / " + maxPlayers);


            buff.position(0);
            String data = Charset.forName(charset).decode(buff).toString();
            Log.d(TAG, "getInfos: " + data);

            return infos;
        }catch (Exception e){
            return null;
        }
    }

    public List<String> getServerIps() {
        List<String> ipList = new ArrayList<>();
        try{
            sendPacket(initPacket("s"));
            ByteBuffer buff = ByteBuffer.wrap(receiveData());
            buff.order(ByteOrder.LITTLE_ENDIAN);

            buff.position(5);

            byte tmp;
            int count = 0;
            byte[] b = new byte[6];
            while((tmp = buff.get()) != (byte)0) {
                b[count] = tmp;
                count++;
                if (count == 6) {
                    int port = (b[5] & 0xFF) << 8 | (b[4] & 0xFF);
                    String ip = String.format("%d.%d.%d.%d:%d", b[3] & 0xFF, b[2] & 0xFF, b[1] & 0xFF, b[0] & 0xFF, port);
                    Log.d(TAG, "getInfos: " + ip);
                    ipList.add(ip);
                    count = 0;
                }
            }

            return ipList;
        }catch (Exception e){
            return null;
        }

    }

    private String convert(ByteBuffer buff, int length) throws UnsupportedEncodingException {
        byte[] n = new byte[length];
        try{
            for (int x = 0; x < length; x++)
                n[x] = buff.get();
        }catch (Exception e){
        }
        return new String(n,charset);
    }

}
