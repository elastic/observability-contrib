package de.spinscale.javalin;

import io.javalin.http.Context;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static de.spinscale.javalin.App.mainHandler;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AppTests {

    final HttpServletRequest req = mock(HttpServletRequest.class);
    final HttpServletResponse res = mock(HttpServletResponse.class);
    final Context ctx = new Context(req, res, new HashMap<>());

    @Test
    public void testMainHandler() throws Exception {
        mainHandler().handle(ctx);

        String response = resultStreamToString(ctx);
        assertThat(response).isEqualTo("Appsolutely perfect");
    }

    private String resultStreamToString(Context ctx) throws IOException {
        final byte[] bytes = ctx.resultStream().readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }
}

