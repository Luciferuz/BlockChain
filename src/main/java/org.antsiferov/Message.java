package org.antsiferov;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {

    private List<Block> blocks;
    private int sender;
    private int receiver;
    private TYPE type;

    @Override
    public String toString() {
        return String.format("\ntimestamp = %s, \nsender = %d, \nreceiver = %d, \nblocks = %s", System.currentTimeMillis(), sender, receiver, blocks);
    }
}