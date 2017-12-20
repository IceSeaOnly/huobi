package site.binghai.coin.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by binghai on 2017/12/20.
 *
 * @ huobi
 */
@Component
@ConfigurationProperties(prefix = "param")
@PropertySource("classpath:authentication.properties")
@Data
public class CoreParams {
    private double btc2rmb;
    private double usdt2rmb;
    private double marginRate;
    private double spotDeviationRate;
    private double marginDeviationRate;
    private double otcDeviationRate;
}
