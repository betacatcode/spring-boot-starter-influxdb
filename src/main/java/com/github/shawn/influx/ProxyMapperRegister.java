package com.github.shawn.influx;

import com.github.shawn.influx.utils.ManualRegisterBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.HashSet;

@Slf4j
@Component
public class ProxyMapperRegister implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${spring.influx.mapper-location}")
    private String mapperLocation;
    @Autowired
    ConfigurableApplicationContext applicationContext;
    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("注册InfluxDBMapper...");
        //获取mapper包下所有的Mapper
        HashSet<Class<?>> classes = new HashSet<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                .concat(ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(mapperLocation))
                        .concat("/**/*.class"));
        ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        MetadataReader metadataReader;
        try {
            Resource[] resources = resolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if(resource.isReadable()){
                    metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> aClass = Class.forName(className);

                    //当这个Mapper实现InfluxBaseMapper时加入集和
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        if(anInterface== InfluxDBBaseMapper.class){
                            classes.add(aClass);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (Class<?> aClass : classes) {
            //注册接口类至工厂Bean，再用动态代理生成对应Mapper
            ManualRegisterBeanUtil.registerBean(applicationContext, aClass.getSimpleName(),InfluxProxyMapperFactory.class,aClass);
        }
    }
}