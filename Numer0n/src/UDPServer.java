import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {

            // DatagramSocketをポート9876で作成し、クライアントからの接続を待ちます。
            // DatagramPacketを使用してデータを受信し、クライアントのアドレスとポートを取得します。
            socket = new DatagramSocket(9876);
            byte[] receiveData = new byte[1024];
            byte[] sendData;
            int num1;
            int num2;

            while (true) {
                // 受信パケットの準備
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                System.out.println("サーバーはクライアントからのメッセージを待っています...");

                // クライアントからのデータを受信
                socket.receive(receivePacket);
                String clientMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // ここで送信元のクライアントを特定し、データの保存場所を変えている

                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                if (IPAddress.getHostAddress().equals("127.0.0.1") && port == 12345) {
                    num1 = Integer.parseInt(clientMessage);
                    System.out.println("Received from Client 1: " + num1);
                } else if (IPAddress.getHostAddress().equals("127.0.0.1") && port == 12346) {
                    num2 = Integer.parseInt(clientMessage);
                    System.out.println("Received from Client 2: " + num2);
                }
                num1 = Integer.parseInt(clientMessage);

                // サーバーからの返信をユーザに入力させる
                String serverResponse = clientMessage;
                sendData = serverResponse.getBytes();

                // クライアントのアドレスとポートを取得
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                // 返信をクライアントに送信
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                socket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
