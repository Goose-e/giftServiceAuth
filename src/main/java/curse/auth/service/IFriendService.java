package curse.auth.service;

import curse.auth.dto.common.EmptyResponseDTO;
import curse.auth.dto.friend.FriendActionRequest;
import curse.auth.dto.friend.FriendListResponseDTO;
import curse.auth.httpResponse.HttpResponseBody;

public interface IFriendService {
    HttpResponseBody<EmptyResponseDTO> addFriend(String currentLogin, FriendActionRequest request);

    HttpResponseBody<EmptyResponseDTO> removeFriend(String currentLogin, FriendActionRequest request);

    HttpResponseBody<FriendListResponseDTO> getFriends(String currentLogin);

    HttpResponseBody<FriendListResponseDTO> searchUsers(String usernamePart);
}
