package org.antsiferov;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class Block implements Serializable {

    private final int index;
    private final String prev_hash;
    private final String data;
    private String hash;
    private Long nonce = 0L;

    public Block(int index, String prev_hash, String data) {
        this.index = index;
        this.prev_hash = prev_hash;
        this.data = data;
        hash = calculateHash(index + prev_hash + data);
    }

    private String calculateHash(String data) {
        StringBuilder hash = new StringBuilder();
        while (!hash.toString().endsWith("0000")) {
            hash.setLength(0);
            String current = nonce.toString() + data;
            hash.append(Hashing.sha256().hashString(current, StandardCharsets.UTF_8));
            nonce++;
        }
        return hash.toString();
    }

    @Override
    public String toString() {
        return String.format("[index = %s; prev_hash = %s; hash = %s; data = %s; nonce = %s]", index, prev_hash, hash, data, nonce);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return index == block.index && nonce.equals(block.nonce) && hash.equals(block.hash) && prev_hash.equals(block.prev_hash) && data.equals(block.data);
    }
}