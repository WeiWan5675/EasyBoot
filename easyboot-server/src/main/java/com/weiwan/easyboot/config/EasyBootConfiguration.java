package com.weiwan.easyboot.config;

import com.weiwan.easyboot.web.WebConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * event listener 通过spring.factories 加载
 * <p>
 * 通过{@code @ComponentScan }扫描advice 也可以使用{@code @import}
 *
 * {@link BeanValidatorPluginsConfiguration} swagger jsr303 使用
 *
 * 开启简易缓存，主要针对字典及基本配置参数，实时性要求不高的业务进行缓存
 *
 * @author hdf
 */
@Configuration
@EnableOpenApi
@Import({WebConfigurer.class, BeanValidatorPluginsConfiguration.class})
@EnableCaching
@RequiredArgsConstructor
public class EasyBootConfiguration {

    private final BootProperties bootProperties;

    /**
     * doc 配置 springfox + knife4j
     *
     * @return
     * @see <a>http://127.0.0.1:8080/swagger-ui/index.html</a> 已屏蔽
     * @see <a>http://127.0.0.1:8080/doc.html</a>
     * @see <a>http://springfox.github.io/springfox/docs/current/</a>
     */
    @Bean
    public Docket openApiStore() {
        ApiInfo apiInfo = new ApiInfoBuilder().title(bootProperties.getDoc().getName())
            .description(bootProperties.getDoc().getDescription())
            .contact(new Contact(bootProperties.getDoc().getName(), bootProperties.getDoc().getUrl(),
                bootProperties.getDoc().getAuthor()))
            .version(bootProperties.getDoc().getVersion()).build();

        return new Docket(DocumentationType.OAS_30).apiInfo(apiInfo).select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class)).paths(PathSelectors.any()).build()
            .enableUrlTemplating(true);
    }
}
