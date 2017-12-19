package site.binghai.coin.response;

import lombok.Data;
import lombok.ToString;

/**
 * Created by binghai on 2017/12/19.
 *
 * @ huobi
 */
@Data
@ToString
public class Account {
    public long id;
    public String type;
    public String state;
}