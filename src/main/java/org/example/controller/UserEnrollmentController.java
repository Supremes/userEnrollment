package org.example.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.example.CertificateLoader;
import org.example.vo.WellKnow;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
@RestController
public class UserEnrollmentController {
    // Assume these are injected or loaded from a keystore
    private final X509Certificate signerCertificate;
    private final PrivateKey signerKey;
    private final X509Certificate[] certificatesChain;

    public UserEnrollmentController() throws Exception {
        CertificateLoader.KeyPair keyPair = CertificateLoader.loadFromKeystore(
                "xmdev.pfx", "nY$CZe7A@KkQpe", "1");
        this.signerCertificate = keyPair.certificate;
        this.signerKey = keyPair.privateKey;
        this.certificatesChain = new X509Certificate[]{}; // Add chain if needed
    }

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
    public void handleEnrollment(HttpServletRequest request, HttpServletResponse response, @RequestBody byte[] signatureBody) throws Exception {
//        if (!validateToken(request.getHeader("Authorization"))) {
//            log.error("return 401");
//            response.setHeader("WWW-Authenticate",
//                    "Bearer method=\"apple-as-web\"; url=\"https://junkangd.xmdev.cloud.com/authenticate\"");
//            response.setStatus(401);
////            return ResponseEntity.status(401).build();
//        }

//        // 创建 DigestCalculatorProvider
//        BcDigestCalculatorProvider digestCalculatorProvider = new BcDigestCalculatorProvider();
//        InputStream inputStream = new ByteArrayInputStream(signatureBody);
//        // 使用 CMSTypedStream 包装输入流
//        CMSTypedStream typedStream = new CMSTypedStream(inputStream);
//        // 创建 CMSSignedDataParser 并传入 DigestCalculatorProvider 和 CMSTypedStream
//        CMSSignedDataParser parser = new CMSSignedDataParser(
//                digestCalculatorProvider,
//                typedStream.getContentStream()
//        );


        // 从 resources/static 加载本地 .mobileconfig 文件
        ClassPathResource resource = new ClassPathResource("enrollment.mobileconfig");

        // 读取文件内容为 byte[]
        byte[] fileContent = resource.getInputStream().readAllBytes();

        // 2. 加载 .pfx 文件
        ClassPathResource pfxResource = new ClassPathResource("xmdev.pfx");
        String pfxPassword = "nY$CZe7A@KkQpe"; // 替换为您的 .pfx 文件密码

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream pfxInputStream = pfxResource.getInputStream()) {
            keyStore.load(pfxInputStream, pfxPassword.toCharArray());
        }

        // 3. 获取私钥和证书
        String alias = keyStore.aliases().nextElement(); // 假设 .pfx 中只有一个条目
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, pfxPassword.toCharArray());
        Certificate cert = keyStore.getCertificate(alias);

        // 4. 对 byte[] 数据进行签名
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(fileContent);
        byte[] digitalSignature = signature.sign();

        // 5. 将签名附加到响应头（可选：也可嵌入文件）
        String signatureBase64 = Base64.getEncoder().encodeToString(digitalSignature);


        // Sign the .mobileconfig data
        byte[] signedData = signAndThrow(fileContent, signerCertificate, signerKey, certificatesChain);


        // Return the response
        response.setContentType("application/x-apple-aspen-config");
        response.setContentLength(fileContent.length);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = enrollment_profile.mobileconfig");
        response.setHeader("Connection", "Close");
        response.getOutputStream().write(fileContent);

    }

    public byte[] signAndThrow(
            final byte[] data,
            final X509Certificate signerCertificate,
            final PrivateKey signerKey,
            final X509Certificate... certificatesChain
    ) throws IOException, GeneralSecurityException, CMSException {
        try {
            List<X509Certificate> certList = new ArrayList<>();
            certList.add(signerCertificate);
            certList.addAll(Arrays.asList(certificatesChain));

            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
            CMSTypedData typedData = new CMSProcessableByteArray(data);
            Store certStore = new JcaCertStore(certList);
            log.info("Security Update: Signature Alg.");
            ContentSigner sha256Signer = new JcaContentSignerBuilder("SHA256withRSA").build(signerKey);

            gen.addSignerInfoGenerator(
                    new JcaSignerInfoGeneratorBuilder(
                            new JcaDigestCalculatorProviderBuilder().build())
                            .build(sha256Signer, signerCertificate));

            gen.addCertificates(certStore);

            CMSSignedData preparedData = gen.generate(typedData, true);

            log.info("signAndThrow: encoded payload generated");
            return preparedData.getEncoded();
        } catch (OperatorCreationException ocex) {
            throw new CMSException("Operator creation failed", ocex);
        }
    }

    @GetMapping("/authenticate")
    public ResponseEntity<String> handleAuth(@RequestParam("user-identifier") String userIdentifier) {
        log.info("user: {}", userIdentifier);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/authenticate-results")
    public void doEnroll(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        log.info("authenticate-results request coming");
        // 设置响应状态码为 308 Permanent Redirect
        response.setStatus(308);
        // 设置 Location 头信息
        String accessToken = "dXNlci1pZGVudGl0eQ"; // 这里可以根据实际情况生成 access-token
        String location = "apple-remotemanagement-user-login://authentication-results?access-token=" + accessToken;
        response.setHeader("Location", location);
        // 设置 Content-Length 为 0
        response.setContentLength(0);
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
