package org.zeroref.borg.diagnostics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroref.borg.directions.MessageDestinations;
import org.zeroref.borg.directions.MessageSubscriptions;
import org.zeroref.borg.pipeline.MessageHandlerTable;
import org.zeroref.borg.recoverability.BackOff;
import org.zeroref.borg.runtime.EndpointId;
import org.zeroref.borg.sagas.SagaPersistence;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigurationInspector {
    private EndpointId endpointId;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationInspector.class);

    private StringBuilder detailsAcc = new StringBuilder();

    public ConfigurationInspector(EndpointId endpointId) {
        this.endpointId = endpointId;

        detailsAcc.append("\n Endpoint Name: " + endpointId);
    }

    public void present() {
        LOGGER.info(detailsAcc.toString());
    }

    public void inspectHandlers(MessageHandlerTable table) {
        detailsAcc.append("\n\t Handles wired:");

        for (Class<?> cl: table.handlers.keySet()) {
            detailsAcc.append("\n\t\t " + cl.getSimpleName());
        }
    }

    public void inspectSubscriptions(MessageSubscriptions subscriptions) {
        detailsAcc.append("\n\t Event Subscriptions:");

        for (Map.Entry<String, List<Class<?>>> cl: subscriptions.sources.entrySet()) {
            List<String> types = cl.getValue().stream().map(x -> x.getSimpleName()).collect(Collectors.toList());
            detailsAcc.append("\n\t\t " + cl.getKey() + " " + String.join(",", types) );
        }
    }

    public void inspectRouting(MessageDestinations router) {
        detailsAcc.append("\n\t Command Routing:");

        for (Map.Entry<Class<?>, List<String>> cl: router.destinations.entrySet()) {
            detailsAcc.append("\n\t\t " + String.join(",", cl.getValue()) + " " +cl.getKey().getSimpleName());
        }
    }

    public void inspectFlr(BackOff backoff) {
        detailsAcc.append("\n\t FLR:" + backoff.getDurations());
    }

    public void inspectSlr(BackOff backoff) {
        detailsAcc.append("\n\t SLR:" + backoff.getDurations());
    }

    public void inspectSagas(SagaPersistence sagaPersistence) {
        detailsAcc.append("\n\t Sagas:");

        Map<Class, List<Class>> confSagas = sagaPersistence.getConfSagas();

        for (Map.Entry<Class, List<Class>> cl: confSagas.entrySet()) {
            List<String> types = cl.getValue().stream().map(x -> x.getSimpleName()).collect(Collectors.toList());
            detailsAcc.append("\n\t\t " + cl.getKey().getSimpleName() + " " + String.join(",", types) );
        }
    }
}
