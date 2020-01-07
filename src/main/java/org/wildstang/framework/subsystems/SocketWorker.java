package org.wildstang.framework.subsystems;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SocketWorker implements Runnable {
    private final String BOUNDARY = "~EOF~";
    private final int SEND_BUFF_SIZE = 8000;
    private int FPS = WsMJPEGstreamer.FPS;
    private OutputStream out;
    private InputStream in;
    private Socket socket;
    private WsMJPEGstreamer ims;
    private int counter;
    private BlockingQueue<byte[]> IMAGES = new ArrayBlockingQueue<>(100);

    public SocketWorker(int counter, Socket socket, WsMJPEGstreamer ims) {
        this.counter = counter;
        this.socket = socket;
        this.ims = ims;
        try {
            out = socket.getOutputStream();
            socket.setSendBufferSize(SEND_BUFF_SIZE);
            in = socket.getInputStream();
            writeHeader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHeader() throws IOException {
        out.write(("HTTP/1.1 200 OK\r\n" + "Connection: close\r\n" + "Max-Age: 0\r\n"
                + "Expires: 0\r\n" + "Cache-Control: "
                + "no-store, no-cache, must-revalidate, no-transform, pre-check=0, post-check=0, max-age=0\r\n"
                + "Pragma: no-cache\r\n" + "Content-Type: multipart/x-mixed-replace; "
                + "boundary=--" + BOUNDARY + "\r\n" + "\r\n" + "--" + BOUNDARY + "\r\n")
                        .getBytes());
    }

    @Override
    public void run() {

        while (true) {
            try {
                byte[] imageBytes = IMAGES.take();

                if (imageBytes != null) {
                    out.write(("Content-type: image/jpeg\r\n" + "Content-Length: "
                            + imageBytes.length + "\r\n" + "\r\n").getBytes());
                    out.write(imageBytes);
                    out.write(("\r\n--" + BOUNDARY + "\r\n").getBytes());
                    out.flush();
                }
                Thread.sleep(FPS);
            } catch (Exception e) {
                close();
            }

        }

    }

    public void close() {
        ims.workers.remove(this);
        close(in);
        close(out);
        close(socket);
        System.out.println("Client #" + counter + " is disconnected.");
    }

    private void close(AutoCloseable stream) {
        try {
            stream.close();
        } catch (Exception e) {
            // don't show the close attempt
        }
    }

    public void send(byte[] image) {
        try {
            if (image != null) {
                IMAGES.put(image);
                // System.out.println("Server sent client #" + counter );
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
