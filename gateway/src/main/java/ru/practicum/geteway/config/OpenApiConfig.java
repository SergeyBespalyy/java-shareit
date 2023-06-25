package ru.practicum.geteway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "ShareIt",
                description = "Share It - это веб-приложение, предназначенное для обмена и совместного использования различных вещей между пользователями. Платформа позволяет людям делиться своими вещами, которые они могут предоставлять другим пользователям в аренду или бесплатно.",
                version = "1.0.0",
                contact = @Contact(
                        name = "Bespalyy Sergey",
                        email = "bespaliy.sergei@yandex.ru",
                        url = "https://github.com/SergeyBespalyy/java-shareit"
                )
        )
)
public class OpenApiConfig {

}