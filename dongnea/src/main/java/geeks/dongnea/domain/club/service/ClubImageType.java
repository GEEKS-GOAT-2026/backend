package geeks.dongnea.domain.club.service;

import java.util.Locale;

public enum ClubImageType {
    PROFILE("profile"),
    EVENT("events"),
    ACTIVITY("activities"),
    NOTICE("notices");

    private final String directory;

    ClubImageType(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public static ClubImageType from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이미지 용도를 입력해야 합니다.");
        }

        try {
            return ClubImageType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("이미지 용도는 PROFILE, EVENT, ACTIVITY, NOTICE 중 하나여야 합니다.");
        }
    }
}
