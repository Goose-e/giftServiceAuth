package curse.auth.controller;

import curse.auth.dto.friend.FriendActionRequest;
import curse.auth.dto.friend.FriendDto;
import curse.auth.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/add")
    public ResponseEntity<Void> addFriend(Authentication authentication, @RequestBody FriendActionRequest request) {
        friendService.addFriend(authentication.getName(), request.getFriendLogin());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFriend(Authentication authentication, @RequestBody FriendActionRequest request) {
        friendService.removeFriend(authentication.getName(), request.getFriendLogin());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FriendDto>> getFriends(Authentication authentication) {
        return ResponseEntity.ok(friendService.getFriends(authentication.getName()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FriendDto>> searchUsers(@RequestParam("username") String username) {
        return ResponseEntity.ok(friendService.searchUsers(username));
    }
}
