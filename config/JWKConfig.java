package com.sploot.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class JWKConfig {

	@Autowired
	RestTemplate restTemplate;

	@Bean
	public ConfigurableJWTProcessor configurableJWTProcessor() throws MalformedURLException {
		ResourceRetriever resourceRetriever = new DefaultResourceRetriever(8000, 8000);
		URL jwkSetURL = new URL("https://appleid.apple.com/auth/keys");
		JWKSource keySource = new RemoteJWKSet(jwkSetURL, resourceRetriever);
		ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
		JWSKeySelector keySelector = new JWSVerificationKeySelector(JWSAlgorithm.ES256, keySource);
		jwtProcessor.setJWSKeySelector(keySelector);
		return jwtProcessor;
	}


	private PublicKey getPublicKey(int arrIndex) throws Exception {
		/*
		String publicKeyString = getKeyFromAppleTokenUrl("n", arrIndex);
		String publicKeyExponent = getKeyFromAppleTokenUrl("e", arrIndex);

		BigInteger n = new BigInteger(1, Decoders.BASE64URL.decode(publicKeyString));
		BigInteger e = new BigInteger(1, Decoders.BASE64URL.decode(publicKeyExponent));

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		KeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		*/
		return null;
	}

	public String getKeyFromAppleTokenUrl(String key, int arrIndex) {
		ObjectMapper objectMapper = new ObjectMapper();
		String uri = "https://appleid.apple.com/auth/keys";
		Map map = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(null, null), Map.class).getBody();
//		String jsonString = "{\n" +
//				"  \"keys\": [\n" +
//				"    {\n" +
//				"      \"kty\": \"RSA\",\n" +
//				"      \"kid\": \"86D88Kf\",\n" +
//				"      \"use\": \"sig\",\n" +
//				"      \"alg\": \"RS256\",\n" +
//				"      \"n\": \"iGaLqP6y-SJCCBq5Hv6pGDbG_SQ11MNjH7rWHcCFYz4hGwHC4lcSurTlV8u3avoVNM8jXevG1Iu1SY11qInqUvjJur--hghr1b56OPJu6H1iKulSxGjEIyDP6c5BdE1uwprYyr4IO9th8fOwCPygjLFrh44XEGbDIFeImwvBAGOhmMB2AD1n1KviyNsH0bEB7phQtiLk-ILjv1bORSRl8AK677-1T8isGfHKXGZ_ZGtStDe7Lu0Ihp8zoUt59kx2o9uWpROkzF56ypresiIl4WprClRCjz8x6cPZXU2qNWhu71TQvUFwvIvbkE1oYaJMb0jcOTmBRZA2QuYw-zHLwQ\",\n" +
//				"      \"e\": \"AQAB\"\n" +
//				"    },\n" +
//				"    {\n" +
//				"      \"kty\": \"RSA\",\n" +
//				"      \"kid\": \"eXaunmL\",\n" +
//				"      \"use\": \"sig\",\n" +
//				"      \"alg\": \"RS256\",\n" +
//				"      \"n\": \"4dGQ7bQK8LgILOdLsYzfZjkEAoQeVC_aqyc8GC6RX7dq_KvRAQAWPvkam8VQv4GK5T4ogklEKEvj5ISBamdDNq1n52TpxQwI2EqxSk7I9fKPKhRt4F8-2yETlYvye-2s6NeWJim0KBtOVrk0gWvEDgd6WOqJl_yt5WBISvILNyVg1qAAM8JeX6dRPosahRVDjA52G2X-Tip84wqwyRpUlq2ybzcLh3zyhCitBOebiRWDQfG26EH9lTlJhll-p_Dg8vAXxJLIJ4SNLcqgFeZe4OfHLgdzMvxXZJnPp_VgmkcpUdRotazKZumj6dBPcXI_XID4Z4Z3OM1KrZPJNdUhxw\",\n" +
//				"      \"e\": \"AQAB\"\n" +
//				"    }\n" +
//				"  ]\n" +
//				"}";
		try {
//			Map<String, Object> map = objectMapper.readValue(jsonString, Map.class);
			Object keyValuePair = map.get("keys");
			if (keyValuePair != null && keyValuePair instanceof ArrayList) {
				Map map1 = (Map) ((ArrayList) keyValuePair).get(arrIndex);
				return (String) map1.get(key);
			}
		} catch (Exception e) {
			log.error("Exception in  converting to string: {} ", e);
		}
		return null;
	}

	private String getEmailAfterDecodingJwt(String jwt) throws Exception {
		PublicKey publicKey0 = getPublicKey(0);
		PublicKey publicKey1 = getPublicKey(1);
		Claims claims = null;
		try {
			claims = Jwts.parser()
					.setSigningKey(publicKey0)
					.parseClaimsJws(jwt)
					.getBody(); // will throw exception if token is expired, etc.
		} catch (Exception e) {
			log.error("Exception in getting claims from public key 1: {}", e);
			claims = Jwts.parser()
					.setSigningKey(publicKey1)
					.parseClaimsJws(jwt)
					.getBody(); // will throw exception if token is expired, etc.
		}
		System.out.print("Claims = " + claims);
		return (String) claims.get("email");
	}

	public Map verifyAuthCode(String authCode) throws Exception {
		String email = getEmailAfterDecodingJwt(authCode);
		return new HashMap() {{
			put("email", email);
		}};
	}
}