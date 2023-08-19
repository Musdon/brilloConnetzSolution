package verification;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static java.lang.Math.abs;

public class UserDetails {
    private static final String SECRET_KEY = "RPTyyaBeHl04wqPFd86G/tssX+pTxPq6HHCa2QnCOAU=";
    private static final long EXPIRATION_TIME_MS = 86400000;

    private static boolean validateUsername(String username){
        return username.length()> 4;
    }

    private static boolean validateEmailAddress(String email){
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(regexPattern).matcher(email).matches();
    }

    private static boolean validatePassword(String password){
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        return Pattern.compile(regex).matcher(password).matches();
    }

    private static boolean validateDateOfBirth(LocalDate dateOfBirth){
        LocalDate currentDate = LocalDate.now();
        long difference = ChronoUnit.YEARS.between(currentDate, dateOfBirth);
        return Math.abs(difference) >= 16;
    }

    public static String validateUser(String username, String email, String password, LocalDate dateOfBirth){
        if (!validateUsername(username)){
            return "Username cannot be empty and cannot be less than 4 in length";
        }
        if (!validateEmailAddress(email)){
            return "Ensure you use a valid email";
        }
        if (!validatePassword(password)){
            return "Password must contain at least one lowercase, uppercase, number and symbol. It must also not be less than 8 characters";
        }

        return generateJwt(email);
    }

    public static String validateUserConcurrently(String username, String email, String password, LocalDate dateOfBirth){
        CompletableFuture<String> usernameFuture = CompletableFuture.supplyAsync(() -> username);
        CompletableFuture<String> passwordFuture = CompletableFuture.supplyAsync(() -> password);
        CompletableFuture<String> emailFuture = CompletableFuture.supplyAsync(() -> email);
        CompletableFuture<String> dateOfBirthFuture = CompletableFuture.supplyAsync(dateOfBirth::toString);

        CompletableFuture<Void> validation = CompletableFuture.allOf(usernameFuture, passwordFuture, emailFuture, dateOfBirthFuture)
                .thenRun(() -> {
                    if (!validateUsername(usernameFuture.join())){
                        System.out.println("Username cannot be empty and cannot be less than 4 in length");
                    }
                    if (!validateEmailAddress(emailFuture.join())){
                        System.out.println("Ensure you use a valid email");
                    }
                    if (!validatePassword(passwordFuture.join())) {
                        System.out.println("Invalid Password");
                    }
                    if (!validateDateOfBirth(LocalDate.parse(dateOfBirthFuture.join()))){
                        System.out.println("Invalid date of birth");
                    }
                });
        validation.join();
        return generateJwt(emailFuture.join());
    }

    public static String generateJwt(String subject) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME_MS);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key())
                .compact();
    }

    public static String validateToken(String token, String email){
        //get email from token, and compare to the user's email
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (claims.getSubject().equalsIgnoreCase(email)){
            return "Verification Passed";
        } else {
            return "Verification failed";
        }
    }

    @org.junit.Test
    public void testGeneratedTokenSubject(){
        String email = "majibade5@gmail.com";
        String token = generateJwt(email);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        assertEquals(claims.getSubject(), email);
    }

    private static Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public static void main(String[] args) {
        System.out.println(validateUser("Musdon", "majibade5@gmail.com", "brilloCon90@", LocalDate.parse("2000-01-01")));
        System.out.println(validateUserConcurrently("Musdon", "majibade5@gmail.com", "brilloCon90@", LocalDate.parse("2000-01-01")));
        System.out.println(validateToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYWppYmFkZTVAZ21haWwuY29tIiwiaWF0IjoxNjkyNDA2MTU5LCJleHAiOjE2OTI0OTI1NTl9.Z__VF_uhKzdy2aFxk9Uii1qFna04zA9gSa5Gkz6J2-g", "majibade5@gmail.com"));
    }
}
