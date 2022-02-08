package main.service;

import lombok.RequiredArgsConstructor;
import main.model.GlobalSetting;
import main.repository.SettingsRepository;
import main.response.api.SettingsResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsRepository repository;

    public SettingsResponse getSettingsResponse() {
        SettingsResponse settingsResponse = new SettingsResponse();

        settingsResponse.setMultiuserMode(repository.getSetting("MULTIUSER_MODE").getValue().equals("YES"));
        settingsResponse.setPostPremoderation(repository.getSetting("POST_PREMODERATION").getValue().equals("YES"));
        settingsResponse.setStatisticsIsPublic(repository.getSetting("STATISTICS_IS_PUBLIC").getValue().equals("YES"));

        return settingsResponse;
    }

    public void editSettings(Map<String, Boolean> request) {

        List<String> settings = new ArrayList<>();
        settings.add("MULTIUSER_MODE");
        settings.add("POST_PREMODERATION");
        settings.add("STATISTICS_IS_PUBLIC");

        settings.forEach(s -> {
            GlobalSetting setting = repository.getSetting(s);
            if (request.get(s)) {
                setting.setValue("YES");
            } else {
                setting.setValue("NO");
            }
            repository.save(setting);
        });
    }
}