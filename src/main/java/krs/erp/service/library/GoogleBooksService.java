package krs.erp.service.library;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;
import krs.erp.dto.library.BookDetailsDTO;

@Service
@Slf4j
public class GoogleBooksService {

    @Value("${google.books.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

    public GoogleBooksService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Optional<BookDetailsDTO> getBookDetailsByIsbn(String isbn) {
        try {
            String url = GOOGLE_BOOKS_API_URL + isbn;
            if (apiKey != null && !apiKey.isEmpty()) {
                url += "&key=" + apiKey;
            }

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (root.path("totalItems").asInt() > 0) {
                JsonNode volumeInfo = root.path("items").get(0).path("volumeInfo");

                BookDetailsDTO bookDetails = new BookDetailsDTO();
                bookDetails.setTitle(volumeInfo.path("title").asText(null));
                bookDetails.setSubtitle(volumeInfo.path("subtitle").asText(null));
                bookDetails.setAuthors(objectMapper.convertValue(volumeInfo.path("authors"), java.util.List.class));
                bookDetails.setPublisher(volumeInfo.path("publisher").asText(null));
                bookDetails.setPublishedDate(volumeInfo.path("publishedDate").asText(null));
                bookDetails.setDescription(volumeInfo.path("description").asText(null));
                bookDetails.setPageCount(volumeInfo.path("pageCount").asInt(0));
                bookDetails
                        .setCategories(objectMapper.convertValue(volumeInfo.path("categories"), java.util.List.class));
                bookDetails.setThumbnailUrl(volumeInfo.path("imageLinks").path("thumbnail").asText(null));
                bookDetails.setIsbn(isbn);

                return Optional.of(bookDetails);
            }
        } catch (Exception e) {
            log.error("Error fetching book details from Google Books API for ISBN: {}", isbn, e);
        }
        return Optional.empty();
    }
}
