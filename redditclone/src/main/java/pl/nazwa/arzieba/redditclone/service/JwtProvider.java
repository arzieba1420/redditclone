package pl.nazwa.arzieba.redditclone.service;


import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import pl.nazwa.arzieba.redditclone.exceptions.SpringRedditException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@Service
public class JwtProvider {

    private KeyStore keyStore;

    @PostConstruct
    public void init(){
        try {
            keyStore = KeyStore.getInstance("PKCS12");
            InputStream inputStream = getClass().getResourceAsStream("/bootsecurity.p12");
            keyStore.load(inputStream,"dtnetwork".toCharArray());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new SpringRedditException("Exception occurred while loading keystore");
        }
    }

    public String generateToken(Authentication authentication){
        org.springframework.security.core.userdetails.User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .compact();
    }

    private Key getPrivateKey() {
        try {
            return  keyStore.getKey("bootsecurity","dtnetwork".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occurred while retrievieng public key from keystore");
        }
    }

}
