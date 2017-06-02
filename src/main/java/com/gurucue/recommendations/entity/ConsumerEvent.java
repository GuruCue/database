/*
 * This file is part of Guru Cue Search & Recommendation Engine.
 * Copyright (C) 2017 Guru Cue Ltd.
 *
 * Guru Cue Search & Recommendation Engine is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * Guru Cue Search & Recommendation Engine is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Guru Cue Search & Recommendation Engine. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.gurucue.recommendations.entity;

import com.gurucue.recommendations.entity.value.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;

/**
 * Represents an instance of a consumer event.
 */
public final class ConsumerEvent implements Serializable {
    private static final long serialVersionUID = -6654335674158921596L;

    Long id;
    Timestamp eventTimestamp;
    Partner partner;
    Consumer consumer;
    com.gurucue.recommendations.entity.product.Product product;
    ConsumerEventType eventType;
    Map<DataType, String> data;
    Long userProfileId;
    Integer responseCode;
    String failureCondition;
    String failedRequest;
    Timestamp requestTimestamp;
    Long requestDuration; // in milliseconds

    private static final Logger log = LogManager.getLogger(ConsumerEvent.class);
    
    public ConsumerEvent() {}
    
    public ConsumerEvent(final Long id, final Timestamp eventTimestamp, final Partner partner, final com.gurucue.recommendations.entity.product.Product product, final Consumer consumer, final ConsumerEventType eventType, final Long userProfileId) {
        this(id, eventTimestamp, partner, product, consumer, eventType, new HashMap<DataType, String>(),userProfileId);
    }

    public ConsumerEvent(final Long id, final Timestamp eventTimestamp, final Partner partner, final com.gurucue.recommendations.entity.product.Product product, final Consumer consumer, final ConsumerEventType eventType, final Map<DataType, String> data, final Long userProfileId) {
        this.id = id;
        this.eventTimestamp = eventTimestamp;
        this.partner = partner;
        this.product = product;
        this.consumer = consumer;
        this.eventType = eventType;
        this.data = data;
        this.userProfileId = userProfileId;
    }

    public void setResponseCode(Integer responseCode){
        this.responseCode = responseCode;
    }

    public Integer getResponseCode(){
        return this.responseCode;
    }

    public void setFailureCondition(String failureCondition){
        this.failureCondition = failureCondition;
    }

    public String getFailureCondition(){
        return this.failureCondition;
    }

    public void setFailedRequest(String failedRequest){
        this.failedRequest = failedRequest;
    }

    public String getFailedRequest(){
        return this.failedRequest;
    }

    public void setRequestTimestamp(Timestamp requestTimestamp){
        this.requestTimestamp = requestTimestamp;
    }

    public Timestamp getRequestTimestamp(){
        return this.requestTimestamp;
    }

    public void setRequestDuration(Long requestDuration){
        this.requestDuration = requestDuration;
    }

    public Long getRequestDuration(){
        return this.requestDuration;
    }

    public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Timestamp getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(final Timestamp timestamp) {
		this.eventTimestamp = timestamp;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(final Partner partner) {
		this.partner = partner;
	}

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(final Consumer consumer) {
        this.consumer = consumer;
    }

    public com.gurucue.recommendations.entity.product.Product getProduct() {
		return product;
	}

	public void setProduct(final com.gurucue.recommendations.entity.product.Product product) {
		this.product = product;
	}

    public ConsumerEventType getEventType() {
        return eventType;
    }
    
    public void setEventType(final ConsumerEventType eventType) {
        this.eventType = eventType;
    }

    public Map<DataType, String> getData() {
        return data;
    }

    public void setData(final Map<DataType, String> data) {
        this.data = data;
    }
    
    public Long getUserProfileId(){
    	return userProfileId;
    }
    
    public void setUserProfileId(Long userProfileId){
    	this.userProfileId = userProfileId;
    }

    /**
     * Writes the key value pairs stored in the data map into a json object
     *
     * @param jsonOutput - the StringBuilder into which the string representation of the json object should be saved
     * @return true if an object was written into the builder (it could still be empty "{}") or false if either the provided
     * StringBuilder or the data object are null
     */
    public boolean getDataAsJsonString(final StringBuilder jsonOutput){
        if(jsonOutput == null){
            log.warn("can't write jsonData - StringBuilder is NULL");
            return false;
        }

        if(this.data == null){
            log.warn("can't write json data - data is NULL");//TODO should this rather write an empty json obj?
            return false;
        }

        final java.util.function.Consumer<Map.Entry<DataType, String>> dataConsumer = entry -> {
            jsonOutput.append("\"");
            Value.escapeJson(entry.getKey().getIdentifier(), jsonOutput);
            jsonOutput.append("\":");
            if(entry.getValue() != null){
                jsonOutput.append("\"");
                Value.escapeJson(entry.getValue(), jsonOutput);
                jsonOutput.append("\"");
            }else{
                jsonOutput.append("null");
            }
        };
        jsonOutput.setLength(0);
        jsonOutput.append("{");
        final Spliterator<Map.Entry<DataType, String>> dataSpliterator = this.data.entrySet().spliterator();
        if (dataSpliterator.tryAdvance(dataConsumer)) {
            dataSpliterator.forEachRemaining((final Map.Entry<DataType, String> entry) -> {
                jsonOutput.append(",");
                dataConsumer.accept(entry);
            });
        }
        jsonOutput.append("}");

        return true;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (obj instanceof ConsumerEvent) {
            ConsumerEvent other = (ConsumerEvent) obj;
            // compare ids
            boolean ret = (this.getId() == other.getId()) ||
                ((this.getId() != null) && this.getId().equals(other.getId()));
            // compare timestamps
            ret = ret && ((this.getEventTimestamp() == other.getEventTimestamp()) ||
                ((this.getEventTimestamp() != null) && this.getEventTimestamp().equals(other.getEventTimestamp())));
            // compare partners
            ret = ret && ((this.getPartner() == other.getPartner()) ||
                ((this.getPartner() != null) && this.getPartner().equals(other.getPartner())));
            // compare consumers
            ret = ret && ((this.getConsumer() == other.getConsumer()) ||
                    ((this.getConsumer() != null) && this.getConsumer().equals(other.getConsumer())));
            // compare products
            ret = ret && ((this.getProduct() == other.getProduct()) ||
                ((this.getProduct() != null) && this.getProduct().equals(other.getProduct())));
            // compare event types
            ret = ret && ((this.getEventType() == other.getEventType()) ||
                ((this.getEventType() != null) && (this.getEventType().equals(other.getEventType()))));
            //compare profiles
            ret = ret && ((this.getUserProfileId() == other.getUserProfileId()) ||
            		((this.getUserProfileId() != null) && (this.getUserProfileId().equals(other.getUserProfileId()))));
            //compare logs
            ret = ret && ((this.getResponseCode() == other.getResponseCode()) ||
                    ((this.getResponseCode() != null) && (this.getResponseCode().equals(other.getResponseCode()))));
            ret = ret && ((this.getFailureCondition() == other.getFailureCondition()) ||
                    ((this.getFailureCondition() != null) && (this.getFailureCondition().equals(other.getFailureCondition()))));
            ret = ret && ((this.getFailedRequest() == other.getFailedRequest()) ||
                    ((this.getFailedRequest() != null) && (this.getFailedRequest().equals(other.getFailedRequest()))));
            ret = ret && ((this.getRequestTimestamp() == other.getRequestTimestamp()) ||
                    ((this.getRequestTimestamp() != null) && (this.getRequestTimestamp().equals(other.getRequestTimestamp()))));
            ret = ret && ((this.getRequestDuration() == other.getRequestDuration()) ||
                    ((this.getRequestDuration() != null) && (this.getRequestDuration().equals(other.getRequestDuration()))));

            return ret;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // recipe taken from Effective Java, 2nd edition (ISBN 978-0-321-35668-0), page 47
        int result = 17;
        result = 31 * result + (getId() == null ? 0 : getId().hashCode());
        result = 31 * result + (getEventTimestamp() == null ? 0 : getEventTimestamp().hashCode());
        result = 31 * result + (getPartner() == null ? 0 : getPartner().hashCode());
        result = 31 * result + (getConsumer() == null ? 0 : getConsumer().hashCode());
        result = 31 * result + (getProduct() == null ? 0 : getProduct().hashCode());
        result = 31 * result + (getEventType() == null ? 0 : getEventType().hashCode());
        result = 31 * result + (getUserProfileId() == null ? 0 : getUserProfileId().hashCode());
        result = 31 * result + (getResponseCode() == null ? 0 : getResponseCode().hashCode());
        result = 31 * result + (getFailureCondition() == null ? 0 : getFailureCondition().hashCode());
        result = 31 * result + (getFailedRequest() == null ? 0 : getFailedRequest().hashCode());
        result = 31 * result + (getRequestTimestamp() == null ? 0 : getRequestTimestamp().hashCode());
        result = 31 * result + (getRequestDuration() == null ? 0 : getRequestDuration().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ConsumerEvent(id=" + (null == getId() ? "null" : getId()) +
            ", timestamp=" + (null == getEventTimestamp() ? "null" : getEventTimestamp().toString()) +
            ", partner=" + (null == getPartner() ? "null" : getPartner().toString()) +
            ", consumer=" + (null == getConsumer() ? "null" : getConsumer().toString()) +
            ", product=" + (null == getProduct() ? "null" : getProduct().toString()) +
            ", eventType=" + (null == getEventType() ? "null" : getEventType().toString()) +
            ", userProfileId=" + (null == getUserProfileId() ? "null" : getUserProfileId()) +
            ", responseCode="+ (getResponseCode() == null ? "null" : getResponseCode())+
            ", failureCondition="+ (getFailureCondition() == null ? "null" : getFailureCondition())+
            ", failedRequest="+ (getFailedRequest() == null ? "null" : getFailedRequest())+
            ", requestTimestamp="+ (getRequestTimestamp() == null ? "null" : getRequestTimestamp())+
            ", requestDuration="+ (getRequestDuration() == null ? "null" : getRequestDuration())+
            ")";
    }

    private static final SimpleDateFormat pgTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Emits TSV-style field data as understood by PostgreSQL's COPY.
     *
     * @param output where to write data
     */
    public void toPgTsv(final StringBuilder output) {
        if (id == null) output.append("\\N");
        else output.append(id.toString());
        output.append('\t');
        if (eventTimestamp == null) output.append("\\N");
        else {
            final String formattedTimestamp;
            synchronized (pgTimestampFormat) {
                formattedTimestamp = pgTimestampFormat.format(eventTimestamp);
            }
            output.append(formattedTimestamp);
        }
        output.append('\t');
        if ((partner == null) || (partner.getId() == null)) output.append("\\N");
        else output.append(partner.getId().toString());
        output.append('\t');
        if ((consumer == null) || (consumer.getId() == null)) output.append("\\N");
        else output.append(consumer.getId().toString());
        output.append('\t');
        if ((product == null) || (product.getId() == null)) output.append("\\N");
        else output.append(product.getId().toString());
        output.append('\t');
        if ((eventType == null) || (eventType.getId() == null)) output.append("\\N");
        else output.append(eventType.getId().toString());
        output.append('\t');
        if ((data == null) || data.isEmpty()) output.append("\\N");
        else {
            final Iterator<Map.Entry<DataType, String>> dataIterator = data.entrySet().iterator();
            final Map.Entry<DataType, String> firstEntry = dataIterator.next();

            //all " around the hstore elements are added in the pgEscapeString method
            pgEscapeString(firstEntry.getKey().getIdentifier(), output, false);//keys are not allowed to be NULL
            output.append("=>");
            pgEscapeString(firstEntry.getValue(), output, true);//values can be null

            while (dataIterator.hasNext()) {
                final Map.Entry<DataType, String> entry = dataIterator.next();
                output.append(",");
                pgEscapeString(entry.getKey().getIdentifier(), output, false);
                output.append("=>");
                pgEscapeString(entry.getValue(), output, true);
            }
        }

        output.append('\t');
        if(userProfileId == null) output.append("\\N");
        else output.append(userProfileId.toString());

        output.append('\t');
        if(responseCode == null) output.append("\\N");
        else output.append(responseCode.toString());
        output.append('\t');
        if(failureCondition == null) output.append("\\N");
        else output.append(failureCondition.toString());
        output.append('\t');
        if(failedRequest == null) output.append("\\N");
        else output.append(failedRequest.toString());
        output.append('\t');
        if(requestTimestamp == null) output.append("\\N");
        else output.append(requestTimestamp.toString());
        output.append('\t');
        if(requestDuration == null) output.append("\\N");
        else output.append(requestDuration.toString());

        //TODO test
        output.append('\t');
        if (userProfileId == null) output.append("\\N");
        else output.append(userProfileId.toString());
        
        output.append('\n');
    }

    private static void pgEscapeString(final String src, final StringBuilder output, final boolean nullable) {
        if(src == null){
            if(nullable)output.append("NULL");//no " required around the null value
            else throw new IllegalArgumentException("Cannot escape a null String to HSTORE");

            return;
        }
        output.append("\"");//add the " around all normal elements
        final int n = src.length();
        for (int i = 0; i < n; i++) {
            final char c = src.charAt(i);
            if (c < ' ') {
                if (c == '\n') output.append("\\n");
                else if (c == '\r') output.append("\\r");
                else if (c == '\t') output.append("\\t");
                else if (c == '\b') output.append("\\b");
                else if (c == '\f') output.append("\\f");
                else output.append(c);
            }
            else if (c == '\\') {
                output.append("\\\\");
            }
            else if (c == '"') {
                output.append("\\\"");
            }
            else {
                output.append(c);
            }
        }
        output.append("\"");
    }
}
