package com.poof.crawler.reviews;

/**
 * @author wilkey 
 * @mail admin@wilkey.vip
 * @Date 2017年1月10日 下午4:28:25
 */
public class FinalCounter {
    private int val;

    public FinalCounter(int intialVal) {
        val=intialVal;
    }
    public void increment(){
        val++;
    }
    public void decrement(){
        val--;
    }
    public int getVal(){
        return val;
    }
    
}