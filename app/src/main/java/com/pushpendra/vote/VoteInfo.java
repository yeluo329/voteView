package com.pushpendra.vote;

import java.util.ArrayList;
import java.util.List;

public class VoteInfo {
    /**
     * 如果用户为0代表为没有选择,如果非0根据optionInfo的id进行区分
     */
    private int myOption = 0;
    private List<optionInfo> optionInfoList = new ArrayList<>();

    public VoteInfo(int myOption, List<optionInfo> optionInfoList) {
        this.myOption = myOption;
        this.optionInfoList = optionInfoList;
    }

    public List<optionInfo> getOptionInfoList() {
        return optionInfoList;
    }

    public int getMyOption() {
        return myOption;
    }

    static class optionInfo {
        private int id;
        private String name = "";
        private int rate = 0;

        public optionInfo(String name, int rate, int id) {
            this.name = name;
            this.rate = rate;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getRate() {
            return rate;
        }
    }
}
