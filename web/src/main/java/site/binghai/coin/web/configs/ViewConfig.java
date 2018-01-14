package site.binghai.coin.web.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by binghai on 2018/1/1.
 *
 * @ huobi
 */
@Configuration
public class ViewConfig extends WebMvcConfigurerAdapter {
    /**
     * 无逻辑处理，直接url -> page 的映射关系
     * */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/relation.html").setViewName("relation");
        super.addViewControllers(registry);
    }
}
