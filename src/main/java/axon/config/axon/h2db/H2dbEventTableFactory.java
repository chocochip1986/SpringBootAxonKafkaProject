package axon.config.axon.h2db;

import org.axonframework.eventsourcing.eventstore.jdbc.AbstractEventTableFactory;
import org.axonframework.eventsourcing.eventstore.jdbc.EventSchema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class H2dbEventTableFactory extends AbstractEventTableFactory {
    @Override
    public PreparedStatement createDomainEventTable(Connection connection, EventSchema schema) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + schema.domainEventTable() + " (\n" + schema.globalIndexColumn() + " " + this.idColumnType() + ",\n" + schema.aggregateIdentifierColumn() + " VARCHAR(255) NOT NULL,\n" + schema.sequenceNumberColumn() + " BIGINT NOT NULL,\n" + schema.typeColumn() + " VARCHAR(255),\n" + schema.eventIdentifierColumn() + " VARCHAR(255) NOT NULL,\n" + schema.metaDataColumn() + " " + this.payloadType() + ",\n" + schema.payloadColumn() + " " + this.payloadType() + " NOT NULL,\n" + schema.payloadRevisionColumn() + " VARCHAR(255),\n" + schema.payloadTypeColumn() + " VARCHAR(255) NOT NULL,\n" + schema.timestampColumn() + " " + this.timestampType() + " ,\nPRIMARY KEY (" + schema.globalIndexColumn() + "),\nUNIQUE (" + schema.aggregateIdentifierColumn() + ", " + schema.sequenceNumberColumn() + "),\nUNIQUE (" + schema.eventIdentifierColumn() + ")\n)";
        return connection.prepareStatement(sql);
    }

    @Override
    protected String idColumnType() {
        return "BIGINT AUTO_INCREMENT";
    }

    @Override
    protected String payloadType() {
        return "BLOB";
    }
}
