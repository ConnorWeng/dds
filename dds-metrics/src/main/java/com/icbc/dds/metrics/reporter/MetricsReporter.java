package com.icbc.dds.metrics.reporter;

import com.codahale.metrics.*;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by kfzx-wengxj on 16/01/2017.
 */
public class MetricsReporter extends ScheduledReporter {
    private Client client;

    public MetricsReporter(MetricRegistry registry,
                           String name,
                           MetricFilter filter,
                           TimeUnit rateUnit,
                           TimeUnit durationUnit) {
        super(registry, name, filter, rateUnit, durationUnit);

        DefaultClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        clientConfig.getClasses().add(JacksonJsonProvider.class);
        Client client = Client.create(clientConfig);
        client.addFilter(new GZIPContentEncodingFilter());
        this.client = client;
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {

    }

    public void setClient(Client client) {
        this.client = client;
    }
}
