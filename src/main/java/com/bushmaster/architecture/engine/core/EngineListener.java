package com.bushmaster.architecture.engine.core;

import org.apache.jmeter.JMeter;
import org.apache.jmeter.engine.JMeterEngine;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.util.HeapDumper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

@Component
public class EngineListener {
    private static final Logger log = LoggerFactory.getLogger(EngineListener.class);

    /**
     * @description     等待停止测试信号的方法
     * @param engine    执行引擎
     * @param socket    监听端口
     */
    public static void waitForSignals(JMeterEngine engine, DatagramSocket socket) {
        byte [] buf = new byte[80];
        log.info("Waiting for possible Shutdown/StopTestNow/Heapdump message on port " + socket.getLocalPort());

        DatagramPacket request = new DatagramPacket(buf, buf.length);

        while (true) {
            try {
                socket.receive(request);
                InetAddress address = request.getAddress();
                // 只接受本地的指令
                if (address.isLoopbackAddress()) {
                    String command = new String(request.getData(), request.getOffset(), request.getLength());
                    log.info("Command: " + command + " received from " + address);
                    switch (command) {
                        case "StopTestNow":
                            engine.stopTest(true);
                            break;
                        case "Shutdown":
                            engine.stopTest(false);
                            break;
                        case "HeapDump":
                            HeapDumper.dumpHeap();
                            break;
                        default:
                            log.info("Command: " + command + " not recognised ");
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }
        }
    }

    /**
     * @description             获取可用的监听Socket端口
     * @param udpPortDefault    默认端口号
     * @param udpPortMax        最大端口号
     * @return                  返回可用的Socket端口
     */
    private static DatagramSocket getSocket(int udpPortDefault, int udpPortMax) {
        DatagramSocket socket = null;
        int port = udpPortDefault;
        while (port <= udpPortMax) {
            try {
                // 给每个端口分配socket连接
                socket = new DatagramSocket(port);
                break;
            } catch (SocketException e) {
                port++;
            }
        }
        return socket;
    }

    /**
     * @description             引擎的命令监听方法
     * @param engine            执行引擎
     */
    public void engineControlListener(final StandardJMeterEngine engine) {
        // 确定监听停止测试的端口范围(4445是JMeter默认端口,最大4455)
        int defaultPort = JMeterUtils.getPropDefault("jmeterengine.nongui.port", 4445);
        int maxPort = JMeterUtils.getPropDefault("jmeterengine.nongui.maxport", 4455);

        // 获取可用的监听Socket端口
        final DatagramSocket socket = getSocket(defaultPort, maxPort);
        if (socket != null) {
            // 建立一个线程进行监听
            Thread listener = new Thread("UDP Listener") {
                @Override
                public void run() {
                    // 执行引擎等待停止测试命令
                    waitForSignals(engine, socket);
                }
            };
            listener.setDaemon(true);     // 后台执行
            listener.start();             // 开始监听
        } else {
            System.out.println("Failed to create UDP port");
        }
    }

    /**
     * @description             向引擎发送指令的方法
     * @param command           命令字符串
     */
    public void stopTestPerform(String command) {
        int port = JMeter.UDP_PORT_DEFAULT;
        log.info("Sending " + command + " request to port " + port);

        EngineController controller = EngineController.getInstance();
        StandardJMeterEngine engine = controller.getEngine();
        System.out.println("引擎运行状态: " + engine.isActive());

        try {
            DatagramSocket socket = new DatagramSocket();
            byte [] buf = command.getBytes("ASCII");
            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
