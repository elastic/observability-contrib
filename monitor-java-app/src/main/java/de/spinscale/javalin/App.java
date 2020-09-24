package de.spinscale.javalin;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.attach.ElasticApmAttacher;
import io.javalin.Javalin;
import io.javalin.core.security.BasicAuthCredentials;
import io.javalin.http.Handler;
import io.javalin.plugin.metrics.MicrometerPlugin;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        ElasticApmAttacher.attach();

        Javalin app = Javalin.create(config -> {
            config.registerPlugin(new MicrometerPlugin());
            config.requestLogger(new Log4j2RequestLogger());
        });

        app.after(ctx -> ElasticApm.currentTransaction().setName(ctx.method() + " " + ctx.endpointHandlerPath()));

        app.get("/", mainHandler());

        app.get("/exception", ctx -> {
            throw new IllegalArgumentException("not yet implemented");
        });

        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Exception found", e);
            ctx.status(500).result(e.getMessage());
        });

        final Micrometer micrometer = new Micrometer();
        app.get("/metrics", ctx -> {
            ctx.status(404);
            if (ctx.basicAuthCredentialsExist()) {
                final BasicAuthCredentials credentials = ctx.basicAuthCredentials();
                if ("metrics".equals(credentials.getUsername()) && "secret".equals(credentials.getPassword())) {
                    ctx.status(200).result(micrometer.scrape());
                }
            }
        });

        final Executor executor = CompletableFuture.delayedExecutor(20, TimeUnit.SECONDS);
        app.get("/wait", ctx -> {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "done", executor);
            ctx.result(future);
        });

        app.get("/weather/:city", ctx -> {
            String city = ctx.pathParam("city");
            ctx.result(Request.Get("https://wttr.in/" + city + "?format=3").execute()
                    .returnContent().asBytes())
                    .contentType("text/plain; charset=utf-8");
        });

        app.start(7000);
    }

    static Handler mainHandler() {
        return ctx -> {
            String userAgent = ctx.userAgent() != null ? ctx.userAgent() : "-";
            logger.info("This is an informative logging message, user agent [{}]", userAgent);
            ctx.result("Appsolutely perfect");
        };
    }
}
