package curse.auth.controller;

import curse.auth.dto.common.EmptyResponseDTO;
import curse.auth.dto.friend.FriendActionRequest;
import curse.auth.dto.friend.FriendListResponseDTO;
import curse.auth.httpResponse.HttpResponseBody;
import curse.auth.service.IFriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
    private final IFriendService friendService;

    @PostMapping("/add")
    public HttpResponseBody<EmptyResponseDTO> addFriend(Authentication authentication, @RequestBody FriendActionRequest request) {
        return friendService.addFriend(authentication.getName(), request);
    }

    @DeleteMapping("/remove")
    public HttpResponseBody<EmptyResponseDTO> removeFriend(Authentication authentication, @RequestBody FriendActionRequest request) {
        return friendService.removeFriend(authentication.getName(), request);
    }

    @GetMapping
    public HttpResponseBody<FriendListResponseDTO> getFriends(Authentication authentication) {
        return friendService.getFriends(authentication.getName());
    }

    @GetMapping("/search")
    public HttpResponseBody<FriendListResponseDTO> searchUsers(@RequestParam("username") String username) {
        return friendService.searchUsers(username);
    }
}
