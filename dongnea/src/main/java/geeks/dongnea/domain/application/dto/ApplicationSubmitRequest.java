package geeks.dongnea.domain.application.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class ApplicationSubmitRequest {
    private Long recruitmentId;
    private Map<String, Object> answers;
}
