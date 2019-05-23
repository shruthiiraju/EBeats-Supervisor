package com.example.ebeats;

public class ChatMessage  {
        public String name;
        public String message;
        String id;

        public ChatMessage() {
        }

        public ChatMessage(String name, String message,String id) {
            this.id=id;
            this.name = name;
            this.message = message;
        }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

