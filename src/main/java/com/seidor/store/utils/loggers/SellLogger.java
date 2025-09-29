package com.seidor.store.utils.loggers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class SellLogger implements AppLogger{
    private static final Logger logger = LogManager.getLogger("sellLogger");
    @Override
    public void logError(String message) {
        try {
            logger.error(message);
        } finally {
            ThreadContext.clearAll();
        }
    }

    @Override
    public void logInfo(String message) {
        try {
            logger.info(message);
        } finally {
            ThreadContext.clearAll();
        }
    }
}
