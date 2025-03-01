package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;
    private ObjectMapper objectMapper;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
        this.objectMapper = new ObjectMapper();
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerUserHandler);
        app.post("/login", this::loginUserHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByUserHandler);
        return app;
    }

    private void registerUserHandler(Context ctx) {
        try {
            Account account = objectMapper.readValue(ctx.body(), Account.class);
            Account createdAccount = accountService.registerAccount(account);
            if (createdAccount != null) {
                ctx.json(createdAccount);
            } else {
                ctx.status(400);
            }
        } catch (Exception e) {
            ctx.status(400);
        }
    }

    private void loginUserHandler(Context ctx) {
        try {
            Account account = objectMapper.readValue(ctx.body(), Account.class);
            Account loggedInAccount = accountService.loginAccount(account);
            if (loggedInAccount != null) {
                ctx.json(loggedInAccount);
            } else {
                ctx.status(401);
            }
        } catch (Exception e) {
            ctx.status(401);
        }
    }

    private void createMessageHandler(Context ctx) {
        try {
            Message message = objectMapper.readValue(ctx.body(), Message.class);
            Message createdMessage = messageService.createMessage(message);
            if (createdMessage != null) {
                ctx.json(createdMessage);
            } else {
                ctx.status(400);
            }
        } catch (Exception e) {
            ctx.status(400);
        }
    }

    private void getAllMessagesHandler(Context ctx) {
        ctx.json(messageService.getAllMessages());
    }

    private void getMessageByIdHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.getMessageById(messageId);
        if (message != null) {
            ctx.json(message);
        } else {
            ctx.status(200).json("");
        }
    }

    private void deleteMessageByIdHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message deletedMessage = messageService.deleteMessageById(messageId);
        if (deletedMessage != null) {
            ctx.json(deletedMessage);
        } else {
            ctx.status(200).json("");
        }
    }

    private void updateMessageHandler(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message message = objectMapper.readValue(ctx.body(), Message.class);
            Message updatedMessage = messageService.updateMessage(messageId, message);
            if (updatedMessage != null) {
                ctx.json(updatedMessage);
            } else {
                ctx.status(400);
            }
        } catch (Exception e) {
            ctx.status(400);
        }
    }

    private void getMessagesByUserHandler(Context ctx) {
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));
        ctx.json(messageService.getMessagesByUser(accountId));
    }
}