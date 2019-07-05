package cn.moke.generator.config;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author moke
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${moke.swagger.base-package}")
    private String basePackage;
    @Value("${moke.swagger.title}")
    private String title;
    @Value("${moke.swagger.description}")
    private String description;
    @Value("${moke.swagger.version}")
    private String version;
    @Value("${moke.swagger.terms-of-service-url}")
    private String termsOfServiceUrl;
    @Value("${moke.swagger.contact.name}")
    private String contactName;
    @Value("${moke.swagger.contact.url}")
    private String contactUrl;
    @Value("${moke.swagger.contact.email}")
    private String contactEmail;


    @Bean
    public Docket api() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
                .name("Authorization")
                .description("认证token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .defaultValue("Bearer ")
                .build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 自行修改为自己的包路径
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(parameters);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 页面标题
                .title(title)
                // 描述
                .description(description)
                // 版本
                .version(version)
                .termsOfServiceUrl(termsOfServiceUrl)
                // 联系人
                .contact(new Contact(contactName,contactUrl,contactEmail))
                .build();
    }
}