package site.binghai.coin.common.client;


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
@ConfigurationProperties(prefix = "mns")
@PropertySource("classpath:authentication.properties")
@Data
public class MnsParams {
    private String serverHead;
    private String accountendpoint;
    private String accesskeyid;
    private String accesskeysecret;
    private String queueName;
    private String phone;
    private String tpl;
    private String wxTpl;
    private String wxWaterMonitorTpl;
    private String wxEmergenceNewsTpl;
}
