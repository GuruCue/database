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

import java.io.Serializable;
import java.sql.Timestamp;

public class LogSvcConsumerEvent implements Serializable {
    private static final long serialVersionUID = 8517696788914361770L;

    Long id;
    Partner partner;
    ConsumerEvent consumerEvent;
    Integer responseCode;
    String failureCondition;
    String failedRequest;
    Timestamp requestTimestamp;
    Long requestDuration; // in milliseconds

    public LogSvcConsumerEvent() {

    }

    public LogSvcConsumerEvent(Long id, Partner partner, ConsumerEvent consumerEvent, Integer responseCode, String failureCondition, String failedRequest, Timestamp requestTimestamp, Long requestDuration) {
        this.id = id;
        this.partner = partner;
        this.consumerEvent = consumerEvent;
        this.responseCode = responseCode;
        this.failureCondition = failureCondition;
        this.failedRequest = failedRequest;
        this.requestTimestamp = requestTimestamp;
        this.requestDuration = requestDuration;
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

    public ConsumerEvent getConsumerEvent() {
        return consumerEvent;
    }

    public void setConsumerEvent(final ConsumerEvent consumerEvent) {
        this.consumerEvent = consumerEvent;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof LogSvcConsumerEvent)) return false;

        LogSvcConsumerEvent that = (LogSvcConsumerEvent) o;

        if (consumerEvent != null ? !consumerEvent.equals(that.consumerEvent) : that.consumerEvent != null)
            return false;
        if (failedRequest != null ? !failedRequest.equals(that.failedRequest) : that.failedRequest != null)
            return false;
        if (failureCondition != null ? !failureCondition.equals(that.failureCondition) : that.failureCondition != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (partner != null ? !partner.equals(that.partner) : that.partner != null) return false;
        if (requestDuration != null ? !requestDuration.equals(that.requestDuration) : that.requestDuration != null)
            return false;
        if (requestTimestamp != null ? !requestTimestamp.equals(that.requestTimestamp) : that.requestTimestamp != null)
            return false;
        if (responseCode != null ? !responseCode.equals(that.responseCode) : that.responseCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (partner != null ? partner.hashCode() : 0);
        result = 31 * result + (consumerEvent != null ? consumerEvent.hashCode() : 0);
        result = 31 * result + (responseCode != null ? responseCode.hashCode() : 0);
        result = 31 * result + (failureCondition != null ? failureCondition.hashCode() : 0);
        result = 31 * result + (failedRequest != null ? failedRequest.hashCode() : 0);
        result = 31 * result + (requestTimestamp != null ? requestTimestamp.hashCode() : 0);
        result = 31 * result + (requestDuration != null ? requestDuration.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LogSvcConsumerEvent(id=" + (null == getId() ? "null" : getId().toString()) +
                ", partner=" + (null == getPartner() ? "null" : getPartner().toString()) +
                ", consumerEvent=" + (null == getConsumerEvent() ? "null" : getConsumerEvent().toString()) +
                ", responseCode=" + (null == getResponseCode() ? "null" : getResponseCode().toString()) +
                ", failureCondition=" + (null == getFailureCondition() ? "null" : "\"" + getFailureCondition().replace("\"", "\\\"") + "\"") +
                ", failedRequest=" + (null == getFailedRequest() ? "null" : "\"" + getFailedRequest().replace("\"", "\\\"") + "\"") +
                ", requestTimestamp=" + (null == getRequestTimestamp() ? "null" : getRequestTimestamp().toString()) +
                ", requestDuration=" + (null == getRequestDuration() ? "null" : getRequestDuration().toString()) +
                ")";
    }

    public void toCsv(final StringBuilder output) {
        if (id != null) output.append(id.toString());
        output.append(',');
        if ((partner != null) && (partner.getId() != null)) output.append(partner.getId().toString());
        output.append(',');
        if ((consumerEvent != null) && (consumerEvent.getId() != null)) output.append(consumerEvent.getId().toString());
        output.append(',');
        if (responseCode != null) output.append(responseCode.toString());
        output.append(',');
        if (failureCondition != null) {
            output.append('"');
            output.append(failureCondition.replace("\"", "\"\""));
            output.append('"');
        }
        output.append(',');
        if (failedRequest != null) {
            output.append('"');
            output.append(failedRequest.replace("\"", "\"\""));
            output.append('"');
        }
        output.append(',');
        if (requestTimestamp != null) output.append(requestTimestamp.getTime());
        output.append(',');
        if (requestDuration != null) output.append(requestDuration.toString());
        output.append('\n');
    }
}
