
package com.listrak.service.email.client;

import java.util.HashMap;
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
    "segmentationFieldId",
    "value"
})
public class SegmentationFieldValue {

    @JsonProperty("segmentationFieldId")
    private String segmentationFieldId;
    @JsonProperty("value")
    private String value;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("segmentationFieldId")
    public String getSegmentationFieldId() {
        return segmentationFieldId;
    }

    @JsonProperty("segmentationFieldId")
    public void setSegmentationFieldId(String segmentationFieldId) {
        this.segmentationFieldId = segmentationFieldId;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
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
        return new ToStringBuilder(this).append("segmentationFieldId", segmentationFieldId).append("value", value).append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(segmentationFieldId).append(additionalProperties).append(value).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SegmentationFieldValue) == false) {
            return false;
        }
        SegmentationFieldValue rhs = ((SegmentationFieldValue) other);
        return new EqualsBuilder().append(segmentationFieldId, rhs.segmentationFieldId).append(additionalProperties, rhs.additionalProperties).append(value, rhs.value).isEquals();
    }

}
