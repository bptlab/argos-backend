package de.hpi.bpt.argos.persistence.model.event.statusUpdate;

import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttributeImpl;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataType;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.event.type.EventTypeImpl;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "EventType")
public class StatusUpdateEventTypeImpl extends EventTypeImpl implements StatusUpdateEventType {

	/**
	 * This constructor initializes the members with default values.
	 */
	public StatusUpdateEventTypeImpl() {
		editable = false;
		deletable = false;

		name = EventType.getStatusUpdateEventTypeName();

		EventAttribute oldStatusAttribute = new EventAttributeImpl();
		oldStatusAttribute.setName(StatusUpdateEventType.getOldStatusAttributeName());
		oldStatusAttribute.setType(EventDataType.STRING);
		attributes.add(oldStatusAttribute);

		EventAttribute newStatusAttribute = new EventAttributeImpl();
		newStatusAttribute.setName(StatusUpdateEventType.getNewStatusAttributeName());
		newStatusAttribute.setType(EventDataType.STRING);
		attributes.add(newStatusAttribute);

		EventAttribute timestampAttribute = new EventAttributeImpl();
		timestampAttribute.setName(StatusUpdateEventType.getTimestampAttributeName());
		timestampAttribute.setType(EventDataType.DATE);
		this.timestampAttribute = timestampAttribute;
		attributes.add(timestampAttribute);
	}
}