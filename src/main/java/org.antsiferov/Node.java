package org.antsiferov;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Getter
@Setter
public class Node {

    private List<Integer> peers;
    private List<Block> blockchain = new ArrayList<>();
    private String name;
    private int port;
    private ServerSocket serverSocket;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(8);
    private boolean listening = true;

    public Node(String name, int port) {
        peers = List.of(8000, 8001, 8002);
        this.name = name;
        this.port = port;
        Block first = new Block(0, "root", "data");
        first.setHash("131412f39658375c668a0b8c0f97a2d17f928dfa82d8d4968216981833ef0000");
        blockchain.add(first);
    }

    public Block createBlock() {
        if (blockchain.isEmpty()) throw new NullPointerException();
        Block prev = getPrevious();
        if (prev == null) throw new NullPointerException();
        int index = prev.getIndex() + 1;
        String data = RandomStringUtils.random(256, true, true);
        Block block = new Block(index, prev.getHash(), data);
        broadcast(block, TYPE.NEW);
        return block;
    }

    private Block getPrevious() {
        if (blockchain.isEmpty()) return null;
        return blockchain.get(blockchain.size() - 1);
    }

    public boolean addBlock(Block block) {
        if (isBlockValid(block)) {
            blockchain.add(block);
            return true;
        }
        return false;
    }

    public boolean isBlockValid(Block block) {
        Block prev = getPrevious();
        if (prev == null) return false;
        if (block.getIndex() != prev.getIndex() + 1) return false;
        return Objects.equals(block.getPrev_hash(), prev.getHash());
    }

    public void run() {
        scheduledThreadPoolExecutor.execute(() -> {
            try {
                serverSocket = new ServerSocket(port);
                listening = true;
                while (listening) {
                    new Server(Node.this, serverSocket.accept()).start();
                }
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        broadcast(null, TYPE.REQ);
    }

    private void broadcast(Block block, TYPE type) {
        peers.forEach(peer -> sendMessage(type, peer, block));
    }

    public void sendMessage(TYPE type, int port, Block... blocks) {
        try (Socket socket = new Socket("localhost", port); ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            Object objectFrom;
            while ((objectFrom = objectInputStream.readObject()) != null) {
                if (objectFrom instanceof Message) {
                    Message msg = (Message) objectFrom;
                    if (TYPE.READY == msg.getType()) {
                        objectOutputStream.writeObject(new Message(Arrays.asList(blocks), this.port, port, type));
                    } else if (TYPE.RSP == msg.getType()) {
                        if (!msg.getBlocks().isEmpty() && this.blockchain.size() == 1) {
                            blockchain = new ArrayList<>(msg.getBlocks());
                        }
                        break;
                    }
                }
            }
        } catch (UnknownHostException | ClassNotFoundException unknownHostException) {
            unknownHostException.printStackTrace();
        } catch (IOException ioException) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }
}