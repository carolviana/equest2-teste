package com.example.newequest.model;

import androidx.core.util.Pair;

import java.util.LinkedList;

public class BlockAnswer {

    private String blockId;
    private LinkedList<Pair<String, String>> block;

    public BlockAnswer() {
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public LinkedList<Pair<String, String>> getBlock() {
        return block;
    }

    public void setAnswer(Pair<String, String> answer) {
        this.block.add(answer);
    }

    public Pair<String, String> getAnswerById(String id){
        for (Pair<String, String> temp : block){
            if(temp.first.equals(id)){
                return temp;
            }
        }
        return null;
    }
}
