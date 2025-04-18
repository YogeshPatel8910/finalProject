package com.example.finalProject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration class for enabling asynchronous method execution.
 * This configuration allows methods annotated with @Async to run
 * in a separate thread rather than the calling thread.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // The class is intentionally empty as @EnableAsync annotation
    // is sufficient to enable Spring's asynchronous method execution capability
}