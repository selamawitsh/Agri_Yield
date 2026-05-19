package com.agriyield.userservice.application.mapper;

import com.agriyield.userservice.core.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserMapper {
    
    private final ModelMapper modelMapper;
    
    public <T> T toDto(User user, Class<T> dtoClass) {
        return modelMapper.map(user, dtoClass);
    }
    
    public void updateUserFromMap(Map<String, Object> updates, User user) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "preferredLanguage" -> user.setPreferredLanguage(
                    com.agriyield.userservice.core.domain.enums.PreferredLanguage.fromCode((String) value)
                );
                case "email" -> user.setEmail((String) value);
                // Only allow updating non-identity fields
            }
        });
        user.setUpdatedAt(java.time.LocalDateTime.now());
    }
}
