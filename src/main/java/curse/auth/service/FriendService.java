package curse.auth.service;

import curse.auth.dto.friend.FriendDto;
import curse.auth.models.Friend;
import curse.auth.models.User;
import curse.auth.repository.FriendRepository;
import curse.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addFriend(String currentLogin, String friendLogin) {
        User currentUser = userRepository.findByLogin(currentLogin)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
        User friendUser = userRepository.findByLogin(friendLogin)
                .orElseThrow(() -> new IllegalArgumentException("Friend user not found"));

        if (currentUser.getUserId().equals(friendUser.getUserId())) {
            throw new IllegalArgumentException("You cannot add yourself as friend");
        }

        if (friendRepository.existsByUserIdAndFriendUserId(currentUser.getUserId(), friendUser.getUserId())) {
            return;
        }

        Friend friend = new Friend();
        friend.setUserId(currentUser.getUserId());
        friend.setFriendUserId(friendUser.getUserId());
        friendRepository.save(friend);
    }

    @Transactional
    public void removeFriend(String currentLogin, String friendLogin) {
        User currentUser = userRepository.findByLogin(currentLogin)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
        User friendUser = userRepository.findByLogin(friendLogin)
                .orElseThrow(() -> new IllegalArgumentException("Friend user not found"));

        friendRepository.deleteByUserIdAndFriendUserId(currentUser.getUserId(), friendUser.getUserId());
    }

    @Transactional(readOnly = true)
    public List<FriendDto> getFriends(String currentLogin) {
        User currentUser = userRepository.findByLogin(currentLogin)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));

        return friendRepository.findByUserId(currentUser.getUserId()).stream()
                .map(Friend::getFriendUserId)
                .map(friendId -> userRepository.findById(friendId)
                        .orElseThrow(() -> new IllegalArgumentException("Friend user not found")))
                .map(user -> new FriendDto(user.getUserId(), user.getUsername(), user.getLogin()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendDto> searchUsers(String usernamePart) {
        return userRepository.findByUsernameContainingIgnoreCase(usernamePart).stream()
                .map(user -> new FriendDto(user.getUserId(), user.getUsername(), user.getLogin()))
                .toList();
    }
}
