    package com.example.automationapk;

    import android.os.AsyncTask;
    import android.widget.Toast;

    import java.io.DataOutputStream;
    import java.io.IOError;
    import java.io.IOException;
    import java.io.PrintWriter;
    import java.net.DatagramPacket;
    import java.net.DatagramSocket;
    import java.net.InetAddress;
    import java.net.Socket;
    import java.net.UnknownHostException;

    public class ServerConnector extends AsyncTask<String,Void,Void> {

        //    public  Socket socket;
        public  String address;
        public  Integer port;
    
        InetAddress serverAddr;
        DatagramSocket socket;
        DatagramPacket packet;
        byte[] buf;


        ServerConnector(String address,Integer port)
        {
            this.address = address;
            this.port = port;
        }

        @Override
        protected Void doInBackground(String... voids) {
            String message  = voids[0];
            try {

                buf = message.getBytes();
                System.out.print(buf);
                serverAddr = InetAddress.getByName(address);
                socket = new DatagramSocket(); //DataGram socket is created
                packet = new DatagramPacket(buf, buf.length, serverAddr, port);//Data is loaded with information where to send on address and port number
                socket.send(packet);//Data is send in the form of packets
                socket.close();//Needs to close the socket before other operation... its a good programming

            }catch (IOError e)
            {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
