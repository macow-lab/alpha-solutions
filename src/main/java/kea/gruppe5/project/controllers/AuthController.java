package kea.gruppe5.project.controllers;
// MM, JLJ & IPN
import javax.servlet.http.HttpSession;


import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kea.gruppe5.project.models.User;
import kea.gruppe5.project.service.AuthService;

@Controller
@RequestMapping("auth")
public class AuthController {

    public void wipeSession(HttpSession session) {

    }

    @GetMapping("/login")
    public String loginView(HttpSession session) {
        if (session.getAttribute("email") != null) {
            session.removeAttribute("personnelNumber");
            session.removeAttribute("name");
            session.removeAttribute("email");
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam MultiValueMap body, RedirectAttributes redirectAttrs, HttpSession session) {
        // Hente varaible fra POST request
        String email = String.valueOf(body.get("email")).replace("[", "").replace("]", "");
        String password = String.valueOf(body.get("password")).replace("[", "").replace("]", "");

        // Metode kaldes for at validere det indtastede login
        User user = AuthService.authenticateUser(email, password);

        // Hvis der bliver fundet en bruger med det givne login, bliver koden i if-statement kørt.
        if (user != null) {
            // Den nuværende får sine oplysninger gemt i en session
            session.setAttribute("personnelNumber", user.getPersonnelNumber());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("name", user.getName());

            return "redirect:/";
        }

        // Hvis login forsøget fejler, bliver der send tilbage til loginformularen, med et URL parameter
        redirectAttrs.addAttribute("status", "fail");
        return "redirect:/auth/login?status={status}";
    }

    @GetMapping("/register")
    public String registerView() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam MultiValueMap body, HttpSession session, RedirectAttributes redirectAttrs) {
        String fullName = String.valueOf(body.get("fullName")).replace("[", "").replace("]", "");
        String email = String.valueOf(body.get("email")).replace("[", "").replace("]", "");
        String password = String.valueOf(body.get("password")).replace("[", "").replace("]", "");
        String address = String.valueOf(body.get("address")).replace("[", "").replace("]", "");
        String city = String.valueOf(body.get("city")).replace("[", "").replace("]", "");
        String postalCode = String.valueOf(body.get("postalCode")).replace("[", "").replace("]", "");
        String phoneNumber = String.valueOf(body.get("phoneNumber")).replace("[", "").replace("]", "");
        String country = String.valueOf(body.get("country")).replace("[", "").replace("]", "");

        User user = AuthService.createUser(fullName, email, password, address, city, postalCode, phoneNumber, country);

        if (user != null) {
            // Den nuværende får sine oplysninger gemt i en session
            session.setAttribute("personnelNumber", user.getPersonnelNumber());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("name", user.getName());

            return "redirect:/";
        }

        redirectAttrs.addAttribute("status", "fail");
        return "redirect:/auth/register?status={status}";

    }



    @GetMapping("/signout")
    public String signoutView(HttpSession session) {
        // Get Request for at logge ud

        session.removeAttribute("personnelNumber");
        session.removeAttribute("name");
        session.removeAttribute("email");
        
        return "root"; // Skal return en redirect til "/" ruten
    }


}
