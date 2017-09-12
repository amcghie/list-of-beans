package au.com.agilepractices.examples.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@SpringBootApplication
@EnableConfigurationProperties(ExampleProperties.class)
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        Arrays.stream(applicationContext.getBeanDefinitionNames())
                .sorted()
                .forEach(System.out::println);
        ((List<SomeObject>)applicationContext.getBean("someObjects"))
                .forEach(System.out::println);
    }

    @Bean
    public SomeObjectFactory someObjectFactory(ExampleProperties exampleProperties) {
        return new SomeObjectFactory(exampleProperties);
    }

    @Bean
    public List<SomeObject> someObjects(SomeObjectFactory someObjectFactory) throws Exception {
        return someObjectFactory.getObject();
    }

    public static class SomeObject {

        private final String name;

        public SomeObject(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class SomeObjectFactory implements FactoryBean<List<SomeObject>> {

        private final ExampleProperties exampleProperties;

        public SomeObjectFactory(ExampleProperties exampleProperties) {
            this.exampleProperties = exampleProperties;
        }

        @Override
        public List<SomeObject> getObject() throws Exception {
            return exampleProperties
                    .getNames()
                    .stream()
                    .map(SomeObject::new)
                    .collect(Collectors.toList());
        }

        @Override
        public Class<?> getObjectType() {
            return SomeObject.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }
}
