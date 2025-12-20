# Telegram Notifications Implementation

## Overview

Real-time Telegram notifications have been added to SmartPOS to alert you whenever a product is sold or returned.

## What Was Added

### 1. Dependencies
- Added `telegrambots-spring-boot-starter` to [pom.xml](smartpos-backend/pom.xml)

### 2. New Files Created

#### TelegramNotificationService
**Location**: [smartpos-backend/src/main/java/com/smartpos/infrastructure/notification/TelegramNotificationService.java](smartpos-backend/src/main/java/com/smartpos/infrastructure/notification/TelegramNotificationService.java)

**Features**:
- Sends sale notifications with ticket details
- Sends return notifications 
- Asynchronous processing (doesn't block main transaction)
- Error handling (failures don't affect sales)
- Configurable enable/disable toggle

#### TelegramController
**Location**: [smartpos-backend/src/main/java/com/smartpos/presentation/controllers/TelegramController.java](smartpos-backend/src/main/java/com/smartpos/presentation/controllers/TelegramController.java)

**Endpoints**:
- `POST /api/v1/telegram/test` - Test Telegram configuration

### 3. Modified Files

#### SmartPOSApplication.java
- Added `@EnableAsync` annotation for asynchronous processing

#### TicketService.java
- Integrated Telegram notification calls after ticket creation
- Added `sendTelegramNotification()` helper method

#### application.properties
**New Configuration Properties**:
```properties
telegram.bot.token=YOUR_BOT_TOKEN_HERE
telegram.bot.username=YOUR_BOT_USERNAME_HERE
telegram.chat.id=YOUR_CHAT_ID_HERE
telegram.notifications.enabled=true
```

## Setup Instructions

### Quick Setup (5 minutes)

1. **Create Telegram Bot**:
   - Open Telegram, search for `@BotFather`
   - Send `/newbot` and follow prompts
   - Save the **bot token**

2. **Get Chat ID**:
   - Send a message to your new bot
   - Visit: `https://api.telegram.org/bot<YOUR_TOKEN>/getUpdates`
   - Find your **chat ID** in the response

3. **Configure Backend**:
   Edit `smartpos-backend/src/main/resources/application.properties`:
   ```properties
   telegram.bot.token=123456789:ABCdefGHIjklMNOpqrsTUVwxyz
   telegram.bot.username=smartpos_alerts_bot
   telegram.chat.id=987654321
   telegram.notifications.enabled=true
   ```

4. **Test**:
   ```bash
   # Restart backend
   cd smartpos-backend
   mvn spring-boot:run
   
   # Test notification
   curl -X POST http://localhost:8080/api/v1/telegram/test
   ```

## Notification Format

### Sale Notification
```
🛒 **NEW SALE** 🛒

📝 Ticket: `TICKET-12345`
👤 Customer: John Doe
📦 Items: 3
💰 Total: $45.99
🕐 Time: 2025-12-19 14:30:00
```

### Return Notification
```
🔄 **PRODUCT RETURN** 🔄

📝 Ticket: `TICKET-12346`
👤 Customer: Jane Smith
📦 Items: 1
💵 Amount: $15.50
🕐 Time: 2025-12-19 15:00:00
```

## How It Works

1. Customer completes a purchase at POS
2. `TicketService.createTicket()` creates the sale record
3. After successful save, `sendTelegramNotification()` is called
4. Notification is sent asynchronously (non-blocking)
5. You receive instant Telegram message
6. If notification fails, sale still completes successfully

## Testing

### Test Endpoint
```bash
# Send test message
curl -X POST http://localhost:8080/api/v1/telegram/test
```

### Test with Actual Sale
1. Go to POS interface (http://localhost:3000/sales)
2. Add products to cart
3. Complete a sale
4. Check Telegram for notification

## Troubleshooting

### No notifications received?
1. Check bot token is correct
2. Verify chat ID is correct
3. Ensure you sent at least one message to the bot first
4. Check `telegram.notifications.enabled=true`
5. Look for errors in backend logs

### Notifications delayed?
- Normal - they're async and may take 1-2 seconds
- Check your internet connection
- Telegram API might be experiencing delays

### Disable notifications temporarily
```properties
telegram.notifications.enabled=false
```

## API Integration

The notification system integrates seamlessly with existing endpoints:
- `POST /api/v1/tickets` - Creates ticket and sends notification automatically

No changes needed to frontend or existing API calls!

## Future Enhancements

Potential additions:
- Daily sales summary
- Low stock alerts
- High-value sale alerts
- Multiple recipient support
- Notification templates
- Rich formatting with product images

## Dependencies

```xml
<dependency>
    <groupId>org.telegram</groupId>
    <artifactId>telegrambots-spring-boot-starter</artifactId>
    <version>6.8.0</version>
</dependency>
```

## Documentation

For detailed setup instructions, see [SETUP_GUIDE.md](SETUP_GUIDE.md#telegram-notifications-setup)

---

**Implementation Date**: December 19, 2025  
**Version**: 1.0.0
