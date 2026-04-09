package mate.academy.lib;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import mate.academy.service.FileReaderService;
import mate.academy.service.ProductParser;
import mate.academy.service.ProductService;
import mate.academy.service.impl.FileReaderServiceImpl;
import mate.academy.service.impl.ProductParserImpl;
import mate.academy.service.impl.ProductServiceImpl;

public class Injector {
    private static final Injector injector = new Injector();

    private Map<Class<?>, Object> instances = new HashMap<>();

    public static Injector getInjector() {
        return injector;
    }

    public Class<?> findImplementation(Class<?> interfaceClazz) {
        Map<Class<?>, Class<?>> findImplementation = new HashMap<>();
        findImplementation.put(FileReaderService.class, FileReaderServiceImpl.class);
        findImplementation.put(ProductParser.class, ProductParserImpl.class);
        findImplementation.put(ProductService.class, ProductServiceImpl.class);

        if (interfaceClazz.isInterface()) {
            return findImplementation.get(interfaceClazz);
        }
        return interfaceClazz;
    }

    public Object getInstance(Class<?> interfaceClazz) throws RuntimeException {
        Class<?> clazz = findImplementation(interfaceClazz);
        
        if (!clazz.isAnnotationPresent(Component.class)) {
            throw new RuntimeException("...");
        }

        if (instances.containsKey(clazz)) {
            return instances.get(clazz);
        }

        try {

            Object newinstance = clazz.getDeclaredConstructor().newInstance();

            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    field.set(newinstance, injector.getInstance(field.getType()));
                }
            }
            instances.put(clazz, newinstance);

            return newinstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
