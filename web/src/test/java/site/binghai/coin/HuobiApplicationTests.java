package site.binghai.coin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import site.binghai.coin.common.client.ApiClient;
import site.binghai.coin.common.entity.WaterLevelMonitor;
import site.binghai.coin.common.utils.SmsNoticeService;
import site.binghai.coin.common.utils.TimeFormat;
import site.binghai.coin.common.utils.WxNoticeService;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HuobiApplicationTests {

	@Autowired
	private ApiClient apiClient;
	@Autowired
	private WxNoticeService wxNoticeService;
	@Autowired
	private SmsNoticeService smsNoticeService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testListOrder(){
		System.out.println(apiClient.listOrder("usdt"));
	}

	private static String openid = "oz1S1v_7W7O1t-KxfdFK5Sk6eJVs";
	@Test
	public void wxNotice(){
		WaterLevelMonitor waterLevelMonitor = new WaterLevelMonitor();
		waterLevelMonitor.setWxNotice(openid);
		waterLevelMonitor.setTargetValue(1000.0);
		waterLevelMonitor.setBaseCoin("btc");
		waterLevelMonitor.setQuoteCoin("usdt");
		waterLevelMonitor.setId(1l);
		waterLevelMonitor.setCreatedTime(TimeFormat.format(System.currentTimeMillis()));

		wxNoticeService.wxNoticeWaterLevelMonitoring(waterLevelMonitor,"99999"," - ADD"," - remark");
	}


}
