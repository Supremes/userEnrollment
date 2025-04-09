package org.example.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.PlistGenerator;
import org.example.vo.WellKnow;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
public class UserEnrollmentService {

    @GetMapping(path = ".well-known/com.apple.remotemanagement")
    public WellKnow serviceDiscovery(@RequestParam("model-family") String modelFamily,
                                     @RequestParam("user-identifier") String userIdentifier,
                                     HttpServletRequest request) {
        log.info("params: {}, {}", modelFamily, userIdentifier);
        WellKnow.AvailableServer availableServer = WellKnow.AvailableServer.builder().baseURL("https://junkangd.xmdev.cloud.com/enroll").version("mdm-byod").build();
        List<WellKnow.AvailableServer> availableServerList = List.of(availableServer);
        WellKnow wellKnow = WellKnow.builder().servers(availableServerList).build();
        return wellKnow;
    }

    @PostMapping(path = "/enroll", consumes = "application/pkcs7-signature;charset=UTF-8", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> handleEnrollment(@RequestHeader HttpHeaders headers, @RequestBody byte[] signatureData) throws Exception {
        if (!validateToken(headers.getFirst("Authorization"))) {
            log.error("return 401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("WWW-Authenticate",
                            "Bearer method=\"apple-as-web\"; url=\"https://junkangd.xmdev.cloud.com/authenticate\"")
                    .build();
        }

        String plistXml = PlistGenerator.generatePlistResponse();
        log.info("plist string: {}", plistXml);
        return ResponseEntity.ok()
                .header("Content-Type", "application/x-apple-aspen-config")
                .header("Content-Length", String.valueOf(plistXml.getBytes().length))
                .body(plistXml);
    }

    @GetMapping("/authenticate")
    public ResponseEntity<String> handleAuth(@RequestParam("user-identifier") String userIdentifier) {
        log.info("user: {}", userIdentifier);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/enroll/byod")
    public void doEnroll(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!request.getHeader("Content-Type").equals("application/pkcs7-signature")) {
            log.error("Unsupported content type");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return ;
        }
    }

    private boolean validateToken(String authHeader) {
        return authHeader != null && authHeader.startsWith("Bearer");
    }

    private String constructXMLBody() {
        return String.format(
                "<dict>\n" +
                        "  <key>PayloadType</key>\n" +
                        "  <string>Configuration</string>\n" +
                        "  <key>AssignedManagedAppleID</key>\n" +
                        "  <string>junkang.du@citrixcom2.appleid.com</string>\n" +
                        "  <key>EnrollmentMode</key>\n" +
                        "  <string>BYOD</string>\n" +
                        "</dict>");
    }

}
