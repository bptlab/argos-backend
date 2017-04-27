package de.hpi.bpt.argos.storage.dataModel.event.query;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;

/**
 * This interface represents an esper event query.
 */
public interface EventQuery extends PersistenceArtifact {
    /**
     * This method returns the uuid of this eventQuery.
     * @return uuid of this eventQuery
     */
    String getUuid();

    /**
     * This method sets the uuid of this eventQuery.
     * @param uuid uuid of this eventQuery to be set
     */
    void setUuid(String uuid);

    /**
     * This method returns the esper query of this eventQuery.
     * @return the query of this eventQuery
     */
    String getQuery();

    /**
     * This method sets the query of this eventQuery.
     * @param query the query to be set
     */
    void setQuery(String query);

    /**
     * This method returns the description of this eventQuery.
     * @return description of this eventQuery
     */
    String getDescription();

    /**
     * This method sets the description of this eventQuery.
     * @param description description of this eventQuery to be set
     */
    void setDescription(String description);

    /**
     * This method returns the type id of the corresponding event type of this eventQuery.
     * @return type id of corresponding event type
     */
    long getTypeId();

    /**
     * This method sets the type id of this eventQuery.
     * @param typeId type id of this eventType to be set.
     */
    void setTypeId(long typeId);
}
