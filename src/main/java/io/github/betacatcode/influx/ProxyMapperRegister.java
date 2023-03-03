package io.github.betacatcode.influx;

import io.github.betacatcode.influx.utils.ManualRegisterBeanUtil;
import io.github.betacatcode.influx.utils.StrUtil;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashSet;

public class ProxyMapperRegister {

    private String mapperLocation;
    ConfigurableApplicationContext applicationContext;
    ResourceLoader resourceLoader;

    public ProxyMapperRegister(String mapperLocation, ConfigurableApplicationContext applicationContext, ResourceLoader resourceLoader) {
        this.mapperLocation = mapperLocation;
        this.applicationContext = applicationContext;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void afterInit() {
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
                if (resource.isReadable()) {
                    metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> aClass = Class.forName(className);

                    //当这个Mapper实现InfluxBaseMapper时加集合
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        if (anInterface == InfluxDBBaseMapper.class) {
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
            ManualRegisterBeanUtil.registerBean(applicationContext, StrUtil.captureName(aClass.getSimpleName()), InfluxProxyMapperFactory.class, aClass);
        }
    }
}
