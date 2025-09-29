package com.seidor.store.utils.loggers;

public class LoggerFactory {
    public static AppLogger getLogger(String originClass, String path) {
        if(originClass != null) {
            if(originClass.contains("SellService")) return new SellLogger();
            if(originClass.contains("ProductService")) return new ProductLogger();
        }
        if(path != null) {
            if(path.contains("/sell")) return new SellLogger();
            if(path.contains("/product")) return new ProductLogger();
        }
        return new AuthLogger(); // default
    }
}
