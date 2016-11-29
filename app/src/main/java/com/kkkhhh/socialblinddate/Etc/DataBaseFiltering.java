package com.kkkhhh.socialblinddate.Etc;

/**
 * Created by Dev1 on 2016-11-16.
 */

public class DataBaseFiltering {

    public String changeLocal(String local) {
        switch (local) {
            case "서울":
                local = "seoul";
                break;
            case "부산":
                local = "busan";
                break;
            case "인천":
                local = "incheon";
                break;
            case "대구":
                local = "deagu";
                break;
            case "대전":
                local = "deajoen";
                break;
            case "울산":
                local = "ulsan";
                break;
            case "광주":
                local = "gwangju";
                break;
            case "세종":
                local = "sejong";
                break;
            case "경기":
                local = "kyunggi";
                break;
            case "경남":
                local = "kyungnam";
                break;
            case "경북":
                local = "kyungbuk";
                break;
            case "전남":
                local = "jeonnam";
                break;
            case "전북":
                local = "jeonbuk";
                break;
            case "강원":
                local = "ikangwon";
                break;
            case "제주":
                local = "jeju";
                break;
            case "충북":
                local = "chungbuk";
                break;
            case "충남":
                local = "chungnam";
                break;
            default:
                break;

        }

        return local;
    }
}
