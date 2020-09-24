package de.spinscale.javalin;

import io.javalin.http.Context;
import io.javalin.http.RequestLogger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4j2RequestLogger implements RequestLogger  {

    private final Logger logger = LoggerFactory.getLogger(Log4j2RequestLogger.class);

    @Override
    public void handle(@NotNull Context ctx, @NotNull Float executionTimeMs) throws Exception {
        String userAgent = ctx.userAgent() != null ? ctx.userAgent() : "-";
        logger.info("{} {} {} {} \"{}\" {}",
                ctx.method(), ctx.req.getPathInfo(), ctx.res.getStatus(),
                ctx.req.getRemoteHost(), userAgent, executionTimeMs.longValue());
    }
}