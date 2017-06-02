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

import com.google.common.collect.ImmutableMap;
import com.gurucue.recommendations.blender.BlendParameters;
import com.gurucue.recommendations.blender.DataSet;
import com.gurucue.recommendations.blender.VideoData;
import com.gurucue.recommendations.data.DataManager;
import com.gurucue.recommendations.entity.product.GeneralVideoProduct;
import com.gurucue.recommendations.entity.value.AttributeValues;
import com.gurucue.recommendations.entity.value.Value;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

public class LogSvcRecommendation implements Serializable {
    private static final long serialVersionUID = -3483620069083700858L;

    Long id;
    Partner partner;
    long consumerId;
    Integer maxRecommendations;
    Integer responseCode;
    String failureCondition;
    String failedRequest;
    Timestamp requestTimestamp;
    Long requestDuration; // in milliseconds
    String partnerRecommenderName;
    String blenderName;

    String jsonReferences = null;
    String jsonCandidates = null;
    String jsonReturned = null;
    String jsonData = null;
    String jsonAttributes = null;
    String jsonFeedback = null;

    public void setResponse(final DataSet<VideoData> response) {
        final Attribute videoId = DataManager.getAttributeCodes().videoId;
        if ((response != null) && !response.isEmpty()) {
            final StringBuilder result = new StringBuilder(response.size() * 128); // guesstimate
            final java.util.function.Consumer<VideoData> dataConsumer = (final VideoData vd) -> {
                result.append("{");
                if(vd.gridLine >= 0) {
                    result.append("\"grid_line\":").append(vd.gridLine).append(",");
                }
                result.append("\"prediction\":").append(vd.prediction).append(",\"video_id\":");
                final Value v = vd.video.related.get(videoId);
                if (v == null) {
                    result.append("null");
                } else {
                    v.toJson(result);
                }
                result.append(",\"explanation\":\"").append(vd.explanation).append("\",\"product_id\":").append(vd.video.id).append("}");
            };
            final Spliterator<VideoData> s = response.spliterator();
            result.append("[");
            if (s.tryAdvance(dataConsumer)) {
                // continue with adding "," first
                s.forEachRemaining((final VideoData vd) -> {
                    result.append(",");
                    dataConsumer.accept(vd);
                });
            }
            result.append("]");
            jsonReturned = result.toString();
        }
        else jsonReturned = null;
    }

    public void setBlenderName(String blenderName){
        this.blenderName = blenderName;
    }

    public String getBlenderName(){
        return this.blenderName;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(final Partner partner) {
        this.partner = partner;
    }

    public long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(final long consumerId) {
        this.consumerId = consumerId;
    }

    public Integer getMaxRecommendations() {
        return maxRecommendations;
    }

    public void setMaxRecommendations(final Integer maxRecommendations) {
        this.maxRecommendations = maxRecommendations;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(final Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getFailureCondition() {
        return failureCondition;
    }

    public void setFailureCondition(final String failureCondition) {
        this.failureCondition = failureCondition;
    }

    public String getFailedRequest() {
        return failedRequest;
    }

    public void setFailedRequest(final String failedRequest) {
        this.failedRequest = failedRequest;
    }

    public Timestamp getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(final Timestamp requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public Long getRequestDuration() {
        return requestDuration;
    }

    public void setRequestDuration(final Long requestDuration) {
        this.requestDuration = requestDuration;
    }

    public String getPartnerRecommenderName() {
        return partnerRecommenderName;
    }

    public void setPartnerRecommenderName(final String partnerRecommenderName) {
        this.partnerRecommenderName = partnerRecommenderName;
    }

    public String getJsonData(){
        return this.jsonData;
    }

    public String getJsonAttributes(){
        return this.jsonAttributes;
    }

    public String getJsonReferences(){return this.jsonReferences;}

    public String getJsonReturned(){return this.jsonReturned;}

    public String getJsonCandidates(){return this.jsonCandidates;}

    public void blenderParamsToJson(final BlendParameters params){
        final StringBuilder builder = new StringBuilder(50);
        final Attribute videoId = DataManager.getAttributeCodes().videoId;

        final Consumer<com.gurucue.recommendations.entity.product.Product> consumer = (final com.gurucue.recommendations.entity.product.Product p) -> {
            builder.append("{\"video_id\":");
            final Value v = p.related.get(videoId);
            if (v == null) {
                builder.append("null");
            } else {
                v.toJson(builder);
            }
            builder.append(",\"product_id\":").append(p.id).append("}");
        };

        // usually only of referenceProducts and referenceProduct is given, but in theory they can both be set
        // -- referenceProduct is only a shortcut singleton of referenceProducts, so add them all together
        GeneralVideoProduct[] references = null;
        try {
            references = (GeneralVideoProduct[]) params.input.get("referencedProducts");
        }
        catch (ClassCastException e) {
            // TODO: report the error
        }
        builder.setLength(0);
        builder.append("[");
        if(references != null && references.length > 0){
            consumer.accept(references[0]);
            final int n = references.length;
            for (int i = 1; i < n; i++) {
                builder.append(",");
                consumer.accept(references[i]);
            }
        }
        if (builder.length() > 1) {
            builder.append("]");
            jsonReferences = builder.toString();
        }
        else jsonReferences = null;

        GeneralVideoProduct[] candidates = null;
        try {
            candidates = (GeneralVideoProduct[]) params.input.get("products");
        }
        catch (ClassCastException e) {
            // TODO: report the error
        }
        if(candidates != null && candidates.length > 0){
            builder.setLength(0);
            builder.append("[");
            consumer.accept(candidates[0]);
            final int n = candidates.length;
            for (int i = 1; i < n; i++) {
                builder.append(",");
                consumer.accept(candidates[i]);
            }
            builder.append("]");
            jsonCandidates = builder.toString();
        }
        else jsonCandidates = null;

        final ImmutableMap<DataType, String> data = params.data;
        if ((data != null) && !data.isEmpty()) {
            final Consumer<Map.Entry<DataType, String>> dataConsumer = entry -> {
                builder.append("\"");
                Value.escapeJson(entry.getKey().getIdentifier(), builder);
                builder.append("\":");
                if(entry.getValue() != null){
                    builder.append("\"");//added this if here because if I add it into the escapeJson method  there will be details to fix all over the project, because the method is commonly used
                    Value.escapeJson(entry.getValue(), builder);
                    builder.append("\"");
                }else{
                    builder.append("null");
                }

            };
            builder.setLength(0);
            builder.append("{");
            final Spliterator<Map.Entry<DataType, String>> dataSpliterator = data.entrySet().spliterator();
            if (dataSpliterator.tryAdvance(dataConsumer)) {
                dataSpliterator.forEachRemaining((final Map.Entry<DataType, String> entry) -> {
                    builder.append(",");
                    dataConsumer.accept(entry);
                });
            }
            builder.append("}");
            jsonData = builder.toString();
        }
        else jsonData = null;

        AttributeValues att = null;
        try {
            att = (AttributeValues) params.input.get("attributes");
        }
        catch (ClassCastException e) {
            // TODO: report the error
        }
        if ((att != null) && !att.values.isEmpty()) {
            builder.setLength(0);
            att.toJson(builder);
            jsonAttributes = builder.toString();
        }
        else jsonAttributes = null;
    }

    private static void addRawJsonName(final StringBuilder jsonBuilder, final String name) {
        jsonBuilder.append("\"");
        Value.escapeJson(name, jsonBuilder);
        jsonBuilder.append("\":");
    }

    public String getJsonFeedback() {
        return jsonFeedback;
    }

    public void setJsonFeedback(final Map<String, Object> blenderFeedback) {
        if ((blenderFeedback == null) || (blenderFeedback.size() == 0)) {
            jsonFeedback = "{}";
            return;
        }
        final StringBuilder jsonBuilder = new StringBuilder(128 + blenderFeedback.size() * 128); // some guesstimate
        jsonBuilder.append("{");
        final java.util.function.Consumer<Map.Entry<String, Object>> dataConsumer = (final Map.Entry<String, Object> entry) -> {
            final String name = entry.getKey();
            final Object value = entry.getValue();
            if (value == null) {
                addRawJsonName(jsonBuilder, name);
                jsonBuilder.append("null");
            }
            else if (value instanceof String) {
                addRawJsonName(jsonBuilder, name);
                jsonBuilder.append("\"");
                Value.escapeJson((String)value, jsonBuilder);
                jsonBuilder.append("\"");
            }
            else if (value instanceof Number) {
                addRawJsonName(jsonBuilder, name);
                jsonBuilder.append(value.toString());
            }
            else if (value instanceof Boolean) {
                addRawJsonName(jsonBuilder, name);
                jsonBuilder.append(((Boolean)value).booleanValue() ? "true" : "false");
            }
            // TODO: add support for arrays and objects, and reporting of unsupported types
        };
        final Iterator<Map.Entry<String, Object>> feedbackIterator = blenderFeedback.entrySet().iterator();
        while (feedbackIterator.hasNext() && (jsonBuilder.length() == 1)) {
            dataConsumer.accept(feedbackIterator.next());
        }
        while (feedbackIterator.hasNext()) {
            jsonBuilder.append(",");
            dataConsumer.accept(feedbackIterator.next());
        }
        jsonBuilder.append("}");
        jsonFeedback = jsonBuilder.toString();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (null == getId() ? 0 : getId().hashCode());
        result = 31 * result + (null == getPartner() ? 0 : getPartner().hashCode());
        result = 31 * result + (int)(consumerId ^(consumerId >>> 32));
        result = 31 * result + (null == getMaxRecommendations() ? 0 : getMaxRecommendations().hashCode());
        result = 31 * result + (null == getResponseCode() ? 0 : getResponseCode().hashCode());
        result = 31 * result + (null == getFailureCondition() ? 0 : getFailureCondition().hashCode());
        result = 31 * result + (null == getFailedRequest() ? 0 : getFailedRequest().hashCode());
        result = 31 * result + (null == getRequestTimestamp() ? 0 : getRequestTimestamp().hashCode());
        result = 31 * result + (null == getRequestDuration() ? 0 : getRequestDuration().hashCode());
        result = 31 * result + (null == getPartnerRecommenderName() ? 0 : getPartnerRecommenderName().hashCode());
        result = 31 * result + (null == getJsonCandidates() ? 0 : getJsonCandidates().hashCode());
        result = 31 * result + (null == getJsonReferences() ? 0 : getJsonReferences().hashCode());
        result = 31 * result + (null == getJsonReturned() ? 0 : getJsonReturned().hashCode());
        result = 31 * result + (null == getJsonData() ? 0 : getJsonData().hashCode());
        result = 31 * result + (null == getJsonAttributes() ? 0 : getJsonAttributes().hashCode());
        result = 31 * result + (null == getJsonFeedback() ? 0 : getJsonFeedback().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "LogSvcRecommendation(id=" + (null == getId() ? "null" : getId().toString()) +
                ", partner=" + (null == getPartner() ? "null" : getPartner().toString()) +
                ", consumerId=" + getConsumerId() +
                ", maxRecommendations=" + (null == getMaxRecommendations() ? "null" : getMaxRecommendations().toString()) +
                ", responseCode=" + (null == getResponseCode() ? "null" : getResponseCode().toString()) +
                ", failureCondition=" + (null == getFailureCondition() ? "null" : "\"" + getFailureCondition().replace("\"", "\\\"") + "\"") +
                ", failedRequest=" + (null == getFailedRequest() ? "null" : "\"" + getFailedRequest().replace("\"", "\\\"") + "\"") +
                ", requestTimestamp=" + (null == getRequestTimestamp() ? "null" : getRequestTimestamp().toString()) +
                ", requestDuration=" + (null == getRequestDuration() ? "null" : getRequestDuration().toString()) +
                ", partnerRecommenderName=" + (null == getPartnerRecommenderName() ? "null" : "\"" + getPartnerRecommenderName().replace("\"", "\\\"") + "\"") +
                ", data=" + (null == getJsonData() ? "null" : getJsonData()) +
                ", attributes=" + (null == getJsonAttributes() ? "null" : getJsonAttributes()) +
                ", references=" + (null == getJsonReferences() ? "null" : getJsonReferences()) +
                ", candidates=" + (null == getJsonCandidates() ? "null" : getJsonCandidates()) +
                ", returned=" + (null == getJsonReturned() ? "null" : getJsonReturned()) +
                ", feedback=" + (null == getJsonFeedback() ? "null" : getJsonFeedback()) +
                ")";
    }
}
