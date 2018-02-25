package site.binghai.coin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import site.binghai.coin.common.client.ApiClient;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HuobiApplicationTests {

	@Autowired
	private ApiClient apiClient;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testListOrder(){
		System.out.println(apiClient.listOrder("usdt"));
	}
}
