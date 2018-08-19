package online.letmesleep.androidapplication.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPUDPDemo {
    /***
     *              作为客户端连接tcp服务端
     * @param ip    IP地址
     * @param port  端口号
     */
    public static void tcpConnectTo(final String ip, final int port){
        new Thread(new Runnable() {     //开线程
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(ip,port);
                    socket.getOutputStream();               //输出流

                    socket.getInputStream();                //输入流


                    // TODO: 2018/8/16       处理逻辑
                    if(socket!=null)
                        socket.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /***
     *              开启一个tcp服务端
     * @param port   本地监听端口
     */
    public static void startTcpServer(final int port){
        new Thread(new Runnable() {     //开线程
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(port);
                    Socket socket = server.accept();                    //等待另一端连接
                    socket.getOutputStream();               //客户端输出流

                    socket.getInputStream();                //客户端输入流

                    // TODO: 2018/8/16       处理逻辑
                    if(socket!=null)
                        socket.close();
                    if(server!=null)
                        server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /***
     *          udp发送端
     * @param ip   ip地址
     * @param port   端口
     * @throws Exception
     */
    public static void udpSender(String ip,int port) throws Exception{	// 所有异常抛出
        DatagramSocket ds = null ;		// 定义发送数据报的对象
        DatagramPacket dp = null ;		// 声明DatagramPacket对象
        ds = new DatagramSocket(port) ;	// 服务端在端口上等待服务器发送信息
        String str = "hello udp" ;
        dp = new DatagramPacket(str.getBytes(),str.length(), InetAddress.getByName(ip),port) ; // 所有的信息使用buf保存
        ds.send(dp);	// 发送信息出去
        ds.close() ;
    }

    /***
     *          udp监听
     * @param port    监听端口
     * @throws Exception
     */
    public static void udpReceiver(int port) throws Exception{	// 所有异常抛出
        DatagramSocket ds = null ;		// 定义接收数据报的对象
        byte[] buf = new byte[1024] ;	// 开辟空间，以接收数据
        DatagramPacket dp = null ;		// 声明DatagramPacket对象
        ds = new DatagramSocket(port) ;	// 客户端在9000端口上等待服务器发送信息
        dp = new DatagramPacket(buf,1024) ; // 所有的信息使用buf保存
        ds.receive(dp)  ;	// 接收数据
        String str = new String(dp.getData(),0,dp.getLength()) + "from " +
                dp.getAddress().getHostAddress() + "：" + dp.getPort() ;

    }
}
