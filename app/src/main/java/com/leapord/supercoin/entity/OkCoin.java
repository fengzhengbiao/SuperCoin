package com.leapord.supercoin.entity;

/**
 * @author Biao
 * @date 2018/1/20
 * @description usdt交易区coin类型
 * @email fengzb0216@sina.com
 */

public interface OkCoin {


    /**
     * USDT交易区币种
     */
    interface USDT {

        String BTC = "btc_usdt";

        String ETH = "eth_usdt";

        String LTC = "ltc_usdt";

        String ETC = "etc_usdt";

        String BCH = "bch_usdt";

        String HSR = "hsr_usdt";

        String NEO = "neo_usdt";

        String GAS = "gas_usdt";

        String SWFTC = "swftc_usdt";
    }

    /**
     * K线时长
     */
    interface TimePeriod {

        String ONE_MIN = "1min";

        String THREE_MIN = "3min";

        String FIVE_MIN = "5min";

        String FIFTEEN_MIN = "5min";

        String THITY_MIN = "30min";

        String ONE_HOUR = "1hour";

        String TWO_HOUR = "2hour";

        String FOUR_HOUR = "4hour";

        String SIX_HOUR = "6hour";


        String ONE_DAY = "1day";

        String THREE_DAY = "3day";

        String ONE_WEEK = "1week";

    }

    interface Trade {

        //限价买卖
        String BUY = "buy";

        String SELL = "sell";

        //市价买卖
        String BUY_MARKET = "buy_market";

        String SELL_MARKET = "sell_market";
    }

    interface API{
        String API_KEY = "ad03fc60-64e7-4464-8806-cce98ba7fd07";

        String SECRET_KEY = "E13734459A6ED1CF4F9BD871FED95323";
    }

}