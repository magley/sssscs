package com.ib.util.recaptcha;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

// Inspired from https://www.baeldung.com/spring-security-registration-captcha#Validation
public class ReCAPTCHAUtil {
    private static final String KEY_SECRET = "recaptcha.key.secret";
    private static final String ENV_LOCATION = "./data/recaptcha.env";
    private static final String VERIFY_API = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";
    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
    private Map<String, String> env_vars;

    public ReCAPTCHAUtil() {
        env_vars = new HashMap<>();

        String line = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(ENV_LOCATION))) {
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                env_vars.put(parts[0], parts[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processResponse(String response) {
        if(!responseSanityCheck(response)) {
            throw new InvalidReCAPTCHAException("Response contains invalid characters");
        }
        // https://stackoverflow.com/a/43204174
        RequestEntity<Void> request = RequestEntity.get(String.format(VERIFY_API, env_vars.get(KEY_SECRET), response))
                .accept(MediaType.APPLICATION_JSON).build();
        RestTemplate restTemplate = new RestTemplate();
        System.err.println("Sending GET request to " + VERIFY_API);
        String res = restTemplate.exchange(request, String.class).getBody();
        JsonParser parser = JsonParserFactory.getJsonParser();
        Map<String, Object> map = parser.parseMap(res);
        if (!map.get("success").toString().equals("true")) {
            throw new InvalidReCAPTCHAException("Captcha was not successfully validated");
        }
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }
}
