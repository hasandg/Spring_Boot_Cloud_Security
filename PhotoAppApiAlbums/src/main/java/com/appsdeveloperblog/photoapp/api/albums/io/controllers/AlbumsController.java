/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appsdeveloperblog.photoapp.api.albums.io.controllers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.photoapp.api.albums.data.AlbumEntity;
import com.appsdeveloperblog.photoapp.api.albums.service.AlbumsService;
import com.appsdeveloperblog.photoapp.api.albums.ui.model.AlbumResponseModel;

@RestController
@RequestMapping("/users/{id}/albums")
public class AlbumsController {
    
    @Autowired
    AlbumsService albumsService;
    
    Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @GetMapping( 
            produces = { 
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_XML_VALUE,
            })
    @PreAuthorize("principal == #id or hasAuthority('PROFILE.READ') or hasRole('ADMIN')")
    public List<AlbumResponseModel> userAlbums(@PathVariable String id) {

        List<AlbumResponseModel> returnValue = new ArrayList<>();
        
        List<AlbumEntity> albumsEntities = albumsService.getAlbums(id);
        
        if(albumsEntities == null || albumsEntities.isEmpty())
        {
            return returnValue;
        }
        
        Type listType = new TypeToken<List<AlbumResponseModel>>(){}.getType();
 
        returnValue = new ModelMapper().map(albumsEntities, listType);
        logger.info("Returning " + returnValue.size() + " albums");
        return returnValue;
    }
    
    @DeleteMapping("/{albumId}")
    @PreAuthorize("principal == #id or hasRole('ADMIN')")
    public String deleteAlbum(@PathVariable String id, @PathVariable String albumId) {
    	return "User with id "+ id + " is allowed to delete album with id " + albumId;
    }
}
