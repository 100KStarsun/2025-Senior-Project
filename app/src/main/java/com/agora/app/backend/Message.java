package com.agora.app.backend;

import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.utils.Either;

import java.io.Serializable;
import java.time.Instant;

public record Message (String text, Instant timestamp, boolean isFromFirst) implements Serializable {}
