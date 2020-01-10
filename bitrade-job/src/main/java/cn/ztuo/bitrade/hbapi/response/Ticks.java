package cn.ztuo.bitrade.hbapi.response;

import java.util.List;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 14:16
 */

public class Ticks {

    /**
     * id : 1499225271
     * ts : 1499225271000
     * close : 1885
     * open : 1960
     * high : 1985
     * low : 1856
     * amount : 81486.2926
     * count : 42122
     * vol : 1.57052744857082E8
     * ask : [1885,21.8804]
     * bid : [1884,1.6702]
     */

    private long id;
    private long ts;
    private double close;
    private double open;
    private double high;
    private double low;
    private double amount;
    private double count;
    private double vol;
    private List<Double> ask;
    private List<Double> bid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }

    public List<Double> getAsk() {
        return ask;
    }

    public void setAsk(List<Double> ask) {
        this.ask = ask;
    }

    public List<Double> getBid() {
        return bid;
    }

    public void setBid(List<Double> bid) {
        this.bid = bid;
    }
}
