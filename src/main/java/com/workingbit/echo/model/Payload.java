package com.workingbit.echo.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by Aleksey Popryaduhin on 16:13 01/10/2017.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Echo.class, name = "echo"),
    @JsonSubTypes.Type(value = Ping.class, name = "ping"),
    @JsonSubTypes.Type(value = Pong.class, name = "pong"),
})
public interface Payload {
}
