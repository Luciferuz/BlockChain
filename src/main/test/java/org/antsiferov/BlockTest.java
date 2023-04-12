package org.antsiferov;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockTest {

    private Node first;
    private Node second;
    private Node third;

    @Test
    public void testAddBlock() {
        System.out.println(third.getBlockchain());
        Block block1 = new Block(2, third.getBlockchain().get(1).getHash(), "data");
        Block block2 = new Block(3, block1.getHash(), "more data");
        assertTrue(third.addBlock(block1));
        assertTrue(third.addBlock(block2));
        assertFalse(third.addBlock(block1));
        assertFalse(third.addBlock(block2));
    }

    @Test
    public void testBlockCreation() {
        Block block = new Block(1, "previous_hash", "data");
        assertNotNull(block);
        assertEquals(block.getIndex(), 1);
        assertEquals(block.getPrev_hash(), "previous_hash");
        assertEquals(block.getData(), "data");
        assertNotNull(block.getHash());
    }

    @Test
    public void testCalculateHash() {
        Block block = new Block(1, "previous_hash", "data");
        assertNotNull(block.getHash());
        assertEquals(block.getHash().length(), 64);
    }

    @Test
    public void testCreateBlockNode() {
        Node node = new Node("node", 8005);
        node.createBlock();
        assertEquals(1, node.getBlockchain().size());
    }

    @Test
    public void testIsBlockValid() {
        Node node = new Node("node", 8009);
        Block block1 = new Block(1, "root", "data");
        Block block2 = new Block(2, block1.getHash(), "more data");
        assertFalse(node.isBlockValid(new Block(2, "root", "data")));
        assertFalse(node.isBlockValid(new Block(3, block2.getHash(), "even more data")));
    }


    @BeforeEach
    void setUp() {
        first = new Node("first", 8000);
        second = new Node("second", 8001);
        third = new Node("third", 8002);
        first.run();
        second.run();
        third.run();
    }

    @Test
    void testFirstBlock() {
        Block firstBlock1 = first.getBlockchain().get(0);
        Block firstBlock2 = second.getBlockchain().get(0);
        Block firstBlock3 = third.getBlockchain().get(0);
        assertEquals(firstBlock1, firstBlock2);
        assertEquals(firstBlock1, firstBlock3);
        assertEquals(firstBlock2, firstBlock3);
    }

    @Test
    void testName() {
        Block firstBlock = first.getBlockchain().get(0);
        Block secondBlock = second.getBlockchain().get(0);
        Block thirdBlock = third.getBlockchain().get(0);
        assertTrue(firstBlock.getHash().endsWith("0000"));
        assertTrue(secondBlock.getHash().endsWith("0000"));
        assertTrue(thirdBlock.getHash().endsWith("0000"));
    }

    @Test
    void testGenerate(){
        Block block1 = first.createBlock();
        Block block2 = second.getBlockchain().get(1);
        Block block3 = third.getBlockchain().get(1);
        assertEquals(block1,block2);
        assertEquals(block1,block3);
        assertEquals(block2,block3);
    }

    @Test
    void testToString() {
        Block block = new Block(0, "prev", "data");
        assertEquals("[index = 0; prev_hash = prev; hash = 427b430a2670ecbee023af81b0efe6fb5e79810a0fe439adc382f6d648a50000; data = data; nonce = 25498]", block.toString());
    }

    @Test
    void equals() {
        Block block = new Block(11, "prev_hash", "dataaaaa");
        Block another = new Block(11, "prev_hash", "dataaaaa");
        assertEquals(block, another);
    }

}