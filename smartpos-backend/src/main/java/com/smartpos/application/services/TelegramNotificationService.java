package com.smartpos.application.services;

import com.smartpos.domain.entities.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for sending real-time notifications to Telegram
 * Uses Telegram Bot API to send messages when sales are completed
 */
@Service
@Slf4j
public class TelegramNotificationService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Send sale notification to Telegram
     * This method is async to avoid blocking the sale transaction
     * If notification fails, the sale still completes successfully
     */
    @Async
    public void notifySale(Ticket ticket, int itemCount) {
        try {
            String message = buildSaleMessage(ticket, itemCount);
            sendTelegramMessage(message);
            log.info("Telegram notification sent for ticket: {}", ticket.getNumber());
        } catch (Exception e) {
            log.error("Failed to send Telegram notification for ticket: {}", ticket.getNumber(), e);
            // Don't throw - we don't want to fail the sale if notification fails
        }
    }

    private String buildSaleMessage(Ticket ticket, int itemCount) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String customerName = ticket.getCustomer() != null 
            ? ticket.getCustomer().getFirstName() + " " + ticket.getCustomer().getLastName()
            : "Walk-in Customer";

        return String.format(
            "🛒 *New Sale Alert!*\n\n" +
            "📋 Ticket: `%s`\n" +
            "👤 Customer: %s\n" +
            "📦 Items: %d\n" +
            "💰 Total: *$%.2f*\n" +
            "🕐 Time: %s\n" +
            "✅ Status: %s",
            ticket.getNumber(),
            customerName,
            itemCount,
            ticket.getTotal(),
            ticket.getCreatedAt().format(formatter),
            ticket.getStatus()
        );
    }

    private void sendTelegramMessage(String message) {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", message);
        body.put("parse_mode", "Markdown");
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForObject(url, request, String.class);
    }
}
