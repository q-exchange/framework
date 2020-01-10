package cn.ztuo.bitrade.hbapi.response;


import cn.ztuo.bitrade.hbapi.hbclient.ApiException;

public class ApiResponse<T> {

    public String status;
    public String errCode;
    public String errMsg;
    public T data;

    public T checkAndReturn() {
        if ("ok".equals(status)) {
            return data;
        }
        throw new ApiException(errCode, errMsg);
    }
}
