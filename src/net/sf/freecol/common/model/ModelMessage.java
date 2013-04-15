/**
 *  Copyright (C) 2002-2012   The FreeCol Team
 *
 *  This file is part of FreeCol.
 *
 *  FreeCol is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  FreeCol is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.common.model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


/**
 * Contains a message about a change in the model.
 */
public class ModelMessage extends StringTemplate {

    /** Constants describing the type of message. */
    public static enum MessageType {

        DEFAULT(""),
        WARNING("model.option.guiShowWarning"),
        SONS_OF_LIBERTY("model.option.guiShowSonsOfLiberty"),
        GOVERNMENT_EFFICIENCY("model.option.guiShowGovernmentEfficiency"),
        WAREHOUSE_CAPACITY("model.option.guiShowWarehouseCapacity"),
        UNIT_IMPROVED("model.option.guiShowUnitImproved"),
        UNIT_DEMOTED("model.option.guiShowUnitDemoted"),
        UNIT_LOST("model.option.guiShowUnitLost"),
        UNIT_ADDED("model.option.guiShowUnitAdded"),
        BUILDING_COMPLETED("model.option.guiShowBuildingCompleted"),
        FOREIGN_DIPLOMACY("model.option.guiShowForeignDiplomacy"),
        MARKET_PRICES("model.option.guiShowMarketPrices"),
        LOST_CITY_RUMOUR(null), // Displayed during the turn
        MISSING_GOODS("model.option.guiShowMissingGoods"),
        TUTORIAL("model.option.guiShowTutorial"),
        COMBAT_RESULT(null), // No option, always display
        GIFT_GOODS("model.option.guiShowGifts"),
        DEMANDS("model.option.guiShowDemands"),
        GOODS_MOVEMENT("model.option.guiShowGoodsMovement");

        private String optionName;

        MessageType(String optionName) {
            this.optionName = optionName;
        }

        public String getOptionName() {
            return optionName;
        }
    }

    private String sourceId;
    private String displayId;
    private MessageType messageType;
    private boolean beenDisplayed = false;
    // @compat 0.9.x
    private String ownerId;
    // end @compat


    /**
     * Empty constructor for serialization.
     */
    public ModelMessage() {}

    /**
     * Creates a new <code>ModelMessage</code>.
     *
     * @param id The ID of the message to display.
     * @param source The source of the message. This is what the
     *               message should be associated with.
     * @param display The <code>FreeColObject</code> to display.
     */
    public ModelMessage(String id, FreeColGameObject source,
                        FreeColObject display) {
        this(MessageType.DEFAULT, id, source, display);
    }

    /**
     * Creates a new <code>ModelMessage</code>.
     *
     * @param messageType The type of this model message.
     * @param id The ID of the message to display.
     * @param source The source of the message. This is what the
     *               message should be associated with.
     */
    public ModelMessage(MessageType messageType, String id,
                        FreeColGameObject source) {
        this(messageType, id, source, getDefaultDisplay(messageType, source));
    }

    /**
     * Creates a new <code>ModelMessage</code>.
     *
     * @param id The ID of the message to display.
     * @param source The source of the message. This is what the
     *               message should be associated with.
     */
    public ModelMessage(String id, FreeColGameObject source) {
        this(MessageType.DEFAULT, id, source,
             getDefaultDisplay(MessageType.DEFAULT, source));
    }

    /**
     * Creates a new <code>ModelMessage</code>.
     *
     * @param messageType The type of this model message.
     * @param id The ID of the message to display.
     * @param source The source of the message. This is what the
     *               message should be associated with.
     * @param display The <code>FreeColObject</code> to display.
     */
    public ModelMessage(MessageType messageType, String id,
                        FreeColGameObject source, FreeColObject display) {
        super(id, TemplateType.TEMPLATE);
        this.messageType = messageType;
        this.sourceId = source.getId();
        this.displayId = (display != null) ? display.getId() : source.getId();
        this.ownerId = null;
    }


    /**
     * Gets the ID of the source of the message.
     *
     * @return The source of the message.
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * Sets the ID of the source object.
     *
     * @param sourceId A new source ID.
     */
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * Gets the ID of the object to display.
     *
     * @return The ID of the object to display.
     */
    public String getDisplayId() {
        return displayId;
    }

    /**
     * Sets the ID of the object to display.
     *
     * @param displayId A new display ID.
     */
    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    /**
     * Switch the source (and display if it is the same) to a new
     * object.  Called when the source object becomes invalid.
     *
     * @param newSource A new source.
     */
    public void divert(FreeColGameObject newSource) {
        if (displayId == sourceId) displayId = newSource.getId();
        sourceId = newSource.getId();
    }

    /**
     * Gets the messageType of the message to display.
     *
     * @return The messageType.
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * Sets the type of the message.
     *
     * @param messageType The new messageType.
     */
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    /**
     * Gets a key for this type of message.
     *
     * @return A message key.
     */
    public String getMessageTypeName() {
        return "model.message." + messageType.toString();
    }

    /**
     * Has this message been displayed?
     *
     * @return True if this message has been displayed.
     */
    public boolean hasBeenDisplayed() {
        return beenDisplayed;
    }

    /**
     * Sets whether this message has been displayed.
     *
     * @param beenDisplayed The new displayed state.
     */
    public void setBeenDisplayed(boolean beenDisplayed) {
        this.beenDisplayed = beenDisplayed;
    }

    /**
     * Compatibility hack.  Do not use.
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Compatibility hack.  Do not use.
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }


    /**
     * Gets the default display object for the given type.
     *
     * @param messageType The type to find the default display object for.
     * @param source The source object
     * @return An object to be displayed for the message.
     */
    static private FreeColObject getDefaultDisplay(MessageType messageType,
                                                   FreeColGameObject source) {
        FreeColObject o = null;
        switch (messageType) {
        case SONS_OF_LIBERTY:
        case GOVERNMENT_EFFICIENCY:
            o = source.getSpecification().getGoodsType("model.goods.bells");
            break;
        case UNIT_IMPROVED:
        case UNIT_DEMOTED:
        case UNIT_LOST:
        case UNIT_ADDED:
        case LOST_CITY_RUMOUR:
        case COMBAT_RESULT:
        case DEMANDS:
        case GOODS_MOVEMENT:
            o = source;
            break;
        case BUILDING_COMPLETED:
            o = source.getSpecification().getGoodsType("model.goods.hammers");
            break;
        case DEFAULT:
        case WARNING:
        case WAREHOUSE_CAPACITY:
        case FOREIGN_DIPLOMACY:
        case MARKET_PRICES:
        case MISSING_GOODS:
        case TUTORIAL:
        case GIFT_GOODS:
        default:
            if (source instanceof Player) o = source;
            break;
        }
        return o;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final ModelMessage setDefaultId(final String newDefaultId) {
        return (ModelMessage) super.setDefaultId(newDefaultId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelMessage add(String key, String value) {
        return (ModelMessage) super.add(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelMessage add(String value) {
        return (ModelMessage) super.add(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelMessage addName(String key, String value) {
        return (ModelMessage) super.addName(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelMessage addName(String value) {
        return (ModelMessage) super.addName(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelMessage addName(String key, FreeColObject object) {
        return (ModelMessage) super.addName(key, object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelMessage addAmount(String key, Number amount) {
        return (ModelMessage) super.addAmount(key, amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelMessage addStringTemplate(String key, StringTemplate template) {
        return (ModelMessage) super.addStringTemplate(key, template);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelMessage addStringTemplate(StringTemplate template) {
        return (ModelMessage) super.addStringTemplate(template);
    }


    // Interface Object

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ModelMessage) {
            ModelMessage m = (ModelMessage) o;
            if (sourceId.equals(m.sourceId)
                && getId().equals(m.getId())
                && messageType == m.messageType) {
                return super.equals(m);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int value = 1;
        value = 37 * value + sourceId.hashCode();
        value = 37 * value + getId().hashCode();
        value = 37 * value + messageType.ordinal();
        value = 37 * value + super.hashCode();
        return value;
    }


    // Serialization

    private static final String DISPLAY_TAG = "display";
    private static final String HAS_BEEN_DISPLAYED_TAG = "hasBeenDisplayed";
    private static final String MESSAGE_TYPE_TAG = "messageType";
    private static final String SOURCE_TAG = "source";
    // @compat 0.9.x
    private static final String OWNER_TAG = "owner";
    // end @compat


    /**
     * {@inheritDoc}
     */
    @Override
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        super.toXML(out, getXMLElementTagName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeAttributes(XMLStreamWriter out) throws XMLStreamException {
        super.writeAttributes(out);

        writeAttribute(out, SOURCE_TAG, sourceId);

        if (displayId != null) {
            writeAttribute(out, DISPLAY_TAG, displayId);
        }

        writeAttribute(out, MESSAGE_TYPE_TAG, messageType);

        writeAttribute(out, HAS_BEEN_DISPLAYED_TAG, beenDisplayed);

        // @compat 0.9.x
        if (ownerId != null) {
            writeAttribute(out, OWNER_TAG, ownerId);
        }
        // end @compat
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readAttributes(XMLStreamReader in) throws XMLStreamException {
        super.readAttributes(in);

        sourceId = getAttribute(in, SOURCE_TAG, (String)null);

        displayId = getAttribute(in, DISPLAY_TAG, (String)null);

        messageType = getAttribute(in, MESSAGE_TYPE_TAG, 
                                   MessageType.class, MessageType.DEFAULT);

        beenDisplayed = getAttribute(in, HAS_BEEN_DISPLAYED_TAG, false);

        // @compat 0.9.x
        ownerId = getAttribute(in, OWNER_TAG, (String)null);
        // end @compat
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("ModelMessage<").append(hashCode())
            .append(", ").append((sourceId == null) ? "null" : sourceId)
            .append(", ").append((displayId == null) ? "null" : displayId)
            .append(", ").append(super.toString())
            .append(", ").append(messageType)
            .append(" >");
        return sb.toString();
    }

    /**
     * Gets the tag name of the root element representing this object.
     *
     * @return "modelMessage"
     */
    public static String getXMLElementTagName() {
        return "modelMessage";
    }
}
