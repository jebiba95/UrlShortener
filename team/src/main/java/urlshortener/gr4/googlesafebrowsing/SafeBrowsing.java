package urlshortener.gr4.googlesafebrowsing;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import urlshortener.common.domain.googlesafebrowsing.Client;
import urlshortener.common.domain.googlesafebrowsing.RequestFormat;
import urlshortener.common.domain.googlesafebrowsing.ResponseFormat;
import urlshortener.common.domain.googlesafebrowsing.ThreatEntry;
import urlshortener.common.domain.googlesafebrowsing.ThreatInfo;
import urlshortener.gr4.web.UrlShortenerControllerWithLogs;

public class SafeBrowsing {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
    public SafeBrowsing() {}

    @Async
    public boolean isSafe(String url) throws JsonProcessingException {

        String urlBase = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=AIzaSyCMQF934rfLzpVNkN64M9qB_V9N5RaessE";
        ObjectMapper mapper = new ObjectMapper();
        Client client = new Client("GROUP_4","1.5.2");       
        ThreatInfo info = new ThreatInfo(new ThreatEntry(url));

        RestTemplate restTemplate = new RestTemplate();
        RequestFormat requestFormat=new RequestFormat(client,info);
        String requestJson=mapper.writeValueAsString(requestFormat);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity request= new HttpEntity(requestJson, headers );

        logger.info("\nREQUEST TO GOOGLE SAFE BROWSING:");
        logger.info(requestJson);
        ResponseEntity<ResponseFormat> response = restTemplate.exchange(urlBase, HttpMethod.POST, request, ResponseFormat.class);
        String jsonRespuesta = mapper.writeValueAsString(response);
        logger.info("RESPONSE FROM  GOOGLE SAFE BROWSING:");
        logger.info(jsonRespuesta+"\n");
        
        boolean isSafe = false;
        
        if(response.getStatusCodeValue() == 200) {                        
            if(response.getBody().getMatches().isEmpty()) {
                logger.info("THE URL " + url + " IS SAFE");
                isSafe = true;
            }
            else {
                logger.info("THE URL " + url + " IS UNSAFE");
            }
        }
        else
        {
        	logger.info("CONNECTION COULD NOT ESTABLISHED. CODE: "+response.getStatusCodeValue());
        }       
        return isSafe;
    }
    
    public static boolean CheckIsSafe(String url) throws JsonProcessingException
    {
    	SafeBrowsing sb = new SafeBrowsing();
		boolean isSafe = true;
        isSafe = sb.isSafe(url);
        logger.info("The uri " + url + "is safe? " + isSafe);
        return isSafe;
    }
}
