package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getByRequesterId(Long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> getAll(Long requesterId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from != null ? from : 0,
                "size", size != null ? size : 10
        );
        return get("/all?from={from}&size={size}", requesterId, parameters);
    }

    public ResponseEntity<Object> getById(Long requesterId, Long requestId) {
        return get("/" + requestId, requesterId);
    }

    public ResponseEntity<Object> create(Long userId, ItemRequestInputDto itemRequestInputDto) {
        return post("", userId, itemRequestInputDto);
    }
}
