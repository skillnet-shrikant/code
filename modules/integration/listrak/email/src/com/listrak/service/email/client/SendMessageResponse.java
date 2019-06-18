
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
    "status",
    "error",
    "message",
    "resourceId"
})
public class SendMessageResponse {

    @JsonProperty("status")
    private Double status;
    @JsonProperty("error")
    private String error;
    @JsonProperty("message")
    private String message;
    @JsonProperty("resourceId")
    private String resourceId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("status")
    public Double getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Double status) {
        this.status = status;
    }

    @JsonProperty("error")
    public String getError() {
        return error;
    }

    @JsonProperty("error")
    public void setError(String error) {
        this.error = error;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("resourceId")
    public String getResourceId() {
        return resourceId;
    }

    @JsonProperty("resourceId")
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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
        return new ToStringBuilder(this).append("status", status).append("error", error).append("message", message).append("resourceId", resourceId).append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(resourceId).append(additionalProperties).append(error).append(message).append(status).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SendMessageResponse) == false) {
            return false;
        }
        SendMessageResponse rhs = ((SendMessageResponse) other);
        return new EqualsBuilder().append(resourceId, rhs.resourceId).append(additionalProperties, rhs.additionalProperties).append(error, rhs.error).append(message, rhs.message).append(status, rhs.status).isEquals();
    }

}
