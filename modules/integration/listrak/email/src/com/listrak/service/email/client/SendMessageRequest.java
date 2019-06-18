
package com.listrak.service.email.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "emailAddress",
    "segmentationFieldValues"
})
public class SendMessageRequest {

    @JsonProperty("emailAddress")
    private String emailAddress;
    @JsonProperty("segmentationFieldValues")
    private List<SegmentationFieldValue> segmentationFieldValues = new ArrayList<SegmentationFieldValue>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("emailAddress")
    public String getEmailAddress() {
        return emailAddress;
    }

    @JsonProperty("emailAddress")
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @JsonProperty("segmentationFieldValues")
    public List<SegmentationFieldValue> getSegmentationFieldValues() {
        return segmentationFieldValues;
    }

    @JsonProperty("segmentationFieldValues")
    public void setSegmentationFieldValues(List<SegmentationFieldValue> segmentationFieldValues) {
        this.segmentationFieldValues = segmentationFieldValues;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("emailAddress", emailAddress).append("segmentationFieldValues", segmentationFieldValues).append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(emailAddress).append(additionalProperties).append(segmentationFieldValues).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SendMessageRequest) == false) {
            return false;
        }
        SendMessageRequest rhs = ((SendMessageRequest) other);
        return new EqualsBuilder().append(emailAddress, rhs.emailAddress).append(additionalProperties, rhs.additionalProperties).append(segmentationFieldValues, rhs.segmentationFieldValues).isEquals();
    }

}
