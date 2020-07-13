package com.yh.config;

import com.yh.EasyRpcSpringException;
import com.yh.ref.EasyRpcReference;
import com.yh.refer.Reference;
import com.yh.refer.References;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * @ClassName InterfaceCallConfiguration
 * @Description
 * @Author yh
 * @Date 2019/12/24 18:24
 * @Version 1.0
 */
public class InterfaceCallProccessor implements BeanPostProcessor {

    private String basePackage;

    InterfaceCallProccessor(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(basePackage==null||"".equals(basePackage)) {
            return bean;
        }
        Class clazz = bean.getClass();
        if(AopUtils.isAopProxy(bean)) {
            clazz = AopUtils.getTargetClass(bean);
        }
        String className = clazz.getName();
        if(!className.startsWith(basePackage)) {
            return bean;
        }
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            if(!field.isAccessible()) {
                field.setAccessible(Boolean.TRUE);
            }
            EasyRpcReference easyRpcReferenceAnnotation = field.getAnnotation(EasyRpcReference.class);
            if(easyRpcReferenceAnnotation == null) {
                continue;
            }
            if(!field.getType().isInterface()) {
                throw new IllegalArgumentException("@EasyRpcReference只可用于interface");
            }
            Class fieldType = field.getType();
            try {
                String key = easyRpcReferenceAnnotation.instanceName()+"/"+fieldType.getName()+"/"+easyRpcReferenceAnnotation.beanName();
                Reference ref = References.getReference(key);
                if(ref != null) {
                    field.set(bean,ref.getRefProxy());
                } else {
                    ref = new Reference(easyRpcReferenceAnnotation,fieldType);
                    if(ref != null) {
                        References.addReference(key,ref);
                        field.set(bean,ref.getRefProxy());
                    }
                }
            } catch (Exception e) {
                throw new EasyRpcSpringException(e.getMessage());
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
