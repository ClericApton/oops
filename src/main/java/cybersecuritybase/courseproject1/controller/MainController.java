package cybersecuritybase.courseproject1.controller;

import cybersecuritybase.courseproject1.database.EntryDao;
import cybersecuritybase.courseproject1.database.MessageDao;
import cybersecuritybase.courseproject1.domain.Entry;
import cybersecuritybase.courseproject1.domain.Message;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    
    @Autowired
    private MessageDao messageDao;
    
    @Autowired
    private EntryDao entryDao;
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginMapping() {
        return "login";
    }
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String redirectByRole(HttpServletRequest request) {
        boolean isAdmin = request.isUserInRole("ADMIN");
        if (isAdmin) {
            return "redirect:/admin";
        }
        return "redirect:/user";
    }
    
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminMapping(Model model) {
        model.addAttribute("messages", this.messageDao.findAll());
        return "admin";
    }
    
    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public String addMessage(@RequestParam("content") String content) {
        if (!content.isEmpty()) {
            this.messageDao.save(new Message(content));
        }
        return "redirect:/admin";
    }
    
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String userMapping(Model model, Authentication authentication, @RequestParam(value = "search", required = false) String search) {
        String user = authentication.getName();
        model.addAttribute("user", user);
        Message message = this.messageDao.findLatest();
        if (message != null) {
            model.addAttribute("message", message);
        }
        if (search != null) {
            model.addAttribute("search", search);
            model.addAttribute("entries", this.entryDao.findBySearchString(user, search));
        }       
        return "user";
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public String addEntry(Authentication authentication, @RequestParam("service") String service, @RequestParam("username") String username, @RequestParam("password") String password) {
        if (!service.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            this.entryDao.save(new Entry(authentication.getName(), service, username, password));
        }
        return "redirect:/user";
    }
    
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteEntries(Authentication authentication) {
        this.entryDao.deleteAll(authentication.getName());
        return "redirect:/user";
    }
    
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public String logout() {
        return "redirect:/login?logout";
    }
}
