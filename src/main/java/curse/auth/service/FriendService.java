package curse.auth.service;

import curse.auth.dto.common.EmptyResponseDTO;
import curse.auth.dto.friend.FriendActionRequest;
import curse.auth.dto.friend.FriendDto;
import curse.auth.dto.friend.FriendListResponseDTO;
import curse.auth.httpResponse.DefaultHttpResponseBody;
import curse.auth.httpResponse.HttpResponseBody;
import curse.auth.models.Friend;
import curse.auth.models.User;
import curse.auth.repository.FriendRepository;
import curse.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static curse.auth.constants.SysConst.OC_OK;

@Service
@RequiredArgsConstructor
public class FriendService implements IFriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public HttpResponseBody<EmptyResponseDTO> addFriend(String currentLogin, FriendActionRequest request) {
        User currentUser = userRepository.findByLogin(currentLogin)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
        User friendUser = userRepository.findByLogin(request.getFriendLogin())
                .orElseThrow(() -> new IllegalArgumentException("Friend user not found"));

        if (currentUser.getUserId().equals(friendUser.getUserId())) {
            throw new IllegalArgumentException("You cannot add yourself as friend");
        }

        if (!friendRepository.existsByUserIdAndFriendUserId(currentUser.getUserId(), friendUser.getUserId())) {
            Friend friend = new Friend();
            friend.setUserId(currentUser.getUserId());
            friend.setFriendUserId(friendUser.getUserId());
            friendRepository.save(friend);
        }

        DefaultHttpResponseBody<EmptyResponseDTO> response = new DefaultHttpResponseBody<>();
        response.setResponseCode(OC_OK);
        response.setMessage("Success");
        response.setResponseEntity(EmptyResponseDTO.INSTANCE);
        return response;
    }

    @Override
    @Transactional
    public HttpResponseBody<EmptyResponseDTO> removeFriend(String currentLogin, FriendActionRequest request) {
        User currentUser = userRepository.findByLogin(currentLogin)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
        User friendUser = userRepository.findByLogin(request.getFriendLogin())
                .orElseThrow(() -> new IllegalArgumentException("Friend user not found"));

        friendRepository.deleteByUserIdAndFriendUserId(currentUser.getUserId(), friendUser.getUserId());

        DefaultHttpResponseBody<EmptyResponseDTO> response = new DefaultHttpResponseBody<>();
        response.setResponseCode(OC_OK);
        response.setMessage("Success");
        response.setResponseEntity(EmptyResponseDTO.INSTANCE);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public HttpResponseBody<FriendListResponseDTO> getFriends(String currentLogin) {
        User currentUser = userRepository.findByLogin(currentLogin)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));

        List<FriendDto> friends = friendRepository.findByUserId(currentUser.getUserId()).stream()
                .map(Friend::getFriendUserId)
                .map(friendId -> userRepository.findById(friendId)
                        .orElseThrow(() -> new IllegalArgumentException("Friend user not found")))
                .map(user -> new FriendDto(user.getUserId(), user.getUsername(), user.getLogin()))
                .toList();

        DefaultHttpResponseBody<FriendListResponseDTO> response = new DefaultHttpResponseBody<>();
        response.setResponseCode(OC_OK);
        response.setMessage("Success");
        response.setResponseEntity(new FriendListResponseDTO(friends));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public HttpResponseBody<FriendListResponseDTO> searchUsers(String usernamePart) {
        List<FriendDto> users = userRepository.findByUsernameContainingIgnoreCase(usernamePart).stream()
                .map(user -> new FriendDto(user.getUserId(), user.getUsername(), user.getLogin()))
                .toList();

        DefaultHttpResponseBody<FriendListResponseDTO> response = new DefaultHttpResponseBody<>();
        response.setResponseCode(OC_OK);
        response.setMessage("Success");
        response.setResponseEntity(new FriendListResponseDTO(users));
        return response;
    }
}
