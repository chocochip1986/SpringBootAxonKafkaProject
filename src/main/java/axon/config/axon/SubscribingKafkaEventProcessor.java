package axon.config.axon;

import org.axonframework.common.DateTimeUtils;
import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.jdbc.JdbcUtils;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.jdbc.EventSchema;
import org.axonframework.eventsourcing.eventstore.jdbc.statements.AppendEventsStatementBuilder;
import org.axonframework.eventsourcing.eventstore.jdbc.statements.JdbcEventStorageEngineStatements;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.serialization.xml.XStreamSerializer;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class SubscribingKafkaEventProcessor {
    private Consumer<List<? extends EventMessage<?>>> kafkaEventProcessor;
    private AppendEventsStatementBuilder appendEvents;
    private EventSchema eventSchema;
    private ConnectionProvider connectionProvider;
    private Serializer serializer;

    public SubscribingKafkaEventProcessor(Builder builder) {
        this.kafkaEventProcessor = builder.processor;
        this.appendEvents = builder.appendEventsFunction;
        this.eventSchema = builder.eventSchema;
        this.connectionProvider = builder.connectionProvider;
        this.serializer = builder.serializer;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public void SubscribingKafkaEventProcessor() {
    }

    public void SubscribingKafkaEventProcessor(Consumer<List<? extends EventMessage<?>>> processor) {
        this.kafkaEventProcessor = processor;
    }

    public Consumer<List<? extends EventMessage<?>>> buildKafkaProcessingFunction() {
        return eventMessages -> {
            try {
                JdbcUtils.executeBatch(connectionProvider.getConnection(), connection -> {
                    return appendEvents.build(connection,
                            eventSchema,
                            byte[].class,
                            eventMessages,
                            serializer,
                            (preparedStatement, i, instant) -> { preparedStatement.setString(i, DateTimeUtils.formatInstant(instant)); });
                }, (e) -> {
                    //Consumer function that processes the error;
                });
            } catch (SQLException e) {
                System.out.println(e);
            } catch (RuntimeException e) {
                System.out.println(e);
            } catch (Exception e) {
                System.out.println(e);
            }
        };
    }

    public static class Builder {
        private Consumer<List<? extends EventMessage<?>>> processor = (eventMessages) -> { /*NO OP*/ };
        private AppendEventsStatementBuilder appendEventsFunction = JdbcEventStorageEngineStatements::appendEvents;
        private EventSchema eventSchema = null;
        private ConnectionProvider connectionProvider = null;
        private Serializer serializer = JacksonSerializer.defaultSerializer();

        public Builder() {
        }

        public Builder setSerializer(Serializer s) {
            serializer = s;
            return this;
        }

        public Builder setConnectionProvider(ConnectionProvider provider) {
            connectionProvider = provider;
            return this;
        }

        public Builder setEventSchema(EventSchema schema) {
            eventSchema = schema;
            return this;
        }

        public Builder setEventProcessor(Consumer<List<? extends EventMessage<?>>> var00) {
            processor = var00;
            return this;
        }

        public Builder setAppendEventsFunction(AppendEventsStatementBuilder appendEventsBuilder) {
            appendEventsFunction = appendEventsBuilder;
            return this;
        }

        public SubscribingKafkaEventProcessor build() {
            afterPropertiesSet();
            return new SubscribingKafkaEventProcessor(this);
        }

        private void afterPropertiesSet() {
            if ( eventSchema == null ) {
                throw new RuntimeException("SubscribingKafkaEventProcessor Builder needs an EventSchema set!");
            }

            if ( connectionProvider == null ) {
                throw new RuntimeException("SubscribingKafkaEventProcessor Builder needs a Connection Provider set!");
            }
        }
    }
}
