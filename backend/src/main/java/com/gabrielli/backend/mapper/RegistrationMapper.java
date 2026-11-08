package com.gabrielli.backend.mapper;

import com.gabrielli.backend.dto.RegistrationResponse;
import com.gabrielli.backend.entity.Registration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegistrationMapper {

    private final TeamMapper teamMapper;

    public RegistrationResponse toResponse(Registration registration) {
        return new RegistrationResponse(
                registration.getId(),
                teamMapper.toResponse(registration.getTeam()),
                registration.getCreatedAt()
        );
    }

    public List<RegistrationResponse> toResponseList(List<Registration> registrations) {
        return registrations.stream().map(this::toResponse).toList();
    }
}