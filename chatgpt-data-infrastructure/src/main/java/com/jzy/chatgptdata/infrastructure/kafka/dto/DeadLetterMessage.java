package com.jzy.chatgptdata.infrastructure.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeadLetterMessage {
    private String originalPayload;
    private String errorMessage;
    private long timestamp;
    private String originalTopic;
    private int partition;
    private long offset;
}