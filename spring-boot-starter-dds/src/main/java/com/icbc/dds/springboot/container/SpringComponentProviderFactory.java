package com.icbc.dds.springboot.container;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCManagedComponentProvider;
import com.sun.jersey.spi.inject.Inject;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * The Spring-based {@link IoCComponentProviderFactory}.
 * <p>
 * Resource and provider classes can be registered Spring-based beans using
 * XML-based registration or auto-wire-based registration.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class SpringComponentProviderFactory implements IoCComponentProviderFactory {

    private static final Logger logger = LoggerFactory.getLogger(SpringComponentProviderFactory.class);

    private final ApplicationContext springContext;

    public SpringComponentProviderFactory(ResourceConfig rc, ApplicationContext springContext) {

        this.springContext = springContext;

        addAppContextInjectableProvider(rc);
        registerSpringBeans(rc);
    }

    private void addAppContextInjectableProvider(final ResourceConfig rc) {
        rc.getSingletons().add(new SingletonTypeInjectableProvider<Context, ApplicationContext>(ApplicationContext.class, springContext) {});
    }

    private void registerSpringBeans(final ResourceConfig rc) {

        String[] names = BeanFactoryUtils.beanNamesIncludingAncestors(springContext);

        for (String name : names) {
            Class<?> type = ClassUtils.getUserClass(springContext.getType(name));
            if (ResourceConfig.isProviderClass(type)) {
                logger.info("Registering Spring bean, " + name +
                        ", of type " + type.getName() +
                        " as a provider class");
                rc.getClasses().add(type);
            } else if (ResourceConfig.isRootResourceClass(type)) {
                logger.info("Registering Spring bean, " + name +
                        ", of type " + type.getName() +
                        " as a root resource class");
                rc.getClasses().add(type);
            }
        }
    }

    @Override
    public IoCComponentProvider getComponentProvider(Class c) {
        return getComponentProvider(null, c);
    }

    @Override
    public IoCComponentProvider getComponentProvider(ComponentContext cc, Class c) {
        final Annotation autowire = c.getAnnotation(Autowire.class);
        if (autowire != null) {
            logger.debug("Creating resource class " +
                    c.getSimpleName() +
                    " annotated with @" +
                    Autowire.class.getSimpleName() +
                    " as spring bean.");
            return new SpringInstantiatedComponentProvider(c, autowire);
        }

        final String beanName = getBeanName(cc, c, springContext);
        if (beanName == null) {
            return null;
        }

        return new SpringManagedComponentProvider(getComponentScope(BeanDefinition.SCOPE_PROTOTYPE), beanName, c);
    }


    private ComponentScope getComponentScope(String scope) {
        ComponentScope cs = scopeMap.get(scope);
        return (cs != null) ? cs : ComponentScope.Undefined;
    }

    private final Map<String, ComponentScope> scopeMap = createScopeMap();

    private Map<String, ComponentScope> createScopeMap() {
        Map<String, ComponentScope> m = new HashMap<String, ComponentScope>();
        m.put(BeanDefinition.SCOPE_SINGLETON, ComponentScope.Singleton);
        m.put(BeanDefinition.SCOPE_PROTOTYPE, ComponentScope.PerRequest);
        m.put("request", ComponentScope.PerRequest);
        return m;
    }

    private class SpringInstantiatedComponentProvider implements IoCInstantiatedComponentProvider {

        private final Class c;
        private final Annotation a;

        SpringInstantiatedComponentProvider(Class c, Annotation a) {
            this.c = c;
            this.a = a;
        }

        @Override
        public Object getInstance() {
            return springContext.getBean(c);
        }

        @Override
        public Object getInjectableInstance(Object o) {
            return SpringComponentProviderFactory.getInjectableInstance(o);
        }
    }

    private class SpringManagedComponentProvider implements IoCManagedComponentProvider {

        private final ComponentScope scope;
        private final String beanName;
        private final Class c;

        SpringManagedComponentProvider(ComponentScope scope, String beanName, Class c) {
            this.scope = scope;
            this.beanName = beanName;
            this.c = c;
        }

        @Override
        public ComponentScope getScope() {
            return scope;
        }

        @Override
        public Object getInjectableInstance(Object o) {
            return SpringComponentProviderFactory.getInjectableInstance(o);
        }

        @Override
        public Object getInstance() {
            return springContext.getBean(beanName, c);
        }
    }

    private static Object getInjectableInstance(Object o) {
        if (AopUtils.isAopProxy(o)) {
            final Advised aopResource = (Advised) o;
            try {
                return aopResource.getTargetSource().getTarget();
            } catch (Exception e) {
                logger.error("Could not get target object from proxy.", e);
                throw new RuntimeException("Could not get target object from proxy.", e);
            }
        } else {
            return o;
        }
    }

    private static String getBeanName(ComponentContext cc, Class<?> c, ApplicationContext springContext) {
        boolean annotatedWithInject = false;
        if (cc != null) {
            final Inject inject = getAnnotation(cc.getAnnotations(), Inject.class);
            if (inject != null) {
                annotatedWithInject = true;
                if (inject.value() != null && !inject.value().equals("")) {
                    return inject.value();
                }

            }

            final InjectParam injectParam = getAnnotation(cc.getAnnotations(), InjectParam.class);
            if (injectParam != null) {
                annotatedWithInject = true;
                if (injectParam.value() != null && !injectParam.value().equals("")) {
                    return injectParam.value();
                }

            }
        }

        final String names[] = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(springContext, c);

        if (names.length == 0) {
            return null;
        } else if (names.length == 1) {
            return names[0];
        } else {
            // Check if types of the beans names are assignable
            // Spring auto-registration for a type A will include the bean
            // names for classes that extend A
            boolean inheritedNames = false;
            String beanName = null;
            for (String name : names) {
                Class<?> beanType = ClassUtils.getUserClass(springContext.getType(name));

                inheritedNames = c.isAssignableFrom(beanType);

                if (c == beanType)
                    beanName = name;
            }

            if (inheritedNames) {
                if (beanName != null)
                    return beanName;
            }

            final StringBuilder sb = new StringBuilder();
            sb.append("There are multiple beans configured in spring for the type ").
                    append(c.getName()).append(".");

            if (annotatedWithInject) {
                sb.append("\nYou should specify the name of the preferred bean with @InjectParam(\"name\") or @Inject(\"name\").");
            } else {
                sb.append("\nAnnotation information was not available, the reason might be because you're not using " +
                        "@InjectParam. You should use @InjectParam and specifiy the bean name via InjectParam(\"name\").");
            }

            sb.append("\nAvailable bean names: ").append(toCSV(names));

            throw new RuntimeException(sb.toString());
        }
    }

    private static <T extends Annotation> T getAnnotation(Annotation[] annotations,
                                                          Class<T> clazz) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(clazz)) {
                    return clazz.cast(annotation);
                }
            }
        }
        return null;
    }

    private static <T> String toCSV(T[] items) {
        if (items == null) {
            return null;
        }
        return toCSV(Arrays.asList(items));
    }

    private static <I> String toCSV(Collection<I> items) {
        return toCSV(items, ", ", null);
    }

    private static <I> String toCSV(Collection<I> items, String separator, String delimiter) {
        if (items == null) {
            return null;
        }
        if (items.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final Iterator<I> iter = items.iterator(); iter.hasNext();) {
            if (delimiter != null) {
                sb.append(delimiter);
            }
            final I item = iter.next();
            sb.append(item);
            if (delimiter != null) {
                sb.append(delimiter);
            }
            if (iter.hasNext()) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }
}
