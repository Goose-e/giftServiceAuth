package curse.auth.jwt;//package com.example.companyReputationManagement.jwt;
//
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class OAuth2Service {
//
//    private final WebClient.Builder webClientBuilder;
//
//    @Autowired
//    public OAuth2Service(WebClient.Builder webClientBuilder) {
//        this.webClientBuilder = webClientBuilder;
//    }
//
//    public String getOAuth2ProtectedResource(String accessToken) {
//        WebClient webClient = webClientBuilder.baseUrl("https://api.example.com")
//                .defaultHeader("Authorization", "Bearer " + accessToken)
//                .build();
//
//        return webClient.get()
//                .uri("/resource-endpoint")
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//    }
//}
