package site.binghai.coin.web.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import site.binghai.coin.web.inters.AdminInterceptor;

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
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/admin/").setViewName("adminDashboard");
        registry.addViewController("/historyOrder").setViewName("historyOrder");
        super.addViewControllers(registry);
    }

    @Bean
    public AdminInterceptor adminInterceptor(){
        return new AdminInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor()).addPathPatterns("/admin/**");
        super.addInterceptors(registry);
    }
}
