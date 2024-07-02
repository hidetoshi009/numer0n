import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient2 {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {

            // DatagramSocketを作成し、サーバのアドレスをlocalhostに設定します。
            socket = new DatagramSocket(12346);
            InetAddress serverAddress = InetAddress.getByName("localhost");
            byte[] sendData;
            byte[] receiveData = new byte[1024];
            Scanner scanner = new Scanner(System.in);

            // クライアントからのメッセージをユーザに入力させる
            System.out.print("3桁の数字を入力してください: ");
            String clientMessage = scanner.nextLine();
            sendData = clientMessage.getBytes();

            // メッセージをサーバに送信
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 9876);
            socket.send(sendPacket);

            // サーバからの返信を受信
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            String serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("サーバとの接続完了。入力した数字： " + serverResponse);

            while (true) {

                // クライアントからのメッセージをユーザに入力させる
                System.out.print("相手の数字を予測してください: ");
                clientMessage = scanner.nextLine();
                sendData = clientMessage.getBytes();

                // メッセージをサーバに送信
                sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 9876);
                socket.send(sendPacket);

                // サーバからの返信を受信
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(serverResponse);
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
