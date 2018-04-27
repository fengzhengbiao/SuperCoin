package com.leapord.supercoin.app;

/**
 * @author Biao
 * @date 2018/1/20
 * @description usdt交易区coin类型
 * @email fengzb0216@sina.com
 */

public interface OkCoin {


    interface Trade {

        //限价买卖
        String BUY = "buy";

        String SELL = "sell";

        //市价买卖
        String BUY_MARKET = "buy_market";

        String SELL_MARKET = "sell_market";

        String CANCEL = "cancel";
    }


    interface CONTRACT_TYPE {

        String THIS_WEEK = "this_week";

        String NEXT_WEEK = "next_week";

        String QUARTER = "quarter";

    }

    interface FUTURE_TYPE {

        String OPEN_INCREASE = "1";        //开多

        String OPEN_DECREASE = "2";       //开空

        String CLOSE_INCREASE = "3";       //平多

        String CLOSE_DECREASE = "4";       //平空
    }


    interface API {
        //        String API_KEY = "2c31b63a-9d64-4889-935c-25f319102832";  //yuan
        String API_KEY = "7752d9e0-d261-491a-8460-49920a13f0ab";          //feng

//        String API_KEY = "dc26f54e-8479-4a10-ad44-d39108696f68";        //liu

        //        String SECRET_KEY = "241CFFFF941F14A3320DC5F9AC349955";
        String SECRET_KEY = "625133937D92A58E9F04BB784A51BC35";
//        String SECRET_KEY = "6DE251C160C8318B7AAA0FD4E7739E47";
    }

    long ONE_PERIOD = 15 * 60 * 1000;

    long ONE_TDAY = 24 * 60 * 60 * 1000;


    double MIN_COIN_AMOUNT = 0.01;

    double MIN_COIN_RIGHT = 0.1;
}
