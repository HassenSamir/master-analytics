package com.app.backend.controllers;

import com.app.backend.dto.SiteDTO;
import com.app.backend.models.Site;
import com.app.backend.services.SiteServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/sites")
public class SiteController {

    @Autowired
    private SiteServices siteServices;

    @PostMapping("/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> createSite(@RequestBody SiteDTO site, @PathVariable String userId) throws Exception {
        return siteServices.createSite(site, userId);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getSitesByUserId(@PathVariable String userId) throws Exception {
        return siteServices.getSitesByUserId(userId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public Site updateSite(@PathVariable String id, @RequestBody Site updatedSite) throws Exception {
        return siteServices.updateSite(id, updatedSite);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteSite(@PathVariable String id)  {
        return siteServices.deleteSite(id);
    }
}
