package org.antsiferov;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.antsiferov.TYPE.READY;

public class Server extends Thread {
    private Node node;
    private Socket client;

    Server(Node node, Socket client) {
        super(node.getName() + System.currentTimeMillis());
        this.node = node;
        this.client = client;
    }

    @Override
    public void start() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream()); ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream())) {
            Message current = new Message();
            current.setSender(node.getPort());
            current.setType(READY);
            objectOutputStream.writeObject(current);
            Object from;
            while ((from = objectInputStream.readObject()) != null) {
                if (from instanceof Message) {
                    Message msg = (Message) from;
                    if (TYPE.NEW == msg.getType()) {
                        synchronized (node) {
                            if (node.addBlock(msg.getBlocks().get(0))) {
                                System.out.printf("NEW %d received: %s%n", node.getPort(), from);
                            }
                        }
                        break;
                    } else if (TYPE.REQ == msg.getType()) {
                        System.out.println("\nREQ\n");
                        Message message = new Message();
                        message.setBlocks(node.getBlockchain());
                        message.setType(TYPE.RSP);
                        message.setSender(node.getPort());
                        objectOutputStream.writeObject(message);
                        break;
                    }
                }
            }
            client.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

}