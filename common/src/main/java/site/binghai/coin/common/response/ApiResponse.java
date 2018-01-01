package site.binghai.coin.common.response;

import site.binghai.coin.web.client.ApiException;

/**
 * Created by binghai on 2017/12/19.
 *
 * @ huobi
 */

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
