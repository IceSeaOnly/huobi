package site.binghai.coin.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by binghai on 2017/12/19.
 *
 * @ huobi
 */
@Component
@ConfigurationProperties(prefix = "auth")
@PropertySource("classpath:authentication.properties")
@Data
public class AuthParams {
    private String accessKeyId;
    private String accessKeySecret;
    private String assetPassword;
}
