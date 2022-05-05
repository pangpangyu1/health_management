package com.pangpangyu;

import org.junit.Test;

public class TestDate {
    @Test
    public void test(){
        String date = "2022-4";
//        String substring = s.substring(s.lastIndexOf("-")+1);
//        System.out.println(substring);
//        String[] split = s.split("-");
//        System.out.println(split[0]);

        String begin = null;
        String end = null;
        String[] split = date.split("-");//4
        String monthNumber = split[1];
        String yearNumber = split[0];
        if (monthNumber != null) {
            if( "1".equals(monthNumber) || "3".equals(monthNumber) ||
                    "5".equals(monthNumber) || "7".equals(monthNumber) ||
                    "8".equals(monthNumber) || "10".equals(monthNumber) || "12".equals(monthNumber)) {
                begin = date + "-1";
                end = date + "-30";
            }else if("2".equals(monthNumber)){
                if(Integer.parseInt(yearNumber) % 4 == 0){
                    begin = date + "-1";
                    end = date + "-29";
                }else {
                    begin = date + "-1";
                    end = date + "-28";
                }
            }else if("4".equals(monthNumber) || "6".equals(monthNumber) ||
                    "9".equals(monthNumber) || "11".equals(monthNumber)){
                begin = date + "-1";
                end = date + "-30";
            }
        }else{
            begin = date + "-1";
            end = date + "-31";
        }
//        System.out.println(end);

    }
}
