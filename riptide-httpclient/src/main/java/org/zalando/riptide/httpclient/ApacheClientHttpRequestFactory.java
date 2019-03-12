package org.zalando.riptide.httpclient;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apiguardian.api.API;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.function.Function;

import static org.apiguardian.api.API.Status.STABLE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.HEAD;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpMethod.TRACE;

@API(status = STABLE)
@AllArgsConstructor
public final class ApacheClientHttpRequestFactory implements ClientHttpRequestFactory, DisposableBean {

    public enum Mode {
        STREAMING, BUFFERING
    }

    private final Map<HttpMethod, Function<URI, HttpUriRequest>> methods = ImmutableMap.<HttpMethod, Function<URI, HttpUriRequest>>builder()
            .put(GET, HttpGet::new)
            .put(HEAD, HttpHead::new)
            .put(POST, HttpPost::new)
            .put(PUT, HttpPut::new)
            .put(PATCH, HttpPatch::new)
            .put(DELETE, HttpDelete::new)
            .put(OPTIONS, HttpOptions::new)
            .put(TRACE, HttpTrace::new)
            .build();

    private final HttpClient client;
    private final Mode mode;

    public ApacheClientHttpRequestFactory(final HttpClient client) {
        this(client, Mode.STREAMING);
    }

    @Override
    public ClientHttpRequest createRequest(final URI uri, final HttpMethod method) {
        final HttpUriRequest request = methods.get(method).apply(uri);

        if (mode == Mode.STREAMING) {
            return new StreamingApacheClientHttpRequest(client, request);
        } else {
            return new BufferingApacheClientHttpRequest(client, request);
        }
    }

    @Override
    public void destroy() throws IOException {
        if (client instanceof Closeable) {
            final Closeable closeable = (Closeable) this.client;
            closeable.close();
        }
    }

}
