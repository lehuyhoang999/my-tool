package com.hoangcode.mytool.common.config;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

  @Autowired
  private Environment env;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // CSS locations
    registry.addResourceHandler("/css/**")
        .addResourceLocations("classpath:/static/css/");

    // JS locations
    registry.addResourceHandler("/js/**")
        .addResourceLocations("classpath:/static/js/");

    System.out.println("********************************************** INIT RESOURCE HANDLERS ***************************");
  }

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }

  @Bean
  public LocaleResolver localeResolver() {
    CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
    cookieLocaleResolver.setDefaultLocale(Locale.getDefault());
//    cookieLocaleResolver.setDefaultLocale(Locale.forLanguageTag("vi"));
    return cookieLocaleResolver;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // Change locale with url, e.g ?lang=vi, ?lang=en
    LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setParamName("lang");
    registry.addInterceptor(localeChangeInterceptor);
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addFormatter(new DateFormatter(env.getProperty("spring.jackson.date-format")));
    registry.addFormatter(new DateFormatter("dd/MM/yyyy"));
    registry.addFormatter(new NumberStyleFormatter("#,###.##"));
    registry.addFormatter(new NumberStyleFormatter("#,###"));
  }

  @Bean(name = "validationMessages")
  public MessageSource validationMessagesSource() {
    ReloadableResourceBundleMessageSource bean = new ReloadableResourceBundleMessageSource();
    bean.addBasenames("classpath:ValidationMessages", "classpath:messages");
    bean.setDefaultEncoding("UTF-8");
    return bean;
  }

  /**
   * Hỗ trợ i18n cho validation
   * @return
   */
  @Bean(name = "validator")
  public LocalValidatorFactoryBean validator() {
    LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
    bean.setValidationMessageSource(validationMessagesSource());
    return bean;
  }

  @Override
  public Validator getValidator() {
    return validator();
  }

}
