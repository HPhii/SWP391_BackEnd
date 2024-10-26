package com.example.koifishfengshui.service;

import com.example.koifishfengshui.enums.LoginProvider;
import com.example.koifishfengshui.model.entity.Account;
import com.example.koifishfengshui.repository.AccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TokenService tokenService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        Account account = accountRepository.findAccountByEmail(email);

        if (account == null) {
            account = new Account();
            account.setEmail(email);
            account.setUsername(oAuth2User.getAttribute("name"));
            account.setLoginProvider(LoginProvider.GOOGLE);
            accountRepository.save(account);
        }

        Map<String, Object> attributes = oAuth2User.getAttributes();
        attributes.put("token", tokenService.generateToken(account));

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "sub");
    }
}

