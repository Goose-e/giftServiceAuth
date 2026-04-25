package curse.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import curse.auth.dto.common.EmptyResponseDTO;
import curse.auth.dto.gift.CreateGiftRequest;
import curse.auth.dto.gift.GiftDto;
import curse.auth.dto.gift.GiftListResponseDTO;
import curse.auth.dto.gift.WishlistActionRequest;
import curse.auth.dto.ollama.OllamaGiftResponse;
import curse.auth.dto.recommendation.RecommendationDto;
import curse.auth.dto.recommendation.RecommendationResponseDTO;
import curse.auth.dto.victim.VictimDto;
import curse.auth.dto.victim.VictimListResponseDTO;
import curse.auth.dto.victim.VictimRequest;
import curse.auth.httpResponse.DefaultHttpResponseBody;
import curse.auth.httpResponse.HttpResponseBody;
import curse.auth.models.*;
import curse.auth.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static curse.auth.constants.SysConst.OC_OK;

@Service
@RequiredArgsConstructor
public class GiftIdeaService implements IGiftIdeaService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final GiftRepository giftRepository;
    private final TagRepository tagRepository;
    private final WishlistDataRepository wishlistDataRepository;
    private final UserTagWeightRepository userTagWeightRepository;
    private final VictimRepository victimRepository;
    private final IOllamaService ollamaService;

    @Override
    @Transactional(readOnly = true)
    public HttpResponseBody<GiftListResponseDTO> listGifts() {
        List<GiftDto> gifts = giftRepository.findAll().stream()
                .map(this::mapGiftDto)
                .toList();
        return ok(new GiftListResponseDTO(gifts));
    }

    @Override
    @Transactional
    public HttpResponseBody<GiftListResponseDTO> createGift(CreateGiftRequest request) {
        validateGiftRequest(request);

        Tag tag = tagRepository.findByTagNameIgnoreCase(request.getTagName())
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setTagName(request.getTagName().trim());
                    return tagRepository.save(newTag);
                });

        Gift gift = new Gift();
        gift.setGiftName(request.getGiftName().trim());
        gift.setGiftAvgPrice(request.getGiftAvgPrice());
        gift.setTag(tag);
        giftRepository.save(gift);

        return listGifts();
    }

    @Override
    @Transactional
    public HttpResponseBody<EmptyResponseDTO> addGiftToWishlist(String currentLogin, WishlistActionRequest request) {
        User currentUser = getUserByLogin(currentLogin);
        Gift gift = giftRepository.findById(request.getGiftId())
                .orElseThrow(() -> new IllegalArgumentException("Gift not found"));

        if (wishlistDataRepository.existsByUserIdAndGiftId(currentUser.getUserId(), gift.getGiftId())) {
            return ok(EmptyResponseDTO.INSTANCE);
        }

        WishlistData wishlistData = new WishlistData();
        wishlistData.setUserId(currentUser.getUserId());
        wishlistData.setGiftId(gift.getGiftId());
        wishlistDataRepository.save(wishlistData);

        recalculateUserTagWeights(currentUser.getUserId());
        return ok(EmptyResponseDTO.INSTANCE);
    }

    @Override
    @Transactional
    public HttpResponseBody<EmptyResponseDTO> removeGiftFromWishlist(String currentLogin, WishlistActionRequest request) {
        User currentUser = getUserByLogin(currentLogin);
        wishlistDataRepository.deleteByUserIdAndGiftId(currentUser.getUserId(), request.getGiftId());
        recalculateUserTagWeights(currentUser.getUserId());
        return ok(EmptyResponseDTO.INSTANCE);
    }

    @Override
    @Transactional(readOnly = true)
    public HttpResponseBody<GiftListResponseDTO> getMyWishlist(String currentLogin) {
        User currentUser = getUserByLogin(currentLogin);
        return ok(new GiftListResponseDTO(loadWishlistByUserId(currentUser.getUserId())));
    }

    @Override
    @Transactional(readOnly = true)
    public HttpResponseBody<GiftListResponseDTO> getFriendWishlist(String currentLogin, String friendLogin) {
        User currentUser = getUserByLogin(currentLogin);
        User friend = getUserByLogin(friendLogin);

        ensureFriendRelation(currentUser.getUserId(), friend.getUserId());
        return ok(new GiftListResponseDTO(loadWishlistByUserId(friend.getUserId())));
    }

    @Override
    @Transactional(readOnly = true)
    public HttpResponseBody<RecommendationResponseDTO> getFriendGiftIdeas(String currentLogin, String friendLogin) {
        User currentUser = getUserByLogin(currentLogin);
        User friend = getUserByLogin(friendLogin);

        ensureFriendRelation(currentUser.getUserId(), friend.getUserId());

        List<UserTagWeight> weights = userTagWeightRepository.findByUserId(friend.getUserId());
        Map<Long, BigDecimal> weightMap = new HashMap<>();
        for (UserTagWeight weight : weights) {
            weightMap.put(weight.getTagId(), weight.getTagWeight() == null ? BigDecimal.ZERO : weight.getTagWeight());
        }

        Set<Long> existingWishlistGiftIds = wishlistDataRepository.findByUserId(friend.getUserId()).stream()
                .map(WishlistData::getGiftId)
                .collect(HashSet::new, Set::add, Set::addAll);

        List<RecommendationDto> recommendations = giftRepository.findAll().stream()
                .filter(g -> !existingWishlistGiftIds.contains(g.getGiftId()))
                .sorted(Comparator.comparing(
                        (Gift g) -> weightMap.getOrDefault(g.getTag().getTagId(), BigDecimal.ZERO)).reversed())
                .limit(10)
                .map(g -> new RecommendationDto(
                        g.getGiftId(),
                        g.getGiftName(),
                        buildFriendReason(g.getTag().getTagId(), weightMap.getOrDefault(g.getTag().getTagId(), BigDecimal.ZERO)),
                        resolveTagName(g.getTag().getTagId()),
                        g.getGiftAvgPrice()))
                .toList();

        return ok(new RecommendationResponseDTO(recommendations));
    }

    @Override
    @Transactional
    public HttpResponseBody<VictimListResponseDTO> createVictimProfile(String currentLogin, VictimRequest request) {
        User currentUser = getUserByLogin(currentLogin);

        Victim victim = new Victim();
        victim.setUserId(currentUser.getUserId());
        victim.setGender(request.getGender());
        victim.setBirthdate(request.getBirthdate());
        victim.setCountry(request.getCountry());
        victim.setCity(request.getCity());
        victim.setInfo(request.getInfo());

        victimRepository.save(victim);

        OllamaGiftResponse aiResponse = ollamaService.analyzeVictim(victim);
        List<String> aiTags = normalizeTags(aiResponse.getTags());
        victim.setTagsAnswer(String.join(", ", aiTags));
        victimRepository.save(victim);

        return getMyVictims(currentLogin);
    }
    @Override
    @Transactional(readOnly = true)
    public HttpResponseBody<VictimListResponseDTO> getMyVictims(String currentLogin) {
        User currentUser = getUserByLogin(currentLogin);
        List<VictimDto> victims = victimRepository.findByUserId(currentUser.getUserId()).stream()
                .map(v -> new VictimDto(v.getVictimId(), v.getGender(), v.getBirthdate(), v.getCountry(), v.getCity(), v.getInfo()))
                .toList();
        return ok(new VictimListResponseDTO(victims));
    }

    @Override
    @Transactional
    public HttpResponseBody<RecommendationResponseDTO> getVictimGiftIdeas(String currentLogin, Long victimId) {
        User currentUser = getUserByLogin(currentLogin);

        Victim victim = victimRepository.findByVictimIdAndUserId(victimId, currentUser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Victim profile not found"));

        OllamaGiftResponse aiResponse = ollamaService.analyzeVictim(victim);
        List<String> aiTags = normalizeTags(aiResponse.getTags());
        List<OllamaGiftResponse.GiftIdea> aiIdeas = aiResponse.getIdeas() == null
                ? Collections.emptyList()
                : aiResponse.getIdeas();

        victim.setTagsAnswer(String.join(", ", aiTags));
        victimRepository.save(victim);

        Tag fallbackTag = resolveOrCreateTag(aiTags);
        String reason = (aiResponse.getReason() == null || aiResponse.getReason().isBlank())
                ? "Предложение, сформированное ИИ по анкете получателя"
                : aiResponse.getReason().trim();

        List<RecommendationDto> recommendations = aiIdeas.stream()
                .filter(Objects::nonNull)
                .map(OllamaGiftResponse.GiftIdea::getName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .distinct()
                .map(name -> upsertGiftFromAi(name, aiIdeas, fallbackTag))
                .map(g -> new RecommendationDto(
                        g.getGiftId(),
                        g.getGiftName(),
                        reason,
                        null,
                        g.getGiftAvgPrice()
                ))
                .limit(10)
                .toList();

        if (recommendations.isEmpty()) {
            recommendations = giftRepository.findAll().stream()
                    .limit(5)
                    .map(g -> new RecommendationDto(
                            g.getGiftId(),
                            g.getGiftName(),
                            "Совпадений по AI-тегам не найдено. Показаны универсальные варианты",
                            g.getTag().getTagName(),
                            g.getGiftAvgPrice()
                    ))
                    .toList();
        }

        return ok(new RecommendationResponseDTO(recommendations));
    }
    public void importFromApi() {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://dummyjson.com/products?limit=0"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());

            for (JsonNode product : root.get("products")) {

                String name = product.get("title").asText();
                Long price = product.get("price").asLong();

                String tagName = product.get("category").asText();


                Tag tag = tagRepository.findByTagNameIgnoreCase(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setTagName(tagName);
                            return tagRepository.save(newTag);
                        });

                // проверка чтобы не было дублей
                boolean exists = giftRepository.findAll().stream()
                        .anyMatch(g -> g.getGiftName().equalsIgnoreCase(name));

                if (exists) continue;

                Gift gift = new Gift();
                gift.setGiftName(name);
                gift.setGiftAvgPrice(price);
                gift.setTag(tag);

                giftRepository.save(gift);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ensureFriendRelation(Long userId, Long friendUserId) {
        boolean related = friendRepository.existsByUserIdAndFriendUserId(userId, friendUserId)
                || friendRepository.existsByUserIdAndFriendUserId(friendUserId, userId);
        if (!related) {
            throw new IllegalArgumentException("This user is not in your friends list");
        }
    }

    private List<GiftDto> loadWishlistByUserId(Long userId) {
        return wishlistDataRepository.findByUserId(userId).stream()
                .map(w -> giftRepository.findById(w.getGiftId())
                        .orElseThrow(() -> new IllegalArgumentException("Gift not found")))
                .map(this::mapGiftDto)
                .toList();
    }

    private GiftDto mapGiftDto(Gift gift) {
        return new GiftDto(gift.getGiftId(), gift.getGiftName(), gift.getGiftAvgPrice(), resolveTagName(gift.getTag().getTagId()));
    }

    private String resolveTagName(Long tagId) {
        return tagRepository.findById(tagId)
                .map(Tag::getTagName)
                .orElse("unknown");
    }

    private User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private String buildFriendReason(Long tagId, BigDecimal weight) {
        String tagName = resolveTagName(tagId);
        if (weight.compareTo(BigDecimal.ZERO) <= 0) {
            return "Нейтральная рекомендация по тегу '" + tagName + "'";
        }
        return "Высокий интерес к тегу '" + tagName + "' (вес=" + weight + ")";
    }

    private void validateGiftRequest(CreateGiftRequest request) {
        if (request.getGiftName() == null || request.getGiftName().isBlank()) {
            throw new IllegalArgumentException("giftName is required");
        }
        if (request.getTagName() == null || request.getTagName().isBlank()) {
            throw new IllegalArgumentException("tagName is required");
        }
        if (request.getGiftAvgPrice() == null || request.getGiftAvgPrice() < 0) {
            throw new IllegalArgumentException("giftAvgPrice must be >= 0");
        }
    }

    private void recalculateUserTagWeights(Long userId) {
        Map<Long, Long> countsByTag = new HashMap<>();
        List<WishlistData> items = wishlistDataRepository.findByUserId(userId);
        for (WishlistData item : items) {
            Gift gift = giftRepository.findById(item.getGiftId()).orElse(null);
            if (gift == null) {
                continue;
            }
            countsByTag.merge(gift.getTag().getTagId(), 1L, Long::sum);
        }

        for (Map.Entry<Long, Long> entry : countsByTag.entrySet()) {
            UserTagWeight weight = userTagWeightRepository.findByUserIdAndTagId(userId, entry.getKey())
                    .orElseGet(UserTagWeight::new);
            weight.setUserId(userId);
            weight.setTagId(entry.getKey());
            weight.setTagWeight(BigDecimal.valueOf(entry.getValue()));
            userTagWeightRepository.save(weight);
        }
    }

    private List<String> normalizeTags(List<String> tags) {
        if (tags == null) {
            return Collections.emptyList();
        }
        return tags.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .toList();
    }

    private Tag resolveOrCreateTag(List<String> aiTags) {
        String tagName = aiTags.isEmpty() ? "ai-suggested" : aiTags.get(0);
        return tagRepository.findByTagNameIgnoreCase(tagName)
                .orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setTagName(tagName);
                    return tagRepository.save(tag);
                });
    }

    private Gift upsertGiftFromAi(String giftName, List<OllamaGiftResponse.GiftIdea> aiIdeas, Tag fallbackTag) {
        Optional<Gift> existingGift = giftRepository.findByGiftNameIgnoreCase(giftName);
        if (existingGift.isPresent()) {
            return existingGift.get();
        }

        Gift gift = new Gift();
        gift.setGiftName(giftName);
        gift.setGiftAvgPrice(resolveAiPrice(giftName, aiIdeas));
        gift.setTag(fallbackTag);
        return giftRepository.save(gift);
    }

    private Long resolveAiPrice(String giftName, List<OllamaGiftResponse.GiftIdea> aiIdeas) {
        return aiIdeas.stream()
                .filter(Objects::nonNull)
                .filter(idea -> idea.getName() != null && giftName.equalsIgnoreCase(idea.getName().trim()))
                .map(OllamaGiftResponse.GiftIdea::getPrice)
                .filter(Objects::nonNull)
                .filter(price -> price >= 0)
                .findFirst()
                .orElse(0L);
    }

    private List<String> inferTagsForVictim(Victim victim) {
        String text = ((victim.getInfo() == null ? "" : victim.getInfo()) + " " +
                (victim.getCity() == null ? "" : victim.getCity()) + " " +
                (victim.getCountry() == null ? "" : victim.getCountry()) + " " +
                (victim.getGender() == null ? "" : victim.getGender())).toLowerCase(Locale.ROOT);

        List<String> tags = new ArrayList<>();
        if (containsAny(text, "спорт", "gym", "run", "fitness")) tags.add("sport");
        if (containsAny(text, "game", "игр", "playstation", "xbox")) tags.add("gaming");
        if (containsAny(text, "book", "чит", "книг")) tags.add("books");
        if (containsAny(text, "tech", "gad", "ноут", "телефон")) tags.add("tech");
        if (containsAny(text, "music", "музык", "guitar", "piano")) tags.add("music");
        if (containsAny(text, "travel", "путеш", "trip")) tags.add("travel");

        Long age = calculateAge(victim.getBirthdate());
        if (age != null && age < 16) {
            tags.add("kids");
        } else if (age != null && age > 50) {
            tags.add("wellness");
        }

        if (tags.isEmpty()) {
            tags.add("universal");
        }
        return tags.stream().distinct().toList();
    }

    private boolean containsAny(String text, String... values) {
        for (String value : values) {
            if (text.contains(value)) {
                return true;
            }
        }
        return false;
    }

    private Long calculateAge(LocalDateTime birthdate) {
        if (birthdate == null) {
            return null;
        }
        return ChronoUnit.YEARS.between(birthdate.toLocalDate(), LocalDate.now());
    }

    private <T extends curse.auth.httpResponse.ResponseDto> HttpResponseBody<T> ok(T payload) {
        DefaultHttpResponseBody<T> response = new DefaultHttpResponseBody<>();
        response.setResponseCode(OC_OK);
        response.setMessage("Success");
        response.setResponseEntity(payload);
        return response;
    }
}
