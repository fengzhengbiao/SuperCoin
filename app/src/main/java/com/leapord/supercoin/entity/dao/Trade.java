package com.leapord.supercoin.entity.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/29
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

@Entity
public class Trade {
    @Id(autoincrement = true)
    private Long id;
    private String symbol;
    private String orderId;
    private String sellType;
    private String amount;
    private String price;
    private boolean status;
    private long createTime = System.currentTimeMillis();
    @Generated(hash = 89647526)
    public Trade(Long id, String symbol, String orderId, String sellType,
            String amount, String price, boolean status, long createTime) {
        this.id = id;
        this.symbol = symbol;
        this.orderId = orderId;
        this.sellType = sellType;
        this.amount = amount;
        this.price = price;
        this.status = status;
        this.createTime = createTime;
    }
    @Generated(hash = 1773414334)
    public Trade() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSymbol() {
        return this.symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public String getOrderId() {
        return this.orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getSellType() {
        return this.sellType;
    }
    public void setSellType(String sellType) {
        this.sellType = sellType;
    }
    public String getAmount() {
        return this.amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    public String getPrice() {
        return this.price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public boolean getStatus() {
        return this.status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public long getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


}
