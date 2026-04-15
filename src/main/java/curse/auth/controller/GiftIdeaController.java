package curse.auth.controller;

import curse.auth.dto.common.EmptyResponseDTO;
import curse.auth.dto.gift.CreateGiftRequest;
import curse.auth.dto.gift.GiftListResponseDTO;
import curse.auth.dto.gift.WishlistActionRequest;
import curse.auth.dto.recommendation.RecommendationResponseDTO;
import curse.auth.dto.victim.VictimListResponseDTO;
import curse.auth.dto.victim.VictimRequest;
import curse.auth.httpResponse.HttpResponseBody;
import curse.auth.service.IGiftIdeaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gift-ideas")
@RequiredArgsConstructor
public class GiftIdeaController {
    private final IGiftIdeaService giftIdeaService;

    @GetMapping("/catalog")
    public HttpResponseBody<GiftListResponseDTO> listCatalog() {
        return giftIdeaService.listGifts();
    }

    @PostMapping("/catalog")
    public HttpResponseBody<GiftListResponseDTO> createGift(@RequestBody CreateGiftRequest request) {
        return giftIdeaService.createGift(request);
    }

    @PostMapping("/wishlist")
    public HttpResponseBody<EmptyResponseDTO> addToWishlist(Authentication authentication,
                                                            @RequestBody WishlistActionRequest request) {
        return giftIdeaService.addGiftToWishlist(authentication.getName(), request);
    }

    @DeleteMapping("/wishlist")
    public HttpResponseBody<EmptyResponseDTO> removeFromWishlist(Authentication authentication,
                                                                 @RequestBody WishlistActionRequest request) {
        return giftIdeaService.removeGiftFromWishlist(authentication.getName(), request);
    }

    @GetMapping("/wishlist/me")
    public HttpResponseBody<GiftListResponseDTO> myWishlist(Authentication authentication) {
        return giftIdeaService.getMyWishlist(authentication.getName());
    }

    @GetMapping("/wishlist/friend/{friendLogin}")
    public HttpResponseBody<GiftListResponseDTO> friendWishlist(Authentication authentication,
                                                                @PathVariable String friendLogin) {
        return giftIdeaService.getFriendWishlist(authentication.getName(), friendLogin);
    }

    @GetMapping("/recommendations/friend/{friendLogin}")
    public HttpResponseBody<RecommendationResponseDTO> friendRecommendations(Authentication authentication,
                                                                             @PathVariable String friendLogin) {
        return giftIdeaService.getFriendGiftIdeas(authentication.getName(), friendLogin);
    }

    @PostMapping("/victim")
    public HttpResponseBody<VictimListResponseDTO> createVictim(Authentication authentication,
                                                                @RequestBody VictimRequest request) {
        return giftIdeaService.createVictimProfile(authentication.getName(), request);
    }

    @GetMapping("/victim")
    public HttpResponseBody<VictimListResponseDTO> listVictims(Authentication authentication) {
        return giftIdeaService.getMyVictims(authentication.getName());
    }

    @GetMapping("/recommendations/victim/{victimId}")
    public HttpResponseBody<RecommendationResponseDTO> victimRecommendations(Authentication authentication,
                                                                             @PathVariable Long victimId) {
        return giftIdeaService.getVictimGiftIdeas(authentication.getName(), victimId);
    }
}
