package antifraud.security;

import antifraud.requests.CardRequest;
import antifraud.requests.IPRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/antifraud/")
public class SecurityController {

    @Autowired
    private final SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<?> addSuspiciousIP(@RequestBody IPRequest ipRequest) {
        return securityService.addSuspiciousIP(ipRequest);
    }

    @DeleteMapping("/suspicious-ip/{ipRequest}")
    public ResponseEntity<?> deleteSuspiciousIP(@PathVariable String ipRequest) {
        return securityService.deleteSuspiciousIP(ipRequest);
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<?> getSuspiciousIP() {
        return securityService.getSuspiciousIP();
    }

    @PostMapping("/stolencard")
    public ResponseEntity<?> addStolenCard(@RequestBody CardRequest cardRequest) {
        return securityService.addStolenCard(cardRequest);
    }

    @DeleteMapping("/stolencard/{cardRequest}")
    public ResponseEntity<?> deleteStolenCard(@PathVariable String cardRequest) {
        return securityService.deleteStolenCard(cardRequest);
    }

    @GetMapping("/stolencard")
    public ResponseEntity<?> getStolenCard() {
        return securityService.getStolenCard();
    }
}
