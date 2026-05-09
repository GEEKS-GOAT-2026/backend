package geeks.dongnea.domain.application.service;

import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    public String health() {
        return "application domain ok";
    }
}
