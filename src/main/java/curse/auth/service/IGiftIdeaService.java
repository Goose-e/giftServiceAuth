package curse.auth.service;

import curse.auth.dto.common.EmptyResponseDTO;
import curse.auth.dto.gift.CreateGiftRequest;
import curse.auth.dto.gift.GiftListResponseDTO;
import curse.auth.dto.gift.WishlistActionRequest;
import curse.auth.dto.recommendation.RecommendationResponseDTO;
import curse.auth.dto.victim.VictimListResponseDTO;
import curse.auth.dto.victim.VictimRequest;
import curse.auth.httpResponse.HttpResponseBody;

public interface IGiftIdeaService {
    HttpResponseBody<GiftListResponseDTO> listGifts();

    HttpResponseBody<GiftListResponseDTO> createGift(CreateGiftRequest request);

    HttpResponseBody<EmptyResponseDTO> addGiftToWishlist(String currentLogin, WishlistActionRequest request);

    HttpResponseBody<EmptyResponseDTO> removeGiftFromWishlist(String currentLogin, WishlistActionRequest request);

    HttpResponseBody<GiftListResponseDTO> getMyWishlist(String currentLogin);

    HttpResponseBody<GiftListResponseDTO> getFriendWishlist(String currentLogin, String friendLogin);

    HttpResponseBody<RecommendationResponseDTO> getFriendGiftIdeas(String currentLogin, String friendLogin);

    HttpResponseBody<VictimListResponseDTO> createVictimProfile(String currentLogin, VictimRequest request);

    HttpResponseBody<VictimListResponseDTO> getMyVictims(String currentLogin);

    HttpResponseBody<RecommendationResponseDTO> getVictimGiftIdeas(String currentLogin, Long victimId);
}
