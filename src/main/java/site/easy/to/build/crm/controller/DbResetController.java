package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import site.easy.to.build.crm.service.dbreset.DbResetService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/db-reset")
public class DbResetController {
    private final DbResetService dbResetService;

    @GetMapping({ "", "/" })
    public String showDbResetPage() {
        return "db-reset/index";
    }

    @PostMapping
    public String handleDbReset() {
        try {
            dbResetService.resetDatabase();
        } catch (Exception e) {
            return "error/500";
        }

        return "redirect:/db-reset";
    }
}
