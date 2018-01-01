package site.binghai.coin;

import org.junit.Test;

/**
 * Created by binghai on 2017/12/31.
 *
 * @ huobi
 */
public class SimpleTest {
    @Test
    public void stringTest(){
        System.out.println(String.format("%-5s %-5s %-4d","aa","bb",12));
        System.out.println(String.format("%-5s %-5s %-4d","a","bb",12));
        System.out.println(String.format("%-5s %-5s %-4d","aaa","b",12));
    }
}
